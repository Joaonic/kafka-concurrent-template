package br.gft.template.demo.events.kafka.concurrent.config;

import br.gft.template.demo.events.*;
import br.gft.template.demo.events.kafka.concurrent.consumer.DefaultKafkaConsumer;
import br.gft.template.demo.events.kafka.concurrent.consumer.KafkaConsumerService;
import br.gft.template.demo.events.kafka.concurrent.producer.KafkaProducer;
import br.gft.template.demo.events.kafka.config.KafkaConsumerProperties;
import br.gft.template.demo.flow.FlowControl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@AutoConfiguration
@RequiredArgsConstructor
public class AutoKafkaConsumerConfiguration implements KafkaListenerConfigurer {

    private final ApplicationContext applicationContext;
    private final KafkaProducer kafkaProducer;
    private final FlowControl flowControl;
    private final ObjectMapper objectMapper;
    private final Environment environment; // injetado pelo Spring Boot

    // Utilizamos um SpEL parser para avaliar expressões
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Resolve um valor que pode conter placeholders (${...}) ou expressões (# {...}).
     */
    private String resolve(String value) {
        if (value == null) {
            return null;
        }
        // Primeiro, resolve placeholders do tipo ${...}
        String resolved = environment.resolvePlaceholders(value);
        // Se o valor resolver para algo que comece com "#{", consideramos uma expressão SpEL
        if (resolved.startsWith("#{") && resolved.endsWith("}")) {
            Expression exp = expressionParser.parseExpression(resolved, new TemplateParserContext());
            String evaluated = exp.getValue(environment, String.class);
            return evaluated != null ? evaluated : resolved;
        }
        return resolved;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        // Obtém o bean MessageHandlerMethodFactory já configurado pelo Spring Boot
        MessageHandlerMethodFactory factory;
        try {
            factory = applicationContext.getBean("messageHandlerMethodFactory", MessageHandlerMethodFactory.class);
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível encontrar o bean 'messageHandlerMethodFactory' no ApplicationContext. " +
                    "Certifique-se de que a configuração default do Spring Kafka esteja ativa.", e);
        }
        registrar.setMessageHandlerMethodFactory(factory);

        ListableBeanFactory listableBeanFactory = applicationContext;
        // Obtém todos os beans que implementam IngestService
        Map<String, IngestService> ingestServices = listableBeanFactory.getBeansOfType(IngestService.class);

        // Obtenha o AutowireCapableBeanFactory para inicializar os objetos manualmente
        var autowireFactory = applicationContext.getAutowireCapableBeanFactory();

        for (Map.Entry<String, IngestService> entry : ingestServices.entrySet()) {
            String beanName = entry.getKey();
            IngestService<?> ingestService = entry.getValue();

            // Verifica se o IngestService está anotado com @KafkaIngestConsumer
            KafkaIngestConsumer annotation = AnnotationUtils.findAnnotation(ingestService.getClass(), KafkaIngestConsumer.class);
            if (annotation == null) {
                continue; // ignora se não estiver anotado
            }

            // Resolve os valores dos atributos da anotação utilizando o método resolve()
            String[] topicsFromAnnotation = annotation.topics();
            // Resolva cada tópico
            for (int i = 0; i < topicsFromAnnotation.length; i++) {
                topicsFromAnnotation[i] = resolve(topicsFromAnnotation[i]);
            }

            // Cria um objeto de propriedades para este consumidor, com base nos valores resolvidos
            KafkaConsumerProperties properties = new KafkaConsumerProperties();
            properties.setTopics(Arrays.asList(topicsFromAnnotation));
            properties.setRetryTopic(resolve(annotation.retryTopic()));
            properties.setDlqTopic(resolve(annotation.dlqTopic()));
            properties.setRetryAttempts(annotation.retryAttempts());
            properties.setValidateEvent(annotation.validateEvent());

            String groupId = resolve(annotation.groupId());

            // Resolve o tipo T do IngestService (ex.: Teste)
            Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(ingestService.getClass(), IngestService.class);
            Class<?> ingestType = (typeArgs != null && typeArgs.length > 0) ? typeArgs[0] : Object.class;

            // Busca, dentre os beans Converter, aquele que tenha o parâmetro genérico compatível com ingestType.
            Converter<Object> converter = findConverterForType(ingestType);
            if (converter == null) {
                // Se não houver, usa o DefaultConverter com o tipo do IngestService
                converter = (Converter<Object>) new DefaultConverter<>(objectMapper, ingestType);
            }

            // Filtra os beans de HandlerBeforeInsert compatíveis com ingestType
            List<HandlerBeforeInsert<Object>> filteredHandlersBefore = filterHandlerBeforeForType(ingestType);
            // Filtra os beans de HandlerAfterInsert compatíveis com ingestType
            List<HandlerAfterInsert<Object>> filteredHandlersAfter = filterHandlerAfterForType(ingestType);

            // Cria o KafkaConsumerService com as dependências específicas
            KafkaConsumerService<Object> consumerService = new KafkaConsumerService<>(
                    kafkaProducer,
                    flowControl,
                    properties,
                    (IngestService<Object>) ingestService,
                    converter,
                    filteredHandlersAfter,
                    filteredHandlersBefore
            );
            consumerService = (KafkaConsumerService<Object>) autowireFactory.initializeBean(consumerService, beanName + "ConsumerService");

            // Cria a instância do consumidor, passando o bean name para nomear as threads corretamente
            DefaultKafkaConsumer<Object> kafkaConsumer = new DefaultKafkaConsumer<>(consumerService, beanName);
            kafkaConsumer = (DefaultKafkaConsumer<Object>) autowireFactory.initializeBean(kafkaConsumer, beanName + "KafkaConsumer");

            // Configura o endpoint do Kafka apontando para o método "receive" do consumidor
            MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
            endpoint.setId(beanName + "KafkaConsumerEndpoint");
            endpoint.setGroupId(groupId);
            endpoint.setTopics(topicsFromAnnotation);
            endpoint.setBean(kafkaConsumer);
            endpoint.setMessageHandlerMethodFactory(factory);
            try {
                Method receiveMethod = DefaultKafkaConsumer.class.getMethod("receive", List.class, Acknowledgment.class);
                endpoint.setMethod(receiveMethod);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Erro ao registrar o método 'receive' do KafkaConsumer", e);
            }
            registrar.registerEndpoint(endpoint);
        }
    }

