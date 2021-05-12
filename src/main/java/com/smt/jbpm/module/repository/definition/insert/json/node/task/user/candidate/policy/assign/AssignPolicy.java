package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.policy.assign;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;

/**
 * 
 * @author DougLei
 */
public class AssignPolicy implements Json2Xml{
	private boolean isDynamic;
	private String assignNum;
	private List<AssignableUserExpression> expressionList;
	
	public AssignPolicy(boolean defaultIsDynamic, String defaultAssignNum, JSONObject json) {
		Boolean bv = json.getBoolean("isDynamic");
		if(bv == null)
			bv = defaultIsDynamic;
		this.isDynamic = bv;
		
		String sv = json.getString("assignNum");
		if(sv == null)
			sv = defaultAssignNum;
		this.assignNum = sv;
		
		JSONArray expressionArray = json.getJSONArray("expressionList");
		this.expressionList = new ArrayList<AssignableUserExpression>(expressionArray.size());
		for(int i=0; i<expressionArray.size();i++)
			this.expressionList.add(expressionArray.getJSONObject(i).toJavaObject(AssignableUserExpression.class));
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(400);
		sb.append("<assignPolicy isDynamic='").append(isDynamic).append("' assignNum='").append(assignNum).append("'>");
		expressionList.forEach(expression -> sb.append("<expression name='").append(expression.getName()).append("' value='").append(expression.getValue()).append("'/>"));
		sb.append("</assignPolicy>");
		return sb.toString();
	}
}
