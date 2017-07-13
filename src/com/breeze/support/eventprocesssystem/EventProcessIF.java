//Source file: D:\\my resource\\myproject\\门户网\\src\\wwwlgy\\commspport\\supportif\\eventprocesssystem\\EventProcess.java

package com.breeze.support.eventprocesssystem;


/**
 * 事件处理器
 * 接受事件并进行处理,是一个抽象逻辑实现类
 */
public interface EventProcessIF {

    /**
   @roseuid 47B837ED03C8
     */
    public int doProcess(ProcessEventAbs event) throws Exception;
    
    public int getPriority();
}
