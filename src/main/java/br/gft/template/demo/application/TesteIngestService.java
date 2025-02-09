package br.gft.template.demo.application;

import br.gft.template.demo.events.IngestService;
import br.gft.template.demo.events.kafka.KafkaIngestConsumer;
import org.springframework.stereotype.Service;

@KafkaIngestConsumer(
    topics = {"meu-topico"},
    groupId = "grupo-teste",
    retryTopic = "meu-retry-topico",
    dlqTopic = "meu-dlq-topico",
    retryAttempts = 5,
    validateEvent = true
)
@Service
public class TesteIngestService implements IngestService<Teste> {

    @Override
    public Teste ingest(Teste value) {
        // Lógica de processamento do objeto Teste
        System.out.println("Processando objeto: " + value);
        return value;
    }
}
