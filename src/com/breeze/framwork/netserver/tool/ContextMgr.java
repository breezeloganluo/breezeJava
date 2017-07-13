/*
 * 这是一个全局的上下文管理类，负责对上下文进行全局管理
 * 这个类采用了threadlocal方法，可以在任意地方进行调用
 */

package com.breeze.framwork.netserver.tool;
import com.breeze.framwork.databus.BreezeContext;
/**
 *
 * @author Administrator
 */
public class ContextMgr {
    private static ThreadLocal<BreezeContext> tl = new ThreadLocal<BreezeContext>();
    public static final BreezeContext global = new BreezeContext();
    public static void initRootContext(){
        BreezeContext root = new BreezeContext();
        tl.set(new BreezeContext());
    }
    
    public static BreezeContext getRootContext(){
        return tl.get();
    }
}
