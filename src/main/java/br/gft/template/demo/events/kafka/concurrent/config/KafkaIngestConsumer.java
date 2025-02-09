package br.gft.template.demo.events.kafka.concurrent.config;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface KafkaIngestConsumer {
    /**
     * Tópicos a serem consumidos.
     */
    String[] topics();

    /**
     * (Opcional) GroupId para o consumidor.
     */
    String groupId() default "";

    /**
     * Tópico de retry (quando o erro é passível de nova tentativa).
     */
    String retryTopic();

    /**
     * Tópico de DLQ (quando o erro não pode ser tratado com retry).
     */
    String dlqTopic();

    /**
     * Número máximo de tentativas de retry.
     */
    int retryAttempts() default 3;

    /**
     * Indica se os eventos devem ser validados.
     */
    boolean validateEvent() default true;
}
