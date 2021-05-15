package com.smt.jbpm.module.execution.task.cmd;

import java.util.Arrays;
import java.util.List;

import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.orm.context.SessionContext;
import com.smt.jbpm.SmtJbpmException;

/**
 * 
 * @author DougLei
 */
public class EnterHistoryTaskCmd {
	private String userId;
	private String taskinstId;
	
	public EnterHistoryTaskCmd(String userId, String taskinstId, ProcessEngineBeans processEngineBeans) {
		this.userId = userId;
		this.taskinstId = taskinstId;
	}
	
	/**
	 * 进入历史任务
	 * @return
	 */
	public TaskDetail execute() {
		// 获取之前的办理意见和办理态度
		List<Object[]> results = SessionContext.getSqlSession().limitQuery_(1, 1, "select distinct suggest, attitude from bpm_hi_assignee where taskinst_id=? and handle_state=6 and user_id=?", Arrays.asList(taskinstId, userId));
		if(results.isEmpty())
			throw new SmtJbpmException("["+userId+"]用户未办理过["+taskinstId+"]任务");
		
		// 构建TaskDetail实例
		TaskDetail detail = SessionContext.getSQLSession().uniqueQuery(TaskDetail.class, "EnterTask", "querHistoryTaskDetail", taskinstId);
		detail.setSuggest(results.get(0)[0].toString());
		detail.setAttitude(results.get(0)[1].toString());
		return detail;
	}
}