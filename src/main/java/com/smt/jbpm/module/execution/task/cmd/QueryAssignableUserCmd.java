package com.smt.jbpm.module.execution.task.cmd;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.module.execution.task.runtime.Task;
import com.douglei.bpm.module.execution.variable.Scope;
import com.douglei.bpm.module.execution.variable.runtime.Variable;
import com.douglei.bpm.process.Type;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.douglei.bpm.process.api.user.option.OptionTypeConstants;
import com.douglei.bpm.process.handler.VariableEntities;
import com.douglei.bpm.process.mapping.metadata.ProcessMetadata;
import com.douglei.bpm.process.mapping.metadata.TaskMetadata;
import com.douglei.bpm.process.mapping.metadata.TaskMetadataEntity;
import com.douglei.bpm.process.mapping.metadata.flow.FlowMetadata;
import com.douglei.bpm.process.mapping.metadata.task.user.UserTaskMetadata;
import com.douglei.bpm.process.mapping.metadata.task.user.candidate.assign.AssignPolicy;
import com.douglei.bpm.process.mapping.metadata.task.user.candidate.assign.AssignableUserExpressionEntity;
import com.douglei.bpm.process.mapping.metadata.task.user.option.Option;
import com.douglei.bpm.process.mapping.metadata.task.user.option.carboncopy.CarbonCopyOption;
import com.douglei.bpm.process.mapping.metadata.task.user.option.delegate.DelegateOption;
import com.douglei.bpm.process.mapping.metadata.task.user.option.dispatch.JumpOption;
import com.douglei.bpm.process.mapping.metadata.task.user.option.transfer.TransferOption;
import com.douglei.orm.context.SessionContext;
import com.douglei.tools.OgnlUtil;
import com.smt.jbpm.SmtJbpmException;
import com.smt.parent.code.filters.token.TokenContext;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralResponse;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralServer;
import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

/**
 * 
 * @author DougLei
 */
public class QueryAssignableUserCmd {
	private String userId;
	private String taskinstId;
	
	private String buttonType; // ????????????
	private String target; // ????????????(??????????????????)id
	
	private JSONObject requestBody;
	private RestTemplateWrapper restTemplate;
	private ProcessEngineBeans processEngineBeans;
	
	private Task task;
	private ProcessMetadata processMetadata;
	
	public QueryAssignableUserCmd(String taskinstId, String buttonType, String target, JSONObject requestBody, RestTemplateWrapper restTemplate, ProcessEngineBeans processEngineBeans) {
		this.userId = TokenContext.get().getUserId();
		this.taskinstId = taskinstId;
		this.buttonType = buttonType;
		this.target = target;
		this.requestBody = requestBody;
		this.restTemplate = restTemplate;
		this.processEngineBeans = processEngineBeans;
		
		this.task = SessionContext.getTableSession().uniqueQuery(Task.class, "select * from bpm_ru_task where taskinst_id=?", Arrays.asList(taskinstId));
		if(task == null)
			throw new SmtJbpmException("?????????id???["+taskinstId+"]?????????");
		this.processMetadata = processEngineBeans.getProcessContainer().getProcess(task.getProcdefId());
	}

	/**
	 * ??????????????????????????????
	 * @return
	 */
	public Map<String, Object> execute() {
		AssignPolicy assignPolicy = getAssignPolicy();
		if(assignPolicy == null)
			return null; 
		return buildAssignableUserList(assignPolicy);
	}
	
	/**
	 * ??????????????????
	 * @return
	 */
	private AssignPolicy getAssignPolicy() {
		// flow??????
		if("flow".equals(buttonType)) 
			return getAssignPolicy4Target(target, null);
			
		// jump??????
		if(OptionTypeConstants.JUMP.equals(buttonType)) {
			UserTaskMetadata taskMetadata = (UserTaskMetadata) processMetadata.getTaskMetadataEntity(task.getKey()).getTaskMetadata();
			
			JumpOption option = null;
			if(taskMetadata.getOptions() != null) {
				for(Option op : taskMetadata.getOptions()) {
					if(op.getType().equals(OptionTypeConstants.JUMP) && ((JumpOption)op).getTarget().equals(target)) {
						option = (JumpOption)op;
						break;
					}
				}
			}
			if(option == null)
				throw new SmtJbpmException("???????????????????????????????????????, taskinstId=["+taskinstId+"], target=["+target+"]");
			if(option.getCandidate() != null && option.getCandidate().getAssignPolicy().isDynamic()) 
				return option.getCandidate().getAssignPolicy();
			return getAssignPolicy4Target(target, null);
		}
		
		// ????????????
		if(OptionTypeConstants.CARBON_COPY.equals(buttonType)) {
			UserTaskMetadata taskMetadata = (UserTaskMetadata) processMetadata.getTaskMetadataEntity(task.getKey()).getTaskMetadata();
			CarbonCopyOption option = (CarbonCopyOption) taskMetadata.getOption(OptionTypeConstants.CARBON_COPY);
			if(option == null)
				throw new SmtJbpmException("???????????????????????????????????????, taskinstId=["+taskinstId+"]");
			return option.getCandidate().getAssignPolicy();
		}
		
		// ????????????
		if(OptionTypeConstants.DELEGATE.equals(buttonType)) {
			UserTaskMetadata taskMetadata = (UserTaskMetadata) processMetadata.getTaskMetadataEntity(task.getKey()).getTaskMetadata();
			DelegateOption option = (DelegateOption) taskMetadata.getOption(OptionTypeConstants.DELEGATE);
			if(option == null)
				throw new SmtJbpmException("???????????????????????????????????????, taskinstId=["+taskinstId+"]");
			
			if(option.getCandidate() != null)
				return option.getCandidate().getAssignPolicy();
			return taskMetadata.getCandidate().getAssignPolicy();
		}
		
		// ????????????
		if(OptionTypeConstants.TRANSFER.equals(buttonType)) {
			UserTaskMetadata taskMetadata = (UserTaskMetadata) processMetadata.getTaskMetadataEntity(task.getKey()).getTaskMetadata();
			TransferOption option = (TransferOption) taskMetadata.getOption(OptionTypeConstants.TRANSFER);
			if(option == null)
				throw new SmtJbpmException("???????????????????????????????????????, taskinstId=["+taskinstId+"]");
			
			if(option.getCandidate() != null)
				return option.getCandidate().getAssignPolicy();
			return taskMetadata.getCandidate().getAssignPolicy();
		}
		
		throw new SmtJbpmException("???????????????????????????, ???????????????buttonType=["+buttonType+"]?????????");
	}
	
