package br.gft.template.demo.timer;

import br.gft.template.demo.timer.aop.TimerAop;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(TimerAop.class)
@EnableAspectJAutoProxy
public @interface EnableTimer {
}
