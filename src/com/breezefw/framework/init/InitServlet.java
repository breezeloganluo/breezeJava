package com.breezefw.framework.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;

import com.breeze.init.InitSorter;
import com.breeze.init.Initable;
import com.breeze.init.LoadClasses;




public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = -1532804494780940455L;
	public HashMap<String,String> cfgMap;

	public void init(ServletConfig conf) {
		try{
			this.cfgMap = new HashMap<String,String>();
			String dir = conf.getServletContext().getRealPath("/");
			char lastChar =dir.charAt(dir.length()-1); 
			if (lastChar != '/' && lastChar !='\\'){
				dir+='/';
			}
			System.out.println("[[new breeze init in]]" + dir);
			this.cfgMap.put("BaseDir", dir);
			
			
			System.out.println("[[[biegin version 1.10]]]");
			 
			//获取所有的初始化资源
			ArrayList<Initable> initList = LoadClasses.createObject("com.breezefw.framework.init.service", Initable.class);
			
			initList.addAll(LoadClasses.createObject("com.breezefw.service", Initable.class));
			
			//排序
			ArrayList<InitSorter> is = new ArrayList<InitSorter>();
			
			for (Initable i:initList){
				is.add(new InitSorter(i));
			}
			Collections.sort(is);
			System.out.println("init total is:" + is.size());
			//按顺序初始化
			for (int i=0;i<is.size();i++){
				Initable initable = is.get(i).initer;
				System.out.println("step " + (i+1) + " inti " + initable.getInitName());
				initable.doInit(cfgMap);
				System.out.println("inti " + initable.getInitName() + " complete!");
			}
			System.out.println(" System inti complete! now begin...");
		}catch(Exception e){
			System.out.println("init fail!---");
			e.printStackTrace();
		}
	}
}
