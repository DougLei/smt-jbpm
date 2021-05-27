package com.smt.jbpm.api.user.assignable.expression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.smt.parent.code.filters.token.TokenContext;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralResponse;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralServer;
import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

/**
 * 
 * @author DougLei
 */
public class DeptExpression implements AssignableUserExpression {
	private RestTemplateWrapper restTemplate;
	public DeptExpression(RestTemplateWrapper restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@Override
	public String getName() {
		return "dept";
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		// 构建请求体
		Map<String, String> requestBody = new HashMap<String, String>(8);
		requestBody.put("parentType", getParentKey());
		requestBody.put("parentValue", value);
		requestBody.put("childType", "USER_ID");
		requestBody.put("projectCode", TokenContext.get().getProjectCode());
		
		// 发起api请求
		List<String> userIds = (List<String>)restTemplate.generalExchange(new APIGeneralServer() {
			
			@Override
			public String getName() {
				return "(同步)查询["+getParentKey()+"]关联的用户id集合";
			}
			
			@Override
			public String getUrl() {
				return "http://smt-base/smt-base/data/rel/value/query";
			}
			
		}, JSONObject.toJSONString(requestBody), APIGeneralResponse.class);
		
		// 返回查询的结果
		if(userIds == null)
			return null;
		return userIds;
	}
	
	/**
	 * 获取getParentKey
	 * @return
	 */
	protected String getParentKey() {
		return "ORG_CODE";
	}
}
