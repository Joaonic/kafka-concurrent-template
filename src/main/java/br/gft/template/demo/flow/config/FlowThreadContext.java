package br.gft.template.demo.shared.flow.config;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class FlowThreadContext {


    private long startTime = System.currentTimeMillis();

    private int processedEvents = 0;

}
