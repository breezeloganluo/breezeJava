package com.breezefw.framework.template;

import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;

public class UploadFlowItem extends TemplateItemBase {
	
	
	
	@FieldDesc(desc = "上传后，将文件路径存入的上下文地址，注意保存的路径是相对web的路径", title = "目标文件上下文地址", valueRange = "")
	private String destFileCtxPath;
	
	@FieldDesc(desc = "上传后，将目录路径存入的上下文地址，注意保存的路径是相对web的路径", title = "目标目录上下文地址", valueRange = "")
	private String destDirCtxPath;
	
	 


	public UploadSetting[] getUploadSettingList() {
		return uploadSettingList;
	}
	
	public String getDestFileCtxPath(){
		return this.destFileCtxPath;
	}

	

	public void setDestFileCtxPath(String destFileCtxPath) {
		this.destFileCtxPath = destFileCtxPath;
	}
	
	
	public String getDestDirCtxPath() {
		return destDirCtxPath;
	}

	public void setDestDirCtxPath(String destDirCtxPath) {
		this.destDirCtxPath = destDirCtxPath;
	}
	
	
}
