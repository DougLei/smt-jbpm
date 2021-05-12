package com.smt.jbpm.module.repository.definition;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.repository.RepositoryModule;
import com.douglei.bpm.module.repository.definition.ProcessDefinition;
import com.douglei.bpm.module.repository.definition.ProcessDefinitionBuilder;
import com.douglei.orm.context.PropagationBehavior;
import com.douglei.orm.context.SessionContext;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;
import com.smt.jbpm.module.repository.definition.insert.ProcessDefinitionExtend;
import com.smt.jbpm.module.repository.definition.insert.ProcessDesign;
import com.smt.parent.code.response.Response;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class ProcessDefinitionService {
	
	@Autowired
	private RepositoryModule repositoryModule;
	
	/**
	 * 插入流程定义(保存或修改)
	 * @param builder
	 * @param struct
	 * @param image
	 * @param pageId
	 * @return
	 */
	@Transaction
	public Response insert(ProcessDefinitionBuilder builder, String struct, String image, String pageId) {
		Result result = repositoryModule.getDefinitionService().insert(builder);
		if(result.isFail()) 
			return new Response(null, null, result.getMessage(), result.getCode(), result.getParams());
		
		// 保存/修改流程定义扩展数据
		ProcessDefinitionExtend extend = new ProcessDefinitionExtend();
		extend.setProcdefId(result.getObject(ProcessDefinition.class).getId());
		extend.setStruct(struct);
		extend.setImage(image);
		extend.setLastUpdateDate(new Date());
		extend.setPageId(pageId);
		
		Object[] obj = SessionContext.getSqlSession().uniqueQuery_("select id from bpm_re_procdef_extend where procdef_id=?", Arrays.asList(extend.getProcdefId()));
		if(obj == null) {
			extend.setCreateDate(extend.getLastUpdateDate());
			SessionContext.getTableSession().save(extend);
		}else {
			extend.setId(Integer.parseInt(obj[0].toString()));
			SessionContext.getTableSession().update(extend);
		}
		return new Response(extend.getId());
	}

	/**
	 * ById查询
	 * @param procdefId
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public Response queryById(int procdefId) {
		ProcessDesign design = new ProcessDesign();
		design.setImage(SessionContext.getSqlSession().uniqueQuery_("select image from bpm_re_procdef_extend where procdef_id=?", Arrays.asList(procdefId))[0].toString());
		design.setExistsInstance(repositoryModule.getDefinitionService().existsInstance(procdefId));
		return new Response(design);
	}
	
	/**
	 * 部署
	 * @param procdefId
	 * @return
	 */
	@Transaction
	public Response deploy(int procdefId) {
		Result result = repositoryModule.getDefinitionService().deploy(procdefId);
		if(result.isFail())
			return new Response(procdefId, null, result.getMessage(), result.getCode(), result.getParams());
		
		SessionContext.getSqlSession().executeUpdate("update bpm_re_procdef set is_major_version=1 where id=?", Arrays.asList(procdefId));
		return new Response(procdefId);
	}
}
