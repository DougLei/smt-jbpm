package com.smt.jbpm.module.repository.definition.insert;

import java.util.Date;

/**
 * 
 * @author DougLei
 */
public class ProcessDefinitionExtend {
	private int id;
	private int procdefId; // 流程定义id
	private String struct; // 流程结构json字符串
	private String image; // 流程界面json字符串
	private String pageId; // 页面id
	private int startMode;
	private Date createDate; // 创建时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProcdefId() {
		return procdefId;
	}
	public void setProcdefId(int procdefId) {
		this.procdefId = procdefId;
	}
	public String getStruct() {
		return struct;
	}
	public void setStruct(String struct) {
		this.struct = struct;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPageId() {
		return pageId;
	}
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	public int getStartMode() {
		return startMode;
	}
	public void setStartMode(int startMode) {
		this.startMode = startMode;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
