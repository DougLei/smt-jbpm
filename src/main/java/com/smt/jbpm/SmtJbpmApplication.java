package com.smt.jbpm;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.douglei.bpm.ProcessEngine;
import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.ProcessEngineBuilder;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.douglei.bpm.module.repository.RepositoryModule;
import com.douglei.orm.context.SessionFactoryContainer;
import com.douglei.orm.spring.boot.starter.TransactionComponentScan;
import com.smt.jbpm.query.QueryCriteriaResolver;

/**
 * 
 * @return
 */
@SpringBootApplication
@ComponentScan("com.smt")
@TransactionComponentScan(packages = "com.smt")
public class SmtJbpmApplication implements WebMvcConfigurer{
	public static void main(String[] args) {
		SpringApplication.run(SmtJbpmApplication.class, args);
	}
	
	@Autowired
	private SessionFactoryContainer container;
	
	@Bean
	public ProcessEngine processEngine() {
		return new ProcessEngineBuilder().build(container.get());
	}
	
	@Bean
	public ProcessEngineBeans processEngineBeans() {
		return processEngine().getProcessEngineBeans();
	}
	
	@Bean
	public RepositoryModule repositoryModule() {
		return processEngine().getRepositoryModule();
	}
	
	@Bean
	public ExecutionModule executionModule() {
		return processEngine().getExecutionModule();
	}
	
	// ----------------------------------------------------------------------------
	@Bean
	public QueryCriteriaResolver queryCriteriaResolver() {
		return new QueryCriteriaResolver();
	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) { 
		resolvers.add(queryCriteriaResolver()); // 添加自定义的方法参数解析器
	}
}
