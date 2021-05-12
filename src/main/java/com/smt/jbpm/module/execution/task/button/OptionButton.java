package com.smt.jbpm.module.execution.task.button;

/**
 * 
 * @author DougLei
 */
public class OptionButton {
	private String type; // 按钮类型
	private String name; // 按钮名称

	public OptionButton(String type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}
}