	/**
	 * ????????????target???????????????
	 * @param target
	 * @param variableMap
	 * @return
	 */
	private AssignPolicy getAssignPolicy4Target(String target, Map<String, Object> variableMap) {
		TaskMetadataEntity<TaskMetadata> targetTaskMetadataEntity = processMetadata.getTaskMetadataEntity(target);
		TaskMetadata targetTaskMetadata = targetTaskMetadataEntity.getTaskMetadata();
		
		// ?????????????????????????????????
		if(targetTaskMetadata.getType() == Type.EXCLUSIVE_GATEWAY) {
			if(variableMap == null)
				variableMap = new VariableEntities(
						taskinstId, 
						SessionContext.getTableSession().query(Variable.class, "select * from bpm_ru_variable where procinst_id=? and (taskinst_id=? or scope =?)", Arrays.asList(task.getProcinstId(), task.getTaskinstId(), Scope.GLOBAL.getValue()))).getVariableMap();
			
			for(FlowMetadata flow: targetTaskMetadataEntity.getOutputFlows()) {
				if(flow.getConditionExpression() == null || OgnlUtil.getBooleanValue(flow.getConditionExpression(), variableMap))
					return getAssignPolicy4Target(flow.getTarget(), variableMap);
			}
			
			FlowMetadata defaultFlowMetadata = targetTaskMetadataEntity.getDefaultOutputFlow();
			if(defaultFlowMetadata != null)
				return getAssignPolicy4Target(defaultFlowMetadata.getTarget(), variableMap);
		}
		
		if(targetTaskMetadata.getType() == Type.USER_TASK) {
			AssignPolicy assignPolicy = ((UserTaskMetadata)targetTaskMetadata).getCandidate().getAssignPolicy();
			if(assignPolicy.isDynamic())
				return assignPolicy;
		}
		return null;
	}
	
	/**
	 * ??????AssignableUserList??????
	 * @param assignPolicy
	 * @return
	 */
	private Map<String, Object> buildAssignableUserList(AssignPolicy assignPolicy) {
		// ??????????????????????????????
		HashSet<String> assignableUserIds = new HashSet<String>();
		AssignableUserExpressionParameter parameter = new AssignableUserExpressionParameter(userId, task.getProcinstId(), task.getTaskinstId());
		for(AssignableUserExpressionEntity expression : assignPolicy.getAssignableUserExpressionEntities()) {
			List<String> userIds = processEngineBeans.getAPIContainer().getAssignableUserExpression(expression.getName()).getUserIds(expression.getValue(), parameter);
			if(userIds != null && !userIds.isEmpty())
				assignableUserIds.addAll(userIds);
		}
		
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("number", assignPolicy.getAssignNumber());
		if(assignableUserIds.isEmpty()) {
			map.put("users", Collections.emptyList());
		}else {
			StringBuilder userIds = new StringBuilder(assignableUserIds.size()*37);
			assignableUserIds.forEach(userId -> userIds.append(userId).append(','));
			userIds.setLength(userIds.length()-1);
			
			// ????????????????????????id??????, ????????????????????????????????????
			// ???????????????
			requestBody.put("$mode$", "QUERY");
			requestBody.put("ID", "IN("+userIds+")");
			
			// ??????api??????; ???????????????????????????
			map.put("users", restTemplate.generalExchange(new APIGeneralServer() {
				
				@Override
				public String getName() {
					return "(??????)????????????id???????????????";
				}
				
				@Override
				public String getUrl() {
					return "http://smt-base/smt-base/user/query4JBPM";
				}
				
			}, JSONObject.toJSONString(requestBody), APIGeneralResponse.class));
		}
		return map;
	}
}