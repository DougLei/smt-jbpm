package com.smt.jbpm.module.execution.task.button;

/**
 * 
 * @author DougLei
 */
public class HandleButton extends OptionButton{
	private boolean suggest; // 是否输入意见
	private boolean attitude; // 是否输入态度
	
	public HandleButton(String type, String name, boolean suggest, boolean attitude) {
		super(type, name);
		this.suggest = suggest;
		this.attitude = attitude;
	}

	public boolean isSuggest() {
		return suggest;
	}
	public boolean isAttitude() {
		return attitude;
	}
}
