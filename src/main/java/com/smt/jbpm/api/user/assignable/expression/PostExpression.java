package com.smt.jbpm.api.user.assignable.expression;

import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

/**
 * 
 * @author DougLei
 */
public class PostExpression extends DeptExpression {
	public PostExpression(RestTemplateWrapper restTemplate) {
		super(restTemplate);
	}
	
	@Override
	public String getName() {
		return "post";
	}
	
	@Override
	protected String getParentKey() {
		return "POST_CODE";
	}
}
