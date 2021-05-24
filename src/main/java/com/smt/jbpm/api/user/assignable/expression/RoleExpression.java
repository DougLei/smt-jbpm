package com.smt.jbpm.api.user.assignable.expression;

import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

/**
 * 
 * @author DougLei
 */
public class RoleExpression extends DeptExpression {
	public RoleExpression(RestTemplateWrapper restTemplate) {
		super(restTemplate);
	}
	
	@Override
	public String getName() {
		return "role";
	}

	@Override
	protected String getParentKey() {
		return "ROLE_CODE";
	}
}
