package com.smt.jbpm.module.repository.definition.insert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.douglei.bpm.module.Result;
import com.douglei.bpm.module.repository.definition.ProcessDefinition;
import com.douglei.bpm.module.repository.definition.ProcessDefinitionBuilder;
import com.douglei.orm.context.PropagationBehavior;
import com.douglei.orm.context.SessionContext;
import com.douglei.orm.context.Transaction;
import com.douglei.orm.context.TransactionComponent;
import com.smt.jbpm.SmtJbpmException;
import com.smt.jbpm.module.repository.definition.insert.json.NodeJsonFactory;
import com.smt.jbpm.module.repository.definition.insert.json.ProcessNodeJson;
import com.smt.jbpm.module.repository.definition.insert.json.edge.EdgeJson;

/**
 * 
 * @author DougLei
 */
@TransactionComponent
public class ProcessDefinitionParser {
	private static final Logger logger = LoggerFactory.getLogger(ProcessDefinitionParser.class);
	
	/**
	 *  
	 * @param struct
	 * @return
	 */
	@Transaction(propagationBehavior=PropagationBehavior.SUPPORTS)
	public ParseResult parse(String struct) {
		JSONObject json = JSONObject.parseObject(struct);
		JSONObject process = json.getJSONObject("process");
		
		try {
			// 进行验证
			Validator validator = new Validator(process);
			validator.execute();
			
			// 解析获取流程xml
			StringBuilder xml = new StringBuilder(3000);
			xml.append("<?xml version='1.0' encoding='UTF-8\'?>");
			xml.append("<bpm-configuration>");
			xml.append("<process code='").append(validator.processDefinition.code).append("' version='").append(validator.processDefinition.version).append("' name='").append(process.getString("name")).append("' title='").append(process.getString("title")).append("' pageID='").append(process.getString("pageID")).append("'>");
			getNodeJsonList(json.getJSONArray("nodejson")).forEach(nj -> xml.append(nj.toXml()));
			getEdgeJsonList(json.getJSONArray("edgejson")).forEach(ej -> xml.append(ej.toXml()));
			xml.append("</process>");
			xml.append("</bpm-configuration>");
			logger.debug("JBPM -> json转换xml的结果为: \n{}", xml);
			
			// 构建ProcessDefinitionBuilder实例
			ProcessDefinitionBuilder builder = new ProcessDefinitionBuilder(xml.toString());
			builder.setIgnore("true".equalsIgnoreCase(process.getString("ignore")));
			builder.setStrict("true".equalsIgnoreCase(process.getString("strict")));
			builder.setTypeId(validator.type.id);
			builder.setDescription(process.getString("description"));
			return new ParseResult(process.getString("pageID"), builder);
		} catch (ValidatorException e) {
			Result result = e.buildClientResult();
			if(result == null)
				throw e;
			return new ParseResult(result);
		}
	}
	
	/**
	 * 
	 * @author DougLei
	 */
	private class Validator {
		TypeEntity type;
		ProcessDefinitionEntity processDefinition;
		
		public Validator(JSONObject process) {
			this.type = new TypeEntity(process.getIntValue("typeId"));
			this.processDefinition = new ProcessDefinitionEntity(process.getString("code"), process.getString("version"));
		}

		/**
		 * 进行验证
		 */
		public void execute() {
			if(type.existsRefProcdef() && !type.code.equals(processDefinition.code)) 
				throw new ValidatorException("同一个类型下只能关联相同code的流程定义信息", "smt.jbpm.process.definition.single.samecode.in.sametype");
				
			if(!processDefinition.isNewProcdef() && processDefinition.typeId != type.id)
				throw new ValidatorException("不支持修改流程定义关联的类型");
		}
	}
	
	/**
	 * 
	 * @author DougLei
	 */
	private class TypeEntity {
		int id; // 当前配置的类型id
		String code; // id关联的流程定义code, 可为null
		
		public TypeEntity(int id) {
			Object[] array = SessionContext.getSqlSession().uniqueQuery_("select parent_id from bpm_re_type where id=?", Arrays.asList(id));
			if(array == null)
				throw new ValidatorException("不存在id=["+id+"]的流程类型");
			
			int parentTypeId = Integer.parseInt(array[0].toString());
			if(parentTypeId == 0)
				throw new ValidatorException("根类型下不能添加流程定义信息");
			
			this.id = id;
			
			// 查询类型关联的流程定义
			ProcessDefinition pd = SessionContext.getSqlSession().uniqueQuery(
					ProcessDefinition.class, "select distinct code from bpm_re_procdef where type_id=?", Arrays.asList(id));
			if(pd != null) 
				this.code = pd.getCode();
		}
		
		/**
		 * 当前类型是否存在关联的流程定义信息
		 * @return
		 */
		public boolean existsRefProcdef() {
			return code != null;
		}
	}
	
	/**
	 * 
	 * @author DougLei
	 */
	private class ProcessDefinitionEntity {
		Integer typeId; // code和version关联的类型id, 可为null
		String code; // 当前配置的流程定义code
		String version; // 当前配置的流程定义version
		
		public ProcessDefinitionEntity(String code, String version) {
			// 尝试记录流程code关联的typeId
			ProcessDefinition pd = SessionContext.getSqlSession().uniqueQuery(
					ProcessDefinition.class, "select distinct type_id, version from bpm_re_procdef where code=?", Arrays.asList(code));
			if(pd != null) {
				if(!pd.getVersion().equals(version))
					throw new ValidatorException(
							"在[%s]分类中已存在code为[%s]的流程定义信息", 
							"smt.jbpm.process.definition.exists.code.in.type", 
							SessionContext.getSqlSession().uniqueQuery_("select name from bpm_re_type where id=?", Arrays.asList(pd.getTypeId()))[0], code);
				
				this.typeId = pd.getTypeId();
			} 
				
			this.code = code;
			this.version = version;
		}
		
		/**
		 * 是否是新增的流程定义信息
		 * @return
		 */
		public boolean isNewProcdef() {
			return typeId == null;
		}
	}
	
	/**
	 * 
	 * @author DougLei
	 */
	private class ValidatorException extends SmtJbpmException {
		private String message;
		private String code;
		private Object[] params;
		
		public ValidatorException(String message) {
			super(message);
		}
		public ValidatorException(String message, String code, Object... params) {
			this.message = message;
			this.code = code;
			this.params = params;
		}

		/**
		 * 构建要返回给客户端的Result实例
		 * @return 返回null, 标识直接抛异常即可
		 */
		public Result buildClientResult() {
			if(message == null)
				return null;
			return new Result(message, code, params);
		}
	}
	
	
	// 获取nodejsonList
	private List<ProcessNodeJson> getNodeJsonList(JSONArray nodeArray) {
		List<ProcessNodeJson> nodejsonList = new ArrayList<ProcessNodeJson>(nodeArray.size());
		for(int i=0;i<nodeArray.size();i++) 
			nodejsonList.add(NodeJsonFactory.buildNodeJson(nodeArray.getJSONObject(i)));
		return nodejsonList;
	}

	// 获取edgejsonList
	private List<ProcessNodeJson> getEdgeJsonList(JSONArray edgeArray) {
		List<ProcessNodeJson> edgejsonList = new ArrayList<ProcessNodeJson>(edgeArray.size());
		for(int i=0;i<edgeArray.size();i++) 
			edgejsonList.add(new EdgeJson(edgeArray.getJSONObject(i)));
		return edgejsonList;
	}
}