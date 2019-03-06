package top.ezttf.graduation.flume;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import top.ezttf.graduation.flume.util.FlumeAvroManager;
import top.ezttf.graduation.flume.util.LogbackAdaptorFactory;
import top.ezttf.graduation.flume.util.LoggingAdaptorFactory;
import top.ezttf.graduation.flume.util.RemoteFlumeAgent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yuwen
 * @date 2018/12/20
 */
@Slf4j
@Setter
@Getter
public class LogbackFlumeAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private final Charset charset = StandardCharsets.UTF_8;
    private static LoggingAdaptorFactory factory = new LogbackAdaptorFactory();

    private FlumeAvroManager avroManager;
    private Layout<ILoggingEvent> layout;

    private Map<String, String> additionHeaders;

    private String type;
    private String flumeAgents;
    private String hostname;
    private String application;
    private String flumeProperties;

    private Long reportingWindow;

    private Integer batchSize;
    private Integer reporterMaxThreadPoolSize;
    private Integer reporterMaxQueueSize;


    @Override
    public void addFilter(Filter<ILoggingEvent> newFilter) {
        newFilter = new LevelFilter();
        ((LevelFilter) newFilter).setLevel(Level.DEBUG);
        ((LevelFilter) newFilter).setOnMatch(FilterReply.ACCEPT);
        ((LevelFilter) newFilter).setOnMismatch(FilterReply.DENY);
        super.addFilter(newFilter);
    }

    @Override
    public void start() {
        if (layout == null) {
            addWarn("layout was not defined, will only log the message, no stack traces or custom layout");
        }
        if (StringUtils.isBlank(application)) {
            application = resolveApplication();
        }
        if (StringUtils.isNotBlank(flumeAgents)) {
            String[] strings = flumeAgents.split(";");
            List<RemoteFlumeAgent> agents = new ArrayList<>();
            for (String string : strings) {
                RemoteFlumeAgent agent = RemoteFlumeAgent.fromString(string.trim(), factory);
                if (agent != null) {
                    agents.add(agent);
                } else {
                    addWarn("Cannot build a Flume agent config for '" + string + "'");
                }
            }
            Properties properties = new Properties();
            properties.putAll(extractProperties(flumeProperties));
            avroManager = FlumeAvroManager
                    .create(agents, properties, batchSize, reportingWindow, reporterMaxThreadPoolSize, reporterMaxQueueSize, factory);
        } else {
            addError("Cannot configure a flume agent with an empty configuration");
        }
        super.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (avroManager != null) {
            String body = layout != null ? layout.doLayout(eventObject) : eventObject.getFormattedMessage();
            Map<String, String> headers = new HashMap<>();
            if (additionHeaders != null) {
                headers.putAll(additionHeaders);
            }
            headers.putAll(extractHeaders(eventObject));
            Event event = EventBuilder.withBody(body, StandardCharsets.UTF_8, headers);
            avroManager.send(event);
        }
    }

    @Override
    public void stop() {
        if (avroManager != null) {
            avroManager.stop();
        }
    }


    /**
     * 提取 header
     *
     * @param eventObject
     * @return
     */
    private Map<String, String> extractHeaders(ILoggingEvent eventObject) {
        Map<String, String> headers = new HashMap<>(9 * 4 / 3 + 1);
        headers.put("timestamp", Long.toString(eventObject.getTimeStamp()));
        headers.put("type", eventObject.getLevel().toString());
        headers.put("level", eventObject.getLevel().toString());
        headers.put("logger", eventObject.getLoggerName());
        headers.put("message", eventObject.getMessage());
        headers.put("thread", eventObject.getThreadName());
        headers.put("host", resolveHostName());
        if (StringUtils.isNotBlank(application)) {
            headers.put("application", application);
        }
        if (StringUtils.isNotBlank(type)) {
            headers.put("type", type);
        }
        return headers;
    }

    /**
     * 处理 host
     *
     * @return
     */
    private String resolveHostName() {
        try {
            return hostname != null ? hostname : InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("should not get here");
    }

    /**
     * 处理应用属性
     *
     * @return
     */
    private String resolveApplication() {
        return System.getProperty("application.name");
    }


    /**
     * 处理Flume属性
     *
     * @param flumeProperties
     * @return
     */
    private Map<String, String> extractProperties(String flumeProperties) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isNotBlank(flumeProperties)) {
            String[] strings = flumeProperties.split(";");
            for (String string : strings) {
                String[] split = string.split("=");
                if (split.length == 2) {
                    final String key = split[0].trim();
                    final String value = split[1].trim();
                    if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                        map.put(key, value);
                    } else {
                        addWarn("blank key or value not accepted : " + string);
                    }
                } else {
                    addError("not a valid \"key=value\" format : " + string);
                }
            }
        } else {
            addInfo("Not overriding any flume agent properties, use the default configuration");
        }
        return map;
    }


}
