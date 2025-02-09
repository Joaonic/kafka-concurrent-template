package br.gft.template.demo.events.kafka.concurrent.aop;

import br.gft.template.demo.logs.LogstashMarkerProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Aspect
@Component
public class CorrelatorAop {

    public static final String X_CORRELATOR = "x-correlator";

    private static final String CORRELATOR_SERVICE_NAME = ":data-sync";


    @Before("@within(advice) || @annotation(advice)")
    public void handleCorrelator(JoinPoint joinPoint, AddCorrelator advice) {

        String correlator = null;

        for (var arg : joinPoint.getArgs()) {
            if (arg instanceof ConsumerRecord<?, ?> consumerRecord) {
                correlator = getCorrelator(consumerRecord.headers());
                break;
            }
        }

        if (correlator != null) {
            LogstashMarkerProvider.add(X_CORRELATOR, correlator.concat(CORRELATOR_SERVICE_NAME));
        } else {
            LogstashMarkerProvider.add(X_CORRELATOR, UUID.randomUUID().toString().concat(CORRELATOR_SERVICE_NAME));
        }
    }

    private static String getCorrelator(final Headers headers) {

        for (final Header header : headers) {
            if (header.key().equals(X_CORRELATOR) || header.key().equals(X_CORRELATOR.replace('-', '_')))
                return new String(header.value(), StandardCharsets.UTF_8);
        }
        return null;
    }

}
