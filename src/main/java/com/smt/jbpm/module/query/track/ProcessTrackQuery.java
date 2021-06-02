package com.smt.jbpm.module.query.track;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.ProcessEngineBeans;
import com.douglei.bpm.process.mapping.metadata.ProcessMetadata;
import com.douglei.bpm.process.mapping.metadata.flow.FlowMetadata;
import com.douglei.orm.context.PropagationBehavior;
import com.douglei.orm.context.SessionContext;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralResponse;
import com.smt.parent.code.spring.eureka.cloud.feign.APIGeneralServer;
import com.smt.parent.code.spring.eureka.cloud.feign.RestTemplateWrapper;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class ProcessTrackQuery {
	
	@Autowired
	private ProcessEngineBeans processEngineBeans;
	
	@Autowired
	private RestTemplateWrapper restTemplate;
	
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
	@SuppressWarnings("unchecked")
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public List<Task> detailQuery(String procinstId, String taskKey) {
		List<Task> tasks = SessionContext.getSQLSession().query(Task.class, "ProcessTrackQuery", "queryTask", Arrays.asList(procinstId, taskKey));
		if(!tasks.isEmpty()) {
			Map<String, Task> tempTaskMap = new HashMap<String, Task>(4);
			tasks.forEach(task -> tempTaskMap.put(task.getId(), task));
			
			List<TaskHandleDetail> details = SessionContext.getSQLSession().query(TaskHandleDetail.class, "ProcessTrackQuery", "queryTaskHandleDetails", tasks);
			
			// 将办理人和具体的任务关联, 并记录每个办理人的id
			StringBuilder userIds = new StringBuilder(details.size()*37);
			details.forEach(detail -> {
				userIds.append(detail.getUser()).append(',');
				tempTaskMap.get(detail.getTaskinstId()).addHandleDetail(detail);
			});
			userIds.setLength(userIds.length()-1);
			
			// 根据办理人的userId, 获取对应的userName, 并进行数据绑定
			// 构建请求体
			Map<String, Object> requestBody = new HashMap<String, Object>(8);
			requestBody.put("$mode$", "QUERY");
			requestBody.put("ID", "IN("+userIds+")");
			requestBody.put("ID,", "RESULT()");
			requestBody.put("NAME", "RESULT()");
			
			// 发起api请求
			List<Map<String, String>> users = (List<Map<String, String>>)restTemplate.generalExchange(new APIGeneralServer() {
				
				@Override
				public String getName() {
					return "(同步)查询指定id的用户name集合";
				}
				
				@Override
				public String getUrl() {
					return "http://smt-base/smt-base/user/query4JBPM";
				}
				
			}, JSONObject.toJSONString(requestBody), APIGeneralResponse.class);
			
			// 进行数据绑定
			if(!users.isEmpty()) {
				details.forEach(detail -> {
					for(Map<String, String> user: users) {
						if(user.get("ID").equals(detail.getUser())) {
							detail.setUser_(user.get("NAME"));
							return;
						}
					}
				});
			}
		}
		return tasks;
	}
}
