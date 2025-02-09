package br.gft.template.demo.events.kafka.concurrent.consumer;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Data
public class ConsumerThreadContext {

    private ConsumerRecord<String, String> consumerRecord;

    private Object parsedObject;

    private Map<String, String> parsedHeaders = new HashMap<>();
}
