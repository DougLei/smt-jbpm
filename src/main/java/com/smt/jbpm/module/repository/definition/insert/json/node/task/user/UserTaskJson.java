package com.smt.jbpm.module.repository.definition.insert.json.node.task.user;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.ProcessNodeJson;

/**
 * 
 * @author DougLei
 */
public class UserTaskJson extends ProcessNodeJson {
	private UserTaskData data;
	
	public UserTaskJson(JSONObject json) {
		super(json);
		this.data = new UserTaskData(json.getJSONObject("data"));
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(2000);
		sb.append("<userTask id='").append(id).append("' name='").append(getLabel()).append("' defaultOutputFlow='").append(data.getDefaultOutputFlow()).append("' pageID='").append(data.getPageID()).append("' timeLimit='").append(data.getTimeLimit()).append("'>");
		sb.append(data.toXml());
		sb.append("</userTask>");
		return sb.toString();
	}
}
