package top.ezttf.graduation.hadoop.flume.util;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public interface LoggingAdaptorFactory {

    LoggingAdaptor create(Class<?> clazz);
}
