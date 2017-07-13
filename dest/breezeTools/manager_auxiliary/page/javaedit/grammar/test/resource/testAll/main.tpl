<form>
     
     java的代码：<br/>
    
     <textarea id="insrc">
    
    
    package com.breezefw.framework.workflow;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser;
import com.breezefw.framework.template.CopyContextItem;

public class CopyContext extends WorkFlowUnit {
	public static final String ITEMNAME = "copyContext";
	private Logger log = Logger.getLogger("com.breezefw.framework.workflow.CopyContext");
	@Override
	public String getName() {
		return ITEMNAME;
	}

	@Override
	public TemplateItemParserAbs[] getProcessParser() {		
		TemplateItemParserAbs[] result = new TemplateItemParserAbs[] {
				new CommTemplateItemParser(ITEMNAME, CopyContextItem.class)};
		return result;
	}

	@Override
	public int process(BreezeContext context, ServiceTemplate st, String alias,
			int lastResult) {
		// 获取根上下文信息
		BreezeContext root = context;
		if (log.isLoggable(Level.FINE)) {
			log.fine("go Process [" + this.getName() + "]lastResult["
					+ lastResult + "]" );
		}
		try{
			CopyContextItem item = (CopyContextItem) this.getItem(context,st,ITEMNAME);
			
			BreezeContext source = root.getContextByPath(item.getSourcePath());
			log.fine("src context is :\n" + source);
			if (source == null){
				root.setContextByPath(item.getDestPath(), null);
				return item.getNotExistErrCode();
			}
			root.setContextByPath(item.getDestPath(), source.getSelf());
			return 0;
		}catch(Exception e){
			log.severe("exception:", e);
			return 999;
		}
	}

}

    
    
    
     </textarea><br/>
     解析后的结果对象:<br/>
     <textarea id="result">
     </textarea><br/>
     <a href="#"  onclick="FireEvent.parserJava('insrc','result')">提交</a>
</form>