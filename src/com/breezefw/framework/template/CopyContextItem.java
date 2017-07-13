package com.breezefw.framework.template;

import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;

public class CopyContextItem extends TemplateItemBase {
	@FieldDesc(desc = "上下文的原路径", title = "sourcePath", valueRange = "[{failTips:'sourcePath不能为空',checkers:['\\\\S+']}]")
	private String sourcePath;
	@FieldDesc(desc = "上下文的目标路径", title = "destPath", valueRange = "[{failTips:'destPath不能为空',checkers:['\\\\S+']}]")
	private String destPath;
	@FieldDesc(desc = "源路径信息不存在时返回的结果码", title = "notExistErrCode", valueRange = "[{failTips:'notExistErrCode不能为空，且为数字',checkers:['\\\\d+']}]")
	private int notExistErrCode;
	/**
	 * @return the notExistErrCode
	 */
	public int getNotExistErrCode() {
		return notExistErrCode;
	}

	/**
	 * @return the sourcePath
	 */
	public String getSourcePath() {
		return sourcePath;
	}

	/**
	 * @return the destPath
	 */
	public String getDestPath() {
		return destPath;
	}	
}
