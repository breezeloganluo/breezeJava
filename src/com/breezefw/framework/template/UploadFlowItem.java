package com.breezefw.framework.template;

import com.breeze.framwork.servicerg.FieldDesc;
import com.breeze.framwork.servicerg.TemplateItemBase;

public class UploadFlowItem extends TemplateItemBase {
	
	public static class UploadSetting extends TemplateItemBase{
		@FieldDesc(desc = "UploadFlow上传字段名，即文件存在于哪个字段中", title = "Upload上传字段", valueRange = "")
		private String uploadField;
		
		@FieldDesc(desc = "UploadFlow保存的文件名，支持BTL，但请用Org函数", title = "Upload保存文件名", valueRange = "")
		private String destFileName;
		
		@FieldDesc(title = "是否解压zip",type="Select", valueRange = "[{\"y\":\"y\",\"n\":\"n\"}]", desc = "")
		private String isZip;

		public String getUploadField() {
			return uploadField;
		}
		public String getDestFileName() {
			return destFileName;
		}
		public String getIsZip(){
			return this.isZip;
		}
	}

	
	private UploadSetting[] uploadSettingList;
	
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
