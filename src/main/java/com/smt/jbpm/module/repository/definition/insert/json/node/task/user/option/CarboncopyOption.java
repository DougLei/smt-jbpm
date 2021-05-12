package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.Candidate;

/**
 * 
 * @author DougLei
 */
public class CarboncopyOption extends AbstractOption {
	protected Candidate candidate;
	
	public CarboncopyOption(JSONObject json) {
		super(json);
		this.candidate = new Candidate(true, "100%", false, json.getJSONObject("candidate"));
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("<option type='").append(type).append("' name='").append(getName()).append("' order='").append(order).append("'>");
		sb.append("<candidate>");
		sb.append(candidate.getAssignPolicy().toXml());
		sb.append("</candidate>");
		sb.append("</option>");
		return sb.toString();
	}
}
