package br.gft.template.demo.timer.aop;

import br.gft.template.demo.logs.LogstashMarkerProvider;
import br.gft.template.demo.timer.Timer;
import br.gft.template.demo.utils.DurationFormatUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimerAop {

    private static final String PROCESS_TIME = "process_time";


    @Around("@within(advice) || @annotation(advice)")
    public Object timer(ProceedingJoinPoint pjp, Timer advice) throws Throwable {
        final Logger logger = LoggerFactory.getLogger(pjp.getSignature().getDeclaringType());

        long startTime = System.currentTimeMillis();

        /* Resolve conflito de pointcuts @within e @annotation, que advice = null, quando anotação está presente tanto em métodos como classes */
        Timer annotation = advice != null ? advice : (Timer) pjp.getSignature().getDeclaringType().getAnnotation(Timer.class);

        try {
            return pjp.proceed();
        } finally {
            long processTime = System.currentTimeMillis() - startTime;

            StringBuilder builder = new StringBuilder(annotation.value()).append(" | ");
            for (int i = 0; i < annotation.params().length; i++) {
                var value = LogstashMarkerProvider.values().get(annotation.params()[i]);
                builder.append(annotation.params()[i]).append(" = ").append(value).append(" | ");
            }

            String timerMessage = builder.toString();
            String readableDuration = DurationFormatUtils.formatDuration(processTime, "HH:mm:ss.SSS");
            LogstashMarkerProvider.add(PROCESS_TIME, processTime);

            logger.info(LogstashMarkerProvider.markers(), timerMessage, readableDuration);
        }
    }


}
