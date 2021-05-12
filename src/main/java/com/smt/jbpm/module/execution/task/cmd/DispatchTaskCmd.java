package com.smt.jbpm.module.execution.task.cmd;

import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.execution.ExecutionModule;
import com.douglei.bpm.module.execution.task.command.parameter.DispatchTaskParameter;
import com.douglei.bpm.process.Type;
import com.smt.jbpm.ProcessWebException;
import com.smt.jbpm.module.execution.task.HandleTaskParameterEntity;
import com.smt.parent.code.response.Response;

/**
 * 
 * @author DougLei
 */
public class DispatchTaskCmd extends HandleTaskCmd{
	
	public DispatchTaskCmd(HandleTaskParameterEntity entity, ExecutionModule executionModule, ProcessEngineBeans processEngineBeans) {
		super(entity, executionModule, processEngineBeans);
	}
	
	/**
	 * 调度任务
	 * @return 
	 */
	public Response execute() {
		if(taskMetadataEntity.getTaskMetadata().getType() != Type.USER_TASK)
			throw new ProcessWebException("不能调度非用户任务");
		
		// 进行调度
		DispatchTaskParameter dispatchParameter = entity.buildDispatchTaskParameter(taskMetadataEntity);
		if(dispatchParameter == null) 
			throw new ProcessWebException("调度参数不能为空");
		
		Result result = executionModule.getTaskService().dispatch(entity.getTaskinstId(), dispatchParameter);
		if(result.isFail())
			return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
		return new Response(entity);
	}
}
