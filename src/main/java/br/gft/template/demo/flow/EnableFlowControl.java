package br.gft.template.demo.flow;


import br.gft.template.demo.flow.config.FlowConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ImportAutoConfiguration(FlowConfig.class)
public @interface EnableFlowControl {
}
