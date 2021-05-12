package com.smt.jbpm.query.mode.impl;

import java.util.List;
import java.util.Map;

import com.douglei.orm.context.SessionContext;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;
import com.douglei.orm.sessionfactory.sessions.sqlsession.PageRecursiveEntity;
import com.douglei.orm.sql.query.page.PageResult;
import com.smt.jbpm.query.mode.Mode;

/**
 * 
 * @author DougLei
 */
public class PageRecursiveQueryMode implements Mode {
	private PageRecursiveEntity entity;
	
	/**
	 * 
	 * @param entity1
	 */
	public PageRecursiveQueryMode(PageRecursiveEntity entity) {
		this.entity = entity;
	}

	@Override
	public PageResult<Map<String, Object>> executeQuery(String name, Object sqlParameter, List<AbstractParameter> parameters) {
		return SessionContext.getSQLQuerySession().pageRecursiveQuery(entity, name, sqlParameter, parameters);
	}
}
