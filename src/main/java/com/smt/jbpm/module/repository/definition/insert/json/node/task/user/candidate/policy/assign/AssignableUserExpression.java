package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.policy.assign;

/**
 * 
 * @author DougLei
 */
public class AssignableUserExpression {
	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		if(value == null)
			return "";
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
