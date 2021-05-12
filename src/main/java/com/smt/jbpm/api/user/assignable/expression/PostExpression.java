package com.smt.jbpm.api.user.assignable.expression;

import java.util.ArrayList;
import java.util.List;

import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.douglei.orm.context.SessionContext;

/**
 * 指派指定岗位下的用户
 * @author DougLei
 */
public class PostExpression implements AssignableUserExpression {

	@Override
	public String getName() {
		return "post";
	}

	@Override
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		// 初始化SQL
		StringBuilder sql = new StringBuilder("select distinct right_id from smt_imp_res_user_link where left_resource='post' and right_resource='user' and left_id");
		
		// 初始化参数
		String[] postIds = value.split(",");
		List<Object> parameters = new ArrayList<Object>(postIds.length);
		
		if(postIds.length == 1) {
			sql.append("=?");
			parameters.add(postIds[0]);
		}else {
			sql.append(" in (");
			for (String postId : postIds) {
				sql.append("?,");
				parameters.add(postId);
			}
			sql.setLength(sql.length()-1);
			sql.append(')');
		}
		
		// 查询并处理相关的用户id集合
		List<Object[]> list = SessionContext.getSqlSession().query_(sql.toString(), parameters);
		if(list.isEmpty())
			return null;
		
		List<String> userIds = new ArrayList<String>(list.size());
		for (Object[] array : list) 
			userIds.add(array[0].toString());
		return userIds;
	}
}
