package br.gft.template.demo.shared.flow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Propriedades do controle de fluxo de processamento multithread
 * maxData / minTime (timeUnit).
 */
@ConfigurationProperties(prefix = "gft.flow")
@Data
public class FlowProperties {

    /**
     * Número máximo de eventos a ser processado em tempo determinado por minTime
     */
    private Integer maxData = 1000;

    /**
     * Tempo mínimo total para aguardar antes de processar o número de eventos em maxData
     */
    private Long minTime = 1L;

    /**
     * Unidade de tempo para tempo mínimo total de processamento
     */
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    /**
     * Número máximo de threads sendo utilizados pela thread pool
     */
    private Integer threadsNumber = 10;

    /**
     * Flag para ativar ou desativar controle de fluxo
     */
    private boolean activated = true;
}
