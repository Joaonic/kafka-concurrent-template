package br.gft.template.demo.events.kafka.concurrent.producer;

import br.gft.template.demo.exceptions.InternalException;
import br.gft.template.demo.mapper.MapperService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private static final String KAFKA_ERRO = "Kafka unable to send message - message={}";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final MapperService mapperService;

    public void sendMessages(String message, String topic) {
        CompletableFuture<SendResult<String, String>> futureSP = sendToKafka(message, topic);

        tryGetFuture(message, futureSP);
    }

    public void sendMessages(String message, String topic, Map<String, String> headers) {
        CompletableFuture<SendResult<String, String>> futureSP = sendToKafka(message, topic, headers);

        tryGetFuture(message, futureSP);
    }

    public <T> void sendDocument(T document, String topic) {
        String message = mapperService.writeValueAsString(document);

        sendMessages(message, topic);
    }

    public <T> void sendDocument(T document, String topic, Map<String, String> headers) {
        String message = mapperService.writeValueAsString(document);

        sendMessages(message, topic, headers);
    }

    private static void tryGetFuture(String message, CompletableFuture<SendResult<String, String>> futureSP) {
        try {
            futureSP.get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.error(KAFKA_ERRO, message, e);
            throw new InternalException(e);
        }
    }

    private CompletableFuture<SendResult<String, String>> sendToKafka(String message, String topic) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        return getListenableFuture(message, topic, future);
    }

    private CompletableFuture<SendResult<String, String>> sendToKafka(String message, String topic, Map<String, String> headers) {
        var producerRecord = new ProducerRecord<String, String>(topic, message);

        for (var pair : headers.entrySet()) {
            if (pair.getValue() != null) {
                producerRecord.headers().add(pair.getKey(), pair.getValue().getBytes());
            }
        }

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(producerRecord);

        return getListenableFuture(message, topic, future);
    }

    private static CompletableFuture<SendResult<String, String>> getListenableFuture(String message, String topic, CompletableFuture<SendResult<String, String>> future) {

        future.whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                LOGGER.error(KAFKA_ERRO, message, throwable);
            } else {
                LOGGER.debug("### Kafka sent message - topic={}, with offset={}", topic, result.getRecordMetadata().offset());
            }
        });

        return future;
    }

}
