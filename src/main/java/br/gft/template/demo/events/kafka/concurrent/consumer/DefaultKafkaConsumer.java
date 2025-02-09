package br.gft.template.demo.events.kafka.concurrent.consumer;

public class DefaultKafkaConsumer<T> extends KafkaConsumer<T> {

    public DefaultKafkaConsumer(KafkaConsumerService<T> consumerService, String consumerName) {
        super(consumerService, consumerName);
    }
}
