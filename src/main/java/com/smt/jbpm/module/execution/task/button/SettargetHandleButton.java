package com.smt.jbpm.module.execution.task.button;

/**
 * 
 * @author DougLei
 */
public class SettargetHandleButton extends HandleButton {
	private String target; // 目标任务(配置文件中的)id
	private boolean assign; // 是否需要指派人
	
	public SettargetHandleButton(String type, String name, boolean suggest, boolean attitude, String target, boolean assign) {
		super(type, name, suggest, attitude);
		this.target = target;
		this.assign = assign;
	}
	
	public String getTarget() {
		return target;
	}
	public boolean isAssign() {
		return assign;
	}
}
