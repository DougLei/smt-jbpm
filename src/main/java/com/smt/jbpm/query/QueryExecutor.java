package com.smt.jbpm.query;

import com.douglei.orm.context.PropagationBehavior;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;
import com.smt.parent.code.response.Response;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class QueryExecutor {
	
	/**
	 * 
	 * @param name
	 * @param sqlParameter
	 * @param entity
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public Response execute(String name, Object sqlParameter, QueryCriteriaEntity entity) {
		return new Response(entity.getMode().executeQuery(name, sqlParameter, entity.getParameters()));
	}
}
