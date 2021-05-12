package com.smt.jbpm.query;

import java.util.List;

import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;
import com.smt.jbpm.query.mode.Mode;

/**
 * 
 * @author DougLei
 */
public class QueryCriteriaEntity {
	private Mode mode;
	private List<AbstractParameter> parameters;
	
	QueryCriteriaEntity(Mode mode, List<AbstractParameter> parameters) {
		this.mode = mode;
		this.parameters = parameters;
	}
	
	/**
	 * 获取查询方式
	 * @return
	 */
	public Mode getMode() {
		return mode;
	}
	/**
	 * 获取(动态)参数值集合
	 * @return
	 */
	public List<AbstractParameter> getParameters() {
		return parameters;
	}
}
