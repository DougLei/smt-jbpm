package com.smt.jbpm.api.user.assignable.expression;

import java.util.ArrayList;
import java.util.List;

import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.douglei.orm.context.SessionContext;

/**
 * 指派指定角色下的用户
 * @author DougLei
 */
public class RoleExpression implements AssignableUserExpression {

	@Override
	public String getName() {
		return "role";
	}

	@Override
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		// 初始化SQL
		StringBuilder sql = new StringBuilder("select distinct right_id from smt_imp_res_user_link where left_resource='role' and right_resource='user' and left_id");
		
		// 初始化参数
		String[] roleIds = value.split(",");
		List<Object> parameters = new ArrayList<Object>(roleIds.length);
		
		if(roleIds.length == 1) {
			sql.append("=?");
			parameters.add(roleIds[0]);
		}else {
			sql.append(" in (");
			for (String roleId : roleIds) {
				sql.append("?,");
				parameters.add(roleId);
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
