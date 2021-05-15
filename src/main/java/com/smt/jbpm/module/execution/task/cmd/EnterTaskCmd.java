package com.smt.jbpm.module.execution.task.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.module.execution.task.HandleState;
import com.douglei.bpm.module.execution.task.runtime.Assignee;
import com.douglei.bpm.module.execution.variable.Scope;
import com.douglei.bpm.module.execution.variable.runtime.Variable;
import com.douglei.bpm.process.Type;
import com.douglei.bpm.process.api.user.task.handle.policy.ClaimPolicy;
import com.douglei.bpm.process.api.user.task.handle.policy.DispatchPolicy;
import com.douglei.bpm.process.mapping.metadata.ProcessMetadata;
import com.douglei.bpm.process.mapping.metadata.TaskMetadata;
import com.douglei.bpm.process.mapping.metadata.TaskMetadataEntity;
import com.douglei.bpm.process.mapping.metadata.flow.FlowMetadata;
import com.douglei.bpm.process.mapping.metadata.task.user.UserTaskMetadata;
import com.douglei.bpm.process.mapping.metadata.task.user.candidate.handle.ClaimPolicyEntity;
import com.douglei.bpm.process.mapping.metadata.task.user.candidate.handle.HandlePolicy;
import com.douglei.bpm.process.mapping.metadata.task.user.option.ActiveTime;
import com.douglei.bpm.process.mapping.metadata.task.user.option.Option;
import com.douglei.bpm.process.mapping.metadata.task.user.option.dispatch.BackOption;
import com.douglei.bpm.process.mapping.metadata.task.user.option.dispatch.JumpOption;
import com.douglei.orm.context.SessionContext;
import com.douglei.tools.OgnlUtil;
import com.smt.jbpm.SmtJbpmException;
import com.smt.jbpm.module.execution.task.button.BackstepsHandleButton;
import com.smt.jbpm.module.execution.task.button.HandleButton;
import com.smt.jbpm.module.execution.task.button.OptionButton;
import com.smt.jbpm.module.execution.task.button.SettargetHandleButton;
import com.smt.jbpm.module.execution.task.button.VirtualButton;

/**
 * 
 * @author DougLei
 */
public class EnterTaskCmd {
	private String userId;
	private String taskinstId;
	private ProcessEngineBeans processEngineBeans;
	
	private List<Object[]> handleModes;
	private TaskDetail detail;
	private ProcessMetadata processMetadata;
	private TaskMetadataEntity<TaskMetadata> taskMetadataEntity;
	protected boolean supportUnclaim=true; // 是否支持取消认领操作
	private boolean isLastHandleUser; // 记录当前用户是否是最后一个办理的用户
	private int waitForPersonNumber; // 当HandleMode为WAITING时, 存储需要等待的人数
	
	public EnterTaskCmd(String userId, String taskinstId, ProcessEngineBeans processEngineBeans) {
		this.userId = userId;
		this.taskinstId = taskinstId;
		this.processEngineBeans = processEngineBeans;
		
		this.handleModes= queryHandleMode();
		if(handleModes == null)
			throw new SmtJbpmException("用户["+userId+"]目前不拥有任务["+taskinstId+"]的办理权限, 无法操作任务");
	}
	
	/**
	 * 查询当前用户要进行的办理模式
	 * @return
	 */
	protected List<Object[]> queryHandleMode() {
		List<Object[]> handleModes= SessionContext.getSQLSession().query_("EnterTask", "queryHandleMode", new Object[] {taskinstId, userId});
		if(handleModes.isEmpty())
			return null;
		return handleModes;
	}
	
