package br.gft.template.demo.flow;


import br.gft.template.demo.flow.config.FlowConfig;
import br.gft.template.demo.flow.config.FlowThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.TimeUnit;


/**
 * <Pre>
 * Classe utilizada para controle de fluxo.
 * O Algoritmo de controle é realizado por Thread.
 * ex:
 * </pre>
 * <pre>
 *     public class Service {
 *         private AnyService anyService;
 *
 *         private FlowControl flowControl;
 *
 *         public void doSomething() {
 *             for (int i = 0; i < 100; i++) {
 *                 CompletableFuture.supplyAsync(() -> {
 *                     try {
 *                         flowControl.stopFlow();
 *                         anyService.doSomething();
 *                     } catch (Exception e) {
 *                         // TRATAMENTO
 *                     } finally {
 *                         flowControl.reset();
 *                     }
 *                     return;
 *                 });
 *             }
 *         }
 *     }
 * </pre>
 */
public final class FlowControl implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowControl.class);

    private final FlowConfig flowConfig;

    public FlowControl(FlowConfig flowConfig) {
        this.flowConfig = flowConfig;
    }

    private static final ThreadLocal<FlowThreadContext> threadContext = ThreadLocal.withInitial(FlowThreadContext::new);


    public void start() {
        this.context().setStartTime(System.currentTimeMillis());
        this.context().setProcessedEvents(0);
    }

    public FlowThreadContext context() {
        return threadContext.get();
    }

    private long startTime() {
        return this.context().getStartTime();
    }

    private int processedEvents() {
        return this.context().getProcessedEvents();
    }


    public boolean isActivated() {
        return flowConfig.getProperties().isActivated();
    }

    public void stopFlow() throws InterruptedException {
        if (!flowConfig.getProperties().isActivated() || processedEvents() == 0) {
            return;
        }
        long timeDifference = System.currentTimeMillis() - startTime();
        long timeLeft = flowConfig.getMinTimeMs() - timeDifference;

        if (processedEvents() < flowConfig.getMaxDataPerThread() && timeDifference < flowConfig.getMinTimeMs() && timeLeft > 0) {
            int eventsLeftToProcess = flowConfig.getMaxDataPerThread() - processedEvents();
            long sleepTime = timeLeft / eventsLeftToProcess;

            LOGGER.debug("Thread {} esperando por {} SEGUNDOS", Thread.currentThread().threadId(), sleepTime / 1000);
            TimeUnit.MILLISECONDS.sleep(sleepTime);
            LOGGER.debug("Thread {} reativada", Thread.currentThread().threadId());
        }
    }

    public void reset() {
        if (!flowConfig.getProperties().isActivated()) return;

        this.context().setProcessedEvents(this.context().getProcessedEvents() + 1);

        LOGGER.debug("THREAD ID [{}] - PROCESSED EVENTS [{}] - THREAD CONTEXT HASH CODE [{}]", Thread.currentThread().threadId(), this.context().getProcessedEvents(), this.context().hashCode());

        boolean shouldReset = processedEvents() >= flowConfig.getMaxDataPerThread();

        if (shouldReset) threadContext.remove();
    }

    @Override
    public void destroy() throws Exception {
        threadContext.remove();
    }
}
