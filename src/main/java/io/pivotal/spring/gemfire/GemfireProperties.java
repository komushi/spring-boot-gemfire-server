package io.pivotal.spring.gemfire;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by lei_xu on 6/19/16.
 */
@ConfigurationProperties(prefix="properties")
public class GemfireProperties {
    private static final Integer DEFAULT_MAX_CONNECTIONS = 100;

    private static final Boolean DEFAULT_AUTO_STARTUP = true;

    private static final Integer DEFAULT_CACHE_SERVER_PORT = 40404;

    private static final String DEFAULT_LOG_LEVEL = "config";

    private static final String DEFAULT_HOSTNAME_FOR_CLIENTS = "localhost";

    private static final String DEFAULT_JMX_MANAGER_PORT = "1099";

    private static final String DEFAULT_LOCATOR_ADDRESS = "localhost[10334]";

    private Boolean autoStartuo;

    private String bindAddress;

    private String hostNameForClients;

    private Integer cacheServerPort;

    private Integer maxConnections;

    private String logLevel;

    private String locatorAddress;

    private String jmxManagerPort;

    public void setAutoStartuo(Boolean autoStartuo) {
        this.autoStartuo = autoStartuo;
    }

    public Boolean getAutoStartuo() {
        if (this.autoStartuo == null) {
            return DEFAULT_AUTO_STARTUP;
        }
        else {
            return this.autoStartuo;
        }
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public String getBindAddress() {
        if (this.bindAddress == null) {
            return DEFAULT_HOSTNAME_FOR_CLIENTS;
        }
        else {
            return this.bindAddress;
        }
    }

    public void setHostNameForClients(String hostNameForClients) {
        this.hostNameForClients = hostNameForClients;
    }

    public String getHostNameForClients() {
        if (this.hostNameForClients == null) {
            return DEFAULT_HOSTNAME_FOR_CLIENTS;
        }
        else {
            return this.hostNameForClients;
        }
    }

    public void setCacheServerPort(Integer cacheServerPort) {
        this.cacheServerPort = cacheServerPort;
    }

    public Integer getCacheServerPort() {
        if (this.cacheServerPort == null) {
            return DEFAULT_CACHE_SERVER_PORT;
        }
        else {
            return this.cacheServerPort;
        }
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public Integer getMaxConnections() {
        if (this.maxConnections == null) {
            return DEFAULT_MAX_CONNECTIONS;
        }
        else {
            return this.maxConnections;
        }
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogLevel() {
        if (this.logLevel == null) {
            return DEFAULT_LOG_LEVEL;
        }
        else {
            return this.logLevel;
        }
    }

    public void setLocatorAddress(String locatorAddress) {
        this.locatorAddress = locatorAddress;
    }

    public String getLocatorAddress() {
        if (this.locatorAddress == null) {
            return DEFAULT_LOCATOR_ADDRESS;
        }
        else {
            return this.locatorAddress;
        }
    }

    public void setJmxManagerPort(String jmxManagerPort) {
        this.jmxManagerPort = jmxManagerPort;
    }

    public String getJmxManagerPort() {
        if (this.jmxManagerPort == null) {
            return DEFAULT_JMX_MANAGER_PORT;
        }
        else {
            return this.jmxManagerPort;
        }
    }



}
