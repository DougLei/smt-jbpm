package com.smt.jbpm.query.mode.impl;

import java.util.List;
import java.util.Map;

import com.douglei.orm.context.SessionContext;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;
import com.douglei.orm.sql.query.page.PageResult;
import com.smt.jbpm.query.mode.Mode;

/**
 * 
 * @author DougLei
 */
public class PageQueryMode implements Mode {
	private int pageNum;
	private int pageSize;
	
	/**
	 * 
	 * @param pageNum 
	 * @param length 
	 */
	public PageQueryMode(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	@Override
	public PageResult<Map<String, Object>> executeQuery(String name, Object sqlParameter, List<AbstractParameter> parameters) {
		return SessionContext.getSQLQuerySession().pageQuery(pageNum, pageSize, name, sqlParameter, parameters);
	}
}
