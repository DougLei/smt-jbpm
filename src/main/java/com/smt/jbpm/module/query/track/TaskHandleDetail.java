package com.smt.jbpm.module.query.track;

import java.util.Date;

/**
 * 
 * @author DougLei
 */
public class TaskHandleDetail {
	private String taskinstId;
	private String user;
	private Date claimTime;
	private Date finishTime;
	private String suggest;
	private String attitude;
	private State state;
	
	public String getTaskinstId() {
		return taskinstId;
	}
	public void setTaskinstId(String taskinstId) {
		this.taskinstId = taskinstId;
	}
	public String getUser() {
		return user;
	}
	public void setUser_(String user) {
		this.user = user;
	}
	public Date getClaimTime() {
		return claimTime;
	}
	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	public String getSuggest() {
		return suggest;
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	public String getAttitude() {
		return attitude;
	}
	public void setAttitude(String attitude) {
		this.attitude = attitude;
	}
	public int getState() {
		return state.getValue();
	}
	public void setState(int state) {
		this.state = State.valueOf(state);
	}
}

/**
 * 
 * @author DougLei
 */
enum State {
	UNCLAIM(4), // 未认领
	CLAIMED(5), // 未办理
	FINISHED(6), // 已办理
	DISPATCHING(7), // 调度中
	DISPATCHING_FINISHED(8); // 已调度
	
	private int value;
	private State(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static State valueOf(int value) {
		for (State state : State.values()) {
			if(state.value == value)
				return state;
		}
		throw new IllegalArgumentException("不存在value为["+value+"]的State Enum");
	}
}
