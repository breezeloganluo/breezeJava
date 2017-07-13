/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.framwork.netserver.process;

import com.breeze.base.log.Logger;
import com.breeze.framwork.databus.BreezeContext;
import com.breeze.framwork.netserver.workflow.WorkFlowUnit;
import com.breeze.framwork.netserver.workflow.WorkFlowUnitMgr;
import com.breeze.framwork.servicerg.AllServiceTemplate;
import com.breeze.framwork.servicerg.ServiceTemplate;
import com.breeze.support.tools.FileTools;
import com.breeze.support.tools.GsonTools;
import java.io.InputStream;
import java.util.*;

/**
 *
 * @author l00162771
 */
public class AutoMachineProcess extends ServerProcess {

    /**
     * 这仅仅是一个数据结构，不提供任何方法
     */
    public static class StatusData {
        
        public WorkFlowUnit action;
        
        //别名
        public String alias;
        //结果码到下一个状态的变化图，-1为默认结果
        public HashMap<Integer, Integer> alertMap;
    }
    
    
    private static Logger log = Logger.getLogger("com.breeze.framwork.netserver.process.AutoMachineProcess");
    public static int STATUS_INIT = 0;//初始状态值
    public static int STATUS_END = -1;//结束状态值
    public static int RESULT_DEFAULT = -1;//默认的结果值
    HashMap<Integer, StatusData> statusMap;

    public HashMap<Integer, StatusData> getStatusMap() {
        return statusMap;
    }
    LinkedHashSet<ServerProcess.WorkFlowUnitDesc> aSet;
    private String processName;
    
    private AutoMachineProcess() {
    }//对外不允许直接实例化

    /**
     * 创造形方法，将根据一个输入流创建本类 输入流是一个文本文件，其格式为：
     * srcStatus,actionName,resultCode,nextStatus
     *
     * @param in
     * @return
     */
    public static AutoMachineProcess createProcess(InputStream in,String cset) {
        String text = FileTools.readFile(in, cset);
        return createProcess(text);
    }

    /**
     * 过载函数，根据输入的字符串解析成一个完整的AutoMachineProcess对象 其输入的文本格式如下： 
     * {<br>
     *   name:"本processor的名称", <br>
     *   statusList:[<br>
     *      {<br>
     *         unit:{<br>
     *      	status:当前状态,<br>
     *      	alias:别名,用于在参数解析的时候，独立一个别名空间<br>
     *      	unitName:"处理单元的名称", <br>
     *      	actionResult:处理结果 <br>
     *      	nextStatus:下个状态 <br>
     *    		}<br>
     *      }<br>
     *   ] <br>
     * } <br>
     *  <br>
     *
     * @param text 输入的待解析的文本
     * @param unitMgr 这是一个为方便测试用的参数，这个参数实际情况下将会是WorkFlowUnit的静态对象
     * @return
     */
    public static AutoMachineProcess createProcess(String text) {
        Map<String,Object>r1 = GsonTools.parserJsonMapObj(text);

        if (r1 == null) {
            log.severe("parser error!:\n" + text);
            return null;
        }
        AutoMachineProcess result = new AutoMachineProcess();
        result.statusMap = new HashMap<Integer, StatusData>();
        result.aSet = new LinkedHashSet<ServerProcess.WorkFlowUnitDesc>();
        
        result.processName = r1.get("name").toString();
        Object[] statusList = (Object[])r1.get("statusList");
        if (statusList == null) {
            log.severe("parser list error!:\n" + text);
            return null;
        }
        for (int i = 0; i < statusList.length; i++) {           
//            Map<String, String> oneStatus = gson.fromJson(oneStr,
//                new com.google.gson.reflect.TypeToken<Map<String, String>>() {
//                }.getType());
            Map<String, String> oneStatus = (HashMap<String,String>)statusList[i];
            int status = Integer.parseInt(oneStatus.get("status"));
            String actionName = oneStatus.get("unitName");
            int resultCode = Integer.parseInt(oneStatus.get("actionResult"));
            int nextStatus = Integer.parseInt(oneStatus.get("nextStatus"));
            String alias = (String)oneStatus.get("alias");
            
            StatusData value = result.statusMap.get(status);
            if (value == null) {
                value = new StatusData();
                value.action = WorkFlowUnitMgr.INSTANCE.getUnit(actionName);
                if (value.action == null) {
                    log.severe("parser error action is null!" + actionName);
                    return null;
                }
                value.alertMap = new HashMap<Integer, Integer>();
                value.alias = alias;
                result.statusMap.put(status, value);
                result.aSet.add(new ServerProcess.WorkFlowUnitDesc(value.action,alias));
            }
            value.alertMap.put(resultCode, nextStatus);
        }
        return result;
    }

    
    
    static String[] getOneProcessInfo(String oneLine) {
        return oneLine.split(",");
    }
    
    @Override
    public void process(BreezeContext context,ServiceTemplate st) {
    	//2017-05-24罗光瑜增加service的使用数量统计
    	AllServiceTemplate.addStatic(st.getServiceName());
    	ServerProcessManager.addStatic(st.getServerName());
    	boolean isThrow = false;
    	String lastProcessName = null;
        try {
            int status = STATUS_INIT;//初始的状态为0
            int result = 0;
            while (true) {
                StatusData sd = this.statusMap.get(status);
                if (sd == null) {
                    log.severe("status:" + status + " not found!");
                    isThrow = true;
                    throw new RuntimeException("inner error!can not get status");
                }
                //获取函数执行
                WorkFlowUnit action = sd.action;
                if (action == null) {
                    log.severe("can not get action :");
                    isThrow = true;
                    throw new RuntimeException("inner error!");
                }
                ServiceTemplate usest = (sd.alias == null || "".equals(sd.alias)?st:st.getSub(sd.alias));
                
                log.fine("go processor unitName:"+action.getName());
                lastProcessName = action.getName();
                result = action.process(context,usest, sd.alias,result);
                log.fine("result from unit process is"+result);


                //下面进行结果判断
                Integer nextstatus = sd.alertMap.get(result);
                if (nextstatus == null) {
                    //没找到的状态说明是进入默认状态
                    nextstatus = sd.alertMap.get(RESULT_DEFAULT);
                    if (nextstatus == null){
                    	String msg = "wrong! result code " + result + " is not defind in the flow!";
                    	log.severe(msg);
                    	isThrow = true;
                    	throw new RuntimeException(msg);
                    }
                }
                status = nextstatus.intValue();
                //下面判断是否结束
                if (status == STATUS_END) {
                    return;
                }
            }
        } catch (Exception e) {
        	if (isThrow){
        		throw new RuntimeException("flow error in AutoMachineProcess.process method see the log msg to getDetail");
        	}else{
        		String sig = String.valueOf(System.currentTimeMillis());
        		log.severe("exception "+sig +" last process name is"+lastProcessName, e);
                throw new RuntimeException("inner process unit error catch and throw!see the log info <exception "+sig+">");
        	}
        }
    }

    
    
    @Override
    public ServerProcess.WorkFlowUnitDesc[] getAllWorkFlowUnit() {
    	ServerProcess.WorkFlowUnitDesc[] result = new ServerProcess.WorkFlowUnitDesc[this.aSet.size()];
        int i = 0;
        for (ServerProcess.WorkFlowUnitDesc w : this.aSet) {
            result[i++] = w;
        }
        return result;
    }
    
    @Override
    public String getProcessName() {
        return this.processName;
    }
}
