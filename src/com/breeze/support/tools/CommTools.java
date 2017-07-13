/*
 * CommTools.java
 *
 * Created on 2008年1月5日, 上午11:21
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.tools;
import java.util.*;
import java.io.*;
/**
 *
 * @author happy
 */
public class CommTools {
    
    /** Creates a new instance of CommTools */
    public CommTools() {
    }
    public static String getExceptionTrace(Exception e){
        ByteArrayOutputStream baout = new ByteArrayOutputStream();
        PrintStream bs = new PrintStream(baout);
        e.printStackTrace(bs);
        return new String(baout.toByteArray());
    }
    
    
    
    public static int ipStr2Int(String ip){
        //先切段分片
        int i=32;
        int resultIp = 0;
        StringTokenizer stk = new StringTokenizer(ip,".");
        while(stk.hasMoreElements()){          
            i-=8;
            String ipzone = stk.nextToken().trim();
            int ipIntZone = Integer.parseInt(ipzone);
            resultIp = resultIp | (ipIntZone << i);
        }
        return resultIp;
    }
    /**
     *按照比例分配
     *输入的是一个数组
     *第一是总数
     *后面是比例数
     *要求返回一个数组
     *第一个仍然是这个总数
     *后面的每个数仍然按照原来比例分配，但仅仅是总数相加等于第一个
     */
    public static void distributeInt(int arrayLen,int[] dv){
        //算法：采用补数法，先用浮点形计算出按比例分配的实际实数
        //然后取整，取整后计算，所算的总合差距多少，然后依据所差数据逐个补
        //补的原则是按照剩余小数部分的大小来进行优先补充
        //如果只有一个，那么就是100%分配，直接返回
        if (arrayLen == 2){
            dv[1]=dv[0];
            return;
        }
        //下面开始对非100%分配情况进行计算
        int [] sorcDv = new int[arrayLen];//记录原始的dv分量信息
        float[] calResult = new float[arrayLen -1];
        //求和
        int sum = 0;
        for (int i = 1;i<arrayLen;i++){
            sum += dv[i];
            sorcDv[i] = dv[i];
        }
        int sum2 = 0;
        //分摊计算,同时将新值覆盖进取作为参考结果
        for (int i = 1;i<arrayLen;i++){
            //计算新值
            calResult[i-1] = ((float)dv[i]/(float)sum)*(float)dv[0];
            //将计算得到的整数放入输入参数作为输出参数的参考
            dv[i] = (int)calResult[i-1];
            //再将剩余的小数部分放入到计算结果中
            calResult[i-1] = calResult[i-1] - dv[i];
            //再累计新的比例总数
            sum2 += dv[i];
        }
        //计算和目标总数相差多少，就是说明下面要补多少次
        int divCount = dv[0]-sum2;
        if (divCount < 0 || divCount > calResult.length){
            StringBuilder arrayStr = new StringBuilder("divCount < 0 or divCount > calResult.length,arr is:");
            for (int i=0;i<sorcDv.length;i++){
                arrayStr.append(sorcDv[i]).append(",");
            }
            throw new RuntimeException(arrayStr.toString());
        }
        
        for (int i=0;i<divCount;i++){
            //说明要补那么多次
            int maxIdx = -1;
            float max = -1f;
            for (int k=0;k<calResult.length;k++){
                //遍历查找最大的
                if (calResult[k]>max){
                    max = calResult[k];
                    maxIdx = k;
                }
            }
            //在序号最大的那个数上加1
            dv[maxIdx+1]++;
            //将这个序号变成最小
            calResult[maxIdx]=-1;
        }
    }
    
    /**
     *判断两个二维数组是否相等
     */
    public static boolean match2Array(Object[][]a,Object[][]b){
        if (a != null && b!= null){
            if (a.length != b.length){
                return false;
            }
            for (int i=0;i<a.length;i++){
                if (!match1Array(a[i],b[i])){
                    return false;
                }
            }
        }else if (b != null || a!= null){
            return false;
        }
        return true;
    }
    /**
     *判断两个二维整形的数组是否相等
     */
    public static boolean match2Array(int[][]a,int[][]b){
        if (a != null && b!= null){
            if (a.length != b.length){
                return false;
            }
            for (int i=0;i<a.length;i++){
                if (!match1Array(a[i],b[i])){
                    return false;
                }
            }
        }else if (b != null || a!= null){
            return false;
        }
        return true;
    }
    /**
     *判断两个一维数组是否相等
     */
    public static boolean match1Array(Object[]a,Object[]b){
        if (a != null && b!= null){
            if (a.length != b.length){
                return false;
            }
            for (int i=0;i<a.length;i++){
                if(!a[i].equals(b[i])){
                    return false;
                }
            }
        }else if (b != null || b!= null){
            return false;
        }
        return true;
    }
    /**
     *判断两个一维的整形数组是否相等
     */
    public static boolean match1Array(int[]a,int[]b){
        if (a != null && b!= null){
            if (a.length != b.length){
                return false;
            }
            for (int i=0;i<a.length;i++){
                if(a[i]!=b[i]){
                    return false;
                }
            }
        }else if (b != null || a!=null){
            return false;
        }
        return true;
    }
}
