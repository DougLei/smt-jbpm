package com.smt.jbpm.module.repository.definition.insert.json.node.task.user;

import com.alibaba.fastjson.JSONObject;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option.AbstractOption;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option.CarboncopyOption;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option.DelegateOption;
import com.smt.jbpm.module.repository.definition.insert.json.node.task.user.option.TransferOption;

/**
 * 
 * @author DougLei
 */
class OptionFactory {
	
	/**
	 * 构建option实例
	 * @param optionJson
	 * @return
	 */
	public static AbstractOption buildOption(JSONObject optionJson) {
		switch(optionJson.getString("type")) {
			case "carbonCopy":
				return new CarboncopyOption(optionJson);
			case "delegate":
				return new DelegateOption(optionJson);
			case "transfer":
				return new TransferOption(optionJson);
				
			
		}
		throw new IllegalArgumentException("不支持["+optionJson.getString("type")+"]的optionType");
	}
}
