package br.gft.template.demo.shared.timer;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface Timer {

    @AliasFor("message")
    String value() default "### Tempo de processamento = {}";

    @AliasFor("value")
    String message() default "### Tempo de processamento = {}";

    /**
     * Chave para valor em log marker no contexto da thread.
     * */
    String[] params() default {};
}
