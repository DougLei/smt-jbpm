package com.smt.jbpm.module.repository.definition.insert.json.edge;

import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;

/**
 * 
 * @author DougLei
 */
public class EdgeData implements Json2Xml{
	private int order;
	private String conditionExpression;

	@Override
	public String toXml() {
		if(conditionExpression != null) {
			StringBuilder sb = new StringBuilder(200);
			sb.append("<conditionExpression>");
			sb.append("<![CDATA[ ").append(conditionExpression).append(" ]]>");
			sb.append("</conditionExpression>");
			return sb.toString();
		}
		return "";
	}

	int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}
}
