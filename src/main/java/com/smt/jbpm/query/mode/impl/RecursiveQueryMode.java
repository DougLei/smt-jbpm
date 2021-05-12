package com.smt.jbpm.query.mode.impl;

import java.util.List;
import java.util.Map;

import com.douglei.orm.context.SessionContext;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;
import com.douglei.orm.sessionfactory.sessions.sqlsession.RecursiveEntity;
import com.smt.jbpm.query.mode.Mode;

/**
 * 
 * @author DougLei
 */
public class RecursiveQueryMode implements Mode {
	private RecursiveEntity entity;
	
	/**
	 * 
	 * @param entity
	 */
	public RecursiveQueryMode(RecursiveEntity entity) {
		this.entity = entity;
	}

	@Override
	public List<Map<String, Object>> executeQuery(String name, Object sqlParameter, List<AbstractParameter> parameters) {
		return SessionContext.getSQLQuerySession().recursiveQuery(entity, name, sqlParameter, parameters);
	}
}
