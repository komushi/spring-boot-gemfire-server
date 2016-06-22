package io.pivotal.spring.gemfire;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
//import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
//import org.springframework.data.gemfire.RegionAttributesFactoryBean;

import com.gemstone.gemfire.cache.Cache;


/**
 * Created by lei_xu on 6/19/16.
 */
@Configuration
@EnableConfigurationProperties(GemfireProperties.class)
public class GemfireCacheServerConfiguration {

    @Autowired
    private GemfireProperties properties;

    @Bean
    CacheServerFactoryBean gemfireCacheServer (Cache gemfireCache) {



        CacheServerFactoryBean gemfireCacheServer = new CacheServerFactoryBean();

        gemfireCacheServer.setCache(gemfireCache);
        gemfireCacheServer.setAutoStartup(properties.getAutoStartuo());
        gemfireCacheServer.setBindAddress(properties.getBindAddress());
        gemfireCacheServer.setHostNameForClients(properties.getHostNameForClients());
        gemfireCacheServer.setPort(properties.getCacheServerPort());
        gemfireCacheServer.setMaxConnections(properties.getMaxConnections());

        return gemfireCacheServer;
    }

    @Bean
    CacheFactoryBean gemfireCache(@Qualifier("gemfireProperties") Properties gemfireProperties) {
        CacheFactoryBean gemfireCache = new CacheFactoryBean();

        gemfireCache.setClose(true);
        gemfireCache.setProperties(gemfireProperties);
        gemfireCache.setUseBeanFactoryLocator(false);

        return gemfireCache;
    }

    @Bean
    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();

        gemfireProperties.setProperty("name", SpringBootGemfireServerApplication.class.getSimpleName());
        gemfireProperties.setProperty("mcast-port", "0");
        gemfireProperties.setProperty("log-level", properties.getLogLevel());
        gemfireProperties.setProperty("locators", properties.getLocatorAddress());
        gemfireProperties.setProperty("start-locator", properties.getLocatorAddress());
        gemfireProperties.setProperty("jmx-manager", "true");
        gemfireProperties.setProperty("jmx-manager-port", properties.getJmxManagerPort());
        gemfireProperties.setProperty("jmx-manager-start", "true");

        String logFile = properties.getLogFile();

        if (logFile != null && !logFile.isEmpty()) {
            gemfireProperties.setProperty("log-file", logFile);
        }
        
        return gemfireProperties;
    }



}
