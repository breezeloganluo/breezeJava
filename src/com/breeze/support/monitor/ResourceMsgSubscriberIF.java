/*
 * ResourceMsgSubscriberIF.java
 *
 * Created on 2008年2月19日, 下午8:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.monitor;

/**
 *
 * @author happy
 */
public interface ResourceMsgSubscriberIF {
    public void beginSubscriberResource();
    public void endSubscriberResource();
    public void subscriberResource(String resourceName,int[] resourceValue);
}
