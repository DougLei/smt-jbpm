package com.smt.jbpm.query.mode.impl;

import java.util.List;
import java.util.Map;

import com.douglei.orm.context.SessionContext;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;
import com.smt.jbpm.query.mode.Mode;

/**
 * 
 * @author DougLei
 */
public class LimitQueryMode implements Mode {
	private int startRow;
	private int length;
	
	/**
	 * 
	 * @param startRow 起始的行数, 值从1开始
	 * @param length 查询的数据长度
	 */
	public LimitQueryMode(int startRow, int length) {
		this.startRow = startRow;
		this.length = length;
	}

	@Override
	public List<Map<String, Object>> executeQuery(String name, Object sqlParameter, List<AbstractParameter> parameters) {
		return SessionContext.getSQLQuerySession().limitQuery(startRow, length, name, sqlParameter, parameters);
	}
}
