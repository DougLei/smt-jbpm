package com.smt.jbpm.module.query.track;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.douglei.bpm.module.execution.instance.State;

/**
 * 
 * @author DougLei
 */
public class ProcessSubject {
	private String id; // 流程实例id
	private int procdefId;
	private String title;
	private String startUser;
	private State state;
	private Date startTime;
	private Date endTime;
	private Date suspendTime;
	private String image;
	private List<String> flows;
	private Map<String, List<Task>> taskMap;
	
	/**
	 * 添加当前流转的流线
	 * @param flow
	 */
	public void addFlow(String flow) {
		if(flows == null)
			flows = new ArrayList<String>();
		flows.add(flow);
	}
	
	/**
	 * 添加任务
	 * @param task
	 */
	public void addTask(Task task) {
		if(taskMap == null)
			taskMap = new HashMap<String, List<Task>>();
		
		List<Task> tasks = taskMap.get(task.getKey());
		if(tasks == null) {
			tasks = new ArrayList<Task>(3);
			taskMap.put(task.getKey(), tasks);
		}
		tasks.add(task);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getProcdefId() {
		return procdefId;
	}
	public void setProcdefId(int procdefId) {
		this.procdefId = procdefId;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStartUser() {
		return startUser;
	}
	public void setStartUser(String startUser) {
		this.startUser = startUser;
	}
	public int getState() {
		return state.getValue();
	}
	public void setState(int state) {
		this.state = State.valueOf(state);
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getSuspendTime() {
		return suspendTime;
	}
	public void setSuspendTime(Date suspendTime) {
		this.suspendTime = suspendTime;
	}
	public List<String> getFlows() {
		return flows;
	}
	public Map<String, List<Task>> getTaskMap() {
		return taskMap;
	}
}
