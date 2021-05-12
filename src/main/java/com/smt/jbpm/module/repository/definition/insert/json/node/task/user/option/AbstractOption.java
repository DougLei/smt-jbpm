package com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;

/**
 * 
 * @author DougLei
 */
public abstract class AbstractOption implements Json2Xml{
	protected String type;
	private String name;
	protected int order;
	
	protected AbstractOption(JSONObject json) {
		this.type = json.getString("type");
		this.name = json.getString("name");
		this.order = json.getIntValue("order");
	}
	
	public void setType(String type) {
		this.type = type;
	}
	protected String getName() {
		if(name == null)
			return "";
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setOrder(int order) {
		this.order = order;
	}
}
