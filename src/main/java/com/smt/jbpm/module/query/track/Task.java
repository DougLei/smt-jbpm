package com.smt.jbpm.module.query.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author DougLei
 */
public class Task {
	private String id; // 任务实例id
	private String key;
	private String sourceKey;
	private Date startTime;
	private Date expiryTime;
	private Date suspendTime;
	private Date endTime;
	private Integer isAllClaimed;
	private List<TaskHandleDetail> handleDetails;
	
	public void addHandleDetail(TaskHandleDetail detail) {
		if(handleDetails == null)
			handleDetails = new ArrayList<TaskHandleDetail>();
		handleDetails.add(detail);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey_(String key) {
		this.key = key;
	}
	public String getSourceKey() {
		return sourceKey;
	}
	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getExpiryTime() {
		return expiryTime;
	}
	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}
	public Date getSuspendTime() {
		return suspendTime;
	}
	public void setSuspendTime(Date suspendTime) {
		this.suspendTime = suspendTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Integer getIsAllClaimed() {
		return isAllClaimed;
	}
	public void setIsAllClaimed(Integer isAllClaimed) {
		this.isAllClaimed = isAllClaimed;
	}
	public List<TaskHandleDetail> getHandleDetails() {
		if(handleDetails == null)
			return Collections.emptyList();
		return handleDetails;
	}
}
