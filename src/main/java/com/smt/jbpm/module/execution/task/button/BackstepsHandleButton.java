package com.smt.jbpm.module.execution.task.button;

/**
 * 
 * @author DougLei
 */
public class BackstepsHandleButton extends HandleButton {
	private int steps; // 回退的步数
	
	public BackstepsHandleButton(String type, String name, boolean suggest, boolean attitude, int steps) {
		super(type, name, suggest, attitude);
		this.steps = steps;
	}

	public int getSteps() {
		return steps;
	}
}
