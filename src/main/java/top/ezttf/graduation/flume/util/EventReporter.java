package top.ezttf.graduation.flume.util;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public class EventReporter {

    private RpcClient client;
    private final LoggingAdaptor logger;
    private final ExecutorService es;
    private final Properties connectionProps;

    public EventReporter(Properties properties, int maximumThreadPoolSize, int maxQueueSize, LoggingAdaptorFactory loggingFactory) {
        this.logger = loggingFactory.create(EventReporter.class);
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(maxQueueSize);
        this.connectionProps = properties;
        int corePoolSize = 1;
        TimeUnit threadKeepAliveUnits = TimeUnit.SECONDS;
        int threadKeepAliveTime = 30;
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("thread-call-runner-%d").build();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        this.es = new ThreadPoolExecutor(corePoolSize, maximumThreadPoolSize, (long) threadKeepAliveTime, threadKeepAliveUnits, blockingQueue, threadFactory, handler);
    }

    public void report(Event[] events) {
        this.es.submit(new EventReporter.ReportingJob(events));
    }

    private synchronized RpcClient createClient() {
        if (this.client == null) {
            this.logger.info("Creating a new Flume Client with properties: " + this.connectionProps);

            try {
                this.client = RpcClientFactory.getInstance(this.connectionProps);
            } catch (Exception e) {
                this.logger.error(e.getLocalizedMessage(), e);
            }
        }

        return this.client;
    }

    public synchronized void close() {
        this.logger.info("Shutting down Flume client");
        if (this.client != null) {
            this.client.close();
            this.client = null;
        }

    }

    public void shutdown() {
        this.close();
        this.es.shutdown();
    }

    private class ReportingJob implements Runnable {
        private static final int retries = 3;
        private final Event[] events;

        public ReportingJob(Event[] events) {
            this.events = events;
            //EventReporter.this.logger.debug("Created a job containing {} events", events.length);
        }

        @Override
        public void run() {
            boolean success = false;
            int count = 0;

            try {
                while (!success && count < 3) {
                    ++count;
                    try {
                        //EventReporter.this.logger.debug("Reporting a batch of {} events, try {}", this.events.length, count);
                        EventReporter.this.createClient().appendBatch(Arrays.asList(this.events));
                        success = true;
                        //EventReporter.this.logger.debug("Successfully reported a batch of {} events", this.events.length);
                    } catch (EventDeliveryException e) {
                        EventReporter.this.logger.warn(e.getLocalizedMessage(), e);
                        EventReporter.this.logger.warn("Will retry " + (3 - count) + " times");
                    }
                }
            } finally {
                if (!success) {
                    EventReporter.this.logger.error("Could not submit events to Flume");
                    EventReporter.this.close();
                }

            }

        }
    }
}
