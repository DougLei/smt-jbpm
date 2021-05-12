package com.smt.jbpm.module.execution.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.smt.parent.code.response.Response;
import com.smt.parent.code.spring.web.LoggingResponse;

/**
 * 
 * @author DougLei
 */
@RestController
@RequestMapping("/task")
public class TaskController {
	
	@Autowired
	private ExecutionModule executionModule;
	
	@Autowired
	private TaskService taskService;
	
	/**
	 * 进入任务
	 * @param taskinstId
	 * @param userId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/enter/{taskinstId}/{userId}", method=RequestMethod.GET)
	public Response enter(@PathVariable String taskinstId, @PathVariable String userId) {
		return taskService.enter(userId, taskinstId);
	}
	
	/**
	 * 进入历史任务
	 * @param taskinstId
	 * @param userId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/history/enter/{taskinstId}/{userId}", method=RequestMethod.GET)
	public Response enterHistory(@PathVariable String taskinstId, @PathVariable String userId) {
		return taskService.enterHistory(userId, taskinstId);
	}
	
	/**
	 * 认领任务
	 * @param taskinstId
	 * @param userId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/claim/{taskinstId}/{userId}", method=RequestMethod.POST)
	public Response claim(@PathVariable() String taskinstId, @PathVariable() String userId) {
		return taskService.claim(userId, taskinstId);
	}
	
	/**
	 * 取消认领任务
	 * @param taskinstId
	 * @param userId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/unclaim/{taskinstId}/{userId}", method=RequestMethod.POST)
	public Response unclaim(@PathVariable String taskinstId, @PathVariable String userId) {
		Result result = executionModule.getTaskService().unclaim(taskinstId, userId);
		if(result.isSuccess())
			return new Response(taskinstId);
		return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 查询可指派的用户集合
	 * @param json
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/assignableUser/query", method=RequestMethod.POST)
	public Response queryAssignableUser(@RequestBody JSONObject json) {
		return taskService.queryAssignableUser(json);
	}
	
	/**
	 * 办理任务
	 * @param entity
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/handle", method=RequestMethod.POST)
	public Response handle(@RequestBody HandleTaskParameterEntity entity) {
		return taskService.handle(entity);
	}
	

	/**
	 * 调度任务
	 * @param entity
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/dispatch", method=RequestMethod.POST)
	public Response dispatch(@RequestBody HandleTaskParameterEntity entity) {
		return taskService.dispatch(entity);
	}
}
