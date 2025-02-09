package br.gft.template.demo.events.kafka.concurrent.consumer;

import br.gft.template.demo.events.Converter;
import br.gft.template.demo.events.HandlerAfterInsert;
import br.gft.template.demo.events.HandlerBeforeInsert;
import br.gft.template.demo.events.IngestService;
import br.gft.template.demo.events.kafka.concurrent.aop.AddCorrelator;
import br.gft.template.demo.events.kafka.concurrent.aop.CorrelatorAop;
import br.gft.template.demo.events.kafka.concurrent.producer.KafkaProducer;
import br.gft.template.demo.events.kafka.config.KafkaConsumerProperties;
import br.gft.template.demo.exceptions.RetryableException;
import br.gft.template.demo.flow.FlowControl;
import br.gft.template.demo.logs.LogstashMarkerProvider;
import br.gft.template.demo.timer.Timer;
import br.gft.template.demo.validator.BeanValidator;
import lombok.RequiredArgsConstructor;
import net.logstash.logback.marker.Markers;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.gft.template.demo.events.kafka.concurrent.util.Constants.*;

@RequiredArgsConstructor
public class KafkaConsumerService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    private static final ThreadLocal<ConsumerThreadContext> threadContext = ThreadLocal.withInitial(ConsumerThreadContext::new);

    private final KafkaProducer producerService;

    private final FlowControl flowControl;

    private final KafkaConsumerProperties properties;

    private final IngestService<T> ingestService;

    private final Converter<T> converter;

    private final List<HandlerAfterInsert<T>> handlersAfterList;

    private final List<HandlerBeforeInsert<T>> handlersBeforeList;

    public static ConsumerThreadContext context() {
        return threadContext.get();
    }

    @Timer("### Processamento finalizado. Tempo de execução = {}")
    @AddCorrelator
    public boolean process(ConsumerRecord<String, String> consumerRecord) {
        threadContext.remove();
        try {

            Map<String, String> headers = getAttributes(consumerRecord);

            setGlobalMarkers(consumerRecord, headers);

            setGlobalInfos(consumerRecord, headers);

            logBefore();

            String adaptedRecordValue = consumerRecordValueAdapter(consumerRecord);

            var object = converter.convert(adaptedRecordValue);

            context().setParsedObject(object);

            logAfterConverter();

            validate(object);

            flowControl.stopFlow();

            actionsBeforeInsert(object);

            ingestService.ingest(object);

            actionsAfterInsert(object);
            return true;
        } catch (Exception e) {
            errorHandler(e);
            return false;
        } finally {
            finallyActions();
        }
    }

    protected void logAfterConverter() {
        LOGGER.info(LogstashMarkerProvider.markers(), "### Evento Deserializado {} | headers {} |", context().getParsedObject(), context().getParsedHeaders());
    }

    protected void setGlobalMarkers(ConsumerRecord<String, String> consumerRecord, Map<String, String> headers) {
        LogstashMarkerProvider.add("is_global", true);
        LogstashMarkerProvider.add("topic", consumerRecord.topic());
        LogstashMarkerProvider.add("partition", consumerRecord.partition());
        LogstashMarkerProvider.add("offset", consumerRecord.offset());
        LogstashMarkerProvider.add("log_type", "EVENTO_RECEBIDO");
        LogstashMarkerProvider.add("key", consumerRecord.key());
        LogstashMarkerProvider.add(KAFKA_HEADERS, headers);
    }

    protected String consumerRecordValueAdapter(ConsumerRecord<String, String> consumerRecord) {
        return consumerRecord.value();
    }

    protected void setGlobalInfos(ConsumerRecord<String, String> consumerRecord, Map<String, String> headers) {
        context().setConsumerRecord(consumerRecord);
        context().setParsedHeaders(headers);
        context().setParsedObject(consumerRecord.value());
    }

    protected void errorHandler(Exception e) {
        context().getParsedHeaders().put(CorrelatorAop.X_CORRELATOR, (String) LogstashMarkerProvider.values().get(CorrelatorAop.X_CORRELATOR));
        LOGGER.error(LogstashMarkerProvider.markers(), e.getLocalizedMessage(), e);
        if (e instanceof RetryableException) {
            sendToQueue();
            return;
        }
        sendToDlq();
        Thread.currentThread().interrupt();
    }

    protected void finallyActions() {
        flowControl.reset();
    }

    protected void logBefore() {
        LOGGER.info(LogstashMarkerProvider.markers(), "### Evento recebido");
    }

    protected void validate(T object) {
        if (properties.isValidateEvent()) BeanValidator.validateBeanAndThrow(object);
    }


    protected void sendToQueue() {
        int retryCount = Integer.parseInt(context().getParsedHeaders().getOrDefault(RETRY_COUNT, "0")) + 1;

        context().getParsedHeaders().put(RETRY_COUNT, String.valueOf(retryCount));

        String topicToSend = retryCount <= properties.getRetryAttempts() ? properties.getRetryTopic() : properties.getDlqTopic();

        LOGGER.info(LogstashMarkerProvider
                        .markers().and(Markers.append(RETRY_COUNT, retryCount)
                                .and(Markers.append(RETRY_TOPIC, topicToSend))
                                .and(Markers.append(KAFKA_HEADERS, context().getParsedHeaders()))),
                RETRY_LOG, context().getParsedObject(), retryCount, topicToSend, context().getParsedHeaders());

        producerService.sendMessages(context().getConsumerRecord().value(), topicToSend, context().getParsedHeaders());
    }

    protected void sendToDlq() {
        LOGGER.info(LogstashMarkerProvider.markers(), DLQ_LOG, context().getParsedObject(), properties.getDlqTopic(), context().getParsedHeaders());

        producerService.sendMessages(context().getConsumerRecord().value(), properties.getDlqTopic(), context().getParsedHeaders());
    }

    protected Map<String, String> getAttributes(final ConsumerRecord<?, ?> consumerRecord) {
        final Map<String, String> attributes = new HashMap<>();

        for (final Header header : consumerRecord.headers()) {
            final String attributeName = header.key();
            attributes.put(attributeName, new String(header.value(), StandardCharsets.UTF_8));

        }
        return attributes;
    }

    protected void actionsBeforeInsert(T object) {
        if (handlersBeforeList == null) return;
        for (var handler : handlersBeforeList) {
            handler.handle(object, context().getConsumerRecord(), context().getParsedHeaders());
        }
    }

    protected void actionsAfterInsert(T object) {
        if (handlersAfterList == null) return;
        for (var handler : handlersAfterList) {
            handler.handle(object, context().getConsumerRecord(), context().getParsedHeaders());
        }
    }
}