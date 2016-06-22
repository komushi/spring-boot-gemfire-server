package io.pivotal.spring.gemfire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// import java.util.*;
// import java.net.URL;
// import java.io.IOException;
// import org.springframework.shell.core.CommandMarker;
// import com.gemstone.gemfire.management.internal.cli.util.ClasspathScanLoadHelper;


@SpringBootApplication
public class SpringBootGemfireServerApplication {

	public static void main(String[] args) {

		// try {
		// 	// Set<Class<?>> foundClasses = ClasspathScanLoadHelper.loadAndGet("com.gemstone.gemfire.management.internal.cli.commands", CommandMarker.class, true);
		// 	Enumeration<URL> foundClasses = Thread.currentThread().getContextClassLoader().getResources("com.gemstone.gemfire.management.internal.cli.commands");

		// 	List arrlist = new ArrayList<URL>();

		// 	arrlist = Collections.list(foundClasses);

		// 	System.out.print("foundClasses.size:" + arrlist.size() + "\n");
		// } catch (IOException e) {
		// 	System.out.print(e.getLocalizedMessage());
		// } 
        


		SpringApplication.run(SpringBootGemfireServerApplication.class, args);
	}
}
