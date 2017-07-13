/** 
* @fileOverview FW配套使用的核心默认函数lang 
* @author <a href="http://www.wgfly.com">Logan</a> 
* @version 0.1
* @version 0.01 罗光瑜修改 getParameter函数
*/ 

/**
* @namespace
* @author Logan 
* @name moketest
* @description  FW的核心基本扩展 
*/ 
define(function(require, exports, module) {
	//用函数自身做域名承载对象
	var _result = function(fw){
		fw.use(_result);
	}
	
	
	
	_result.getDomain = function(){
		return "moketest";
	}
	
	
	
	FW && FW.use(_result);
	/**
	* @function
	* @memberOf moketest
	* @name getMokeData
	* @param {Object} app
	* @desctiption 远程去获取模拟数据，参数从threadSignal=xx来获取对应的数据对象
	*/
	_result.getMokeData = function(app){
		//获取名字
		var name = app.gadgetName;
		
		//获取方法名
		var funName = "";
		var method = FW.getCaller(1);
		for (var n in app){
			if (app[n] == method){
				funName = n;
			}
		}
		if (funName == ""){
			for (var n in app.private){
				if (app.private[n] == method){
					funName = n;
				}
			}
		}
		
		if (funName == ""){
			for (var n in app.FireEvent){
				if (app.FireEvent[n] == method){
					funName = n;
				}
			}
		}
		
		//获取方法参数
		var args = [];
		if (method.arguments && method.arguments.length > 0){
			for(var i=0;i<method.arguments.length;i++){
				if(/function/i.test(method.arguments[i])){
					continue;
				}
				args.push(method.arguments[i]);
			}
		}
		
	    //获取url文件
		var myurl = window.location.toString();
		var url = myurl.replace(/^http[s]?:\/\/[^\/]+/i, "");
		//去掉尾部?部分
		url = url.replace(/[#\?][\s\S]*$/i, "");
		//去掉应用名部分
		if (/undefined/.test(typeof(Cfg))){
			alert("Cfg.baseUrl必须被定义，在/config/config.jsp中定义按照js方式加载");
			return;
		}
		if (!/undefined/.test(typeof(Cfg)) && url.indexOf(Cfg.baseUrl) == 0) {
			url = url.substr(Cfg.baseUrl.length);
		}else{
			url = url.replace(/^.*(\/page\/)/i,function(a,b,c,d,e){
				return b;
			})
		}
		//变成可用符号
		url=("/"+url).replace(/[\\\/]+/ig,"_");
		
		var result = FW.doServer("getMokeData","moke",{
			url:url,
			gadgetName:name,
			funName:funName,
			input:args
		},null,null,Cfg.baseUrl+"/breeze/framework/jsp/BreezeFW.jsp");
		!/undefined/i.test(typeof(console)) && console.log(result);
		if (result.code != 0 || result.data == null){
			return{};
		}
		return result.data;
	}

	return _result;
});