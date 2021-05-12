package com.smt.jbpm.module.repository.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.repository.RepositoryModule;
import com.douglei.bpm.module.repository.type.Type;
import com.douglei.orm.context.SessionContext;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;
import com.smt.jbpm.ProcessWebException;
import com.smt.parent.code.response.Response;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class TypeService {
	
	@Autowired
	private RepositoryModule repositoryModule;
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	@Transaction
	public Response insert(Type type) {
		if(type.getParentId() > 0) {
			List<Object> parentId = new ArrayList<Object>(1);
			parentId.add(type.getParentId());
			
			int deep = 2, flag =deep;
			while(--deep == 0) 
				parentId.set(0, Integer.parseInt(SessionContext.getSqlSession().uniqueQuery_("select parent_id from bpm_re_type where id=?", parentId)[0].toString()));
			
			if(((Integer)parentId.get(0)) > 0)
				throw new ProcessWebException("流程类型树最多支持"+flag+"层结构");
		}
		
		Result result = repositoryModule.getTypeService().insert(type);
		if(result.isFail())
			return new Response(type, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(type);
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	@Transaction
	public Response update(Type type) {
		int oldParentId = Integer.parseInt(SessionContext.getSqlSession().uniqueQuery_("select parent_id from bpm_re_type where id=?", Arrays.asList(type.getId()))[0].toString());
		if(type.getParentId() != oldParentId)
			throw new ProcessWebException("不支持修改流程类型的parentId");
		
		Result result = repositoryModule.getTypeService().update(type);
		if(result.isFail())
			return new Response(type, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(type);
	}
	
	/**
	 * 
	 * @param typeId
	 * @param strict
	 * @return
	 */
	@Transaction
	public Response delete(int typeId, boolean strict) {
		Result result = repositoryModule.getTypeService().delete(typeId, strict);
		if(result.isFail())
			return new Response(typeId, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(typeId);
	}
}
