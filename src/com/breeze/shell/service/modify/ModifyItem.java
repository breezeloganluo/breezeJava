/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.shell.service.modify;

import java.util.Map;

/**
 *
 * @author lili
 */
public abstract class ModifyItem {
    /**
     * 如果有改动返回true否则返回false
     * @param item 输入输出参数，改动的内容体现在这个参数中
     * @return 有改动返回true 否则返回false
     */
    public abstract boolean modify(Map<String,String> item);
}
