/**
* @namespace
* @name javalv2 
* @version 0.01 罗光瑜 初始版本设计
0.02 罗光瑜 解决函数注释类parserJavaClassFunctionComment的一个注释换行就解析失败的bug
0.03 罗光瑜 parserJavaClassFunctionBodyCode判断for循环后,content写成contetn
0.04 罗光瑜 parserJavaClassFunctionBody函数判断分支不要用“如果”关键词
0.05 罗光瑜 parserJavaClassFunction函数增加调试日志输出，好歹知道解析错在哪
0.06 罗光瑜 parserJavaClassVar也增加支出复杂的hashMap<d,f>类型，把处理这个类型的函数processCVar从parserJavaClassFunction中抽出来做成私有函数
0.07 罗光瑜 处理构造函数出问题了parserJavaClassFunction
0.08 罗光瑜 parserJavaClassFunction抛异常throw处理有问题，当只有一个异常时加入不进去
0.09 罗光瑜 解决注释类参数换行失败
0.10 罗光瑜 parserJavaClassFunctionBodyNormal部分如果代码是空，则不要加入
* @description  第二层的类解析器，这层解析就要解析出结构化的java结构对象，这里直接解析是不行的要利用词法分析和分段解析，分别是lex类和javalv1类进行解析，中间结果后进行处理
*这个类解析器目标是能够通过函数将java类解析如下的结构：
*{
*	package:"com.breeze.abc.com",
*	version:[{
*                version: "1.0",
*                date:"2016-01-31",
*		author: "lgy",
*		description: "xxx"
*		
*	}],
*	comments: "desc ",
*	name: "MyClass",	
*	include:[{
*		vDir: "com.breeze.service.sample"
*	}],
*	extends:[
*		"MyGadget"
*	],
*	implements:["abc"],
*	attributeFragment:[{
*		comments: "描述内容",
*		type: "public",
*		prifix: "static final",
*		attType:"String"
*		name: "newName",
*		content: ""albb"",
*		
*	}],
*	functionFragment:[
*		{
*			comments:{
*				description:"描述内容",
*				param:{
*					"参数1":参数说明
*				},
*				example:"样例"
*				return:"返回值说明"
*			}, 
*			name:"",
*			parameters:[
*				{
*				   type:"",
*				   name:""
*				}
*			],
*			type: "public",
*			prifix: "static final",
*                        throws:"Exception",
*			fragments:[
*				{
*					type: "block",//类型有normal,block,cycle,branchList其中block类型是没有code的,
*					command:"注释内容，block通常是block(xx){xxxx}",
*                                        subCommand:[
*                                            type:"++/--",
*                                            content:"注释内容"
*                                        ],
*					subList:[
*						{
*							type:"normal",
*							command:"注释内容可以没有",
*							code:"sss"
*						}
*					],
*					
*				},
*				{
*					type:"cycle",
*					command:"注释内容,通常的语法是for (xxx)",
*					code:"for(){xxx",
*					subList:[
*						{
*							type:"normal",
*							command:"注释内容可以没有",
*							code:"sss"
*						}
*					]
*				},
*				{
*					type:"branchList",//branchList是没注释，没code没command，只有subList，而且subList只能用branchBlock
*					subList:[
*						{
*							type:"branchBlock",
*							command:"分支的注释",
*							code:"分支部分的语句",
*							subList:[
*							  ...
*							]
*						}
*					]
*				}
*			]
*		}
*	]
*}                                                     
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("./javalv1");
    FW.register({
        "name": "javalv2",
        /**
        *@function
        *@memberOf javalv2
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf javalv2
            *@name public$parserJava
            *@description [功能]解析分解java文本形成一个结构化的对象
            *[思路]首先经过javalv1的一层解析，然后对这个结果，按照结果结构，逐个调用对应的解析私有函数实现
            *[接口.this.MY.lv1Struct]这里是经过lv1层解析好的java符号结构数据
            *@param javatext 输入的java结构
            *@return {
            package:"com.breeze.abc.com",
            version:[{
            author: "lgy",
            description: "xxx",
            version: "1.0"
            }],
            comments: "desc ",
            name: "MyClass",
            include:[{
            vDir: "com.breeze.service.sample"
            }],
            extends:[
            "MyGadget"
            ],
            implements:["abc"],
            attributeFragment:{
            comments: "描述内容",
            type: "public",
            prifix: "static final",
            attType:"String"
            name: "newName",
            content: ""albb"",
            
            },
            functionFragment:{
            "+函数名":{
            comments:{
            description:"描述内容",
            param:{
            "参数1":参数说明
            },
            example:"样例"
            return:"返回值说明"
            },
            name:"",
            parameters:[
            {
            type:"",
            name:""
            }
            ],
            type: "public",
            prifix: "static final",
            fragments:[
            {
            type: "block",//类型有normal,block,cycle,branchList其中block类型是没有code的,
            command:"注释内容，block通常是block(xx){xxxx}",
            subList:[
            {
            type:"normal",
            command:"注释内容可以没有",
            code:"sss"
            }
            ],
            
            },
            {
            type:"cycle",
            command:"注释内容,通常的语法是for (xxx)",
            code:"for(){xxx",
            subList:[
            {
            type:"normal",
            command:"注释内容可以没有",
            code:"sss"
            }
            ]
            },
            {
            type:"branchList",//branchList是没注释，没code没command，只有subList，而且subList只能用branchBlock
            subList:[
            {
            type:"branchBlock",
            command:"分支的注释",
            code:"分支部分的语句",
            subList:[
            ...
            ]
            }
            ]
            }
            ]
            }
            }
            }
            */
            "parserJava": function(javatext) {
                //声明结果变量
                var result = {};
                //创建lv1解析类，并执行解析获取lv1结果
                //--下面操作全部针对lv1结果进行处理
                //--如果返回null那么要返回null，这说明解析失败
                var lv1 = FW.createApp("javalv1", "javalv1", this);
                if (!lv1) {
                    FW.alert('lv1初始化失败');
                }
                this.MY.lv1Struct = lv1.parser(javatext);
                if (!this.MY.lv1Struct) {
                    FW.alert('解析java文本出错');
                }
                console.log("begin parser class");
                //while(循环lv1元素){分别判断和解析
                for (var i = 0; i < this.MY.lv1Struct.length; ++i) {
                    //获取其中一个结构
                    var item = this.MY.lv1Struct[i];
                    //if(package情况){调用私有函数解析
                    if (item.type == 'package') {
                        //调用私有函数处理
                        //--调用parserJavaPackage
                        //--还要将调用结果放入结果中
                        result.package = this.API.private('parserJavaPackage', item);
                    }
                    //}
                    //else if(如果是lv1的note类型){调用私有函数解析这个类型
                    else if (item.type == 'note') {
                        //调用私有函数解析
                        //--调用的是parserJavaClassNote
                        var rd = this.API.private('parserJavaClassNote', item);
                        //--这个步骤完成两个类型的解析，一个是version，一个是comments
                        //--调用完后，如果不为空就把调用结果放到总结果中
                        if (rd == null) {
                            continue;
                        }
                        result.version = rd.version || null;
                        result.comments = rd.comments || null;
                        result.design = rd.design || null;
                        result.unittest = rd.unittest || null;
                    }
                    //}
                    //else if(如果是import的情况){处理import
                    else if (item.type == 'import') {
                        //调用私有函数处理
                        //--调用parserJavaImport函数
                        var rd = this.API.private('parserJavaImport', item);
                        //--处理完后，要加入结果中，注意结果是一个数组，加入这个数组
                        //--放入总结果中的include成员中
                        if (!result.include) {
                            result.include = [];
                        }
                        result.include.push(rd);
                    }
                    //}
                    //else if(如果是class的情况){调用私有函数处理class的情况
                    else if (item.type == 'class') {
                        //调用私有函数进行解析
                        //--调用parserJavaClass函数
                        var rd = this.API.private('parserJavaClass', item);
                        //--调用往后，将结果放入到总结果中
                        //--这个函数将处理掉和类名，前缀，成员变量，所有函数等结构
                        result.name = rd.name || null;
                        result.extends = rd.extends || null;
                        result.implements = rd.implements || null;
                        result.attributeFragment = rd.attributeFragment || null;
                        result.functionFragment = rd.functionFragment || null;
                    }
                    //}
                }
                //}
                //返回结果
                return result;
                return {
                    msg: "这是临时测试，正式代码要体会此"
                }
            },
            /**
            *@function
            *@memberOf javalv2
            *@name public$parserJavaFun
            *@description [功能]重新解析某个函数内容，注意只解析函数体，不解析其他
            *[思路]把函数补全，然后当做完整对象解析，解析完后再获取其中的对象返回
            *@param code 函数对象
            *@return 函数对象
            */
            "parserJavaFun": function(code) {
                //补全函数文本
                var all = "public class tmp {\n";
                all += code;
                all += "\n}";
                //解析对象
                var javaStruct = this.parserJava(all);
                var funStruct = javaStruct.functionFragment[0];
                //返回
                return funStruct;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name public$parserJavaFunFragment
            *@description [功能]将java代码片段转换成对象
            *[思路]将代码补齐后处理
            *@param code 代码片段
            *@return 函数代码片段
            */
            "parserJavaFunFragment": function(code) {
                //补全函数文本
                var all = "public class tmp {\n";
                all += "void f(){\n";
                all += code;
                all += ("\n}");
                all += "\n}";
                //解析对象
                var javaStruct = this.parserJava(all);
                var funStruct = javaStruct.functionFragment[0].fragments;
                //返回
                return funStruct;
            }
        },
        "private": {
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaImport
            *@description [功能]解析import部分
            *[思路]根据输入输出的结构进行类型转换即可
            *@param lv1Struct lv1传入的import部分的结构
            {
            type:"import"
            content:[],这里是这个类型的完整的详细内容，其内容是由词法分析返回的结果的数组，比如import com.go;词法分析将得到import com  .   go   ;这5个元素
            }
            *@return import的字符串"java.util.*"
            */
            "parserJavaImport": function(lv1Struct) {
                var result = "";
                for (var i = 0; i < lv1Struct.content.length; ++i) {
                    var v = lv1Struct.content[i].content;
                    if (v == ';') {
                        break;
                    }
                    result += v;
                }
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClass
            *@description [功能]这里要将java的类体的结构，根据lv1的结构全部解析出来
            *[思路]这里分两个阶段，主函数只解析主内容，下面子类容，再调用对应的子函数进行解析
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param lv1Struct 对于java的结构
            {
            type:"class",其中note表示注释
            content:[],这里是这个类型的完整的详细内容，其内容是由词法分析返回的结果的数组，比如import com.go;词法分析将得到import com  .   go   ;这5个元素
            children:[//只有type为class才会有，就是类里面的详细内容了
            {
            type:"var/function/note/@"
            content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容
            children:[]//如果是函数或者是变量，那么就是{}之间的内容
            }
            ]
            }
            *@return {
            name: "MyClass",
            extends:[
            "MyGadget"
            ],
            implements:["abc"],
            attributeFragment:{
            参数部分
            },
            functionFragment:{
            函数部分
            }
            }
            }
            */
            "parserJavaClass": function(lv1Struct) {
                //声明并初始化结果对象
                var result = {};
                result.attributeFragment = [];
                result.functionFragment = [];
                var item = {};
                //block(块){解析类的基本信息
                //--在输入参数的content数组中解析
                //--完成基本类的name，extends，implements，的解析
                //--提示，这里可以参考parserJavaClassFunction的函数名解析的那段代码，也是简单的语法分析过程
                //--要将类名记入到this.MY.className中，便于后面判断是否构造函数
                result.name = "";
                result.extends = [];
                result.implements = [];
                for (var i = 0; i < lv1Struct.content.length; ++i) {
                    item = lv1Struct.content[i].content;
                    if (item == 'class' && i + 1 < lv1Struct.content.length) {
                        result.name = lv1Struct.content[i + 1].content;
                        this.MY.className = result.name;
                    } else if (item == 'extends' && i + 1 < lv1Struct.content.length) {
                        result.extends.push(lv1Struct.content[i + 1].content);
                    } else if (item == 'implenments') {
                        var douhao = 0;
                        for (var j = i + 1; j < lv1Struct.content.length; ++j) {
                            var v = lv1Struct.content[j].content;
                            if (v == ',') {
                                douhao++;
                            } else {
                                douhao--;
                                if (douhao < -1) {
                                    break;
                                }
                                result.implements.push(v);
                            }
                        }
                    }
                }
                //}
                //block(块){解析子部分
                //--解析参数和函数部分
                //while(所有儿子){分开函数和属性进行处理
                for (var i = 0; i < lv1Struct.children.length; ++i) {
                    //--这里约定注释一定是某个函数或者属性前面的
                    //取其中一个
                    item = lv1Struct.children[i];
                    //if(是属性情况及var的情况){跳到var私有函数去处理
                    if (item.type == 'var') {
                        //调用私有的var处理函数处理
                        //--处理var要把var的注释也处理了
                        //--因此调用的函数传入的不仅仅是var部分的，还要包括整个数组
                        //--调用的函数名是parserJavaClassVar
                        var rd = this.API.private('parserJavaClassVar', lv1Struct.children, i);
                        //--调用完后，将结果写入到总结果中
                        result.attributeFragment.push(rd);
                    }
                    //}
                    //else if(是函数情况){调用私有的函数解析函数解析
                    else if (item.type == 'function') {
                        //调用私有函数解析
                        //--调用的函数名是parserJavaClassFunction
                        var rd = this.API.private('parserJavaClassFunction', lv1Struct.children, i);
                        //--调用完后将调用的结果放入到总体结果中
                        result.functionFragment.push(rd);
                    }
                    //}
                }
                //}
                //}
                //返回结果
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassVar
            *@description [功能]处理一个类里面var部分的解析，返回解析后的结果
            *[思路]var部分比较简单，直接硬解析就可以，不过在做var时要先判断之前那个节点是不是注释，如果是注释要调用私有的var注释内容进行注释处理。注意注释可以接受N和n类型，反正就是一句话，把两端的*号去掉即可
            *@param lv1Array 整个class内部的大数组：
            [//只有type为class才会有，就是类里面的详细内容了
            {
            type:"var/function/note/@"
            content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容
            children:[]//如果是函数或者是变量，那么就是{}之间的内容
            }
            ]
            *@param idx 当前这个要解析的这个var在数组的索引位置，注意：注释是当前数组减一，看是否类型正确
            *@return {
            comments: "描述内容",
            type: "public",
            prifix: "static final",
            attType:"String"
            name: "newName",
            content: ""albb"",
            
            }
            */
            "parserJavaClassVar": function(lv1Array, idx) {
                //声明和初始化结果变量
                var result = {};
                var item = lv1Array[idx];
                //校验，如果当前的类型不对就返回
                if (item.type != 'var' && item.type != 'note') {
                    return result;
                }
                //if(前面一个对象){调用注释私有函数处理
                if (idx > 0 && (lv1Array[idx - 1].type == "note" || lv1Array[idx - 1].type == "@")) {
                    //调用私有方法解析注释
                    //--调用方法名是parserJavaClassFunctionNote
                    //--调用完后，将调用结果放入到总结果中
                    if (lv1Array[idx - 1].type == "note") {
                        result.comments = this.API.private('parserJavaClassVarNote', lv1Array[idx - 1]);
                        if (idx > 1 && lv1Array[idx - 2].type == "@") {
                            result["@"] = lv1Array[idx - 2].content[0].content
                        }
                    } else {
                        result["@"] = lv1Array[idx - 1].content[0].content;
                        if (idx > 1 && lv1Array[idx - 2].type == "note") {
                            result.comments = this.API.private('parserJavaClassVarNote', lv1Array[idx - 2]);
                        }
                    }
                }
                //}
                //block(块){解析变量部分代码
                result.type = 'public';
                result.prifix = '';
                result.attType = 'String';
                result.name = 'newVariable';
                result.content = 'This is a TEMP var';
                //初始化状态和准备变量
                var declearArray = lv1Array[idx].content;
                var declearIdx = 0;
                var declearStatus = "type";
                var tmpOne = null;
                var tmpStr = "";
                //while(遍历所有的lv1结构){进行解析
                while (true) {
                    //获取当前内容，初始化临时变量
                    if (declearIdx >= declearArray.length || declearStatus == "end") {
                        break;
                    }
                    var one = declearArray[declearIdx].content;
                    //if(type状态){处理type状态代码
                    if (declearStatus == "type") {
                        //处理type状态
                        //--只处理public，private等其他直接跳入下一状态
                        if (/^\s*(public|private|protected)\s*$/.test(one)) {
                            result.type = one;
                            declearStatus = "prifix";
                            declearIdx++;
                        } else {
                            declearStatus = "prifix";
                        }
                    }
                    //}
                    //else if(当前是prifix状态){处理prifix内容
                    else if (declearStatus == "prifix") {
                        //处理prifix
                        //--接受static，final等，如果不是就进入name状态了
                        if (/^\s*(static|final|synchronized)\s*$/.test(one)) {
                            tmpStr = tmpStr + " " + one;
                            declearIdx++;
                        } else {
                            result.prifix = tmpStr;
                            tmpStr = "";
                            declearStatus = "attType";
                        }
                    }
                    //}
                    //else if(当前是attType状态){处理attType内容
                    else if (declearStatus == "attType") {
                        //处理attType
                        result.attType = "";
                        if (one != null) {
                            var returnTypeObj = this.API.private("processCVar", declearArray, declearIdx);
                            result.attType = returnTypeObj.vardeclear;
                            declearStatus = "name";
                            declearIdx = returnTypeObj.idx;
                        } else {
                            console.error("one is null");
                        }
                    }
                    //}
                    //else if(处理name状态){处理name状态
                    else if (declearStatus == "name") {
                        //处理name状态
                        //--这个函数用于处理变量名
                        result.name = one;
                        declearIdx++;
                        var nextOne = declearArray[declearIdx].content;
                        if (nextOne == ';') {
                            result.content = "";
                            break;
                        }
                        if (nextOne != '=') {
                            console.error("解析变量的name出错了，碰到应该是=但没找到");
                            return null;
                        }
                        declearStatus = "content";
                        declearIdx++;
                    }
                    //}
                    //else if(处理content状态){处理content状态
                    else if (declearStatus == "content") {
                        //处理变量值
                        //--如果最后一个字符是普通字母，那么要空格
                        if (/\w$/.test(tmpStr)) {
                            tmpStr += " ";
                        }
                        tmpStr = tmpStr + one;
                        declearIdx++;
                        if (one == ";") {
                            result.content = tmpStr.replace(/;\s*$/, "");
                            break;
                        }
                    }
                    //}
                }
                //}
                //}
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassVarNote
            *@description [功能]解析java的变量部分的注释功能
            *[思路]硬解析即可，兼容n和N两种类型的注释N/n只的是词法分析的注释类型
            *@param lv1Struct lve结构的注释数组
            {
            type:"var/function/note/@"
            content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容
            }
            *@return 注释结果字符串
            */
            "parserJavaClassVarNote": function(lv1Struct) {
                var result = "";
                var content = lv1Struct.content;
                for (var i = 0; i < content.length; ++i) {
                    var vArr = content[i].content.split(/[\n\r]/);
                    for (var j = 0; j < vArr.length; j++) {
                        var v = vArr[j].replace(/^\s*\**\s*/, "");
                        if (v != "") {
                            v += "\r\n";
                        }
                        result += v;
                    }
                }
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunction
            *@description [功能]函数部分解析，包括函数的注释解析，所以这里的输入参数不能是单个函数自身的lv1部分，而是整个数组
            *[思路]这里要把整个lv1数组拿到，根据索引解析函数本身，函数本身的解析又是一个语法分析过程，这个参见伪代码。而另外也需要判断索引前面一个是否是注释，如果是注释，那么要完成注释部分的编码。
            *这个代码以及这个代码被调用部分的代码都不用写，我已经完成。
            *@param lv1Array [//只有type为class才会有，就是类里面的详细内容了
            {
            type:"var/function/note/@"
            content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容
            children:[]//如果是函数或者是变量，那么就是{}之间的内容
            }
            ]
            *@param idx 索引
            *@return 返回函数的整体结构：
            {
            comments:{
            description:"描述内容",
            param:{
            "参数1":参数说明
            },
            example:"样例"
            return:"返回值说明"
            },
            name:"",
            parameters:[
            {
            type:"",
            name:""
            }
            ],
            type: "public",
            prifix: "static final",
            fragments:[
            {
            type: "block",//类型有normal,block,cycle,branchList其中block类型是没有code的,
            command:"注释内容，block通常是block(xx){xxxx}",
            subList:[
            {
            type:"normal",
            command:"注释内容可以没有",
            code:"sss"
            }
            ],
            
            },
            {
            type:"cycle",
            command:"注释内容,通常的语法是for (xxx)",
            code:"for(){xxx",
            subList:[
            {
            type:"normal",
            command:"注释内容可以没有",
            code:"sss"
            }
            ]
            },
            {
            type:"branchList",//branchList是没注释，没code没command，只有subList，而且subList只能用branchBlock
            subList:[
            {
            type:"branchBlock",
            command:"分支的注释",
            code:"分支部分的语句",
            subList:[
            ...
            ]
            }
            ]
            }
            ]
            }
            }
            */
            "parserJavaClassFunction": function(lv1Array, idx) {
                //初始化结果变量
                var result = {
                    parameters: [],
                    type: "default",
                    fragments: []
                };
                var curFunLv1Struct = lv1Array[idx];
                if (curFunLv1Struct == null || curFunLv1Struct.type != "function") {
                    return null;
                }
                //if(上一个索引是注释或注解){调用注释函数处理注释内容
                if (idx > 0 && (lv1Array[idx - 1].type == "note" || lv1Array[idx - 1].type == "@")) {
                    //调用私有方法解析注释
                    //--调用方法名是parserJavaClassFunctionNote
                    //--调用完后，将调用结果放入到总结果中
                    if (lv1Array[idx - 1].type == "note") {
                        result.comments = this.API.private('parserJavaClassFunctionComment', lv1Array[idx - 1].content[0].content);
                        if (idx > 1 && lv1Array[idx - 2].type == "@") {
                            result["@"] = lv1Array[idx - 2].content[0].content
                        }
                    } else {
                        result["@"] = lv1Array[idx - 1].content[0].content;
                        if (idx > 1 && lv1Array[idx - 2].type == "note") {
                            result.comments = this.API.private('parserJavaClassFunctionComment', lv1Array[idx - 2].content[0].content);
                        }
                    }
                }
                //}
                //block(块){函数声明部分解析
                //--也是采用自动机方式，注意，这个状态跳转是单线的，所以相对简单
                //--状态:type,prifix,return,name,var,throws
                //--type就是public，private等
                //--prifix就是static,final等
                //--return 就是返回类型
                //--name就是函数名
                //--var就是读入(开始函数参数解析
                //--throws就是开始异常处理
                //--状态机状态如果发生跳转，那么索引停留在下个要处理状态，每个状态只处理自己的内容
                //--返回的是{vardeclear:"声明字符串",idx:解析完后的下一个索引值}
                //--处理类型<xxx,xx>或者类型[]的内部函数
                //初始化状态和准备变量
                var declearArray = lv1Array[idx].content;
                var declearIdx = 0;
                var declearStatus = "type";
                var tmpOne = null;
                var tmpStr = "";
                //while(遍历所有的lv1结构){进行解析
                while (true) {
                    //获取当前内容，初始化临时变量
                    if (declearIdx >= declearArray.length || declearStatus == "end") {
                        break;
                    }
                    var one = declearArray[declearIdx];
                    //if(type状态){处理type状态代码
                    if (declearStatus == "type") {
                        //处理type状态
                        //--只处理public，private等其他直接跳入下一状态
                        //--要判断函数名和类名是否一致，一致就是构造函数，直接跳入name状态
                        if (one.type == 'l' && /^\s*(public|private|protected)\s*$/.test(one.content)) {
                            result.type = one.content;
                            declearStatus = "prifix";
                            declearIdx++;
                        } else {
                            declearStatus = "prifix";
                        }
                        //处理构造函数的情况
                        if (declearArray[declearIdx].content == this.MY.className) {
                            declearStatus = "name";
                        }
                    }
                    //}
                    //else if(当前是prifix状态){处理prifix内容
                    else if (declearStatus == "prifix") {
                        //处理prifix
                        //--接受static，final等，如果不是就进入name状态了
                        if (one.type == 'l' && /^\s*(static|final|synchronized)\s*$/.test(one.content)) {
                            tmpStr = tmpStr + " " + one.content;
                            declearIdx++;
                        } else {
                            result.prifix = tmpStr;
                            tmpStr = "";
                            declearStatus = "return";
                        }
                    }
                    //}
                    //else if(return状态处理){处理return状态
                    else if (declearStatus == "return") {
                        //处理return状态
                        //--这个状态是处理函数返回值声明部分，主要是Map<String>情况会复杂
                        if (one.type == "l") {
                            var returnTypeObj = this.API.private("processCVar", declearArray, declearIdx);
                            declearIdx = returnTypeObj.idx;
                            result["return"] = returnTypeObj.vardeclear;
                            declearStatus = "name";
                        } else {
                            console.log("解析函数体的return出错了");
                            return null;
                        }
                    }
                    //}
                    //else if(处理name状态){处理name状态
                    else if (declearStatus == "name") {
                        //处理name状态
                        //--这个函数用于处理函数的函数名
                        if (one.type == "l") {
                            result.name = one.content;
                            declearIdx++;
                            if (declearArray[declearIdx].type != 'b' || declearArray[declearIdx].content != '(') {
                                console.error("解析函数体的name后面出错了，碰到应该是(但没找到");
                                return null;
                            }
                            declearStatus = "var";
                        } else {
                            console.error("解析函数体的name出错了，碰到非字符");
                            return null;
                        }
                    }
                    //}
                    //else if(var){处理beginvar
                    else if (declearStatus == "var") {
                        //处理var
                        //--这个部分处理函数的参数声明，例如(String a,int[] b)
                        if (one.type == "b" && one.content == "(") {
                            tmpOne = {};
                            declearIdx++;
                        } else if (one.type == "b" && one.content == ",") {
                            if (result.parameters == null) {
                                result.parameters = [];
                            }
                            if (Object.keys(tmpOne).length > 0) {
                                result.parameters.push(tmpOne);
                            }
                            tmpOne = {};
                            declearIdx++;
                        } else if (one.type == "l") {
                            var tResult = this.API.private("processCVar", declearArray, declearIdx);
                            if (tResult == null) {
                                console.error("解析函数参数中的成员变量出错");
                                return null;
                            }
                            tmpOne.type = tResult.vardeclear;
                            declearIdx = tResult.idx;
                            one = declearArray[declearIdx];
                            if (one.type != 'l') {
                                console.error("解析出错，参数类型定义后面应该是变量名");
                                return null;
                            }
                            tmpOne.name = one.content;
                            declearIdx++;
                        } else if (one.type == "b" && one.content == ")") {
                            if (result.parameters == null) {
                                result.parameters = [];
                            }
                            if (Object.keys(tmpOne).length > 0) {
                                result.parameters.push(tmpOne);
                            }
                            tmpOne = null;
                            declearIdx++;
                            declearStatus = "throws";
                            tmpStr = "";
                        } else {
                            console.error("解析函数参数错误！");
                        }
                    }
                    //}
                    //else if(throws){处理throws部分
                    else if (declearStatus == "throws") {
                        //处理throws
                        //--这个状态处理函数的异常抛出声明throws abc.Exception,sdffException
                        //--碰到throws就初始化，跳入下一条
                        //--碰到字符或者.就记录临时变量
                        //--碰到，号就将临时变量记录结果并情况临时变量
                        //--碰到{就结束
                        if (one.type == "l" && one.content == "throws") {
                            tmpStr = "";
                            declearIdx++;
                        } else if ((one.type == "b" && one.content == ",") || one.type == "l") {
                            tmpStr += one.content;
                            declearIdx++;
                        } else if (one.type == "b" && one.content == ",") {
                            if (result.throws == null) {
                                result.throws = [];
                            }
                            result.throws.push(tmpStr);
                            tmpStr = "";
                            declearIdx++;
                        } else if (one.type == "b" && one.content == "{") {
                            declearStatus = "end";
                            if (tmpStr != "") {
                                if (result.throws == null) {
                                    result.throws = [];
                                }
                                result.throws.push(tmpStr);
                            }
                            declearIdx++;
                        } else {
                            console.error("解析错误，碰到非法字符");
                            return null;
                        }
                    }
                    //}
                }
                //}
                //}
                //调用私有函数解析函数体
                console.log("parser function :" + result.name + " begin...");
                this.API.private('parserJavaClassFunctionBody', lv1Array[idx].children, 0, result.fragments);
                console.log("parser function :" + result.name + " finished.\n\n");
                //返回结果
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBodyNormal
            *@description [功能]处理解析函数normal部分的代码
            *[思路]参考原来js的解析类编写，已经实现，无需再编写代码
            *@param lv1Struct 函数体的结构，其结构参见：parserJavaClassFunctionBody
            *@param idx 当前的索引
            *@param destArray 目标函数结果，其结构参见parserJavaClassFunctionBody
            *@return 返回索引
            */
            "parserJavaClassFunctionBodyNormal": function(lv1Struct, idx, destArray) {
                //声明结果变量
                var result = {
                    type: "normal"
                }
                var nextidx = idx + 1;
                //if(当前是纯代码){记录村代码的结果
                if (lv1Struct[idx].type == "code") {
                    //记录纯代码情况
                    result.command = "";
                    //调用代码解析，将代码连起来
                    result.code = this.API.private('parserJavaClassFunctionBodyCode', lv1Struct[idx].content);
                }
                //}
                //else if(如果是注释类型){写注释写代码
                else if (lv1Struct[idx].type == "comment") {
                    //写注释写代码
                    result.command = (lv1Struct[idx].content[0] && lv1Struct[idx].content[0].content) ? lv1Struct[idx].content[0].content: "";
                    nextidx = this.API.private('parserJavaClassFunctionBodySubCommand', lv1Struct, nextidx, result);
                    if (lv1Struct[nextidx].type == "code") {
                        result.code = this.API.private('parserJavaClassFunctionBodyCode', lv1Struct[nextidx].content);
                        nextidx++;
                    } else {
                        result.code = "";
                    }
                }
                //}
                //返回结果
                if (result.code != "" || result.command != "") {
                    destArray.push(result);
                }
                //重新解析下一个
                return this.API.private("parserJavaClassFunctionBody", lv1Struct, nextidx, destArray, result);
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBodyCode
            *@description [功能]将一个代码片段即一窜java的cifa数组，连成一个完整的字符串
            *[思路]硬解析，关键是找准回车换行的地方，也是用自动机原理，程序分成两个状态，普通状态和for状态，进入for状态后，';'不换行，'{'才换行,同时'{'将状态转入普通状态。而普通状态中,';'.'{','}'
            *@param lexArray 一串java的词法数组组成的数组
            *@return 合成好的字符串
            */
            "parserJavaClassFunctionBodyCode": function(lexArray) {
                //保护性校验
                if (lexArray == null) {
                    return "";
                }
                //记录初始状态和结果变量
                var status = "normal";
                var result = "";
                var charPatn = /[0-9a-zA-Z_]/;
                //while(遍历数组){解析处理
                for (var i = 0; i < lexArray.length; i++) {
                    //获取其中一个元素
                    var one = lexArray[i];
                    //if(普通状态下for情况){记录结果中，并进入for状态
                    if (one.type == "l" && one.content == "for") {
                        //记录结果，并将状态转成for状态
                        result += (one.content + " ");
                        status = "for";
                    }
                    //}
                    //else if(普通状态下的';'){记录结果，并换行
                    else if (status == "normal" && one.type == "b" && one.content == ";") {
                        //记录结果，并换行
                        result += (one.content + "\n");
                    }
                    //}
                    //else if('{'){换行并转成普通状态
                    else if (one.type == "b" && one.content == "{") {
                        //换行，并记录普通状态
                        result += (one.content + "\n");
                        status = "normal"
                    }
                    //}
                    //else if('}'){直接换行
                    else if (one.type == "b" && one.content == "}") {
                        //直接换行
                        result += (one.content + "\n");
                    }
                    //}
                    //else{记录结果
                    else {
                        //记录结果
                        if (result.length > 0) {
                            var lastChar = result[result.length - 1];
                            //前后都是单词的时候中间加空格，要不然空格就太多了
                            if (charPatn.test(lastChar) && charPatn.test(one.content[0]) || lastChar == '\n') {
                                result = result + " " + one.content;
                            } else {
                                result += one.content;
                            }
                        } else {
                            result += one.content;
                        }
                    }
                    //}
                }
                //}
                //返回字符串
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBodyIf
            *@description [功能]处理if分支
            *[思路]参考原来gadget的if分支的处理逻辑进行处理
            *分析是if的情况,分支的情况每个分子都是对等的，所以分支直接取儿子分析
            *isElse说明了这个是否是else分支，如果是else分子那么，就不要push新的
            *注意：数组是三层分支结构，第一层是if空的，第二层是他的每个分支，第三层是每个分支上的内容
            *@param lv1Struct 参见parserJavaClassFunctionBody对应参数说明
            *@param idx 对应的索引
            *@param destArray 目标数组
            *@param isElse 说明了这个是否是else分支，如果是else分子那么，就不要push新的
            *@return 解析完的函数的对应索引所在地
            */
            "parserJavaClassFunctionBodyIf": function(lv1Struct, idx, destArray, isElse) {
                //初始化返回结果变量
                var oneResult = {
                    type: 'branchList',
                    subList: [],
                }
                //if(不是else情况){创建新的并将加入到destArray中
                if (!isElse || destArray.length == 0) {
                    //创建新的并将加入到destArray中
                    destArray.push(oneResult);
                }
                //}
                //else {创建新的并将加入到destArray中
                else {
                    //创建新的并将加入到destArray中
                    oneResult = destArray[destArray.length - 1];
                }
                //}
                //初始化分支部分的结果变量
                var oneInner = {
                    type: 'branchBlock',
                    subList: []
                }
                var code = null;
                var childCode = null;
                var comment = lv1Struct[idx].content[0] && lv1Struct[idx].content[0].content;
                oneInner.command = comment;
                //处理子注释
                //--子注释处理完后，索引会加到下一个位置的
                idx = this.API.private('parserJavaClassFunctionBodySubCommand', lv1Struct, idx + 1, oneInner);
                var nextIdx = idx;
                //block(块){处理if部分的代码块
                //处理if的代码块前部
                //--首先要判断是否是代码块
                //--紧跟着if注释的代码，必须是if语句，否则当做一个独立的其他normal处理，而if本身的code部分是空的
                //--如果有if语句包含body，要把if语句那行代码和后面if内部代码分离出来，后面做normal处理
                if (idx < lv1Struct.length && lv1Struct[idx].type == "code") {
                    code = this.API.private('parserJavaClassFunctionBodyCode', lv1Struct[idx].content);
                    var execResult = /(((if)|(else))[^\r\n]*{)\s*[\r\n]+([\s\S]+)/.exec(code);
                    if (execResult != null && !/^[\s\t\r\n]*$/.test(execResult[2])) {
                        code = execResult[1];
                        childCode = execResult[5].replace(/(^[\r\n\s\t]*)|([\r\n\s\t]*$)/g, "");
                    }
                    nextIdx++;
                };
                oneInner.code = code;
                if (childCode != null) {
                    oneInner.subList.push({
                        type: 'normal',
                        code: childCode
                    });
                }
                //处理分支和子分支
                //--if语句比较特殊，有分支和子分支，都用同一个语句的，参见函数头部说明
                //--如果是新if语句的话，就要一次性将分支子分支建立好
                oneResult.subList.push(oneInner);
                //处理if的代码块后面部分
                //--代码块在解决头部问题后，就重新调用parserJavaClassFunctionBody解析整个儿子部分
                //--注意，这种递归调用在if情况下能返回的唯一个请就是返回}注释，否则就是错误
                //--围绕返回的注释}要把这个注释}的前面和后面的代码的}去掉
                //--若果去掉后，仅仅剩一个空格或若干个换行，那么把这个儿子整个去掉
                var resultIdx = this.API.private("parserJavaClassFunctionBody", lv1Struct, nextIdx, oneInner.subList, oneInner);
                var lastCodeChild = oneInner.subList[oneInner.subList.length - 1];
                if (lastCodeChild != null && lastCodeChild.type == "normal" && lastCodeChild.code != null) {
                    lastCodeChild.code = lastCodeChild.code.replace(/[\s\t\r\n]*\}[\s\t\r\n]*$/, "");
                    if (/^[\s\t\r\n]*$/.test(lastCodeChild.code)) {
                        oneInner.subList.splice(oneInner.subList.length - 1, 1);
                    }
                }

                nextIdx = resultIdx + 1;
                //}
                //处理返回的是}else的情况
                //++这段代码是移植的gadget的，这个代码即使是gadget部分应该也是不可到达的无用代码
                if (/^[\s\r\n\t]*\}[\s\t]*else/.test(lv1Struct[resultIdx].content[0].content)) {
                    lv1Struct[resultIdx].content[0].content = lv1Struct[resultIdx].content[0].content.replace(/^[\s\r\n\t]*\}/g, "");
                    return this.API.private("parserJavaClassFunctionBodyIf", lv1Struct, resultIdx, destArray, true);
                }
                //继续递归返回下一个
                return this.API.private("parserJavaClassFunctionBody", lv1Struct, nextIdx, destArray, oneResult);
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBodySubCommand
            *@description [功能]处理子注释情况
            *[思路]参照gadget的写法
            *@param lv1Struct 参照parserJavaClassFunctionBody
            *@param idx 参见parserJavaClassFunctionBody
            *@param parent 自己的父节点
            *@return 下一个所处的索引位置
            */
            "parserJavaClassFunctionBodySubCommand": function(lv1Struct, idx, parent) {
                //基本校验parent
                if (!parent) {
                    return idx;
                }
                //记录当前索引
                //--下面要不停加1所以这里先减一
                var i = idx - 1;
                //while(死循环){处理子注释
                while (true) {
                    //索引位置校验和初始化
                    i++;
                    if (i >= lv1Struct.length) {
                        return i;
                    }
                    var content = lv1Struct[idx].content[0] && lv1Struct[idx].content[0].content;
                    //if(是子注释){处理子注释
                    if (lv1Struct[i].type == "command" && /^((\+\+)|(--))/.test(content)) {
                        //处理子注释
                        var sub = parent.subCommand;
                        if (!sub) {
                            sub = parent.subCommand = [];
                        }
                        var type = content && content.substr(0, 2);
                        content = content && content.substring(2, content.length);
                        sub.push({
                            type: type,
                            content: content
                        })
                    }
                    //}
                    //else{退回索引
                    else {
                        //退回索引
                        return i;
                    }
                    //}
                }
                //}
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBody
            *@description [功能]解析函数体信息
            *[思路]参考gadget编辑器的实现，输入的是整个函数body的词法结果数组
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param lv1Struct 函数体的结构
            [
            {
            type:"code/comments",//代码类型或者是注释类型，其实只要不是注释类型就是代码类型，这里是用注释作为间隔将整个函数体切分成一段段的结果
            content:[]//对应实体的词法数组
            }
            ]
            *@param idx 表示正在解析的段落索引
            *@param destArray 目标数组，结构就是函数结构中fragments的值
            [
            {
            type: "block",//类型有normal,block,cycle,branchList其中block类型是没有code的,
            command:"注释内容，block通常是block(xx){xxxx}",
            subList:[
            {
            type:"normal",
            command:"注释内容可以没有",
            code:"sss"
            }
            ],
            
            },
            {
            type:"cycle",
            command:"注释内容,通常的语法是for (xxx)",
            code:"for(){xxx",
            subList:[
            {
            type:"normal",
            command:"注释内容可以没有",
            code:"sss"
            }
            ]
            },
            {
            type:"branchList",//branchList是没注释，没code没command，只有subList，而且subList只能用branchBlock
            subList:[
            {
            type:"branchBlock",
            command:"分支的注释",
            subCommand:[//子注释情况
            type:"++/--",
            content:"子注释内容"
            ],
            code:"分支部分的语句",
            subList:[
            ...
            ]
            }
            ]
            }
            ]
            *@param parent 上一级节点，这是给递归的时候使用的,原来的js代码有，但这个参数可能已经没有用了
            *@return 返回的是当前解析的索引值
            *@example 外部使用
            *var dest= [];
            *this.API.private("parserJavaClassFunctionBody",src,0,dest);
            */
            "parserJavaClassFunctionBody": function(lv1Struct, idx, destArray, parent) {
                //进行初始化
                //--校验
                //--取出基本值
                if (idx >= lv1Struct.length) {
                    return - 1;
                }
                var type = lv1Struct[idx].type;
                var content = lv1Struct[idx].content[0] && lv1Struct[idx].content[0].content;
                //if(是代码类型){进入normal模式
                if (type == "code") {
                    //调用normal分支处理
                    return this.API.private('parserJavaClassFunctionBodyNormal', lv1Struct, idx, destArray);
                }
                //}
                //else{进一步进行注释分支判断
                else {
                    //if(是if分支){处理if分支
                    if (/^\s*(if).+/.test(content)) {
                        //进行if分子处理
                        return this.API.private('parserJavaClassFunctionBodyIf', lv1Struct, idx, destArray);
                    }
                    //}
                    //else if(是}结束标志){直接返回不处理
                    else if (/^\s*\}.*/.test(content)) {
                        //直接返回，这是if和for等结果
                        return idx;
                    }
                    //}
                    //else if(如果是else if的情况){处理else if
                    else if (/^\s*else.*/.test(content)) {
                        //处理else if的情况
                        return this.API.private('parserJavaClassFunctionBodyIf', lv1Struct, idx, destArray, true);
                    }
                    //}
                    //else if(循环情况){处理循环
                    else if (/^\s*(for|while|cycle).*/.test(content)) {
                        //处理循环情况
                        return this.API.private('parserJavaClassFunctionBodyCycle', lv1Struct, idx, destArray);
                    }
                    //}
                    //else if(代码块){处理代码块
                    else if (/^\s*block.*/.test(content)) {
                        //调用代码块完成解析
                        return this.API.private('parserJavaClassFunctionBodyBlock', lv1Struct, idx, destArray);
                    }
                    //}
                    //else{其他情况
                    else {
                        //当做普通情况处理
                        return this.API.private('parserJavaClassFunctionBodyNormal', lv1Struct, idx, destArray);
                    }
                    //}
                }
                //}
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBodyCycle
            *@description [功能]解析循环情况的函数
            *[思路]参考gadget的解析器完成循环模式的解析
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param lv1Struct 参见parserJavaClassFunctionBody
            *@param idx 参见parserJavaClassFunctionBody
            *@param destArray 参见parserJavaClassFunctionBody
            *@return 解析完后的索引所在位置
            */
            "parserJavaClassFunctionBodyCycle": function(lv1Struct, idx, destArray) {
                //初始化结果变量
                //--将注释部分记录下来
                var oneResult = {
                    type: "cycle",
                    command: lv1Struct[idx].content[0].content,
                    subList: []
                }
                destArray.push(oneResult);
                //处理子注释
                idx = this.API.private('parserJavaClassFunctionBodySubCommand', lv1Struct, idx + 1, oneResult);
                var nextIdx = idx;
                //循环代码头部分离
                //--将{后的代码分离成子代码块内容
                //--将代码部分记录下来
                var childCode = null;
                if (idx < lv1Struct.length && lv1Struct[idx].type == "code") {
                    oneResult.code = this.API.private('parserJavaClassFunctionBodyCode', lv1Struct[idx].content);
                    var execResult = /(\w+[^\r\n]+{)\s*[\r\n]+([\s\S]+)/.exec(oneResult.code);
                    if (execResult != null && !/^[\s\t\r\n]*$/.test(execResult[2])) {
                        oneResult.code = execResult[1];
                        childCode = execResult[2].replace(/(^[\r\n\s\t]*)|([\r\n\s\t]*$)/g, "");
                        oneResult.subList.push({
                            type: "normal",
                            code: childCode
                        });
                    }
                    nextIdx++;
                }
                //递归调用处理子代码块
                //--这里调用parserJavaClassFunctionBody一定会返回}注释，这个时候，索引落回这个注释本身的索引，并不会下移
                var nextIdx = this.API.private("parserJavaClassFunctionBody", lv1Struct, nextIdx, oneResult.subList, oneResult) + 1;
                //处理循环结束部分
                //--吃掉最后一个代码类型}
                //--吃掉注释}后面的那一个代码}
                //--如果剩下最后的代码只有回车或者空格或者换行，则吃掉这个
                var lastCodeChild = oneResult.subList[oneResult.subList.length - 1];
                if (lastCodeChild != null && lastCodeChild.type == "normal" && lastCodeChild.code != null) {
                    lastCodeChild.code = lastCodeChild.code.replace(/[\s\t\r\n]*\}[\s\t\r\n]*$/, "");
                    if (/^[\r\n\s]*$/i.test(lastCodeChild.code)) {
                        oneResult.subList.splice(oneResult.subList.length - 1, 1);
                    }
                }
                //递归调用下一个状态
                return this.API.private("parserJavaClassFunctionBody", lv1Struct, nextIdx, destArray, oneResult);
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionBodyBlock
            *@description [功能]处理和解析代码块部分代码
            *[思路]参考gadget解析部分函数实现，该函数代码已经实现
            *@param lv1Struct 参见parserJavaClassFunctionBody说明
            *@param idx 参见parserJavaClassFunctionBody说明
            *@param destArray 参见parserJavaClassFunctionBody说明
            *@return 解析完的索引值
            */
            "parserJavaClassFunctionBodyBlock": function(lv1Struct, idx, destArray) {
                //初始化结果变量
                var oneResult = {
                    type: "block",
                    command: lv1Struct[idx].content[0].content,
                    subList: []
                }
                destArray.push(oneResult);
                //处理子注释
                //--子注释处理逻辑会将索引自动移动到下一个位置
                idx = this.API.private('parserJavaClassFunctionBodySubCommand', lv1Struct, idx + 1, oneResult);
                var nextIdx = idx;
                //处理块表示的子代码
                //--代码完成时去掉代码块注释后面的代码}
                var nextIdx = this.API.private("parserJavaClassFunctionBody", lv1Struct, nextIdx, oneResult.subList, oneResult) + 1;
                if (nextIdx < lv1Struct.length && lv1Struct[nextIdx].type == "code" && lv1Struct[nextIdx].content) {
                    var code = this.API.private('parserJavaClassFunctionBodyCode', lv1Struct[nextIdx].content);
                    if (/^[\s\t\r\n]*\}/.test(code) || /^[\s\t\r\n]*$/.test(code)) {
                        nextIdx++;
                    }
                }
                //递归下一个
                return this.API.private("parserJavaClassFunctionBody", lv1Struct, nextIdx, destArray, oneResult);
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassFunctionComment
            *@description [功能]解析函数上面的注释信息
            *[思路]参考原来gadget的解析代码
            *@param commentStr 注释的字符串
            *@return {
            name:'名称',
            description:'描述'
            param:{
            '参数名':'参数说明'
            },
            return:'返回值',
            example:'样例'
            }
            */
            "parserJavaClassFunctionComment": function(commentStr) {
                //初始化变量
                if (!commentStr) {
                    return null;
                }

                var result = {
                    description: "",
                    param: {}
                };
                //用换行拆分每个注释内容
                var cmArray = commentStr.split(/[\n\r]/);
                var lastObj = result;
                var lastMenber = "description";
                //while(每个行内容){
                for (var i = 0; i < cmArray.length; i++) {
                    //去掉每个注释前面的*以及*前面的空格
                    var oneComm = cmArray[i].replace(/^[\t\s]*\*/, '');
                    if (oneComm == '' || /^\/\*?/.test(oneComm)) {
                        continue;
                    }
                    //解析又@的域
                    //--合并到上一个类型中
                    var execResult = /^\s*@\s*(\w+)\s*(.*)/.exec(oneComm);
                    if (execResult == null) {

                        if (lastObj[lastMenber]) {
                            lastObj[lastMenber] = lastObj[lastMenber] + "\n" + oneComm;
                        } else {
                            lastObj[lastMenber] = oneComm;
                        }
                        continue;
                    }
                    //解析param，这个是@后面的二目参数
                    lastMenber = execResult[1];
                    if (lastMenber == "param") {
                        var execResult2 = /(\w+)(\s*.+)/.exec(execResult[2]);
                        if (execResult2 != null) {
                            result.param[execResult2[1]] = execResult2[2].replace(/^\s+/, "");
                            lastObj = result.param;
                            lastMenber = execResult2[1];
                        }
                        continue;
                    }
                    //剩下的就是没有@的部分，就归属于上一个解析的解析
                    result[lastMenber] = execResult[2];
                    lastObj = result;
                }
                //}
                //返回结果
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaPackage
            *@description [功能]传入的是package的结构体
            *[思路]这个函数比较简单，就按照输入输出的要求进行转换
            *@param packageStruct 输入的lv1的package结构
            {
            type:"package",其中note表示注释
            content:[],这里是这个类型的完整的详细内容，其内容是由词法分析返回的结果的数组，比如import com.go;词法分析将得到import com  .   go   ;这5个元素对象，对象结构参见lex的结果
            }
            *@return 一个包名字符串，例如："com.breeze.abc.com"
            */
            "parserJavaPackage": function(packageStruct) {
                var result = "";
                for (var i = 0; i < packageStruct.content.length; ++i) {
                    var v = packageStruct.content[i].content;
                    if (v == ';') {
                        break;
                    }
                    result += v;
                }
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClassNote
            *@description [功能]解析java的类的注释，并返回对应的结构信息
            *[思路]这个有一点点难度，就是要解析类里面的注释点，不过总体上说是比较简单的，总体思路可以参见私有函数parserJavaClassFunctionComment，先用split按照回车换行切割成每行每行进行处理
            *这里注意处理实际处理每行前面的*号，另外，对于desc部分的描述，可以有@desc的前缀，也可以没有，这时候都属于这个结构所需要的。注意，这里统一都是要求注释类型是N形的，如果不是，就返回null
            *@param lv1Structs lv1Struct的结构体：
            {
            type:"note",其中note表示注释
            content:[],
            }
            其中content只有一个元素{
            type:"N",
            content:"注释内容"
            }
            
            注释内容，就是/**...的java的注释类说明注释
            每行第一个字符是*，以空格为间隔符
            每个语义注释开头为@xxx这些xxx空格包括:
            desc:描述说明,后面包括可以换行都是描述的一部分
            version:版本说明,格式为 版本号 日期 作者 描述
            design: 设计链接 后面就是一个链接地址
            unittest:单元测试地址 后面就是一个单元测试地址
            说明，其中@xxx是可以省略的，如果省略就是desc的内容
            
            
            *@return {
            version:[{
            version: "1.0",
            date:"xxxx",
            author: "lgy",
            description: "xxx"
            }],
            comments: "desc ",
            design:xxxx,
            unittest:xxxxx
            }
            */
            "parserJavaClassNote": function(lv1Structs) {
                //初始化变量
                if (!lv1Structs) {
                    return null;
                }
                if (lv1Structs.type != "note" || lv1Structs.content == null) {
                    return null;
                }
                var lv1Struct = lv1Structs.content[0].content;
                var result = {
                    version: [],
                    comments: "",
                    design: "",
                    unittest: ""
                };
                //通过换行分割字符
                var lv1StructArray = lv1Struct.split(/[\n\r]/);
                var versionitem = {
                    version: "",
                    date: "",
                    author: "",
                    description: ""
                };
                var des = "";
                //while(遍历每一行){
                for (var i = 0; i < lv1StructArray.length; i++) {
                    //去掉前面的*以及*前面的空格
                    var oneLv1Str = lv1StructArray[i].replace(/[\t\s]*(\*)*/, '');
                    //匹配/*
                    var patt1 = new RegExp("/\s?(\/\*)");
                    var flag = patt1.test(oneLv1Str);
                    if (oneLv1Str == "") {
                        continue;
                    }
                    if (flag && !/@/.test(oneLv1Str)) {
                        continue;
                    }
                    //匹配@的参数
                    var resparam = /^\s*@\s*(\w+)\s*/.exec(oneLv1Str);
                    // if(参数为空）{ 
                    if (resparam == null) {
                        //说明是注释的说明部分
                        if (des) {
                            des = des + "\n" + oneLv1Str;
                        } else {
                            des = oneLv1Str;
                        }
                        continue;
                    }
                    //}
                    //通过分割字符分解
                    var paramArray = oneLv1Str.split(/\s+/);
                    var attributeStr = resparam[1];

                    if (attributeStr == "version") {
                        if (paramArray.length >= 4) {
                            versionitem.version = paramArray[1];
                            versionitem.date = paramArray[2];
                            versionitem.author = paramArray[3];
                            versionitem.description = paramArray[4];
                        }
                        if (result.version == null) {
                            result.version = [];
                        }
                        result.version.push(versionitem);

                    } else if (attributeStr == "design") {
                        if (paramArray.length >= 2) {

                            result.design = paramArray[1];
                        }
                    } else if (attributeStr == "unittest") {
                        if (paramArray.length >= 2) {
                            result.unittest = paramArray[1];
                        }
                    }
                }
                //}
                //遍历结束
                result.comments = des;
                return result;
            },
            /**
            *@function
            *@memberOf javalv2
            *@name private$processCVar
            *@description [功能]处理变量名主要是针对<dd,dd>的模式类型，给parserJavaClassVar和parserJavaClassFunction使用
            *[思路]
            *@param objArr 分解的数组内容
            *@param idx 当前索引
            *@return toDo
            *@example toDO
            */
            "processCVar": function(objArr, idx) {
                var result = {};
                var one = objArr[idx];
                if (one.type != "l") {
                    return null;
                }
                result.vardeclear = one.content;
                idx++;
                result.idx = idx;
                one = objArr[idx];

                if (one.type == "b" && one.content == "<") {
                    result.vardeclear += "<";
                    while (true) {
                        idx++;
                        one = objArr[idx];
                        if (one.type == 'l') {
                            var subResult = this.API.private("processCVar", objArr, idx);
                            result.vardeclear += subResult.vardeclear;
                            idx = subResult.idx - 1;
                        } else if (one.type == 'b' && one.content == '>') {
                            result.vardeclear += '>';
                            result.idx = idx + 1;
                            return result;
                        } else {
                            result.vardeclear += one.content;
                        }
                    }
                } else if (one.type == "b" && one.content == "[") {
                    while (true) {
                        one = objArr[idx];
                        if (one.type != 'b' || (one.content != "[" && one.content != "]")) {
                            result.idx = idx;
                            return result;
                        }
                        result.vardeclear += one.content;
                        idx++;
                    }
                }
                return result;
            }
        }
    },
    module);
    return FW;
});