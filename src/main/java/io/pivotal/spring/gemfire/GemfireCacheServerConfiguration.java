package io.pivotal.spring.gemfire;

import java.util.Properties;

import com.gemstone.gemfire.cache.ExpirationAction;


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

import io.pivotal.spring.gemfire.async.MultiRawListener;
import io.pivotal.spring.gemfire.async.ServerCacheListener;

/**
 * Created by lei_xu on 6/19/16.
 */
@Configuration
@EnableConfigurationProperties(GemfireProperties.class)
public class GemfireCacheServerConfiguration {

    // Gemfire Locator and Server Configurations

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

//        gemfireCache.setClose(true);
        gemfireCache.setProperties(gemfireProperties);
//        gemfireCache.setUseBeanFactoryLocator(false);
//        gemfireCache.setPdxSerializer(new RawPdxSerializer());
        gemfireCache.setPdxReadSerialized(false);

        return gemfireCache;
    }

    @Bean
    Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();

        gemfireProperties.setProperty("name", BootApplication.class.getSimpleName());
        gemfireProperties.setProperty("log-level", properties.getLogLevel());


        if (properties.getUseLocator().equals("true")) {
            gemfireProperties.setProperty("mcast-port", "0");
            gemfireProperties.setProperty("locators", properties.getLocatorAddress());
            gemfireProperties.setProperty("start-locator", properties.getLocatorAddress());
        }

        if (properties.getUseJmx().equals("true")) {
            gemfireProperties.setProperty("jmx-manager", properties.getUseJmx());
            gemfireProperties.setProperty("jmx-manager-port", properties.getJmxManagerPort());
            gemfireProperties.setProperty("jmx-manager-start", properties.getStartJmx());
        }


        String logFile = properties.getLogFile();

        if (logFile != null && !logFile.isEmpty()) {
            gemfireProperties.setProperty("log-file", logFile);
        }
        
        return gemfireProperties;
    }

    // RegionRaw Configurations
    @Bean(name = "RegionRaw")
    PartitionedRegionFactoryBean<String, Object> rawRegion(Cache gemfireCache,
                                                              @Qualifier("rawRegionAttributes") RegionAttributes<String, Object> rawRegionAttributes)
    {
        PartitionedRegionFactoryBean<String, Object> rawRegion = new PartitionedRegionFactoryBean<>();

        rawRegion.setCache(gemfireCache);
//        rawRegion.setClose(false);
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
        rawRegionAttributes.addCacheListener(serverCacheListener());

        return rawRegionAttributes;
    }

    @Bean
    @SuppressWarnings("unchecked")
    ExpirationAttributesFactoryBean expirationAttributes() {
        ExpirationAttributesFactoryBean expirationAttributes = new ExpirationAttributesFactoryBean();

        expirationAttributes.setTimeout(30);
        expirationAttributes.setAction(ExpirationAction.DESTROY);

        return expirationAttributes;
    }

//    @Bean
//    RawChangeListener rawChangeListener() {
//        return new RawChangeListener();
//    }

    @Bean
    MultiRawListener rawChangeListener() {
        return new MultiRawListener();
    }

    @Bean
    ServerCacheListener serverCacheListener() {
        return new ServerCacheListener();
    }
//    @Bean
//    public GemfireRepositoryFactoryBean rawRecordRepository() {
//        GemfireRepositoryFactoryBean<RawRecordRepository, RawRecord, String> repositoryFactoryBean =
//                new GemfireRepositoryFactoryBean<>();
//
//        repositoryFactoryBean.setRepositoryInterface(RawRecordRepository.class);
//
//        return repositoryFactoryBean;
//    }

    @Bean
    AsyncEventQueueFactoryBean asyncEventQueue(Cache gemfireCache) {
        AsyncEventQueueFactoryBean asyncEventQueue = new AsyncEventQueueFactoryBean(gemfireCache, rawChangeListener());
        asyncEventQueue.setName("rawQueue");
        asyncEventQueue.setParallel(false);
        asyncEventQueue.setDispatcherThreads(1);
        asyncEventQueue.setBatchTimeInterval(50);
        asyncEventQueue.setBatchSize(100);
        asyncEventQueue.setBatchConflationEnabled(true);
        asyncEventQueue.setPersistent(false);
        asyncEventQueue.setDiskSynchronous(false);

        return asyncEventQueue;
    }

    // RegionCount Configurations
    @Bean(name = "RegionCount")
    PartitionedRegionFactoryBean<String, Integer> countRegion(Cache gemfireCache)
    {
        PartitionedRegionFactoryBean<String, Integer> countRegion = new PartitionedRegionFactoryBean<>();

        countRegion.setCache(gemfireCache);
//        countRegion.setClose(false);
        countRegion.setName("RegionCount");
        countRegion.setPersistent(false);

        return countRegion;
    }

    // RegionTop Configurations
    @Bean(name = "RegionTop")
    PartitionedRegionFactoryBean<Integer, Object> topRegion(Cache gemfireCache)
    {
        PartitionedRegionFactoryBean<Integer, Object> topRegion = new PartitionedRegionFactoryBean<>();

        topRegion.setCache(gemfireCache);
//        topRegion.setClose(false);
        topRegion.setName("RegionTop");
        topRegion.setPersistent(false);

        return topRegion;
    }

    // RegionTopTen Configurations
    @Bean(name = "RegionTopTen")
    PartitionedRegionFactoryBean<Integer, Object> topTenRegion(Cache gemfireCache)
    {
        PartitionedRegionFactoryBean<Integer, Object> topTenRegion = new PartitionedRegionFactoryBean<>();

        topTenRegion.setCache(gemfireCache);
//        topTenRegion.setClose(false);
        topTenRegion.setName("RegionTopTen");
        topTenRegion.setPersistent(false);

        return topTenRegion;
    }

}
