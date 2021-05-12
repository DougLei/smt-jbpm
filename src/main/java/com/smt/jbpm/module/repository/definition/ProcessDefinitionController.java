package com.smt.jbpm.module.repository.definition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.repository.RepositoryModule;
import com.smt.jbpm.module.repository.definition.insert.ParseResult;
import com.smt.jbpm.module.repository.definition.insert.ProcessDefinitionParser;
import com.smt.jbpm.module.repository.definition.insert.ProcessDesign;
import com.smt.jbpm.query.QueryCriteria;
import com.smt.jbpm.query.QueryCriteriaEntity;
import com.smt.jbpm.query.QueryExecutor;
import com.smt.parent.code.response.Response;
import com.smt.parent.code.spring.web.LoggingResponse;

/**
 * 
 * @author DougLei
 */
@RestController
@RequestMapping("/process/definition")
public class ProcessDefinitionController {
	
	@Autowired
	private ProcessDefinitionParser processDefinitionParser;
	
	@Autowired
	private ProcessDefinitionService processDefinitionService;
	
	@Autowired
	private RepositoryModule repositoryModule;
	
	@Autowired
	private QueryExecutor queryExecutor;
	
	/**
	 * 查询
	 * @param entity
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/query", method=RequestMethod.POST)
	public Response query(@QueryCriteria QueryCriteriaEntity entity) {
		return queryExecutor.execute("QueryProcessDefinitionList", null, entity);
	}
	
	/**
	 * ById查询
	 * @param entity
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/query/{procdefId}", method=RequestMethod.GET)
	public Response queryById(@PathVariable int procdefId) {
		if(procdefId < 1)
			return null;
		return processDefinitionService.queryById(procdefId);
	}
	
	/**
	 * 保存或修改
	 * @param design
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public Response insert(@RequestBody ProcessDesign design) {
		ParseResult result = processDefinitionParser.parse(design.getStruct());
		if(result.isSuccess())
			return processDefinitionService.insert(result.getProcessDefinitionBuilder(), design.getStruct(), design.getImage(), result.getPageId());
		return new Response(null, null, result.getFailReason().getMessage(), result.getFailReason().getCode(), result.getFailReason().getParams());
	}
	
	/**
	 * 部署
	 * @param procdefId 
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/deploy/{procdefId}", method=RequestMethod.POST)
	public Response deploy(@PathVariable Integer procdefId) {
		return processDefinitionService.deploy(procdefId);
	}
	
	/**
	 * 取消部署
	 * @param procdefId 
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/undeploy/{procdefId}", method=RequestMethod.POST)
	public Response undeploy(@PathVariable Integer procdefId) {
		Result result = repositoryModule.getDefinitionService().undeploy(procdefId);
		if(result.isFail())
			return new Response(procdefId, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(procdefId);
	}
	
	/**
	 * 删除
	 * @param procdefId 
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/delete/{procdefId}", method=RequestMethod.DELETE)
	public Response delete(@PathVariable Integer procdefId) {
		Result result = repositoryModule.getDefinitionService().delete(procdefId);
		if(result.isFail())
			return new Response(procdefId, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(procdefId);
	}

	/**
	 * 物理删除
	 * @param procdefId 
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/delete/physical/{procdefId}", method=RequestMethod.DELETE)
	public Response delete4Physical(@PathVariable Integer procdefId) {
		Result result = repositoryModule.getDefinitionService().delete4Physical(procdefId);
		if(result.isFail())
			return new Response(procdefId, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(procdefId);
	}
	
	/**
	 * 设置流程的主要子版本
	 * @param procdefId 
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/set/majorSubversion/{procdefId}", method=RequestMethod.POST)
	public Response setMajorSubversion(@PathVariable Integer procdefId) {
		Result result = repositoryModule.getDefinitionService().setMajorSubversion(procdefId);
		if(result.isFail())
			return new Response(procdefId, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(procdefId);
	}
	
	/**
	 * 撤销删除
	 * @param procdefId 
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/undoDelete/{procdefId}", method=RequestMethod.POST)
	public Response undoDelete(@PathVariable Integer procdefId) {
		Result result = repositoryModule.getDefinitionService().undoDelete(procdefId);
		if(result.isFail())
			return new Response(procdefId, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(procdefId);
	}
}
