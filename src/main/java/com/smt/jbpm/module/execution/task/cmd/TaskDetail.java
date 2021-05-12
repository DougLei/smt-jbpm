package com.smt.jbpm.module.execution.task.cmd;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.douglei.bpm.module.execution.instance.State;
import com.smt.jbpm.module.execution.task.button.HandleButton;
import com.smt.jbpm.module.execution.task.button.OptionButton;

/**
 * 
 * @author DougLei
 */
public class TaskDetail {
	private int procdefId;
	private String procinstId;
	private String taskinstId;
	private String name;
	private Integer assignCount;
	private Integer isAllClaimed;
	private String key;
	
	private Date tsuspendTime;
	private State procinstState;
	private String tbusinessId;
	private String pbusinessId;
	private String tpageId;
	private String ppageId;
	
	private String suggest; // 办理时的意见
	private String attitude; // 办理时的态度
	
	private HandleMode mode; // 当前要进行的办理模式
	private List<HandleButton> handleButtions; // 办理型按钮, 例如提交下一步等, 会影响流程流转的按钮
	private List<OptionButton> optionButtions; // 选项型按钮, 例如委托, 转办, 抄送等对流程流转无影响的按钮
	
	// -----------------------------------------------------------
	// getter
	// -----------------------------------------------------------
	public int getProcdefId() {
		return procdefId;
	}
	public String getProcinstId() {
		return procinstId;
	}
	public String getTaskinstId() {
		return taskinstId;
	}
	public String getName() {
		return name;
	}
	public Integer getAssignCount() {
		return assignCount;
	}
	public boolean isAllClaimed() {
		return isAllClaimed != null && isAllClaimed == 1;
	}
	public String getKey() {
		return key;
	}
	public boolean isActive() {
		if(procinstState == State.ACTIVE)
			return tsuspendTime == null;
		return false;
	}
	public String getBusinessId() {
		if(tbusinessId != null)
			return tbusinessId;
		return pbusinessId;
	}
	public String getPageId() {
		if(tpageId != null)
			return tpageId;
		return ppageId;
	}
	public String getSuggest() {
		return suggest;
	}
	public String getAttitude() {
		return attitude;
	}
	public HandleMode getMode() {
		return mode;
	}
	public List<HandleButton> getHandleButtions() {
		if(handleButtions == null)
			return Collections.emptyList();
		return handleButtions;
	}
	public List<OptionButton> getOptionButtions() {
		if(optionButtions == null)
			return Collections.emptyList();
		return optionButtions;
	}
	
	
	// -----------------------------------------------------------
	// setter
	// -----------------------------------------------------------
	public void setProcdefId(int procdefId) {
		this.procdefId = procdefId;
	}
	public void setProcinstId(String procinstId) {
		this.procinstId = procinstId;
	}
	public void setTaskinstId(String taskinstId) {
		this.taskinstId = taskinstId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAssignCount(Integer assignCount) {
		this.assignCount = assignCount;
	}
	public void setIsAllClaimed(Integer isAllClaimed) {
		this.isAllClaimed = isAllClaimed;
	}
	public void setKey_(String key) {
		this.key = key;
	}
	public void setTsuspendTime(Date tsuspendTime) {
		this.tsuspendTime = tsuspendTime;
	}
	public void setProcinstState(int procinstState) {
		this.procinstState = State.valueOf(procinstState);
	}
	public void setTbusinessId(String tbusinessId) {
		this.tbusinessId = tbusinessId;
	}
	public void setPbusinessId(String pbusinessId) {
		this.pbusinessId = pbusinessId;
	}
	public void setTpageId(String tpageId) {
		this.tpageId = tpageId;
	}
	public void setPpageId(String ppageId) {
		this.ppageId = ppageId;
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	public void setAttitude(String attitude) {
		this.attitude = attitude;
	}
	public void setMode(HandleMode mode) {
		this.mode = mode;
	}
	public void setHandleButtions(List<HandleButton> handleButtions) {
		this.handleButtions = handleButtions;
	}
	public void setOptionButtions(List<OptionButton> optionButtions) {
		this.optionButtions = optionButtions;
	}
}
