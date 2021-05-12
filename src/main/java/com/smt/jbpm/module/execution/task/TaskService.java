package com.smt.jbpm.module.execution.task;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.douglei.orm.context.PropagationBehavior;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;
import com.smt.jbpm.module.execution.task.cmd.DispatchTaskCmd;
import com.smt.jbpm.module.execution.task.cmd.EnterHistoryTaskCmd;
import com.smt.jbpm.module.execution.task.cmd.EnterTaskCmd;
import com.smt.jbpm.module.execution.task.cmd.HandleTaskCmd;
import com.smt.jbpm.module.execution.task.cmd.QueryAssignableUserCmd;
import com.smt.jbpm.module.execution.task.cmd.TaskDetail;
import com.smt.jbpm.query.QueryCriteriaEntity;
import com.smt.jbpm.query.QueryCriteriaResolver;
import com.smt.parent.code.response.Response;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class TaskService {
	
	@Autowired
	private ProcessEngineBeans processEngineBeans;
	
	@Autowired
	private ExecutionModule executionModule;
	
	@Autowired
	private QueryCriteriaResolver queryCriteriaResolver;
	
	/**
	 * 进入任务
	 * @param userId
	 * @param taskinstId
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public Response enter(String userId, String taskinstId) {
		TaskDetail detail = new EnterTaskCmd(userId, taskinstId, processEngineBeans).execute();
		return new Response(detail);
	}
	
	/**
	 * 进入历史任务
	 * @param userId
	 * @param taskinstId
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public Response enterHistory(String userId, String taskinstId) {
		TaskDetail detail = new EnterHistoryTaskCmd(userId, taskinstId, processEngineBeans).execute();
		return new Response(detail);
	}
	
	/**
	 * 认领任务
	 * @param userId
	 * @param taskinstId
	 * @return
	 */
	@Transaction
	public Response claim(String userId, String taskinstId) {
		Result result = executionModule.getTaskService().claim(taskinstId, userId);
		if(result.isFail())
			return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
		
		EnterTaskCmd enterTaskCmd = new EnterTaskCmd(userId, taskinstId, processEngineBeans) {
			@Override
			protected List<Object[]> queryHandleMode() {
				return Collections.emptyList();
			}
		};
		TaskDetail detail = enterTaskCmd.execute();
		return new Response(detail);
	}
	
	/**
	 * 查询可指派的用户集合
	 * @param json
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public Response queryAssignableUser(JSONObject json) {
		String userId = json.remove("userId").toString();
		String taskinstId = json.remove("taskinstId").toString();
		String buttonType = json.remove("buttonType").toString();
		String target = json.remove("target").toString();
		QueryCriteriaEntity queryCriteriaEntity = queryCriteriaResolver.resolve(json);
		
		Map<String, Object> assignableUser = new QueryAssignableUserCmd(userId, taskinstId, buttonType, target, queryCriteriaEntity, processEngineBeans).execute();
		return new Response(assignableUser);
	}
	
	/**
	 * 办理任务
	 * @param entity
	 * @return
	 */
	@Transaction
	public Response handle(HandleTaskParameterEntity entity) {
		HandleTaskCmd cmd = new HandleTaskCmd(entity, executionModule, processEngineBeans);
		return cmd.execute();
	}
	
	/**
	 * 调度任务
	 * @param entity
	 * @return
	 */
	@Transaction
	public Response dispatch(HandleTaskParameterEntity entity) {
		DispatchTaskCmd cmd = new DispatchTaskCmd(entity, executionModule, processEngineBeans);
		return cmd.execute();
	}
}