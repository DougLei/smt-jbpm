package com.smt.jbpm.module.repository.definition.insert.json.node.event;

import com.smt.jbpm.module.repository.definition.insert.json.edge.EdgeData;

/**
 * 
 * @author DougLei
 */
public class StartEventData extends EdgeData{
	private String defaultOutputFlow;
	
	public String getDefaultOutputFlow() {
		if(defaultOutputFlow == null)
			return "";
		return defaultOutputFlow;
	}
	public void setDefaultOutputFlow(String defaultOutputFlow) {
		this.defaultOutputFlow = defaultOutputFlow;
	}
}