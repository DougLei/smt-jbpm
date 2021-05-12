package com.smt.jbpm.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.alibaba.fastjson.JSONObject;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.AbstractParameter;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.Operator;
import com.douglei.orm.sessionfactory.sessions.session.sqlquery.impl.Parameter;
import com.douglei.orm.sessionfactory.sessions.sqlsession.PageRecursiveEntity;
import com.douglei.orm.sessionfactory.sessions.sqlsession.RecursiveEntity;
import com.douglei.tools.StringUtil;
import com.smt.jbpm.query.mode.Mode;
import com.smt.jbpm.query.mode.impl.CountQueryMode;
import com.smt.jbpm.query.mode.impl.LimitQueryMode;
import com.smt.jbpm.query.mode.impl.PageQueryMode;
import com.smt.jbpm.query.mode.impl.PageRecursiveQueryMode;
import com.smt.jbpm.query.mode.impl.QueryMode;
import com.smt.jbpm.query.mode.impl.RecursiveQueryMode;
import com.smt.jbpm.query.mode.impl.UniqueQueryMode;
import com.smt.parent.code.filters.log.HttpServletRequest4Log;

/**
 * 
 * @author DougLei
 */
public class QueryCriteriaResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		  return parameter.getParameterType() == QueryCriteriaEntity.class && parameter.hasParameterAnnotation(QueryCriteria.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		return resolve(JSONObject.parseObject(((HttpServletRequest4Log)webRequest.getNativeRequest(HttpServletRequest.class)).getRequestBody2String()));
	}
	
	/**
	 * 解析获取QueryCriteriaEntity
	 * @param json
	 * @return
	 */
	public QueryCriteriaEntity resolve(JSONObject json) {
		String mode = json.getString("$mode$").toUpperCase();
		
		if("QUERY".equals(mode))
			return build(new QueryMode(), json);
		
		if("UNIQUE_QUERY".equals(mode))
			return build(new UniqueQueryMode(), json);
		
		if("LIMIT_QUERY".equals(mode))
			return build(new LimitQueryMode(json.getIntValue("$startRow$"), json.getIntValue("$length$")), json);
		
		if("COUNT_QUERY".equals(mode))
			return build(new CountQueryMode(), json);
		
		if("PAGE_QUERY".equals(mode))
			return build(new PageQueryMode(json.getIntValue("$pageNum$"), json.getIntValue("$pageSize$")), json);
		
		if("RECURSIVE_QUERY".equals(mode)) {
			RecursiveEntity entity = new RecursiveEntity();
			setPropertyValues(json, entity);
			return build(new RecursiveQueryMode(entity), json);
		}
		
		if("PAGE_RECURSIVE_QUERY".equals(mode)) {
			PageRecursiveEntity entity = new PageRecursiveEntity(json.getIntValue("$pageNum$"), json.getIntValue("$pageSize$"));
			setPropertyValues(json, entity);
			return build(new PageRecursiveQueryMode(entity), json);
		}
		
		throw new QueryCriteriaException("目前不支持["+mode+"]的查询方式");
	}
	
	// 设置entity的属性值
	private void setPropertyValues(JSONObject json, RecursiveEntity entity) {
		Integer deep = json.getInteger("$deep$");
		if(deep != null)
			entity.setDeep(deep);
		
		String column = json.getString("$column$");
		if(StringUtil.unEmpty(column))
			entity.setColumn(column);
		
		String parentColumn = json.getString("$parentColumn$");
		if(StringUtil.unEmpty(parentColumn))
			entity.setParentColumn(parentColumn);
		
		String children = json.getString("$children$");
		if(StringUtil.unEmpty(children))
			entity.setChildren(children);
		
		String values = json.getString("$values$");
		if(StringUtil.unEmpty(values))
			entity.setValue(values.split(","));
	}
	
	// 构建QueryCriteriaEntity实例
	private QueryCriteriaEntity build(Mode mode, JSONObject json) {
		List<AbstractParameter> parameters = null;
		for(Entry<String, Object> entry : json.entrySet()) {
			if(entry.getKey().charAt(0) == '$')
				continue;
			
			if(parameters == null)
				parameters = new ArrayList<AbstractParameter>();
			parameters.add(getParameter(entry.getKey(), entry.getValue().toString()));
		}
		return new QueryCriteriaEntity(mode, parameters);
	}

	// 获取Parameter实例
	private AbstractParameter getParameter(String name, String value) {
		int splitIndex = 0;
		for(; splitIndex < value.length(); splitIndex++) {
			if(value.charAt(splitIndex) == '(')
				break;
		}
		
		String val = value.substring(splitIndex+1, value.length()-1);
		if(val.equalsIgnoreCase("null"))
			return new Parameter(false, Operator.valueOf(value.substring(0, splitIndex).toUpperCase()), name);
		
		Object[] values = val.split(",");
		return new Parameter(false, Operator.valueOf(value.substring(0, splitIndex).toUpperCase()), name, values);
	}
}
