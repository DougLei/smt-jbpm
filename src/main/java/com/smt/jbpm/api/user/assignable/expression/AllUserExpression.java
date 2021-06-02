package com.smt.jbpm.api.user.assignable.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralResponse;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralServer;
import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

/**
 * 
 * @author DougLei
 */
public class AllUserExpression implements AssignableUserExpression {
	private RestTemplateWrapper restTemplate;
	public AllUserExpression(RestTemplateWrapper restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public String getName() {
		return "allUser";
	}

	@Override
	public boolean valueIsRequired() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		// 构建请求体
		Map<String, Object> requestBody = new HashMap<String, Object>(4);
		requestBody.put("$mode$", "QUERY");
		requestBody.put("ID", "RESULT()");
		
		// 发起api请求
		List<Map<String, Object>> users = (List<Map<String, Object>>)restTemplate.generalExchange(new APIGeneralServer() {
			
			@Override
			public String getName() {
				return "(同步)查询所有用户id集合";
			}
			
			@Override
			public String getUrl() {
				return "http://smt-base/smt-base/user/query4JBPM";
			}
			
		}, JSONObject.toJSONString(requestBody), APIGeneralResponse.class);
		
		// 返回查询的结果
		if(users.isEmpty())
			return null;
		
		List<String> userIds = new ArrayList<String>(users.size());
		users.forEach(user -> userIds.add(user.get("ID").toString()));
		return userIds;
	}
}
