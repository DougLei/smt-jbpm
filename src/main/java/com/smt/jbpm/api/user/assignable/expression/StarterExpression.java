package com.smt.jbpm.api.user.assignable.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpression;
import com.douglei.bpm.process.api.user.assignable.expression.AssignableUserExpressionParameter;
import com.douglei.orm.context.SessionContext;

/**
 * 指派流程发起人
 * @author DougLei
 */
public class StarterExpression implements AssignableUserExpression {

	@Override
	public String getName() {
		return "starter";
	}

	@Override
	public boolean valueIsRequired() {
		return false;
	}

	@Override
	public List<String> getUserIds(String value, AssignableUserExpressionParameter parameter) {
		Object[] array = SessionContext.getSqlSession().uniqueQuery_("select start_user_id from bpm_ru_procinst where procinst_id=?", Arrays.asList(parameter.getProcinstId()));
		List<String> userIds = new ArrayList<String>(1);
		userIds.add(array[0].toString());
		return userIds;
	}
}
