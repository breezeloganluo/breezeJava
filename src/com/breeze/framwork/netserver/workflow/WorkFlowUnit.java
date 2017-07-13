//Source file: D:\\my resource\\myproject\\千百汇\\研发\\设计\\com\\qbhui\\framwork\\netserver\\workflow\\WorkFlowUnit.java
package com.breeze.framwork.netserver.workflow;

import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;

/**
 * 工作流的基类，同时他自己也是一个管理类。包含所有子类的名称和实例的对应关系，方便 上层调用获取。 构造函数的时候，就要将所有子对象写好。
 */
public abstract class WorkFlowUnit {
    public WorkFlowUnit() {
    }

    public abstract String getName() ;

    /**
     * 具体单元处理方法，子类必须重载
     * 返回值注意，通常0为成功，不能为负值
     * @param context 上下文信息的请求对象
     * @param st 对应的文档模板
     * @param alias 配置时该处理单元的处理别名
     * @param lastResult 上一个处理单元的处理结果
     * @return 本单元处理结果
     */
    public abstract int process(BreezeContext context,ServiceTemplate st,String alias,int lastResult);

   

    /**
     * 这个方法中返回本处理方法需要的所有的模板项名成以及对应的模板项类<br>
     * 注意，在被子类重载后，子类方法实际只返回CommTemplateItemParser对象
     * @return
     */
    public abstract TemplateItemParserAbs[] getProcessParser();
    
    /**
     * 给子类调用的，获取基础模板内容
     * @param context
     * @param st
     * @param itemName
     * @return
     */
    protected TemplateItemBase getItem(BreezeContext context,ServiceTemplate st,String itemName){
    	TemplateItemBase result = st.getItem(itemName);
    	if (result == null){
    		return null;
    	}
    	return result.runingInit(context);
    }
    
    /**
     * 给workflow进行初始化的时候一个自己初始化自己业务的机会
     */
    protected void loadingInit(){
    	
    }
    
}
