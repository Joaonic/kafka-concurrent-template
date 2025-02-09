package br.gft.template.demo.events.kafka.concurrent.config;

import br.gft.template.demo.events.kafka.concurrent.aop.CorrelatorAop;
import br.gft.template.demo.events.kafka.concurrent.producer.KafkaProducer;
import br.gft.template.demo.flow.EnableFlowControl;
import br.gft.template.demo.mapper.MapperService;
import br.gft.template.demo.mapper.json.EnableJsonMapperService;
import br.gft.template.demo.mapper.json.JsonMapperAutoConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;

@AutoConfiguration(after = {JsonMapperAutoConfig.class, KafkaAutoConfiguration.class})
@EnableFlowControl
@EnableJsonMapperService
@Import({CorrelatorAop.class})
@ImportAutoConfiguration({AutoKafkaConsumerConfiguration.class})
public class KafkaAutoConfig {


    @Bean
    public MessageHandlerMethodFactory messageHandlerMethodFactory() {
        return new DefaultMessageHandlerMethodFactory();
    }

    @Bean
    @ConditionalOnMissingBean(KafkaProducer.class)
    public KafkaProducer kafkaProducer(KafkaTemplate<String, String> kafkaTemplate, MapperService mapperService) {
        return new KafkaProducer(kafkaTemplate, mapperService);
    }


}
