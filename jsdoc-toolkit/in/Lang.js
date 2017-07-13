/** 
* @fileOverview FW配套使用的核心默认函数lang 
* @author <a href="http://www.wgfly.com">Logan</a> 
* @version 0.1
*/ 

/**
* @namespace
* @author Logan 
* @name lang
* @description  FW的核心基本扩展 
*/ 
define(function(require, exports, module) {
	//用函数自身做域名承载对象
	//这样在外部使用的时候，可以简化比如use("xx/x/xx")(FW);
	var _result = function(fw){
		fw.use(_result);
	}
	
	_result.getDomain = function(){
		return "lang";
	}
	
	var APICtr = {
		JsonAPI:require("./lib/json"),
		BreezeTemplate:require("./lib/BreezeTemplate"),
		FormAPI:require("./lib/FormOper")
	}
	// alert(APICtr.JsonAPI.toJSONString({name:"alec"}));
	/**
	 * @function
	 * @memberOf lang
	 * @name getParameter
	 * @description 从url中获取参数
	 * @param {String}pName 参数名
	 * @returns 参数名对应的参数值
	 */
	_result.getParameter=function(__pName){
		var url = window.location.toString();
		var execResult = (new RegExp(__pName+"=([\\w%\\.]+)")).exec(url);
		if (execResult){
			return execResult[1];
		}
		return null;
	};
	
	/**
	 * @function
	 * @memberOf lang
	 * @name save
	 * @desctiption 本地存储,存
	 * @param {String} name 存储名称
	 * @param {Object} data 被存储的对象，如果为null，则是把name删除掉
	 * @param {boolean} [isPermanent] 是否永久性存储，true是永久存储，false或默认不填是会话存储
	 * @see load
	 * @example
	 * FW.use().save("abc",{a:'hello'});//存储
	 * FW.use().save("abc",null);//删除
	 * 
	 */
	_result.save=function(name,data,isPermanent){
		if(!window.localStorage || !window.sessionStorage ){
			seajs.log("browser not supprt window.loalStorage!");
			return;
		}
		var storage = (isPermanent)?localStorage:sessionStorage;
		if (data){
			storage.setItem(name,APICtr.JsonAPI.toJSONString(data));					
		}else{
			//如果是null，表示删除
			storage.removeItem(name);
		}
	};

	/**
	 * @function
	 * @memberOf lang
	 * @name load
	 * @desctiption 本地存储，取
	 * @param {String} name 存储名称
	 * @param {boolean} [isPermanent] 是否永久性存储，true是永久存储，false或默认不填是会话存储
	 * @return 返回本地存储name对应的值
	 * @see save
	 * @example
	 * FW.use().load("abc");
	 * 
	 */
	_result.load=function(name,isPermanent){
		if(!window.localStorage || !window.sessionStorage ){
			seajs.log("browser not supprt window.loalStorage!");
			return null;
		}
		var storage = (isPermanent)?localStorage:sessionStorage;
		var resultJson = storage.getItem(name);
		return eval("("+resultJson+")");
	};
	
	/**
	 * @function
	 * @memberOf lang
	 * @name parserTemplate
	 * @desctiption 模板解析函数
	 * @param {String} src 模板名称
	 * @param {Object} data 用于模板的数据对象
	 * @param {Object} useAPI 在模板中，可用于xx:xx模式的api对象，例如{a:{f:function(){}}}则在模板中使用${a:f()}调用
	 * @return 模板解析后的值
	 */
	_result.parserTemplate = function(__src,__data,_useAPI){
		return APICtr.BreezeTemplate.parserTemplate(__src,__data,_useAPI);
	}

	/**
	 * @function
	 * @memberOf lang
	 * @name evalJSON
	 * @desctiption 将Json转换函数
	 * @param {String} strJson json字符串
	 * @return 返回object,array,string等对象
	 * @example
	 * FW.use().evalJSON("{name:'alec'}");
	 */
	_result.evalJSON = function(strJson){
		return APICtr.JsonAPI.evalJSON(strJson);
	}

	/**
	 * @function
	 * @memberOf lang
	 * @name toJSONString
	 * @desctiption 将javascript数据类型转换为json字符串的方法
	 * @param {object} object 需转换为json字符串的对象, 一般为Json 【支持object,array,string,function,number,boolean,regexp *】
	 * @return 返回json字符串
	 * @example
	 * FW.use().toJSONString({name:'alec'});
	 */
	_result.toJSONString = function(object){
		return APICtr.JsonAPI.toJSONString(object);
	}
	
	/**
	 * @function
	 * @memberOf lang
	 * @name createForm
	 * @desctiption 使用对象描述符，创建表单
	 * @param {Object}desc 对象描述符 
	 * @param {JqueryContainer}dom 加载生成的表单，挂接到dom节点
	 * @param {Object}data 对象实际的值，可以为空
	 */
	_result.createForm = function(__desc,__dom,__data){
		APICtr.FormAPI.createFormByObjDesc(__desc,__dom,__data);
	}

	/**
	 * @function
	 * @memberOf lang
	 * @name createFormList
	 * @desctiption 使用对象描述符，创建表单列表页
	 * @param {Object}desc 对象描述符 
	 * @param {JqueryContainer}dom 加载生成的表单，挂接到dom节点
	 * @param {Object}data 对象实际的值，可以为空
	 */
	_result.createFormList = function(__desc,__dom,__data){
		APICtr.FormAPI.createFormListByObjDesc(__desc,__dom,__data);
	}
	_result.bindFormList = function(desc, dom, countService, countParam, service, formListFun){
		// this.FW.blockUI("<a href=''>alec</a>",0,0,400,400,0);
	}
	
	/**
	 * @function
	 * @memberOf lang
	 * @name lang
	 * @desctiption 使用语言信息格式化要显示的内容，如果要显示的内容对象包含了多语信息，则显示成多语言，否则直接显示成字符串
	 * @param {String}_displayObj 对象描述符 
	 */
	_result.lang = function(__displayObj){
		try{
			var langObj = eval("("+__displayObj+")");
			if (langObj[Cfg.lang]){
				return langObj[cfg.lang];
			}
			return __displayObj;
		}catch(e){
			return __displayObj;
		}
	}

	return _result;
});