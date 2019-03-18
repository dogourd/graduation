package top.ezttf.graduation.hadoop.flume.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public class LogbackAdaptorFactory implements LoggingAdaptorFactory {

    @Override
    public LoggingAdaptor create(Class clazz) {
        return new LogbackAdaptorFactory.LogbackAdapter(LoggerFactory.getLogger(clazz));
    }

    private static class LogbackAdapter implements LoggingAdaptor {

        private final Logger logger;

        LogbackAdapter(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void debug(String msg) {
            logger.debug(msg);
        }

        @Override
        public void debug(String msg, Object... objects) {
            logger.debug(msg, objects);
        }

        @Override
        public void info(String msg) {
            logger.info(msg);
        }

        @Override
        public void warn(String msg) {
            logger.warn(msg);
        }

        @Override
        public void warn(String msg, Throwable t) {
            logger.warn(msg, t);
        }

        @Override
        public void error(String msg) {
            logger.error(msg);
        }

        @Override
        public void error(String msg, Throwable t) {
            logger.error(msg, t);
        }
    }
}
