package com.smt.jbpm.module.repository.definition.insert;

/**
 * 
 * @author DougLei
 */
public class ProcessDesign {
	private String struct; // 流程结构json
	private String image; // 流程界面json
	private boolean existsInstance; // 是否存在(运行/历史)实例

	public String getStruct() {
		return struct;
	}
	public void setStruct(String struct) {
		this.struct = struct;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public boolean isExistsInstance() {
		return existsInstance;
	}
	public void setExistsInstance(boolean existsInstance) {
		this.existsInstance = existsInstance;
	}
}
