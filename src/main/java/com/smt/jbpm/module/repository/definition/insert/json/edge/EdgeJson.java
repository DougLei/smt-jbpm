package com.smt.jbpm.module.repository.definition.insert.json.edge;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.ProcessNodeJson;

/**
 * 
 * @author DougLei
 */
public class EdgeJson extends ProcessNodeJson {
	private String source;
	private String target;
	private EdgeData data;
	
	public EdgeJson(JSONObject json) {
		super(json);
		this.source = json.getString("source");
		this.target = json.getString("target");
		
		json = json.getJSONObject("data");
		if(json != null)
			this.data = json.toJavaObject(EdgeData.class);
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(350);
		sb.append("<flow id='%s' name='%s' order='%d' source='%s' target='%s'>");
		if(data != null)
			sb.append(data.toXml());
		sb.append("</flow>");
		return String.format(sb.toString(), id, getLabel(), (data==null?0:data.getOrder()), source, target);
	}
}
