package com.smt.jbpm.module.execution.instance;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.douglei.bpm.module.execution.instance.command.parameter.StartParameter;
import com.smt.parent.code.filters.token.TokenContext;
import com.smt.parent.code.response.Response;
import com.smt.parent.code.spring.web.LoggingResponse;

/**
 * 
 * @author DougLei
 */
@RestController
@RequestMapping("/process/instance")
public class ProcessInstanceController {
	
	@Autowired
	private ExecutionModule executionModule;
	
	/**
	 * 
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/wake/{procinstId}", method=RequestMethod.POST)
	public Response wake(@PathVariable String procinstId) {
		Result result = executionModule.getProcessInstanceService().wake(procinstId);
		if(result.isSuccess())
			return new Response(procinstId);
		return new Response(procinstId, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/suspend/{procinstId}", method=RequestMethod.POST)
	public Response suspend(@PathVariable String procinstId) {
		Result result = executionModule.getProcessInstanceService().suspend(procinstId);
		if(result.isSuccess())
			return new Response(procinstId);
		return new Response(procinstId, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/terminate", method=RequestMethod.POST)
	public Response terminate(@RequestBody TerminateParameterEntity entity) {
		Result result = entity.validate();
		if(result == null) {
			result = executionModule.getProcessInstanceService().terminate(entity.getProcinstId(), TokenContext.get().getUserId(), entity.getReason());
			if(result.isSuccess())
				return new Response(entity);
			
		}
		return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/delete/{procinstId}", method=RequestMethod.POST)
	public Response delete(@PathVariable String procinstId) {
		Result result = executionModule.getProcessInstanceService().delete(procinstId);
		if(result.isSuccess())
			return new Response(procinstId);
		return new Response(procinstId, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/undoDelete/{procinstId}", method=RequestMethod.POST)
	public Response undoDelete(@PathVariable String procinstId) {
		Result result = executionModule.getProcessInstanceService().undoDelete(procinstId);
		if(result.isSuccess())
			return new Response(procinstId);
		return new Response(procinstId, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 
	 * @param procinstId
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/delete/physical/{procinstId}", method=RequestMethod.POST)
	public Response delete4Physical(@PathVariable String procinstId) {
		Result result = executionModule.getProcessInstanceService().delete4Physical(procinstId);
		if(result.isSuccess())
			return new Response(procinstId);
		return new Response(procinstId, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 
	 * @param procinstId
	 * @param request
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/recovery/{procinstId}", method=RequestMethod.POST)
	public Response recovery(@PathVariable String procinstId, HttpServletRequest request) {
		Result result = executionModule.getProcessInstanceService().recovery(procinstId, "true".equalsIgnoreCase(request.getParameter("active")));
		if(result.isSuccess())
			return new Response(procinstId);
		return new Response(procinstId, null, result.getMessage(), result.getCode(), result.getParams());
	}
	
	/**
	 * 启动流程
	 * @param entity
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/start", method=RequestMethod.POST)
	public Response start(@RequestBody StartParameterEntity entity) {
		Result result = entity.validateAndBuild();
		if(result.isSuccess()) {
			result = executionModule.getProcessInstanceService().start(result.getObject(StartParameter.class));
			if(result.isSuccess()) 
				return new Response(result.getObject());
		}
		return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
	}
}
