package com.smt.jbpm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.douglei.bpm.ProcessEngine;
import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.ProcessEngineBuilder;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.douglei.bpm.module.repository.RepositoryModule;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.orm.context.SessionFactoryContainer;
import com.douglei.orm.spring.boot.starter.TransactionComponentScan;
import com.smt.jbpm.api.user.assignable.expression.AllUserExpression;
import com.smt.jbpm.api.user.assignable.expression.DeptExpression;
import com.smt.jbpm.api.user.assignable.expression.PostExpression;
import com.smt.jbpm.api.user.assignable.expression.RoleExpression;
import com.smt.jbpm.api.user.assignable.expression.StarterExpression;
import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

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
	
	@Autowired
	private RestTemplateWrapper restTemplate;
	
	@Bean
	public ProcessEngine processEngine() {
		ProcessEngineBuilder builder = new ProcessEngineBuilder();
		// 注册自定义的可指派用户表达式
		builder.registerCustomAPIBean(AssignableUserExpression.class, new AllUserExpression(restTemplate));
		builder.registerCustomAPIBean(AssignableUserExpression.class, new DeptExpression(restTemplate));
		builder.registerCustomAPIBean(AssignableUserExpression.class, new PostExpression(restTemplate));
		builder.registerCustomAPIBean(AssignableUserExpression.class, new RoleExpression(restTemplate));
		builder.registerCustomAPIBean(AssignableUserExpression.class, new StarterExpression());
		return builder.build(container.get());
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
}