	/**
	 * 进入任务
	 * @return
	 */
	public TaskDetail execute() {
		// 构建TaskDetail实例
		this.detail = SessionContext.getSQLSession().uniqueQuery(TaskDetail.class, "EnterTask", "querTaskDetail", taskinstId);
		if(!this.detail.isActive())
			throw new SmtJbpmException("["+detail.getName()+"]任务处于挂起状态, 无法进入");

		this.processMetadata = processEngineBeans.getProcessContainer().getProcess(detail.getProcdefId());
		this.taskMetadataEntity = processMetadata.getTaskMetadataEntity(detail.getKey());
		this.detail.setMode(analyseMode(handleModes));
		
		// 设置相关的按钮
		detail.setHandleButtions(getHandleButtions());
		detail.setOptionButtions(getOptionButtions());
		return detail;
	}
	
	/**
	 * 分析当前用户要进行的办理模式
	 * @param handleModes
	 * @return
	 */
	protected HandleMode analyseMode(List<Object[]> handleModes) {
		for (Object[] array : handleModes) {
			if(array[0].equals("CLAIMING"))
				return HandleMode.CLAIMING;
			if(array[0].equals("DISPATCHING")) {
				// 明确拥有调度权限时, 不能进行取消认领操作, 且需要获取之前的办理意见和办理态度
				Map<String, Object> record = SessionContext.getSqlSession().limitQuery(1, 1, "select distinct suggest, attitude from bpm_hi_assignee where taskinst_id=? and handle_state=6 and user_id=?", Arrays.asList(taskinstId, userId)).get(0);
				this.detail.setSuggest(record.get("SUGGEST").toString());
				this.detail.setAttitude(record.get("ATTITUDE").toString());
				this.supportUnclaim=false;
				return HandleMode.DISPATCHING;
			}
		}
		
		// 办理状态下, 如果当前用户是最后一个办理人, 且可以拥有调度权, 则直接授予调度权
		if(canDispatch())
			return HandleMode.DISPATCHING;
		if(canHandle())
			return HandleMode.HANDLING;
		return HandleMode.WAITING;
	}
	private boolean canDispatch() {
		// 如果任务没有被全部认领, 则不能调度
		if(!detail.isAllClaimed()) 
			return false;
		
		// 如果任务的认领人数上限为1, 则可以调度
		ClaimPolicyEntity entity = ((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getCandidate().getHandlePolicy().getClaimPolicyEntity();
		ClaimPolicy claimPolicy = processEngineBeans.getAPIContainer().getClaimPolicy(entity.getName());
		if(claimPolicy.calcUpperLimit(entity.getValue(), detail.getAssignCount()) == 1)
			return true;
		
		// 查询当前任务, 剩余已经认领的指派信息数量(排除当前用户), 如果大于0, 则不能调度
		int leftClaimedAssigneeCount = Integer.parseInt(SessionContext.getSqlSession().uniqueQuery_(
				"select count(id) from bpm_ru_assignee where taskinst_id=? and handle_state=? and user_id<>?", 
				Arrays.asList(detail.getTaskinstId(), HandleState.CLAIMED.getValue(), userId))[0].toString());
		if(leftClaimedAssigneeCount > 0)
			return false;
		
		this.isLastHandleUser = true;
		
		// 获取办理过当前任务的用户id集合
		List<Object[]> list = SessionContext.getSqlSession().query_("select distinct user_id from bpm_hi_assignee where taskinst_id=? and handle_state = 'FINISHED'", Arrays.asList(taskinstId));
		List<String> handledUserIds = new ArrayList<String>(list.size()+1);
		list.forEach(array -> handledUserIds.add(array[0].toString()));
		handledUserIds.add(userId);
		
		// 根据调度策略, 获取可进行任务调度的userId
		DispatchPolicy dispatchPolicy = processEngineBeans.getAPIContainer().getDispatchPolicy(((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getCandidate().getHandlePolicy().getDispatchPolicyEntity().getName());
		return userId.equals(dispatchPolicy.getUserId(userId, handledUserIds));
	}
	private boolean canHandle() {
		HandlePolicy handlePolicy = ((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getCandidate().getHandlePolicy();
		if(handlePolicy.getSerialHandlePolicyEntity() == null)
			return true;
		
		if(isLastHandleUser)
			return true;
		
		// 查询当前任务, 所有认领状态的指派信息
		List<Assignee> claimedAssigneeList = SessionContext.getSqlSession()
				.query(Assignee.class, 
						"select user_id, claim_time from bpm_ru_assignee where taskinst_id=? and handle_state=?", 
						Arrays.asList(taskinstId, HandleState.CLAIMED.getValue()));
		
		int count = 0; // 记录是否全部是当前用户的指派信息
		for (Assignee assignee : claimedAssigneeList) {
			if(!assignee.getUserId().equals(userId))
				break;
			count++;
		}
		if(count == claimedAssigneeList.size())
			return true;
		
		this.waitForPersonNumber= processEngineBeans.getAPIContainer().getSerialHandlePolicy(handlePolicy.getSerialHandlePolicyEntity().getName()).canHandle(userId, claimedAssigneeList);
		return waitForPersonNumber==0;
	}
	
	// 获取办理型按钮集合
	private List<HandleButton> getHandleButtions() {
		// 认领状态下, 展示认领按钮
		if(detail.getMode() == HandleMode.CLAIMING) {
			List<HandleButton> buttons = new ArrayList<HandleButton>(1);
			buttons.add(new VirtualButton("claim", "认领"));
			return buttons;
		}
		
		// 办理状态下, 展示办理按钮
		if(detail.getMode() == HandleMode.HANDLING) {
			List<HandleButton> buttons = new ArrayList<HandleButton>(2);
			buttons.add(new VirtualButton("unclaim", "取消认领"));
			HandlePolicy handlePolicy = ((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getCandidate().getHandlePolicy();
			buttons.add(new HandleButton("handle", "办理", handlePolicy.suggestIsRequired(), handlePolicy.attitudeIsRequired()));
			return buttons;
		}
		
		// 等待状态下, 展示等待按钮
		if(detail.getMode() == HandleMode.WAITING) {
			List<HandleButton> buttons = new ArrayList<HandleButton>(2);
			buttons.add(new VirtualButton("unclaim", "取消认领"));
			buttons.add(new VirtualButton("wait", "暂缓办理") {
				@SuppressWarnings("unused")
				public int getWaitForPersonNumber() {
					return waitForPersonNumber;
				}
			});
			return buttons;
		}
		
		// 可以调度时, 展示所有按钮
		List<HandleButton> buttons = new ArrayList<HandleButton>();
		if(supportUnclaim)
			buttons.add(new VirtualButton("unclaim", "取消认领"));
		
		// 先处理flow类型的按钮
		HandlePolicy handlePolicy = ((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getCandidate().getHandlePolicy();
		taskMetadataEntity.getOutputFlows().stream()
			.filter(flow -> flow.getConditionExpression()==null || OgnlUtil.getBooleanValue(flow.getConditionExpression(), getVariableMap()))
			.forEach(flow -> buttons.add(new SettargetHandleButton("flow", flow.getName(), handlePolicy.suggestIsRequired(), handlePolicy.attitudeIsRequired(), flow.getTarget(), isDynamicAssign(flow.getTarget()))));
		
		// 如果没有匹配到合适的flow, 尝试添加默认的flow
		if(buttons.isEmpty()) {
			FlowMetadata defaultFlow = taskMetadataEntity.getDefaultOutputFlow();
			if(defaultFlow != null)
				buttons.add(new SettargetHandleButton("flow", defaultFlow.getName(), handlePolicy.suggestIsRequired(), handlePolicy.attitudeIsRequired(), defaultFlow.getTarget(), isDynamicAssign(defaultFlow.getTarget())));
		}
		
		// 再处理option类型的按钮
		List<Option> options = ((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getOptions();
		if(options != null) {
			for (Option option : options) {
				if(!option.supportActiveTime(ActiveTime.TASK_DISPATCHING)) 
					continue;
				
				if(option instanceof JumpOption) {
					JumpOption jumpOption = (JumpOption) option;
					buttons.add(new SettargetHandleButton(
							jumpOption.getType(), 
							jumpOption.getName(), 
							jumpOption.suggestIsRequired(), 
							jumpOption.attitudeIsRequired(), 
							jumpOption.getTarget(), 
							jumpOption.getCandidate()!=null?jumpOption.getCandidate().getAssignPolicy().isDynamic():isDynamicAssign(jumpOption.getTarget())));
				}else if(option instanceof BackOption) {
					BackOption backOption = (BackOption) option;
					buttons.add(new BackstepsHandleButton(
							backOption.getType(), backOption.getName(), backOption.suggestIsRequired(), backOption.attitudeIsRequired(), backOption.getSteps()));
				}
				throw new SmtJbpmException("在获取调度性的Option按钮时, 无法解析["+option.getClass()+"]类型的Option实例");
			}
		}
		return buttons;
	}
	// 目标任务是否需要动态指派人员
	private Map<String, TaskMetadataEntity<TaskMetadata>> targetTaskMetadataEntityCache;
	public boolean isDynamicAssign(String target) {
		if(targetTaskMetadataEntityCache == null)
			targetTaskMetadataEntityCache = new HashMap<String, TaskMetadataEntity<TaskMetadata>>();
		
		TaskMetadataEntity<TaskMetadata> taskMetadataEntity = targetTaskMetadataEntityCache.get(target);
		if(taskMetadataEntity == null) {
			taskMetadataEntity = processMetadata.getTaskMetadataEntity(target);
			targetTaskMetadataEntityCache.put(target, taskMetadataEntity);
		}
			
		TaskMetadata taskMetadata = taskMetadataEntity.getTaskMetadata();
		
		// 排他网关时需要递归判断
		if(taskMetadata.getType() == Type.EXCLUSIVE_GATEWAY) {
			for(FlowMetadata flow: taskMetadataEntity.getOutputFlows()) {
				if(flow.getConditionExpression() == null || OgnlUtil.getBooleanValue(flow.getConditionExpression(), getVariableMap()))
					return isDynamicAssign(flow.getTarget());
			}
			
			FlowMetadata defaultFlowMetadata = taskMetadataEntity.getDefaultOutputFlow();
			if(defaultFlowMetadata != null)
				return isDynamicAssign(defaultFlowMetadata.getTarget());
		}
		
		if(taskMetadata.getType() == Type.USER_TASK) 
			return ((UserTaskMetadata)taskMetadata).getCandidate().getAssignPolicy().isDynamic();
		return false;
	}
	
	// 获取指定任务拥有的流程变量集合
	private Map<String, Object> variableMap;
	private Map<String, Object> getVariableMap() {
		if(variableMap == null) {
			List<Variable> variables = SessionContext.getTableSession().query(
					Variable.class,
					"select * from bpm_ru_variable where procinst_id=? and (taskinst_id=? or scope =?)", 
					Arrays.asList(detail.getProcinstId(), taskinstId, Scope.GLOBAL.getValue()));
			
			if(variables.isEmpty()) {
				variableMap = Collections.emptyMap();
			}else {
				variableMap = new HashMap<String, Object>();
				for (Variable variable : variables) 
					variableMap.put(variable.getName(), variable.getValue());
			}
		}
		return variableMap;
	}
	
	/**
	 * 获取选项型按钮集合
	 * @return
	 */
	protected List<OptionButton> getOptionButtions() {
		if(detail.getMode() != HandleMode.HANDLING)
			return Collections.emptyList();
		
		List<Option> options = ((UserTaskMetadata)taskMetadataEntity.getTaskMetadata()).getOptions();
		if(options == null)
			return Collections.emptyList();
		
		List<OptionButton> buttons = new ArrayList<OptionButton>();
		
		for (Option option : options) {
			if(option.supportActiveTime(ActiveTime.TASK_HANDLING)) 
				buttons.add(new OptionButton(option.getType(), option.getName()));
		}
		return buttons;
	}
}