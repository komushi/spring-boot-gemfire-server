## Quick Start Steps 

## Download and Build

```
git clone https://github.com/komushi/spring-boot-gemfire-server.git
cd spring-boot-gemfire-server
mvn clean package -DskipTests
```

## Run with Maven

which works fine

```
mvn spring-boot:run
```

## Run in java cli

```
java -jar target/spring-boot-gemfire-server-0.0.1-SNAPSHOT.jar
```

which will have the following error.
It seems to related to this bug (https://issues.apache.org/jira/browse/GEODE-800) but did not find a resolution.

```
[warning 2016/06/20 11:37:38.030 JST SpringBootGemfireServerApplication <main> tid=0x1] (tid=1 msgId=0) Required Commands classes were not loaded. Check logs for errors.
java.lang.IllegalStateException: Required Commands classes were not loaded. Check logs for errors.
	at com.gemstone.gemfire.management.internal.cli.CommandManager.raiseExceptionIfEmpty(CommandManager.java:240)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.loadCommands(CommandManager.java:179)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.<init>(CommandManager.java:77)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.getInstance(CommandManager.java:269)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.getInstance(CommandManager.java:249)
	at com.gemstone.gemfire.management.internal.cli.remote.CommandProcessor.<init>(CommandProcessor.java:50)
	at com.gemstone.gemfire.management.internal.cli.remote.MemberCommandService.<init>(MemberCommandService.java:32)
	at com.gemstone.gemfire.management.cli.CommandService.createLocalCommandService(CommandService.java:138)
	at com.gemstone.gemfire.management.internal.beans.MemberMBeanBridge.<init>(MemberMBeanBridge.java:352)
	at com.gemstone.gemfire.management.internal.beans.ManagementAdapter.handleCacheCreation(ManagementAdapter.java:152)
	at com.gemstone.gemfire.management.internal.beans.ManagementListener.handleEvent(ManagementListener.java:111)
	at com.gemstone.gemfire.distributed.internal.InternalDistributedSystem.notifyResourceEventListeners(InternalDistributedSystem.java:2245)
	at com.gemstone.gemfire.distributed.internal.InternalDistributedSystem.handleResourceEvent(InternalDistributedSystem.java:505)
	at com.gemstone.gemfire.internal.cache.GemFireCacheImpl.init(GemFireCacheImpl.java:1009)
	at com.gemstone.gemfire.internal.cache.GemFireCacheImpl.create(GemFireCacheImpl.java:682)
	at com.gemstone.gemfire.cache.CacheFactory.create(CacheFactory.java:182)
	at com.gemstone.gemfire.cache.CacheFactory.create(CacheFactory.java:229)
	at org.springframework.data.gemfire.CacheFactoryBean.createCache(CacheFactoryBean.java:356)
	at org.springframework.data.gemfire.CacheFactoryBean.resolveCache(CacheFactoryBean.java:250)
	at org.springframework.data.gemfire.CacheFactoryBean.init(CacheFactoryBean.java:191)
	at org.springframework.data.gemfire.CacheFactoryBean.getObject(CacheFactoryBean.java:178)
	at org.springframework.data.gemfire.CacheFactoryBean.getObject(CacheFactoryBean.java:88)
	at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.doGetObjectFromFactoryBean(FactoryBeanRegistrySupport.java:168)
	at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.getObjectFromFactoryBean(FactoryBeanRegistrySupport.java:103)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getObjectForBeanInstance(AbstractBeanFactory.java:1600)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:317)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:197)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:187)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.findAutowireCandidates(DefaultListableBeanFactory.java:1208)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1048)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1018)
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:834)
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:741)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:467)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1123)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1018)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:510)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:482)
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:306)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:230)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:302)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:197)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:756)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:861)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:541)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:759)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:369)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:313)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1176)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1165)
	at io.pivotal.spring.gemfire.SpringBootGemfireServerApplication.main(SpringBootGemfireServerApplication.java:10)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.springframework.boot.loader.MainMethodRunner.run(MainMethodRunner.java:48)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:87)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:50)
	at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:58)

[info 2016/06/20 11:37:38.033 JST SpringBootGemfireServerApplication <main> tid=0x1] Command Service could not be initialized. Required Commands classes were not loaded. Check logs for errors.
com.gemstone.gemfire.management.cli.CommandServiceException: Required Commands classes were not loaded. Check logs for errors.
	at com.gemstone.gemfire.management.internal.cli.remote.MemberCommandService.<init>(MemberCommandService.java:38)
	at com.gemstone.gemfire.management.cli.CommandService.createLocalCommandService(CommandService.java:138)
	at com.gemstone.gemfire.management.internal.beans.MemberMBeanBridge.<init>(MemberMBeanBridge.java:352)
	at com.gemstone.gemfire.management.internal.beans.ManagementAdapter.handleCacheCreation(ManagementAdapter.java:152)
	at com.gemstone.gemfire.management.internal.beans.ManagementListener.handleEvent(ManagementListener.java:111)
	at com.gemstone.gemfire.distributed.internal.InternalDistributedSystem.notifyResourceEventListeners(InternalDistributedSystem.java:2245)
	at com.gemstone.gemfire.distributed.internal.InternalDistributedSystem.handleResourceEvent(InternalDistributedSystem.java:505)
	at com.gemstone.gemfire.internal.cache.GemFireCacheImpl.init(GemFireCacheImpl.java:1009)
	at com.gemstone.gemfire.internal.cache.GemFireCacheImpl.create(GemFireCacheImpl.java:682)
	at com.gemstone.gemfire.cache.CacheFactory.create(CacheFactory.java:182)
	at com.gemstone.gemfire.cache.CacheFactory.create(CacheFactory.java:229)
	at org.springframework.data.gemfire.CacheFactoryBean.createCache(CacheFactoryBean.java:356)
	at org.springframework.data.gemfire.CacheFactoryBean.resolveCache(CacheFactoryBean.java:250)
	at org.springframework.data.gemfire.CacheFactoryBean.init(CacheFactoryBean.java:191)
	at org.springframework.data.gemfire.CacheFactoryBean.getObject(CacheFactoryBean.java:178)
	at org.springframework.data.gemfire.CacheFactoryBean.getObject(CacheFactoryBean.java:88)
	at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.doGetObjectFromFactoryBean(FactoryBeanRegistrySupport.java:168)
	at org.springframework.beans.factory.support.FactoryBeanRegistrySupport.getObjectFromFactoryBean(FactoryBeanRegistrySupport.java:103)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getObjectForBeanInstance(AbstractBeanFactory.java:1600)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:317)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:197)
	at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:187)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.findAutowireCandidates(DefaultListableBeanFactory.java:1208)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1048)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1018)
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:834)
	at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:741)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:467)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1123)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1018)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:510)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:482)
	at org.springframework.beans.factory.support.AbstractBeanFactory$1.getObject(AbstractBeanFactory.java:306)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:230)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:302)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:197)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:756)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:861)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:541)
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:759)
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:369)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:313)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1176)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1165)
	at io.pivotal.spring.gemfire.SpringBootGemfireServerApplication.main(SpringBootGemfireServerApplication.java:10)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.springframework.boot.loader.MainMethodRunner.run(MainMethodRunner.java:48)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:87)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:50)
	at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:58)
Caused by: java.lang.IllegalStateException: Required Commands classes were not loaded. Check logs for errors.
	at com.gemstone.gemfire.management.internal.cli.CommandManager.raiseExceptionIfEmpty(CommandManager.java:240)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.loadCommands(CommandManager.java:179)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.<init>(CommandManager.java:77)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.getInstance(CommandManager.java:269)
	at com.gemstone.gemfire.management.internal.cli.CommandManager.getInstance(CommandManager.java:249)
	at com.gemstone.gemfire.management.internal.cli.remote.CommandProcessor.<init>(CommandProcessor.java:50)
	at com.gemstone.gemfire.management.internal.cli.remote.MemberCommandService.<init>(MemberCommandService.java:32)
	... 52 more
```

