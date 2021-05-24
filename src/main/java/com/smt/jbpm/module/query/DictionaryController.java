package com.smt.jbpm.module.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.smt.parent.code.response.Response;

/**
 * 数据字典相关查询
 * @author DougLei
 */
@RestController
@RequestMapping("/process/dictionary")
public class DictionaryController {
	private Map<String, List<Object>> dictionary = new HashMap<String, List<Object>>();
	
	public DictionaryController() {
		// 可指派的用户表达式
		List<Object> list1 = new ArrayList<Object>(2);
		list1.add(new Expression("fixed", "按用户", true));
		list1.add(new Expression("variable", "按流程变量", true));
		list1.add(new Expression("dept", "按部门", true));
		list1.add(new Expression("role", "按角色", true));
		list1.add(new Expression("post", "按岗位", true));
		list1.add(new Expression("starter", "流程发起人", false));
		list1.add(new Expression("allUser", "所有人", false));
		dictionary.put("ASSIGNABLE_USER_EXPRESSION", list1);
		
		// 任务认领策略
		List<Object> list2 = new ArrayList<Object>(2);
		list2.add(new Expression("bySingle", "单用户", false));
		list2.add(new Expression("byNumber", "多用户", true));
		dictionary.put("CLAIM_POLICY", list2);
		
		// 串行办理任务时, 办理顺序的策略
		List<Object> list3 = new ArrayList<Object>(1);
		list3.add(new Expression("byClaimTimeASC", "按认领时间(正序)办理", false));
		dictionary.put("SERIAL_HANDLE_POLICY", list3);
		
		// 用户任务操作
		List<Object> list4 = new ArrayList<Object>(3);
		list4.add(new Expression("carbonCopy", "抄送", true));
		list4.add(new Expression("delegate", "委托", true));
		list4.add(new Expression("transfer", "转办", true));
		dictionary.put("USER_TASK_OPTION", list4);
	}


	/**
	 * 获取指定类型的字典数据
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/query/{type}", method=RequestMethod.GET)
	public Response query(@PathVariable String type) {
		return new Response(dictionary.get(type.toUpperCase()));
	}
}

/**
 * 
 * @author DougLei
 */
class Expression {
	private String code;
	private String name;
	private boolean valueIsRequired; // 是否必须输入值

	public Expression(String code, String name, boolean valueIsRequired) {
		this.code = code;
		this.name = name;
		this.valueIsRequired = valueIsRequired;
	}
	
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	public boolean isValueIsRequired() {
		return valueIsRequired;
	}
}
