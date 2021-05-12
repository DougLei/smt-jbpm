package com.smt.jbpm.module.repository.definition.insert.json;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author DougLei
 */
public abstract class ProcessNodeJson implements Json2Xml {
	protected String id;
	private String label;
	
	protected ProcessNodeJson(JSONObject json) {
		this.id = json.getString("id");
		this.label = json.getString("label");
	}
	
	protected String getLabel() {
		if(label == null)
			return "";
		return label;
	}
}
