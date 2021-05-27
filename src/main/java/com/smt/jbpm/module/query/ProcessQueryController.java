package com.smt.jbpm.module.query;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smt.jbpm.module.query.track.ProcessSubject;
import com.smt.jbpm.module.query.track.ProcessTrackQuery;
import com.smt.jbpm.module.query.track.Task;
import com.smt.parent.code.filters.token.TokenContext;
import com.smt.parent.code.query.QueryExecutor;
import com.smt.parent.code.response.Response;
import com.smt.parent.code.spring.web.LoggingResponse;

/**
 * 流程相关查询
 * @author DougLei
 */
@RestController
@RequestMapping("/process")
public class ProcessQueryController {
	
	@Autowired
	private QueryExecutor queryExecutor;
	
	@Autowired
	private ProcessTrackQuery processTrackQuery;
	
	/**
	 * 待办查询
	 * @param request
	 * @return
	 */
	@LoggingResponse(loggingBody=false)
	@RequestMapping(value="/user/todo/query", method=RequestMethod.GET)
	public Response todoQuery(HttpServletRequest request) {
		return queryExecutor.execute("QueryUserTodoList", TokenContext.get().getUserId(), request);
	}
	
	/**
	 * 已办查询
	 * @param request
	 * @return
	 */
	@LoggingResponse(loggingBody=false)
	@RequestMapping(value="/user/done/query", method=RequestMethod.GET)
	public Response doneQuery(HttpServletRequest request) {
		return queryExecutor.execute("QueryUserDoneList", TokenContext.get().getUserId(), request);
	}
	
	/**
	 * 实例查询
	 * @param request
	 * @return
	 */
	@LoggingResponse(loggingBody=false)
	@RequestMapping(value="/instance/query", method=RequestMethod.GET)
	public Response instanceQuery(HttpServletRequest request) {
		return queryExecutor.execute("QueryProcessInstanceList", TokenContext.get().getTenantId(), request);
	}
	
	/**
	 * 流程跟踪主体查询
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse(loggingBody=false)
	@RequestMapping(value="/track/subject/query/{procinstId}", method=RequestMethod.GET)
	public Response trackSubjectQuery(@PathVariable String procinstId) {
		ProcessSubject subject = processTrackQuery.subjectQuery(procinstId);
		return new Response(subject);
	}
	
	/**
	 * 流程跟踪任务办理明细查询
	 * @param procinstId
	 * @param taskKey
	 * @return
	 */
	@LoggingResponse(loggingBody=false)
	@RequestMapping(value="/track/detail/query/{procinstId}/{taskKey}", method=RequestMethod.GET)
	public Response trackDetailQuery(@PathVariable String procinstId, @PathVariable String taskKey) {
		List<Task> tasks = processTrackQuery.detailQuery(procinstId, taskKey);
		return new Response(tasks);
	}
}
