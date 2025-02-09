package br.gft.template.demo.logs;


import br.gft.template.demo.logs.config.LogsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@EnableConfigurationProperties(LogsProperties.class)
public @interface EnableThinkGrowthLogs {
}
