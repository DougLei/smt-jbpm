package com.smt.jbpm.query.mode;

import java.util.List;

import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;

/**
 * 
 * @author DougLei
 */
public interface Mode {

	/**
	 * 执行查询
	 * @param entity
	 * @return
	 */
	Object executeQuery(String name, Object sqlParameter, List<AbstractParameter> parameters);
}
