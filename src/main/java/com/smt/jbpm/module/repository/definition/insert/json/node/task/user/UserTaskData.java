package com.smt.jbpm.module.repository.definition.insert.json.node.task.user;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.Json2Xml;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.candidate.Candidate;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option.AbstractOption;

/**
 * 
 * @author DougLei
 */
public class UserTaskData implements Json2Xml{
	private String defaultOutputFlow;
	private String pageID;
	private String timeLimit;
	
	private Candidate candidate;
	private List<AbstractOption> optionList;

	public UserTaskData(JSONObject json) {
		this.defaultOutputFlow = json.getString("defaultOutputFlow");
		this.pageID = json.getString("pageID");
		this.timeLimit = json.getString("timeLimit");
		this.candidate = new Candidate(true, "1", true, json.getJSONObject("candidate"));
		
		JSONArray optionArray = json.getJSONArray("optionList");
		if(optionArray == null || optionArray.isEmpty())
			return;
		
		this.optionList = new ArrayList<AbstractOption>(optionArray.size());
		for(int i=0;i<optionArray.size();i++)
			this.optionList.add(OptionFactory.buildOption(optionArray.getJSONObject(i)));
	}
	
	@Override
	public String toXml() {
		StringBuilder sb = new StringBuilder(1500);
		sb.append(candidate.toXml());
		
		if(optionList != null)
			optionList.forEach(option -> sb.append(option.toXml()));
		return sb.toString();
	}

	public String getDefaultOutputFlow() {
		if(defaultOutputFlow == null)
			return "";
		return defaultOutputFlow;
	}
	public void setDefaultOutputFlow(String defaultOutputFlow) {
		this.defaultOutputFlow = defaultOutputFlow;
	}
	public String getPageID() {
		if(pageID == null)
			return "";
		return pageID;
	}
	public void setPageID(String pageID) {
		this.pageID = pageID;
	}
	public String getTimeLimit() {
		if(timeLimit == null)
			return "";
		return timeLimit;
	}
	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}
	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}
	public void setOptionList(List<AbstractOption> optionList) {
		this.optionList = optionList;
	}
}
