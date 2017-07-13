/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.servicerg.templateitem;

import java.util.Map;

import com.breeze.base.log.Level;
import com.breeze.base.log.Logger;
import com.breeze.framwork.servicerg.ServiceRegister;
import com.breeze.framwork.servicerg.TemplateItemBase;
import com.breeze.framwork.servicerg.TemplateItemParserAbs;
import com.breeze.support.tools.ClassTools;
import com.breeze.support.tools.GsonTools;
import com.breeze.support.tools.GsonTools.OS;
import com.google.gson.Gson;

/**
 * @author Administrator
 * 这是一个通用的模板解析类，它将所有的模板都用一个东西解析，即从map中获取的内容后直接转换成json，或者是一个直接的字符串。<br>
 * 该通用解析类实际上通过实例告诉WorkFlowUnit 用哪个templateitem名称获取怎么样的TemplateBase子类<br>
 * 即绑定了templateitem的名称和具体子类的关系<br> 该类主要在WorkFlowUnit的子类中getProcessParser使用
 */
public class CommTemplateItemParser extends TemplateItemParserAbs {

    private Class templateItemClass;
    private static Logger log = Logger.getLogger("com.breeze.framwork.servicerg.templateitem.CommTemplateItemParser");

    /**
     * 构造函数绑定了模板项名称和对应解析出来的模板项子类的类型
     * @param name 模板项名称
     * @param pc 模板项的class类
     * @param pfd 可以为空
     */
    public CommTemplateItemParser(String name, Class pc) {
        super(name);
        this.templateItemClass = pc;
    }

    @Override
    public TemplateItemBase parserTempLe(Map<String, OS> s) {
    	try{
        String itemName = super.getTempleItemName();
        if (String.class.equals(this.templateItemClass)){
        	return new TemplateItemBase(s.get(itemName).toString());
        }
        
        Map<String,GsonTools.OS> m = s;
        TemplateItemBase result = (TemplateItemBase)ClassTools.getObjFromGsonOsMap(this.templateItemClass, m,true);
        result.loadingInit();
        return result;
        }catch(Exception e){
        	log.severe("Exception", e);
        	return null;
        }
    }

	@Override
	public Class getItempClass() {
		return this.templateItemClass;
	}

}
