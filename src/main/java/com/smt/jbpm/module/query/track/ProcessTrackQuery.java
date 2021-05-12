package com.smt.jbpm.module.query.track;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.process.mapping.metadata.ProcessMetadata;
import com.douglei.bpm.process.mapping.metadata.flow.FlowMetadata;
import com.douglei.orm.context.PropagationBehavior;
import com.douglei.orm.context.SessionContext;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class ProcessTrackQuery {
	
	@Autowired
	private ProcessEngineBeans processEngineBeans;
	
	/**
	 * 流程跟踪主体查询
	 * @param procinstId
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public ProcessSubject subjectQuery(String procinstId) {
		// 查询流程信息, 并存储到流程主体中
		ProcessSubject subject = SessionContext.getSQLSession().uniqueQuery(ProcessSubject.class, "ProcessTrackQuery", "queryProcess", procinstId);
		
		// 查询所有任务信息, 并添加到流程主体中
		ProcessMetadata processMetadata = processEngineBeans.getProcessContainer().getProcess(subject.getProcdefId());
		SessionContext.getSQLSession().query(Task.class, "ProcessTrackQuery", "queryAllTask", subject.getId()).forEach(task -> {
			subject.addTask(task);
			
			if(task.getEndTime() == null) {
				for(FlowMetadata flow : processMetadata.getTaskMetadataEntity(task.getKey()).getInputFlows()) {
					if(flow.getSource().equals(task.getSourceKey())) {
						subject.addFlow(flow.getId());
						break;
					}
				}
			}
		});
		return subject;
	}
	
	/**
	 * 流程跟踪任务办理明细查询
	 * @param procinstId
	 * @param taskKey
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public List<Task> detailQuery(String procinstId, String taskKey) {
		List<Task> tasks = SessionContext.getSQLSession().query(Task.class, "ProcessTrackQuery", "queryTask", Arrays.asList(procinstId, taskKey));
		if(!tasks.isEmpty()) {
			Map<String, Task> tempTaskMap = new HashMap<String, Task>(4);
			tasks.forEach(task -> tempTaskMap.put(task.getId(), task));
			
			SessionContext.getSQLSession().query(TaskHandleDetail.class, "ProcessTrackQuery", "queryTaskHandleDetails", tasks).forEach(detail -> {
				tempTaskMap.get(detail.getTaskinstId()).addHandleDetail(detail);
			});
		}
		return tasks;
	}
}
