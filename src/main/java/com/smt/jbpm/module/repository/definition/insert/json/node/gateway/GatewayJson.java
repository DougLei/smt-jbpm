package com.smt.jbpm.module.repository.definition.insert.json.node.gateway;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.ProcessNodeJson;

/**
 * 
 * @author DougLei
 */
public class GatewayJson extends ProcessNodeJson{
	private String gatewayName;
	private GatewayData data;

	public GatewayJson(String gatewayName, JSONObject json) {
		super(json);
		this.gatewayName = gatewayName;
		
		json = json.getJSONObject("data");
		if(data != null)
			json.toJavaObject(GatewayData.class);
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(350);
		sb.append("<%s id='%s' name='%s' defaultOutputFlow='%s'>");
		if(data != null)
			sb.append(data.toXml());
		sb.append("</%s>");
		return String.format(sb.toString(), gatewayName, id, getLabel(), (data==null?"":data.getDefaultOutputFlow()), gatewayName);
	}
}
