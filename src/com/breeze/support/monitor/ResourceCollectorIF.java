/*
 * ResourceCollectorIF.java
 *
 * Created on 2008年2月19日, 下午8:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.monitor;

/**
 *
 * @author happy
 */
public interface ResourceCollectorIF {
    public String getResourceName();
    public int[] collectorMsg();
}
