package br.gft.template.demo.events.kafka.config;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Data
@Validated
public class KafkaConsumerProperties {

    /**
     * Indica se os eventos devem ser validados de acordo com contraints da classe.
     */
    private boolean validateEvent = true;

    /**
     * Quantidade de tentativas de retry caso ocorra um erro apto a retry
     */
    private int retryAttempts = 3;

    @NotEmpty
    private List<String> topics;

    @NotBlank
    private String retryTopic;

    @NotBlank
    private String dlqTopic;
}
