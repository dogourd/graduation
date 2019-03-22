package top.ezttf.graduation.appender.util;


import org.apache.flume.Event;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public class FlumeAvroManager {

    private final LoggingAdaptor LOGGER;
    private final LoggingAdaptorFactory loggerFactory;
    private static final AtomicLong THREAD_SEQUENCE = new AtomicLong(1L);
    private final BlockingQueue<Event> EV_QUEUE;
    private final FlumeAvroManager.AsyncThread ASYNC_THREAD;
    private final EventReporter reporter;

    public static FlumeAvroManager create(List<RemoteFlumeAgent> agents, Properties overrides, Integer batchSize, Long reportingWindow, Integer reporterMaxThreadPoolSize, Integer reporterMaxQueueSize, LoggingAdaptorFactory loggerFactory) {
        if (agents != null && agents.size() > 0) {
            Properties props = buildFlumeProperties(agents);
            props.putAll(overrides);
            return new FlumeAvroManager(props, reportingWindow, batchSize, reporterMaxThreadPoolSize, reporterMaxQueueSize, loggerFactory);
        } else {
            loggerFactory.create(FlumeAvroManager.class).error("No valid agents configured");
            return null;
        }
    }

    private FlumeAvroManager(Properties props, Long reportingWindowReq, Integer batchSizeReq, Integer reporterMaxThreadPoolSizeReq, Integer reporterMaxQueueSizeReq, LoggingAdaptorFactory loggerFactory) {
        this.LOGGER = loggerFactory.create(FlumeAvroManager.class);
        this.loggerFactory = loggerFactory;
        int reporterMaxThreadPoolSize = reporterMaxThreadPoolSizeReq == null ? 2 : reporterMaxThreadPoolSizeReq;
        int reporterMaxQueueSize = reporterMaxQueueSizeReq == null ? 50 : reporterMaxQueueSizeReq;
        this.reporter = new EventReporter(props, reporterMaxThreadPoolSize, reporterMaxQueueSize, loggerFactory);
        this.EV_QUEUE = new ArrayBlockingQueue<>(1000);
        long reportingWindow = this.harmonizeReportingWindow(reportingWindowReq);
        int batchSize = batchSizeReq == null ? 50 : batchSizeReq;
        this.ASYNC_THREAD = new FlumeAvroManager.AsyncThread(this.EV_QUEUE, batchSize, reportingWindow);
        this.LOGGER.info("Created a new flume agent with properties: " + props.toString());
        this.ASYNC_THREAD.start();
    }

    private long harmonizeReportingWindow(Long reportingWindowReq) {
        if (reportingWindowReq == null) {
            return 10000L;
        } else if (reportingWindowReq > 10000L) {
            return 10000L;
        } else {
            return reportingWindowReq < 100L ? 100L : reportingWindowReq;
        }
    }

    public void stop() {
        this.ASYNC_THREAD.shutdown();
    }

    public void send(Event event) {
        if (event != null) {
            this.EV_QUEUE.add(event);
        } else {
            this.LOGGER.warn("Trying to send a NULL event");
        }

    }

    private static Properties buildFlumeProperties(List<RemoteFlumeAgent> agents) {
        Properties props = new Properties();
        int i = 0;
        Iterator i$ = agents.iterator();

        while (i$.hasNext()) {
            RemoteFlumeAgent agent = (RemoteFlumeAgent) i$.next();
            props.put("hosts.h" + i++, agent.getHostname() + ':' + agent.getPort());
        }

        StringBuilder builder = new StringBuilder(i * 4);

        for (int j = 0; j < i; ++j) {
            builder.append("h").append(j).append(" ");
        }

        props.put("hosts", builder.toString());
        props.put("max-attempts", Integer.toString(3 * agents.size()));
        props.put("request-timeout", Integer.toString(1000));
        props.put("connect-timeout", Integer.toString(1000));
        if (i > 1) {
            props.put("client.type", "default_loadbalance");
            props.put("host-selector", "round_robin");
        }

        props.put("backoff", "true");
        props.put("maxBackoff", "10000");
        return props;
    }

    private class AsyncThread extends Thread {
        private final BlockingQueue<Event> queue;
        private final long reportingWindow;
        private final int batchSize;
        private volatile boolean shutdown;

        private AsyncThread(BlockingQueue<Event> queue, int batchSize, long reportingWindow) {
            this.shutdown = false;
            this.queue = queue;
            this.batchSize = batchSize;
            this.reportingWindow = reportingWindow;
            this.setDaemon(true);
            this.setName("FlumeAvroManager-" + FlumeAvroManager.THREAD_SEQUENCE.getAndIncrement());
            FlumeAvroManager.this.LOGGER.info("Started a new " + FlumeAvroManager.AsyncThread.class.getSimpleName() + " thread");
        }

        @Override
        public void run() {
            while (!this.shutdown) {
                long lastPoll = System.currentTimeMillis();
                long maxTime = lastPoll + this.reportingWindow;
                Event[] events = new Event[this.batchSize];
                int count = 0;

                try {
                    while (count < this.batchSize && System.currentTimeMillis() < maxTime) {
                        lastPoll = Math.max(System.currentTimeMillis(), lastPoll);
                        Event ev = this.queue.poll(maxTime - lastPoll, TimeUnit.MILLISECONDS);
                        if (ev != null) {
                            events[count++] = ev;
                        }
                    }
                } catch (InterruptedException e) {
                    FlumeAvroManager.this.LOGGER.warn(e.getLocalizedMessage(), e);
                }

                if (count > 0) {
                    Event[] batch;
                    if (count == events.length) {
                        batch = events;
                    } else {
                        batch = new Event[count];
                        System.arraycopy(events, 0, batch, 0, count);
                    }

                    try {
                        FlumeAvroManager.this.reporter.report(batch);
                    } catch (RejectedExecutionException e) {
                        FlumeAvroManager.this.LOGGER.error("Logging events batch rejected by EventReporter. Check reporter connectivity or consider increasing reporterMaxThreadPoolSize or reporterMaxQueueSize", e);
                    }
                }
            }

            FlumeAvroManager.this.reporter.shutdown();
        }

        public void shutdown() {
            FlumeAvroManager.this.LOGGER.error("Shutting down command received");
            this.shutdown = true;
        }
    }
}
