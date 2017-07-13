/*
 * 这是一个模板的解析类的基类
 * 由外部传入一个ServiceRegister进来，这个result
 */

package com.breeze.framwork.servicerg;

import java.util.Map;

import com.breeze.support.tools.GsonTools.OS;


/**
 *
 * @author Logan
 * 2010-8-25日重构生成
 * 这个类是解析输入参数的积累，产生一个特定的templeItem
 */
public abstract class TemplateItemParserAbs {
    private String templeItemName;
    public TemplateItemParserAbs(String pItemName){
        this.templeItemName = pItemName;
    }
    public final String getTempleItemName(){
        return this.templeItemName;
    }

    public abstract TemplateItemBase parserTempLe(Map<String, OS> s);

    public abstract Class getItempClass();
}