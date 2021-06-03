package com.smt.jbpm.module.execution.instance;

import java.util.HashSet;
import java.util.Map;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.instance.command.parameter.StartParameter;
import com.douglei.bpm.module.execution.variable.Scope;
import com.douglei.tools.StringUtil;
import com.smt.parent.code.filters.token.TokenContext;
import com.smt.parent.code.filters.token.TokenEntity;

/**
 * 
 * @author DougLei
 */
public class StartParameterEntity {
	private String code;
	private String version;
	private HashSet<String> assignedUserIds; // 指派的用户id集合;  TODO 后续会根据StartMode决定如何使用该属性
	private String businessId; // 当前的业务id
	private Map<String, Object> businessData; // 当前的业务数据
	
	/**
	 * 验证并创建出StartParameter实例
	 * @return
	 */
	public Result validateAndBuild() {
		if(StringUtil.isEmpty(code))
			return new Result("启动的流程定义code不能为空", "smt.jbpm.process.start.fail.code.notnull");
		if(StringUtil.isEmpty(businessId))
			return new Result("启动的业务id不能为空", "smt.jbpm.process.start.fail.business.id.notnull");
		
		// 构建StartParameter实例
		TokenEntity token = TokenContext.get();
		StartParameter parameter = new StartParameter(code, version, token.getTenantId());
		parameter.setUserId(token.getUserId());
		parameter.getAssignEntity().addAssignedUserId(token.getUserId());
		
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
