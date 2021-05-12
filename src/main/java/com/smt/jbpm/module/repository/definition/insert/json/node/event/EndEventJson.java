package com.smt.jbpm.module.repository.definition.insert.json.node.event;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.ProcessNodeJson;

/**
 * 
 * @author DougLei
 */
public class EndEventJson extends ProcessNodeJson{

	public EndEventJson(JSONObject json) {
		super(json);
	}

	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(39);
		sb.append("<endEvent id='%s' name='%s'>");
		sb.append("</endEvent>");
		return String.format(sb.toString(), id, getLabel());
	}
}
