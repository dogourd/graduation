package top.ezttf.graduation.appender.util;


import org.apache.commons.lang3.StringUtils;

/**
 * @author yuwen
 * @date 2018/12/20
 */
public class RemoteFlumeAgent {

    private final String hostname;
    private final int port;

    public RemoteFlumeAgent(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public static RemoteFlumeAgent fromString(String input, LoggingAdaptorFactory loggingAdapterFactory) {
        LoggingAdaptor logger = loggingAdapterFactory.create(RemoteFlumeAgent.class);
        if (StringUtils.isNotBlank(input)) {
            String[] parts = input.split(":");
            if (parts.length == 2) {
                String portString = parts[1];

                try {
                    int port = Integer.parseInt(portString);
                    return new RemoteFlumeAgent(parts[0], port);
                } catch (NumberFormatException e) {
                    logger.error("Not a valid int: " + portString);
                }
            } else {
                logger.error("Not a valid [host]:[port] configuration: " + input);
            }
        } else {
            logger.error("Empty flume agent entry, an extra comma?");
        }

        return null;
    }
}
