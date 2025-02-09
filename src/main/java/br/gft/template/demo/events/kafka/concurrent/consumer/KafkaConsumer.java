package br.gft.template.demo.events.kafka.concurrent.consumer;

import br.gft.template.demo.logs.LogstashMarkerProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class KafkaConsumer<T> {

    protected final KafkaConsumerService<T> kafkaConsumerService;
    protected final ExecutorService executor;

    /**
     * Construtor que recebe o nome do consumidor e cria um executor com threads cujo nome
     * terá o prefixo "listener-<nome>-" (onde se remove o sufixo "IngestService" se presente e
     * o nome é convertido para minúsculas).
     *
     * @param kafkaConsumerService Serviço que contém a lógica de processamento.
     * @param consumerName         Nome do consumidor (por exemplo, o bean name do IngestService).
     */
    protected KafkaConsumer(KafkaConsumerService<T> kafkaConsumerService, String consumerName) {
        this.kafkaConsumerService = kafkaConsumerService;
        String prefix = consumerName;
        if (prefix.toLowerCase().endsWith("ingestservice")) {
            prefix = prefix.substring(0, prefix.length() - "ingestservice".length());
        }
        prefix = prefix.toLowerCase();
        this.executor = Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("listener-" + prefix + "-", 0).factory()
        );
    }

    /**
     * Método que será registrado como listener (via Spring Kafka) e que processa a lista de mensagens.
     */
    public void receive(List<ConsumerRecord<String, String>> consumerRecords, Acknowledgment acknowledgment) {
        LogstashMarkerProvider.resetLogs();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
            futures.add(processAsync(consumerRecord));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        acknowledgment.acknowledge();
    }

    protected CompletableFuture<Boolean> processAsync(ConsumerRecord<String, String> consumerRecord) {
        return CompletableFuture.supplyAsync(() -> kafkaConsumerService.process(consumerRecord), executor);
    }
}