    // Métodos auxiliares para encontrar e filtrar beans (permanece inalterado)

    private Converter<Object> findConverterForType(Class<?> targetType) {
        Map<String, Converter> converters = applicationContext.getBeansOfType(Converter.class);
        for (Converter candidate : converters.values()) {
            Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(candidate.getClass(), Converter.class);
            if (typeArgs != null && typeArgs.length > 0 && typeArgs[0] != null) {
                if (typeArgs[0].equals(targetType)) {
                    return candidate;
                }
            }
        }
        return null;
    }

    private List<HandlerBeforeInsert<Object>> filterHandlerBeforeForType(Class<?> targetType) {
        Map<String, HandlerBeforeInsert> handlers = applicationContext.getBeansOfType(HandlerBeforeInsert.class);
        List<HandlerBeforeInsert<Object>> result = new ArrayList<>();
        for (HandlerBeforeInsert handler : handlers.values()) {
            Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(handler.getClass(), HandlerBeforeInsert.class);
            if (typeArgs != null && typeArgs.length > 0 && typeArgs[0] != null) {
                if (typeArgs[0].equals(targetType)) {
                    result.add(handler);
                }
            }
        }
        return result;
    }

    private List<HandlerAfterInsert<Object>> filterHandlerAfterForType(Class<?> targetType) {
        Map<String, HandlerAfterInsert> handlers = applicationContext.getBeansOfType(HandlerAfterInsert.class);
        List<HandlerAfterInsert<Object>> result = new ArrayList<>();
        for (HandlerAfterInsert handler : handlers.values()) {
            Class<?>[] typeArgs = GenericTypeResolver.resolveTypeArguments(handler.getClass(), HandlerAfterInsert.class);
            if (typeArgs != null && typeArgs.length > 0 && typeArgs[0] != null) {
                if (typeArgs[0].equals(targetType)) {
                    result.add(handler);
                }
            }
        }
        return result;
    }
}
