package com.smt.jbpm.module.execution.instance;

import java.util.HashSet;
import java.util.Map;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.instance.command.parameter.StartParameter;
import com.douglei.bpm.module.execution.variable.Scope;
import com.douglei.tools.StringUtil;

/**
 * 
 * @author DougLei
 */
public class StartParameterEntity {
	private String code;
	private String version;
	private String tenantId;
	
	private String userId; // 当前的用户id
	private HashSet<String> assignedUserIds; // 指派的用户id集合
	
	private String businessId; // 当前的业务id
	private Map<String, Object> businessData; // 当前的业务数据
	
	/**
	 * 验证并创建出StartParameter实例
	 * @return
	 */
	public Result validateAndBuild() {
		if(StringUtil.isEmpty(code))
			return new Result("启动的流程定义code不能为空", "smt.jbpm.process.start.fail.code.notnull");
		if(StringUtil.isEmpty(userId))
			return new Result("启动的用户id不能为空", "smt.jbpm.process.start.fail.user.id.notnull");		
		if(StringUtil.isEmpty(businessId))
			return new Result("启动的业务id不能为空", "smt.jbpm.process.start.fail.business.id.notnull");
		
		// 构建StartParameter实例
		StartParameter parameter = new StartParameter(code, version, tenantId);
		parameter.setUserId(userId);
		if(assignedUserIds == null) {
			parameter.getAssignEntity().addAssignedUserId(userId);
		}else {
			parameter.getAssignEntity().addAssignedUserIds(assignedUserIds);
		}
		
		parameter.setBusinessId(businessId);
		if(businessData != null)
			parameter.getVariableEntities().addVariables(Scope.GLOBAL, businessData);
		return new Result(parameter);
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public HashSet<String> getAssignedUserIds() {
		return assignedUserIds;
	}
	public void setAssignedUserIds(HashSet<String> assignedUserIds) {
		this.assignedUserIds = assignedUserIds;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public Map<String, Object> getBusinessData() {
		return businessData;
	}
	public void setBusinessData(Map<String, Object> businessData) {
		this.businessData = businessData;
	}
}
