package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.policy.assign.AssignPolicy;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.policy.handle.HandlePolicy;

/**
 * 
 * @author DougLei
 */
public class Candidate implements Json2Xml{
	private AssignPolicy assignPolicy;
	private HandlePolicy handlePolicy;
	
	public Candidate(boolean defaultIsDynamic, String defaultAssignNum, boolean parseHandlePolicy, JSONObject json) {
		this.assignPolicy = new AssignPolicy(defaultIsDynamic, defaultAssignNum, json.getJSONObject("assignPolicy"));
		
		if(parseHandlePolicy) {
			json = json.getJSONObject("handlePolicy");
			if(json != null)
				this.handlePolicy = new HandlePolicy(json);
		}
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(800);
		sb.append("<candidate>");
		sb.append(assignPolicy.toXml());
		
		if(handlePolicy != null) 
			sb.append(handlePolicy.toXml());
		sb.append("</candidate>");
		return sb.toString();
	}
	
	public AssignPolicy getAssignPolicy() {
		return assignPolicy;
	}
}
