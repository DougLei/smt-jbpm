package com.smt.jbpm.module.repository.definition.insert.json.node.event;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.ProcessNodeJson;

/**
 * 
 * @author DougLei
 */
public class StartEventJson extends ProcessNodeJson{
	private StartEventData data;
	
	public StartEventJson(JSONObject json) {
		super(json);
		
		json = json.getJSONObject("data");
		if(json != null)
			json.toJavaObject(StartEventData.class);
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(350);
		sb.append("<startEvent id='%s' name='%s' defaultOutputFlow='%s'>");
		if(data != null)
			sb.append(data.toXml());
		sb.append("</startEvent>");
		return String.format(sb.toString(), id, getLabel(), (data==null?"":data.getDefaultOutputFlow()));
	}
}
