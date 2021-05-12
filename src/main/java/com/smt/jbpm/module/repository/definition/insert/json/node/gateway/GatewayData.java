package com.smt.jbpm.module.repository.definition.insert.json.node.gateway;

import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;

/**
 * 
 * @author DougLei
 */
public class GatewayData implements Json2Xml{
	private String defaultOutputFlow;
	private boolean extendGlobal=true;
	private boolean extendLocal=true;
	private boolean extendTransient=true;
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(64);
		sb.append("<variableExtend global='%s' local='%s' transient='%s'/>");
		return String.format(sb.toString(), extendGlobal, extendLocal, extendTransient);
	}

	public String getDefaultOutputFlow() {
		if(defaultOutputFlow == null)
			return "";
		return defaultOutputFlow;
	}
	public void setDefaultOutputFlow(String defaultOutputFlow) {
		this.defaultOutputFlow = defaultOutputFlow;
	}
	public void setExtendGlobal(boolean extendGlobal) {
		this.extendGlobal = extendGlobal;
	}
	public void setExtendLocal(boolean extendLocal) {
		this.extendLocal = extendLocal;
	}
	public void setExtendTransient(boolean extendTransient) {
		this.extendTransient = extendTransient;
	}
}
