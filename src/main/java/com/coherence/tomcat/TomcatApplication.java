package com.coherence.tomcat;

import com.oracle.coherence.spring.configuration.annotation.CoherenceCache;
import com.tangosol.net.Coherence;
import com.tangosol.net.NamedCache;
import com.tangosol.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import javax.servlet.ServletContext;

@SpringBootApplication
public class TomcatApplication implements ApplicationRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(TomcatApplication.class);

	@CoherenceCache
	private NamedCache<String, String> myCacheName;

	@Autowired
	private Coherence coherence;

	@Autowired
	private ServletContext context;

	public static void main(String[] args) {
		SpringApplication.run(TomcatApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {

		LOGGER.info("context: '{}'", this.context.getContextPath());

		final String cacheNameToUse;

		if (StringUtils.hasText(context.getContextPath())) {
			cacheNameToUse = context.getContextPath().substring(1);
		}
		else {
			cacheNameToUse = "root";
		}

		this.myCacheName.put(cacheNameToUse, "Hello " + cacheNameToUse);

		NamedCache<Integer, String> namedCache = coherence.getSession().getCache(cacheNameToUse);

		for (int i = 1; i<=10000; i++) {
			namedCache.put(i, "Hello Coherence Spring");
		}
	}
}
