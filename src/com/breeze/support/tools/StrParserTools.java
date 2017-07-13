/*
 * StrParserTools.java
 *
 * Created on 2010年7月3日, 下午6:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.breeze.support.tools;
import java.util.regex.*;
/**
 *
 * @author happy
 */
public class StrParserTools {
    /**
     *将{[][]}{[][][]}...解析成一个二维的字符串数组
     */
    public static String[][] parserNormalStr(String s){
        String[][] tmpResult = new String[100][];//100个缓存，足够了
        int tmpResultLen = 0;
        Pattern p = Pattern.compile("\\{([^\\{\\}\\s]+)\\}");
        Matcher m = p.matcher(s);
        String [] midTmpResult = new String[100];//100个缓存，足够
        int midTmpResultLen = 0;
        while(m.find()){
            String tmpStr = m.group(1);
            Pattern pp = Pattern.compile("\\[([^\\[\\]]+)\\]");
            Matcher mm = pp.matcher(tmpStr);
            midTmpResultLen = 0;
            while(mm.find()){
                midTmpResult[midTmpResultLen++] = mm.group(1);
            }
            String[] midResult = new String[midTmpResultLen];
            System.arraycopy(midTmpResult,0,midResult,0,midTmpResultLen);
            tmpResult[tmpResultLen++] = midResult;
        }
        String[][] result= new String[tmpResultLen][];
        System.arraycopy(tmpResult,0,result,0,tmpResultLen);
        return result;
    }
}
