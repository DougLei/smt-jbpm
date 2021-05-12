package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.policy.handle;

/**
 * 
 * @author DougLei
 */
public class SerialHandle {
	private String name;

	public String getName() {
		if(name==null)
			return "";
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
