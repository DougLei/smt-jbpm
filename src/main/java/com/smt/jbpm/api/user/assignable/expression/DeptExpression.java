package com.smt.jbpm.api.user.assignable.expression;

import java.util.ArrayList;
import java.util.List;

import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.douglei.orm.context.SessionContext;

/**
 * 指派指定部门下的用户
 * @author DougLei
 */
public class DeptExpression implements AssignableUserExpression {

	@Override
	public String getName() {
		return "dept";
	}

	@Override
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		// 初始化SQL
		StringBuilder sql = new StringBuilder("select distinct right_id from smt_imp_dept_user_link dul where dul.left_id");
		
		// 初始化参数
		String[] deptIds = value.split(",");
		List<Object> parameters = new ArrayList<Object>(deptIds.length);
		
		if(deptIds.length == 1) {
			sql.append("=?");
			parameters.add(deptIds[0]);
		}else {
			sql.append(" in (");
			for (String deptId : deptIds) {
				sql.append("?,");
				parameters.add(deptId);
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
