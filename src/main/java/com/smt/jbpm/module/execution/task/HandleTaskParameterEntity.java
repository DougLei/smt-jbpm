package com.smt.jbpm.module.execution.task;

import java.util.List;

import com.douglei.bpm.module.execution.task.command.dispatch.DispatchExecutor;
import com.douglei.bpm.module.execution.task.command.dispatch.impl.BackstepsDispatchExecutor;
import com.douglei.bpm.module.execution.task.command.dispatch.impl.SettargetDispatchExecutor;
import com.douglei.bpm.module.execution.task.command.parameter.DispatchTaskParameter;
import com.douglei.bpm.module.execution.task.command.parameter.HandleTaskParameter;
import com.douglei.bpm.module.execution.task.history.Attitude;
import com.douglei.bpm.process.api.user.option.OptionTypeConstants;
import com.douglei.bpm.process.handler.task.user.assignee.startup.AssigneeHandler;
import com.douglei.bpm.process.handler.task.user.assignee.startup.AssigneeHandler4Backsteps;
import com.douglei.bpm.process.handler.task.user.assignee.startup.AssigneeHandler4Jump;
import com.douglei.bpm.process.mapping.metadata.TaskMetadata;
import com.douglei.bpm.process.mapping.metadata.TaskMetadataEntity;
import com.douglei.bpm.process.mapping.metadata.flow.FlowMetadata;
import com.douglei.bpm.process.mapping.metadata.task.user.UserTaskMetadata;
import com.douglei.bpm.process.mapping.metadata.task.user.option.Option;
import com.douglei.bpm.process.mapping.metadata.task.user.option.dispatch.BackOption;
import com.douglei.bpm.process.mapping.metadata.task.user.option.dispatch.JumpOption;
import com.smt.jbpm.ProcessWebException;

/**
 * 办理任务参数实体
 * @author DougLei
 */
public class HandleTaskParameterEntity {
	private String taskinstId; // 办理的任务实例id
	private String userId; // 办理人id
	private String suggest; // 办理人意见
	private String attitude; // 办理人态度
	private String reason; // 办理人的办理原因, 即为什么办理; 该值会存储在task表的reason列中; 其和 suggest和attitude是互斥的; 用来表示特殊的任务办理, 例如网关的办理, 结束事件的办理, 流程跳转等
	private String businessId; // 关联的业务id
	
	private String buttonType;// 按钮类型
	private String target; // 要调度的目标任务(配置文件中的)id
	private int steps; // 回退的步数
	private String assignedUserIds; // 指派的用户id集合
	
	/**
	 * 构建HandleTaskParameter实例
	 * @return
	 */
	public HandleTaskParameter buildHandleTaskParameter() {
		HandleTaskParameter parameter = new HandleTaskParameter();
		parameter.setUserId(userId);
		parameter.setSuggest(suggest);
		parameter.setAttitude(attitude != null?Attitude.valueOf(attitude):null);
		parameter.setReason(reason);
		parameter.setBusinessId(businessId);
		return parameter;
	}
	
	/**
	 * 构建DispatchTaskParameter实例
	 * @param taskMetadataEntity
	 * @return
	 */
	public DispatchTaskParameter buildDispatchTaskParameter(TaskMetadataEntity<TaskMetadata> taskMetadataEntity) {
		if(buttonType == null)
			return null;
		
		DispatchTaskParameter parameter = new DispatchTaskParameter(userId);
		if(assignedUserIds != null) {
			for(String assignedUserId : assignedUserIds.split(",")) 
				parameter.getAssignEntity().addAssignedUserId(assignedUserId);
		}
		
		DispatchEntity entity = buildDispatchEntity(taskMetadataEntity);
		parameter.setDispatchExecutor(entity.dispatchExecutor);
		parameter.getAssignEntity().setAssigneeHandler(entity.assigneeHandler);
		return parameter;
	}
	
