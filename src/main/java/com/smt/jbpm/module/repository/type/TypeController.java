package com.smt.jbpm.module.repository.type;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.douglei.bpm.module.repository.type.Type;
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
@RequestMapping("/process/type")
public class TypeController {
	
	@Autowired
	private QueryExecutor queryExecutor;
	
	@Autowired
	private TypeService typeService;
	
	/**
	 * 查询类型
	 * @param entity
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/query", method=RequestMethod.POST)
	public Response query(@QueryCriteria QueryCriteriaEntity entity) {
		return queryExecutor.execute("QueryProcessTypeList", null, entity);
	}
	
	/**
	 * 保存类型
	 * @param type
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/insert", method=RequestMethod.POST)
	public Response insert(@RequestBody Type type) {
		return typeService.insert(type);
	}
	
	/**
	 * 修改类型
	 * @param type
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public Response update(@RequestBody Type type) {
		return typeService.update(type);
	}
	
	/**
	 * 删除类型
	 * @param delete
	 * @return
	 */
	@LoggingResponse
	@RequestMapping(value="/delete/{typeId}", method=RequestMethod.DELETE)
	public Response delete(@PathVariable int typeId, HttpServletRequest request) {
		return typeService.delete(typeId, false);
	}
}
