/**
* @namespace
* @name java2code 
* @version 0.01 罗光瑜 初始版本
0.02 罗光瑜 反写}的时候，将后续对应开头的部分写入，知道哪个括号对哪个
0.03 罗光瑜 代码开头很好，extends没有按数组处理，是错误的
0.04 罗光瑜 处理构造函数以及throws的时候的函数声明
0.05 罗光瑜 解决if子注释后，没有真正写入}
* @description  这个类是将java的内存结构转换成对应的代码文本               
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "java2code",
        /**
        *@function
        *@memberOf java2code
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf java2code
            *@name public$decodeFun
            *@description [功能]解析其中一个函数
            *[思路]根据函数的内部结构去反向的拼接这个函数，并获取他的代码
            *{
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
            *				   name:"",
            *                                   desc:""
            *				}
            *			],
            *			type: "public",
            *			prifix: "static final",
            *                       returnDesc:"返回值说明"
            *                        return:"返回值",
            *                        example:样例说明
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
            *@param funObj 函数对象
            */
            "decodeFun": function(funObj) {
                //解析头部
                var javascode = this.decodeFunHead(funObj);
                javascode += "{\n"
                //block(块){处理函数体部分
                //获取函数体数组
                var funBody = funObj.fragments;
                //while(所有函数体片段){调用decodeany解析
                for (var i = 0; funBody != null && i < funBody.length; i++) {
                    javascode += (this.API.private('decodeFunAny', funBody[i]));
                }
                //}
                //}
                //结束部分
                javascode += "}"
                return javascode;
            },
            /**
            *@function
            *@memberOf java2code
            *@name public$decodeFunFagment
            *@description [功能]反向解析某个函数片段
            *[思路]直接调用私有方法decodeFunAny即可
            *@param fagment 函数片段对象
            *@return 函数的code文本
            */
            "decodeFunFagment": function(fagment) {
                return this.API.private('decodeFunAny', fagment);
            },
            /**
            *@function
            *@memberOf java2code
            *@name public$decodeFunHead
            *@description [功能]解析出函数的头部
            *@param funObj 函数对象
            *@return 解析好的函数代码
            */
            "decodeFunHead": function(funObj) {
                //解析头部
                //--返回值部分return void情况要求在解析的时候处理，因为构造函数要返回""
                var javascode = funObj.type;
                if (/default/i.test(javascode)) {
                    javascode = "";
                }
                javascode += (" " + (funObj.prifix || ""));
                javascode += (" " + (funObj["return"] || ""));
                javascode += (" " + funObj.name);
                javascode += "(";
                for (var i = 0; funObj != null && i < funObj.parameters.length; i++) {
                    if (i != 0) {
                        javascode += ",";
                    }
                    javascode += (funObj.parameters[i].type + " " + funObj.parameters[i].name);
                }
                javascode += ")";
                if (funObj.throws && funObj.throws.length > 0) {
                    javascode += (" throws " + funObj.throws.join(","));
                }
                return javascode;
            },
            /**
            *@function
            *@memberOf java2code
            *@name public$decodeJava
            *@description [功能]解压整个java文件
            *[思路]逐个结构解压
            *@param javaObj java的结构对象：
            第二层的类解析器，这层解析就要解析出结构化的java结构对象，这里直接解析是不行的要利用词法分析和分段解析，分别是lex类和javalv1类进行解析，中间结果后进行处理
            这个类解析器目标是能够通过函数将java类解析如下的结构：
            {
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
            functionFragment:[
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
            subCommand:[
            type:"++/--",
            content:"注释内容"
            ],
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
            ]
            }
            */
            "decodeJava": function(javaObj) {
                //声明结果变量
                var result = "";
                //解析package
                if (javaObj.package) {
                    result += ("package " + javaObj.package + ";\n\n");
                }
                //解析import
                for (var i = 0; javaObj.include && i < javaObj.include.length; i++) {
                    result += ("import " + javaObj.include[i] + ";\n");
                }
                result += ("\n");
                //解析类注释
                result += ("/**\n");
                if (javaObj.comments) {
                    var cArr = javaObj.comments.split(/[\r\n]/);
                    for (var i = 0; i < cArr.length; i++) {
                        result += ("*" + cArr[i] + "\n");
                    }
                }

                if (javaObj.version) {
                    for (var i = 0; i < javaObj.version.length; i++) {
                        var one = javaObj.version[i];
                        result += ("*@version " + one.version + " " + one.date + " " + one.author + " " + one.description + "\n");
                    }
                }

                if (javaObj.design) {
                    result += ("@design " + javaObj.design + "\n");
                }

                if (javaObj.unittest) {
                    result += ("@unittest " + javaObj.unittest + "\n");
                }
                result += ("*/\n");
                //block(块){解析类
                //类声明
                result += ("public class " + javaObj.name + " ");
                if (javaObj["extends"]) {
                    result += ("extends " + javaObj["extends"] + " ");
                }
                if (javaObj.implements && javaObj.implements.length > 0) {
                    result += ("implements ");
                    for (var i = 0; i < javaObj.implements.length; i++) {
                        if (i > 0) {
                            result += ",";
                        }
                        result += javaObj.implements[i];
                    }
                }
                result += "{\n";
                //block(块){类属性
                //if(如果有类属性){则开始解析
                if (javaObj.attributeFragment) {
                    //for (遍历所有属性){
                    for (var i = 0; i < javaObj.attributeFragment.length; i++) {
                        //获取一个
                        var one = javaObj.attributeFragment[i];
                        //处理注释
                        if (one.comments) {
                            result += ("/**\n*" + one.comments.replace(/(^[\n\n\s]*)|([\r\n\s]*$)/g, "") + "\n*/\n");
                        }
                        //处理note信息
                        if (one["@"]) {
                            result += ("@" + one["@"].replace(/(^\s*)|(\s*$)/, "") + "\n");
                        }
                        //处理类型
                        if (one.type != "default") {
                            result += (one.type + " ");
                        }
                        //处理前缀
                        if (one.prifix) {
                            result += (one.prifix + " ");
                        }
                        //处理变量类型
                        result += (one.attType + " ");
                        var content = "";
                        if (one.content != null && one.content != "") {
                            content = "= " + one.content;
                        }
                        //处理最后内容
                        result += (one.name + " " + content + ";\n");
                    }
                    //}
                }
                //}
                //}
                //block(块){类函数
                //if (如果有函数){解析函数
                if (javaObj.functionFragment) {
                    //for(所有函数片段){解析每一个函数
                    for (var i = 0; i < javaObj.functionFragment.length; i++) {
                        //获取其中一个
                        var one = javaObj.functionFragment[i];
                        //处理注释
                        if (one.comments) {
                            result += ("/**\n");
                            if (one.comments.description) {
                                result += ("* " + one.comments.description.replace(/(^[\n\n\s]*)|([\r\n\s]*$)/g, "") + "\n");
                            }
                            if (one.comments.param) {
                                for (var n in one.comments.param) {
                                    result += ("* @param " + n + " " + one.comments.param[n] + "\n");
                                }
                            }
                            if (one.comments.example) {
                                result += ("* @example " + one.comments.example + "\n");
                            }
                            if (one.comments["return"]) {
                                result += ("* @return " + one.comments["return"] + "\n");
                            }
                            result += "*/\n";
                        }
                        //处理标注
                        if (one["@"]) {
                            result += ("@ " + one["@"].replace(/(^\s*)|(\s*$)/, "") + "\n");
                        }
                        //函数体
                        result += this.decodeFun(one);
                        result += "\n";
                    }
                    //}
                }
                //}
                //}
                //类结束
                result += "}";
                //}
                return result;
            }
        },
        "private": {
            /**
            *@function
            *@memberOf java2code
            *@name private$decodeFunAny
            *@description [功能]还原不同类型的代码
            *[思路]整体上就是给decodeFun调用的不过是递归的方式进行调用
            *@param one 函数片段
            *@return 本片段的实际值
            */
            "decodeFunAny": function(one) {
                //if(normal情况){处理normal情况信息
                if (/normal/i.test(one.type)) {
                    //调用decodeFunNormal反向解析normal情况函数
                    return this.API.private('decodeFunNormal', one);
                }
                //}
                //else if(分支情况){
                else if (/branchList/i.test(one.type)) {
                    //处理分支情况
                    return this.API.private('decodeFunBranchList', one);
                }
                //}
                //else if(循环情况){处理循环情况
                else if (/cycle/i.test(one.type)) {
                    //调用递归函数处理循环
                    return this.API.private('decodeFunCycle', one);
                }
                //}
                //else if(block情况){处理block
                else if (/block/i.test(one.type)) {
                    //处理block
                    return this.API.private('decodeFunBlock', one);
                }
                //}
            },
            /**
            *@function
            *@memberOf java2code
            *@name private$decodeFunNormal
            *@description [功能]解析函数的normal片段内容
            *[思路]解析函数的normal片段内容，这里用递归的方式将内容返回
            *@param one 函数片段内容
            */
            "decodeFunNormal": function(one) {
                //获取对象
                var result = "";
                //设置注释
                if (one.command) {
                    result += ("//" + one.command + "\n");
                }
                //设置子注释
                if (one.subCommand) {
                    for (var i = 0; i < one.subCommand.length; i++) {
                        var onesub = one.subCommand[i];
                        result += ("//" + onesub.type + onesub.content + "\n");
                    }
                }
                //添加代码
                if (one.code) {
                    result += (one.code + "\n");
                }
                return result;
            },
            /**
            *@function
            *@memberOf java2code
            *@name private$decodeFunBranchList
            *@description [功能]解析函数分支情况
            *[思路]分支本身是没有内容的，这里要接着解析子分支请
            *@param one 代码片段列表对象
            *@return 反向解析出来的代码值
            */
            "decodeFunBranchList": function(one) {
                //获取对象
                var result = "";
                //获取到儿子
                var sub = one.subList;
                if (sub == null) {
                    return "";
                }
                //while(所有儿子){逐个调用并合并代码
                for (var i = 0; i < sub.length; i++) {
                    //获取其中一个
                    //--这是一个branchBlock类型
                    oneSub = sub[i];
                    //获取注释值
                    if (oneSub.command) {
                        result += ("//" + oneSub.command + "\n");
                    }
                    //设置子注释
                    if (oneSub.subCommand) {
                        for (var i = 0; i < oneSub.subCommand.length; i++) {
                            var onesubCommand = oneSub.subCommand[i];
                            result += ("//" + onesubCommand.type + onesubCommand.content + "\n");
                        }
                    }
                    //添加代码
                    if (oneSub.code) {
                        result += (oneSub.code + "\n");
                    }
                    //处理儿子的儿子
                    var subsubList = oneSub.subList;
                    if (subsubList != null) {
                        for (var j = 0; j < subsubList.length; j++) {
                            result += this.API.private('decodeFunAny', subsubList[j]);
                        }
                    }
                    //处理本次结束内容
                    //--如果没有代码，这里不要输出}
                    if (oneSub.code && "" != oneSub.code) {
                        result += "}\n";
                    }
                    result += "//} end " + oneSub.command.replace(/\{.*/g, "") + "\n";
                }
                //}
                //返回结果
                return result;
            },
            /**
            *@function
            *@memberOf java2code
            *@name private$decodeFunCycle
            *@description [功能]解析循环功能
            *[思路]反向掉循环情况下的结果
            *@param one 其中一个代码片段
            */
            "decodeFunCycle": function(one) {
                //获取对象
                var result = "";
                //获取到儿子
                //获取注释值
                if (one.command) {
                    result += ("//" + one.command + "\n");
                }
                //设置子注释
                if (one.subCommand) {
                    for (var i = 0; i < one.subCommand.length; i++) {
                        var onesubCommand = one.subCommand[i];
                        result += ("//" + onesubCommand.type + onesubCommand.content + "\n");
                    }
                }
                //添加代码
                if (one.code) {
                    result += (one.code + "\n");
                }
                //处理儿子
                var subsubList = one.subList;
                if (subsubList != null) {
                    for (var j = 0; j < subsubList.length; j++) {
                        result += this.API.private('decodeFunAny', subsubList[j]);
                    }
                }
                //处理本次结束内容
                //--如果没有代码，这里不要输出}
                if (one.code && "" != one.code) {
                    result += "}\n";
                }
                //处理本次结束内容
                result += "//} end " + one.command.replace(/\{.*/g, "") + "\n";
                //返回结果
                return result;
            },
            /**
            *@function
            *@memberOf java2code
            *@name private$decodeFunBlock
            *@description [功能]处理block的情况代码
            *[思路]根据结构解析处理
            *@param one 代码片段
            *@return 本片段的结果信息
            */
            "decodeFunBlock": function(one) {
                //获取对象
                var result = "";
                //获取到儿子
                //获取注释值
                if (one.command) {
                    result += ("//" + one.command + "\n");
                }
                //设置子注释
                if (one.subCommand) {
                    for (var i = 0; i < one.subCommand.length; i++) {
                        var onesubCommand = one.subCommand[i];
                        result += ("//" + onesubCommand.type + onesubCommand.content + "\n");
                    }
                }
                //添加代码
                if (one.code) {
                    result += (one.code + "\n");
                }
                //处理儿子
                var subsubList = one.subList;
                if (subsubList != null) {
                    for (var j = 0; j < subsubList.length; j++) {
                        result += this.API.private('decodeFunAny', subsubList[j]);
                    }
                }
                //处理本次结束内容
                result += "//} end " + one.command.replace(/\{.*/g, "") + "\n";
                //返回结果
                return result;
            }
        }
    },
    module);
    return FW;
});