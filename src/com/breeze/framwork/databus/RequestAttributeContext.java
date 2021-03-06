/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.databus;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Administrator
 */
public class RequestAttributeContext extends BreezeContext {

    private HttpServletRequest request = null;

    public RequestAttributeContext(HttpServletRequest prequest) {
        this.request = prequest;
    }

    /**
     * 获请求对象对应的值，该方法重载了父类的函数
     *
     * @param pname
     * @return
     */
    @Override
    public BreezeContext getContext(String pname) {
        Object data = this.request.getAttribute(pname);
        if (data instanceof BreezeContext) {
            return (BreezeContext)data;
        }
        return null;
    }

    /**
     * 本子类应该不再支持该方法
     *
     * @param pname
     * @param value
     */
    @Override
    public void setContext(String pname, BreezeContext pvalue) {
        this.request.setAttribute(pname, pvalue);
    }
}
