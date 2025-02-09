package br.gft.template.demo.events.kafka;


import br.gft.template.demo.events.kafka.concurrent.config.KafkaAutoConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ImportAutoConfiguration(KafkaAutoConfig.class)
public @interface EnableDataSyncKafka {
}