	// 构造DispatchEntity实例
	private DispatchEntity buildDispatchEntity(TaskMetadataEntity<TaskMetadata> taskMetadataEntity) {
		// 验证实际传入的target是否存在于配置中
		if(buttonType.equals("flow")) {
			boolean targetExists = false; 
			for(FlowMetadata flow : taskMetadataEntity.getOutputFlows()) {
				if(flow.getTarget().equals(target)) {
					targetExists = true;
					break;
				}
			}
			
			if(!targetExists) {
				FlowMetadata defaultFlow = taskMetadataEntity.getDefaultOutputFlow();
				if(defaultFlow == null || !defaultFlow.getTarget().equals(target))
					throw new ProcessWebException("buildDispatchExecutor失败, 参数不合法: taskinstId="+taskinstId+", ButtonType=flow, target="+target);
			}
			return new DispatchEntity(new SettargetDispatchExecutor(target), new AssigneeHandler()); 
		}
		
		// 验证实际传入的target是否存在于配置中
		if(buttonType.equals(OptionTypeConstants.JUMP)) {
			JumpOption jumpOption = null; 
			List<Option> options = ((UserTaskMetadata) taskMetadataEntity.getTaskMetadata()).getOptions();
			if(options != null) {
				for(Option option : options) {
					if(option.getType().equals(OptionTypeConstants.JUMP) && ((JumpOption)option).getTarget().equals(target)) {
						jumpOption = (JumpOption) option;
						break;
					}
				}
			}
			
			if(jumpOption!=null)
				return new DispatchEntity(new SettargetDispatchExecutor(target), new AssigneeHandler4Jump(jumpOption)); 
			throw new ProcessWebException("buildDispatchExecutor失败, 参数不合法: taskinstId="+taskinstId+", ButtonType="+OptionTypeConstants.JUMP+", target="+target);
		}
		
		// 验证实际传入的steps是否存在于配置中
		if(buttonType.equals(OptionTypeConstants.BACK)) {
			boolean stepsExists = false; 
			List<Option> options = ((UserTaskMetadata) taskMetadataEntity.getTaskMetadata()).getOptions();
			if(options != null) {
				for(Option option : options) {
					if(option.getType().equals(OptionTypeConstants.BACK) && ((BackOption)option).getSteps() == steps) {
						stepsExists = true;
						break;
					}
				}
			}
			
			if(stepsExists)
				return new DispatchEntity(new BackstepsDispatchExecutor(steps), new AssigneeHandler4Backsteps()); 
			throw new ProcessWebException("buildDispatchExecutor失败, 参数不合法: taskinstId="+taskinstId+", ButtonType="+OptionTypeConstants.BACK+", steps="+steps);
		}
		throw new ProcessWebException("buttonType的值["+buttonType+"]不合法");
	}
	
	/**
	 * 
	 * @author DougLei
	 */
	private class DispatchEntity {
		private DispatchExecutor dispatchExecutor; // 调度执行器
		private AssigneeHandler assigneeHandler; // 指派信息处理器
		
		public DispatchEntity(DispatchExecutor dispatchExecutor, AssigneeHandler assigneeHandler) {
			this.dispatchExecutor = dispatchExecutor;
			this.assigneeHandler = assigneeHandler;
		}
	}
	
	
	public String getTaskinstId() {
		return taskinstId;
	}
	public void setTaskinstId(String taskinstId) {
		this.taskinstId = taskinstId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSuggest() {
		return suggest;
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	public String getAttitude() {
		return attitude;
	}
	public void setAttitude(String attitude) {
		this.attitude = attitude.toUpperCase();
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getButtonType() {
		return buttonType;
	}
	public void setButtonType(String buttonType) {
		this.buttonType = buttonType;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public int getSteps() {
		return steps;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
	public String getAssignedUserIds() {
		return assignedUserIds;
	}
	public void setAssignedUserIds(String assignedUserIds) {
		this.assignedUserIds = assignedUserIds;
	}
}
