package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.policy.handle;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;

/**
 * 
 * @author DougLei
 */
public class HandlePolicy implements Json2Xml{
	private boolean suggest;
	private boolean attitude;
	private ClaimPolicy claim;
	private SerialHandle serialHandle;
	
	public HandlePolicy(JSONObject json) {
		this.suggest = json.getBooleanValue("suggest");
		this.attitude = json.getBooleanValue("attitude");
		
		JSONObject claimJson = json.getJSONObject("claim");
		if(claimJson != null)
			this.claim = claimJson.toJavaObject(ClaimPolicy.class);
		
		JSONObject serialHandleJson = json.getJSONObject("serialHandle");
		if(serialHandleJson != null)
			this.serialHandle = serialHandleJson.toJavaObject(SerialHandle.class);
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(400);
		sb.append("<handlePolicy suggest='").append(suggest).append("' attitude='").append(attitude).append("'>");
		
		if(claim != null)
			sb.append("<claim name='").append(claim.getName()).append("' value='").append(claim.getValue()).append("'/>");
		
		if(serialHandle != null)
			sb.append("<serialHandle name='").append(serialHandle.getName()).append("'/>");
		
		sb.append("</handlePolicy>");
		return sb.toString();
	}
}
