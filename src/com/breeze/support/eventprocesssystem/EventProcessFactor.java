//Source file: D:\\my resource\\myproject\\门户网\\src\\wwwlgy\\commspport\\supportif\\eventprocesssystem\\EventProcessFactor.java

package com.breeze.support.eventprocesssystem;
import com.breeze.support.monitor.SystemMonitorMsgCollector;
import java.util.*;

/**
 * 整体管理类，没有处理逻辑，只负责初始化和返回对应的类实例
 */
public class EventProcessFactor {
    private static EventManager eventManager;
    private static ProcessManager theProcessManager;
    private static TimerTriger processTimer;
    
    
    /**
   @roseuid 47B841700242
     */
    public static void init(HashMap param,SystemMonitorMsgCollector mo) {
        theProcessManager = new ProcessManager();
        eventManager = new EventManager(theProcessManager);
        theProcessManager.setEventManager(eventManager);
        
        theProcessManager.init(param,mo);
        eventManager.init(param,mo);
        
        //下面初始化timer
        processTimer = new TimerTriger();
        processTimer.init();
    }
    
    /**
   @roseuid 47B841AD002E
     */
    public static EventManager getEventManager() {
        return eventManager ;
    }
    
    public static ProcessManager getProcess(){
        return theProcessManager;
    }
    
    public static TimerTriger getTimer(){
        return processTimer;
    }
}
