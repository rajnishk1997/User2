package com.optum.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAsync
public class AppConfig {
	 @Bean
	    public static PropertySourcesPlaceholderConfigurer propertyConfig() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }
}