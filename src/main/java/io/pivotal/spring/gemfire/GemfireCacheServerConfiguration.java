package io.pivotal.spring.gemfire;

import java.util.List;
import java.util.Properties;

import com.gemstone.gemfire.cache.ExpirationAction;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEvent;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventListener;
import com.gemstone.gemfire.cache.util.Gateway;
import com.gemstone.gemfire.cache.wan.GatewayEventFilter;
import com.gemstone.gemfire.cache.wan.GatewayEventSubstitutionFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.server.CacheServerFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.ExpirationAttributesFactoryBean;
import org.springframework.data.gemfire.wan.AsyncEventQueueFactoryBean;

import com.gemstone.gemfire.cache.Cache;

import com.gemstone.gemfire.cache.RegionAttributes;
import com.gemstone.gemfire.cache.ExpirationAttributes;


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

    @Bean
    PartitionedRegionFactoryBean<String, Object> rawRegion(Cache gemfireCache,
                                                              @Qualifier("rawRegionAttributes") RegionAttributes<String, Object> rawRegionAttributes)
    {
        PartitionedRegionFactoryBean<String, Object> rawRegion = new PartitionedRegionFactoryBean<>();

        rawRegion.setCache(gemfireCache);
        rawRegion.setClose(false);
        rawRegion.setAttributes(rawRegionAttributes);
        rawRegion.setName("RegionRaw");
        rawRegion.setPersistent(false);

        return rawRegion;
    }

    @Bean
    @SuppressWarnings("unchecked")
    RegionAttributesFactoryBean rawRegionAttributes(@Qualifier("expirationAttributes") ExpirationAttributes expirationAttributes) {
        RegionAttributesFactoryBean rawRegionAttributes = new RegionAttributesFactoryBean();

        rawRegionAttributes.addAsyncEventQueueId("rawQueue");
        rawRegionAttributes.setKeyConstraint(String.class);
        rawRegionAttributes.setValueConstraint(Object.class);
        rawRegionAttributes.setEntryTimeToLive(expirationAttributes);

        return rawRegionAttributes;
    }

    @Bean
    @SuppressWarnings("unchecked")
    ExpirationAttributesFactoryBean expirationAttributes() {
        ExpirationAttributesFactoryBean expirationAttributes = new ExpirationAttributesFactoryBean();

        expirationAttributes.setTimeout(3600);
        expirationAttributes.setAction(ExpirationAction.DESTROY);

        return expirationAttributes;
    }

    @Bean
    RawChangeListener rawChangeListener() {
        return new RawChangeListener();
    }

    @Bean
    AsyncEventQueueFactoryBean asyncEventQueue(Cache gemfireCache) {
        AsyncEventQueueFactoryBean asyncEventQueue = new AsyncEventQueueFactoryBean(gemfireCache, rawChangeListener());
        asyncEventQueue.setName("rawQueue");
        asyncEventQueue.setParallel(false);
        asyncEventQueue.setDispatcherThreads(1);
        asyncEventQueue.setBatchTimeInterval(5);
        asyncEventQueue.setBatchSize(100);
        asyncEventQueue.setBatchConflationEnabled(true);
        asyncEventQueue.setPersistent(false);
        asyncEventQueue.setDiskSynchronous(false);



        return asyncEventQueue;
    }
}
