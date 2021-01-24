package com.example.demo.filters.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.filters.RequestResponseLoggersFilter;

@Configuration
public class FilterConfig {

	// filtering requests to be logged
	@Bean
	FilterRegistrationBean<RequestResponseLoggersFilter> filterRegistrationBean(RequestResponseLoggersFilter requestResponseLoggersFilter){
		FilterRegistrationBean<RequestResponseLoggersFilter> filterRegistrationBean=new FilterRegistrationBean<>();
		filterRegistrationBean.setFilter(requestResponseLoggersFilter);
		filterRegistrationBean.addUrlPatterns("/api/signUp");
		return filterRegistrationBean;
	}
}
