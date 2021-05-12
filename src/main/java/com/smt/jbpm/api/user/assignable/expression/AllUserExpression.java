package com.smt.jbpm.api.user.assignable.expression;

import java.util.ArrayList;
import java.util.List;

import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.douglei.orm.context.SessionContext;

/**
 * 指派流程发起人
 * @author DougLei
 */
public class AllUserExpression implements AssignableUserExpression {

	@Override
	public String getName() {
		return "allUser";
	}

	@Override
	public boolean valueIsRequired() {
		return false;
	}

	@Override
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		// 查询所有用户id集合
		List<Object[]> list = SessionContext.getSqlSession().query_("select id from smt_imp_user");
		if(list.isEmpty())
			return null;
		
		List<String> userIds = new ArrayList<String>(list.size());
		for (Object[] array : list) 
			userIds.add(array[0].toString());
		return userIds;
	}
}
