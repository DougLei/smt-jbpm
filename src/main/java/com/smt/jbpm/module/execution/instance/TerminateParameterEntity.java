package com.smt.jbpm.module.execution.instance;

import com.douglei.bpm.module.Result;
import com.douglei.tools.StringUtil;

/**
 * 
 * @author DougLei
 */
public class TerminateParameterEntity {
	private String procinstId;
	private String reason;
	
	/**
	 * 验证
	 * @return
	 */
	public Result validate() {
		if(StringUtil.isEmpty(procinstId))
			return new Result("终止的流程实例id不能为空", "smt.jbpm.process.instance.terminate.fail.id.notnull");
		if(StringUtil.isEmpty(reason))
			return new Result("终止流程实例的原因不能为空", "smt.jbpm.process.instance.terminate.fail.reason.notnull");
		return null;
	}
	
	public String getProcinstId() {
		return procinstId;
	}
	public void setProcinstId(String procinstId) {
		this.procinstId = procinstId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}
