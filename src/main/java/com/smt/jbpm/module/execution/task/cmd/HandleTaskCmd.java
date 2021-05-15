package com.smt.jbpm.module.execution.task.cmd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.douglei.bpm.module.execution.task.command.parameter.DispatchTaskParameter;
import com.douglei.bpm.module.execution.task.runtime.Task;
import com.douglei.bpm.process.Type;
import com.douglei.bpm.process.mapping.metadata.TaskMetadata;
import com.douglei.bpm.process.mapping.metadata.TaskMetadataEntity;
import com.douglei.orm.context.SessionContext;
import com.smt.jbpm.SmtJbpmException;
import com.smt.jbpm.module.execution.task.HandleTaskParameterEntity;
import com.smt.jbpm.module.execution.task.button.OptionButton;
import com.smt.parent.code.response.Response;

/**
 * 
 * @author DougLei
 */
public class HandleTaskCmd {
	protected HandleTaskParameterEntity entity;
	protected ExecutionModule executionModule;
	protected ProcessEngineBeans processEngineBeans;
	protected TaskMetadataEntity<TaskMetadata> taskMetadataEntity;
	
	public HandleTaskCmd(HandleTaskParameterEntity entity, ExecutionModule executionModule, ProcessEngineBeans processEngineBeans) {
		this.entity = entity;
		this.executionModule = executionModule;
		this.processEngineBeans = processEngineBeans;
		
		Task task = SessionContext.getTableSession().uniqueQuery(Task.class, "select * from bpm_ru_task where taskinst_id=?", Arrays.asList(entity.getTaskinstId()));
		this.taskMetadataEntity = processEngineBeans.getProcessContainer().getProcess(task.getProcdefId()).getTaskMetadataEntity(task.getKey());
	}
	
	/**
	 * 办理任务
	 * @return 
	 */
	public Response execute() {
		if(taskMetadataEntity.getTaskMetadata().getType() != Type.USER_TASK)
			throw new SmtJbpmException("不能办理非用户任务");
		
		Result result = executionModule.getTaskService().handle(entity.getTaskinstId(), entity.buildHandleTaskParameter());
		if(result.isFail())
			return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
		
		// 是否可以调度
		if(result.getObject(Boolean.class)) {
			DispatchTaskParameter dispatchParameter = entity.buildDispatchTaskParameter(taskMetadataEntity);
			
			// 如果没有传入调度相关的参数, 则返回调度的按钮组
			if(dispatchParameter == null) 
				return new Response(getDispatchButtons());
			
			// 否则直接进行调度
			result = executionModule.getTaskService().dispatch(entity.getTaskinstId(), dispatchParameter);
			if(result.isFail())
				return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
		}
		return new Response(entity);
	}

	/**
	 * 返回调度的按钮
	 * @return
	 */
	private TaskDetail getDispatchButtons() {
		EnterTaskCmd enterTaskCmd = new EnterTaskCmd(entity.getUserId(), entity.getTaskinstId(), processEngineBeans) {
			@Override
			protected List<Object[]> queryHandleMode() {
				return Collections.emptyList();
			}
			
			@Override
			protected HandleMode analyseMode(List<Object[]> handleModes) {
				this.supportUnclaim = false;
				return HandleMode.DISPATCHING;
			}

			@Override
			protected List<OptionButton> getOptionButtions() {
				return Collections.emptyList();
			}
		};
		return enterTaskCmd.execute();
	}
}
