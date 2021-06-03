package com.smt.jbpm.module.repository.definition.insert;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.repository.definition.ProcessDefinitionBuilder;

/**
 * 
 * @author DougLei
 */
public class ParseResult {
	 // 是否成功
	private boolean success;
	
	// 失败时记录失败原因
	private Result failReason;
	
	// 成功时记录解析结果
	private String pageId;
	private int startMode;
	private ProcessDefinitionBuilder builder;
	
	ParseResult(Result failReason) {
		this.failReason = failReason;
	}
	ParseResult(String pageId, int startMode, ProcessDefinitionBuilder builder) {
		this.success = true;
		this.pageId = pageId;
		this.startMode = startMode;
		this.builder = builder;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public Result getFailReason() {
		return failReason;
	}
	public String getPageId() {
		return pageId;
	}
	public int getStartMode() {
		return startMode;
	}
	public ProcessDefinitionBuilder getProcessDefinitionBuilder() {
		return builder;
	}
}