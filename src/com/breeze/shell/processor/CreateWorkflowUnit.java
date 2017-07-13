/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.shell.processor;

import com.breeze.framwork.netserver.workflow.WorkFlowUnitMgr;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author l00162771
 */
public class CreateWorkflowUnit {

    public static void main(String[] args) throws Exception {
        StringBuilder sb = new StringBuilder("var workUnitData = [\n");
        boolean first = true;
        for (String key:WorkFlowUnitMgr.INSTANCE.getAllUnitKey()){
            if (first){
                first = false;
            }else{
               sb.append("    ,\n");
            }
            sb.append("    ").append("{innerHTML:'").append(key).append("',value:'").append(key).append("'}\n");
        }
        sb.append(']');
        String fullPath = "D:\\my resource\\myProgram\\10000100hui\\web\\creator\\buildProcess\\data\\WorkUnit.js";
        System.out.println(sb.toString());
        File f = new File(fullPath);
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        out.write(sb.toString().getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
