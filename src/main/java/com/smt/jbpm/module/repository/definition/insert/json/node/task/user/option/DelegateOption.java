package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.Candidate;

/**
 * 
 * @author DougLei
 */
public class DelegateOption extends CarboncopyOption {
	private boolean reason;

	public DelegateOption(JSONObject json) {
		super(json);
		this.reason = json.getBooleanValue("reason");
		
		json = json.getJSONObject("candidate");
		if(json != null)
			super.candidate = new Candidate(false, "1", false, json);
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("<option type='").append(type).append("' name='").append(getName()).append("' order='").append(order).append("'>");
		
		if(reason)
			sb.append("<parameter reason='true'/>");
		
		if(candidate != null) 
			sb.append("<candidate>").append(candidate.getAssignPolicy().toXml()).append("</candidate>");
		
		sb.append("</option>");
		return sb.toString();
	}
}
