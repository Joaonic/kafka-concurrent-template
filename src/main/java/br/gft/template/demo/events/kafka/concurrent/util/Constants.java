package br.gft.template.demo.events.kafka.concurrent.util;

public class Constants {

    public static final String RETRY_COUNT = "retry-count";

    public static final String FIRST_ARRIVAL_DATE = "first-arrival";

    public static final String RETRY_LOG = "### Enviando mensagem {} para {}º retry, topico {}, headers {}";
    public static final String DLQ_LOG = "### Enviando mensagem {} para DLQ, topico {}, headers {}";
    public static final String RETRY_TOPIC = "retry-topic";
    public static final String KAFKA_HEADERS = "kafka-headers";

}
