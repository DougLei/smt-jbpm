package com.smt.jbpm.module.repository.definition.insert.json;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.node.event.EndEventJson;
import com.smt.jbpm.module.repository.definition.insert.json.node.event.StartEventJson;
import com.smt.jbpm.module.repository.definition.insert.json.node.gateway.GatewayJson;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.UserTaskJson;

/**
 * 
 * @author DougLei
 */
public class NodeJsonFactory {
	
	/**
	 * 构建nodeJson实例
	 * @param nodeJson
	 * @return
	 */
	public static ProcessNodeJson buildNodeJson(JSONObject nodeJson) {
		switch(nodeJson.getString("nodeType")) {
			case "startEvent":
				return new StartEventJson(nodeJson);
			case "endEvent":
				return new EndEventJson(nodeJson);
				
			case "exclusiveGateway":
				return new GatewayJson("exclusiveGateway", nodeJson);
			case "parallelGateway":
				return new GatewayJson("parallelGateway", nodeJson);
			case "inclusiveGateway":
				return new GatewayJson("inclusiveGateway", nodeJson);
				
			case "userTask":
				return new UserTaskJson(nodeJson);
		}
		throw new ProcessParseException("不支持["+nodeJson.getString("nodeType")+"]的nodeType");
	}
}
