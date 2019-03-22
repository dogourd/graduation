package top.ezttf.graduation.appender.util;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public interface LoggingAdaptorFactory {

    LoggingAdaptor create(Class<?> clazz);
}
