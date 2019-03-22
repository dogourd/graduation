package top.ezttf.graduation.appender.util;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public interface LoggingAdaptor {

    void debug(String message);

    void debug(String message, Object... obj);

    void info(String message);

    void warn(String message);

    void warn(String message, Throwable t);

    void error(String message);

    void error(String message, Throwable t);
}
