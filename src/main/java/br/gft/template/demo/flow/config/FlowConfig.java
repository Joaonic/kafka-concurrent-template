package br.gft.template.demo.flow.config;

import br.gft.template.demo.flow.FlowControl;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@Data
@EnableConfigurationProperties(FlowProperties.class)
public class FlowConfig implements InitializingBean {

    private final FlowProperties properties;


    /**
     * Número máximo de eventos a ser processado para cada thread em tempo determinado por minTime
     */
    private Integer maxDataPerThread;

    /**
     * Tempo mínimo(ms) total para aguardar antes de processar o número de eventos em maxData
     */
    private Long minTimeMs;

    @Override
    public void afterPropertiesSet() {
        this.maxDataPerThread = this.properties.getMaxData() / this.properties.getThreadsNumber();
        this.minTimeMs = this.properties.getTimeUnit().toMillis(this.properties.getMinTime());
    }


    @Bean
    @ConditionalOnMissingBean(FlowControl.class)
    public FlowControl flowControl() {
        return new FlowControl(this);
    }

}
