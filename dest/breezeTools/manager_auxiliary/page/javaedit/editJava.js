/**
* @namespace
* @name editJava 
* @version 0.01 罗光瑜 初始化版本，和文本编辑器能联通
* @description  这是一个java的编辑器，参考原来的gadget编辑器实现的                        
*注意，这里调用外面使用的gadget，这些gadget都是要和页面绑定的，例如formoper，图形显示等，所以这些组件都是和主框架有约定好id信息，就是在创建这些组件时，固定一个成员id，然后在实际绘图时候，这个id要固定下来    
*另外，文件处理全部用父类完成，父类对象已经被保存，父类有机制将自己的对象设置到本类参数中：
*one.content.param.main = this;
*one.content.param.dataArr = objArr;
*one.content.param.idx = i;
*one.content.param.tagIdx = tagIdx;
*one.content.param.tagPath = tagPath;             
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("./grammar/javalv2");
    require("../../commtools/fileselect");
    require("../../commtools/treeView");
    require("../../commtools/formOper");
    require("./graphyFunMod");
    require("./grammar/java2code");
    require("../../commtools/codeEditor");
    var textformat = require("../../../breeze/framework/js/tools/formatJS");
    FW.register({
        "name": "editJava",
        /**
        *@function
        *@memberOf editJava
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //创建java解析组件
            this.MY.javaParser = FW.createApp("javalv2", "javalv2", {});
            //创建文件对象
            var pageParam = {
                id: 'fileselect',
                dom: this.dom,
                param: {
                    viewid: "--"
                },
                view: {}

            }
            this.MY.fileOper = FW.createApp("fileselect", "fileselect", pageParam);
            //初始化左边菜单树
            this.MY.treeView = FW.createApp("javaTree", "treeView", {},
            true);
            //初始化表单操作
            this.MY.formOper = FW.createApp("formOper", "formOper", this);
            this.MY.formOper.owner = this;
            //初始化流程图对象
            this.MY.graphyMod = FW.createApp("graphyFunMod", "graphyFunMod", {
                dom: "graphyFunMod"
            });
            //初始化内存对象到code的转换对象
            this.MY.java2code = FW.createApp("java2code", "java2code", {});
            //初始化codeMirror
            this.MY.codeEditor = FW.createApp("codeEditor", "codeEditor", {
                dom: "codeEditor"
            });
            //初始化内部对象
            this.MY.java = null;
        },
        "public": {
            /**
            *@function
            *@memberOf editJava
            *@name public$save
            *@description [功能]将内存保存到文件中
            *[思路]从内存中获取数据，然后反向解析成文件，然后保存
            *@return 成功返回0否则返回失败
            */
            "save": function() {
                //获取包名和类名
                var packageName = this.MY.java.package;
                var className = this.MY.java.name;
                var classFile = className + ".java";
                var classDir = "/WEB-INF/classes/djava/" + packageName.replace(/\./ig, "/");
                classDir = classDir.replace(/[\\\/]+/ig, "/");
                //获取文件路径名
                var fileDir = this.MY.fileDir;
                var fileDir = fileDir.replace(/[\\\/]+/ig, "/");
                var fileFile = this.MY.fileName;
                //处理包名和文件存储路径一直性
                if (classFile != fileFile || classDir != fileDir) {
                    if (!confirm("您的类路径和文件路径不一致，按照类声明移动文件吗？")) {
                        return 10;
                    }
                    this.MY.fileOper.setPath(fileDir);
                    this.MY.fileOper.setFile(fileFile);
                    this.MY.fileOper.deleteFile();
                    this.MY.fileDir = classDir;
                    this.MY.fileName = classFile;
                }
                //获取所有代码
                var code = this.MY.java2code.decodeJava(this.MY.java);
                code = textformat.js_beautify(code);
                //保存到网络端
                this.MY.fileOper.setPath(classDir);
                this.MY.fileOper.setFile(classFile);
                this.MY.fileOper.saveFile(code);
                //返回结果
                return 0;
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$initNew
            *@description [功能]用java文本文件进行初始化
            *[思路]调用java解析器进行解析内容
            *@param text java文本内容
            */
            "initNew": function(text) {
                //直接用解析类解析
                this.MY.java = this.MY.javaParser.parserJava(text);
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showTree
            *@description [功能]显示左边java的菜单树
            *[思路]使用treeView组件完成显示
            *[接口.this.MY.treeView]包含菜单树组件
            *[接口.this.MY.clickInfo]点击的状态记录，便于方便的恢复左侧菜单的点击事件
            */
            "showTree": function() {
                //处理默认的点击状态，如果点击状态为空，默认就是基础信息了
                if (this.MY.clickInfo == null) {
                    this.MY.clickInfo = "base";
                }
                //block(块){转换菜单树的数据结构
                //创建基本信息
                var treeData = [{
                    name: "基本信息",
                    type: "item",
                    clickInfo: "base"
                }]
                if (this.MY.clickInfo == "base") {
                    treeData[0].selected = true;
                }
                //创建属性
                if (this.MY.java.attributeFragment != null) {
                    var attribute = {
                        name: "成员变量",
                        type: "folder",
                        children: []
                    }
                    if (/attribute/i.test(this.MY.clickInfo)) {
                        attribute.expanded = true;
                    }
                    treeData.push(attribute);
                    for (var i = 0; i < this.MY.java.attributeFragment.length; i++) {
                        one = this.MY.java.attributeFragment[i];
                        var pushOne = {
                            name: one.name,
                            type: "item",
                            clickInfo: "attribute[" + i + "]"
                        };
                        if (this.MY.clickInfo == pushOne.clickInfo) {
                            pushOne.selected = true;
                        }
                        attribute.children.push(pushOne);
                    }
                }
                //加入基本的公有函数节点
                var publicfun = null;
                var privatefun = null;
                var defaultfun = null;
                var prodedtedfun = null;
                //while(遍历函数部分){分别加入不同的函数体
                for (var i = 0; this.MY.java.functionFragment != null && i < this.MY.java.functionFragment.length; i++) {
                    //获取其中一个
                    var one = this.MY.java.functionFragment[i];
                    var oneItem = null;
                    //if(公有函数){处理公有函数
                    if (one.type == "public") {
                        //处理公有函数
                        if (publicfun == null) {
                            publicfun = {
                                name: "public",
                                type: "folder",
                                children: []
                            }
                            treeData.push(publicfun);
                        }

                        var oneItem = {
                            name: one.name,
                            type: "item",
                            clickInfo: "function[" + i + "]"
                        }

                        publicfun.children.push(oneItem);
                        //处理节点是否展开
                        if (oneItem != null && this.MY.clickInfo == oneItem.clickInfo) {
                            oneItem.selected = true;
                            publicfun.expanded = true;
                        }
                    }
                    //}
                    //else if(私有函数){处理私有函数
                    else if (one.type == "private") {
                        //处理公有函数
                        if (privatefun == null) {
                            privatefun = {
                                name: "private",
                                type: "folder",
                                children: []
                            }
                            treeData.push(privatefun);
                        }

                        var oneItem = {
                            name: one.name,
                            type: "item",
                            clickInfo: "function[" + i + "]"
                        }
                        privatefun.children.push(oneItem);
                        //处理节点是否展开
                        if (oneItem != null && this.MY.clickInfo == oneItem.clickInfo) {
                            oneItem.selected = true;
                            privatefun.expanded = true;
                        }
                    }
                    //}
                    //else if(默认函数){处理默认函数
                    else if (one.type == "default") {
                        //处理公有函数
                        if (defaultfun == null) {
                            defaultfun = {
                                name: "default",
                                type: "folder",
                                children: []
                            }
                            treeData.push(defaultfun);
                        }

                        var oneItem = {
                            name: one.name,
                            type: "item",
                            clickInfo: "function[" + i + "]"
                        }
                        defaultfun.children.push(oneItem);
                        //处理节点是否展开
                        if (oneItem != null && this.MY.clickInfo == oneItem.clickInfo) {
                            oneItem.selected = true;
                            defaultfun.expanded = true;
                        }
                    }
                    //}
                    //else if(保护方法){处理保护方法
                    else if (one.type == "protected") {
                        //处理公有函数
                        if (prodedtedfun == null) {
                            prodedtedfun = {
                                name: "protected",
                                type: "folder",
                                children: []
                            }
                            treeData.push(prodedtedfun);
                        }

                        var oneItem = {
                            name: one.name,
                            type: "item",
                            clickInfo: "function[" + i + "]"
                        }
                        prodedtedfun.children.push(oneItem);
                        //处理节点是否展开
                        if (oneItem != null && this.MY.clickInfo == oneItem.clickInfo) {
                            oneItem.selected = true;
                            prodedtedfun.expanded = true;
                        }
                    }
                    //}
                }
                //}
                //}
                //使用组件显示菜单树
                this.MY.treeView.init(treeData);
                //设定回调方法
                var _this = this;
                this.MY.treeView.setEventCall(function(e, o) {
                    var info = o.info && o.info[0];
                    if (info.clickInfo) {
                        _this.API.private('clickEvent', info);
                    }
                })
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showBaseInfo
            *@description [功能]显示基本信息
            *[思路]从内存中获取接口，整理成表单自动生成，然后自动生成表单
            *表单的格式如下：
            *{
            *   field:{
            *         name:"名称",
            *         type:"Text/select...",
            *         valueRange:"[{a:b}]"
            *   }
            *}
            *[接口.this.MY.formOper]
            *[接口.this.MY.java]是整个java的内部存储结构体，和基本信息相关的如下：
            *{
            *	package:"com.breeze.abc.com",
            *	version:[{
            *		author: "lgy",
            *		description: "xxx",
            *		version: "1.0",
            *               date:日期
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
            *        design:设计链接
            *        unittest:单元测试
            *}
            */
            "showBaseInfo": function() {
                //整理表单数据
                var formDesc = {

                    "package": {
                        name: "包名",
                        type: "Text"
                    },
                    name: {
                        name: "类名",
                        type: "Text"
                    },
                    comments: {
                        name: "类说明",
                        type: "TextArea"
                    },
                    design: {
                        name: "设计链接",
                        type: "Text"
                    },
                    unittest: {
                        name: "测试链接",
                        type: "Text"
                    },
                    include: {
                        name: "import",
                        type: "List",
                        valueRange: ["Text"],
                        fun: {
                            tips: "收起",
                            fun: "hidden"
                        }
                    },
                    "extends": {
                        name: "extends",
                        type: "List",
                        valueRange: ["Text"]
                    },
                    "implements": {
                        name: "implements",
                        type: "List",
                        valueRange: ["Text"]
                    },
                    version: {
                        name: "版本信息",
                        type: "List",
                        valueRange: [{

                            version: {
                                name: "版本",
                                type: "Text"
                            },
                            date: {
                                name: "日期",
                                type: "Text"
                            },
                            author: {
                                name: "作者",
                                type: "Text",
                            },
                            description: {
                                name: "描述",
                                type: "Text"
                            }

                        }]
                    }
                }
                //在页面上显示
                this.API.show("baseInfo", null, "mainContent");
                this.MY.formOper.showForm("baseInfo", formDesc, this.MY.java);
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showAttribute
            *@description [功能]显示成员变量属性
            *[思路]使用统一的form表单实现，先整理数据，然后显示
            *[接口.this.MY.formOper]表单操作类
            *[接口.this.MY.java]java结构类，其中和本方法相关的属性如下：
            *{
            *	attributeFragment:{
            *		comments: "描述内容",
            *		type: "public",
            *		prifix: "static final",
            *		attType:"String"
            *		name: "newName",
            *		content: ""albb"",
            *	}
            *}
            *@param idx 要编辑的属性的索引
            */
            "showAttribute": function(idx) {
                //整理元数据
                var metadata = {
                    name: {
                        name: "变量名",
                        type: "Text"
                    },
                    "@": {
                        name: "@标注",
                        type: "Text"
                    },
                    type: {
                        name: "访问类型",
                        type: "Select",
                        valueRange: [{
                            "public": "public",
                            "protected": "protected",
                            "defalut": "default",
                            "private": "private"
                        }]
                    },
                    attType: {
                        name: "变量类型",
                        type: "Text"
                    },
                    prifix: {
                        name: "前缀信息",
                        type: "Text",
                        desc: "变量前的一些前缀修饰符，比如static final"
                    },
                    content: {
                        name: "初始值",
                        type: "TextArea"
                    },
                    comments: {
                        name: "注释说明",
                        type: "TextArea"
                    }

                }
                //获取实际的值
                var data = this.MY.java.attributeFragment[idx];
                //显示出来
                this.API.show("attrView", {
                    i: idx,
                    d: data
                },
                "mainContent");
                this.MY.formOper.showForm("attrInfo", metadata, data);
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showFunGraphy
            *@description [功能]显示函数流程图
            *[思路]获取函数对象，然后用js的流程图函数显示，因为这里函数结构已经和js保持兼容的，所以显示是可以重用的
            *[接口.this.MY.java]java代码的内部结构体
            *相关的函数部分结构，参见showFunBase函数说明
            *@param idx 函数对应的索引
            */
            "showFunGraphy": function(idx) {
                //获取函数主体对象
                var funObj = this.MY.java.functionFragment[idx];
                this.API.show("funGraphy", {
                    i: idx,
                    d: funObj
                },
                "mainContent");
                var _this = this;
                this.MY.graphyMod.destroy();
                this.MY.graphyMod.showFun(funObj,
                function(n, p) {
                    _this.showFunFragmentBase(n, p, idx);
                });
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showFunCode
            *@description [功能]显示某个函数的代码片段
            *[思路]获取这个函数的对象，然后调用对应的方法，将函数的代码片段显示出来
            *[接口.this.MY.java2code]内存对象到java的转换类
            *@param idx 函数的代码片段
            */
            "showFunCode": function(idx) {
                //找到函数对象
                var funObj = this.MY.java.functionFragment[idx];
                //获取代码文本，并格式化
                var code = this.MY.java2code.decodeFun(funObj);
                code = textformat.js_beautify(code);
                //block(块){用codemirror进行代码显示
                //调用显示方法显示
                this.API.show("funCode", {
                    i: idx
                },
                "mainContent");
                //准备回调函数
                var _this = this;
                var fun = {
                    getBaseTipsStruct: function() {
                        return _this.API.private('getBaseTipsStruct');
                    },
                    getVar: function() {
                        return _this.API.private('getVar', code);
                    },
                    getOneTipsStruct: function(objPath) {
                        return _this.API.private('getOneTipsStruct', objPath);
                    },
                    save: function() {
                        _this.API.private('saveOneFun', idx);
                    }
                }
                //显示代码
                this.MY.codeEditor.showText(code, "javascript", fun);
                //}
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$chuangeFunFragmentType
            *@description [功能]在函数片段编辑页面，改变类型后，将触发这个方法
            *[思路]调用form方法，获取对应type值，然后再反向回调回去，重新显示片段列表页面
            *@param fieldName 字段名
            */
            "chuangeFunFragmentType": function(fieldName) {
                //获取数据
                var type = this.MY.formOper.getFormData().type;
                //获取当前表单对象信息
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                var idx = pageObj.idx;
                //强制重新显示
                this.showFunFragmentBase(fragmentObj, myParent, idx, type);
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showFunFragmentBase
            *@description [功能]显示函数片段基本信息
            *[思路]用表单实现即可
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param fragmentObj 函数片段对象
            *@param parent 双亲对象
            *@param idx 对应函数索引
            *@param type 强制更改的类型
            默认用数据的，但是因为表单过程中是可以修改type类型重新显示的，这个参数支持这个
            */
            "showFunFragmentBase": function(fragmentObj, parent, idx, type) {
                //设定显示初值
                var dataDesc = {
                    type: {
                        name: "类型",
                        type: "Select",
                        valueRange: [{
                            "普通": "normal",
                            "分支": "branchList",
                            "循环": "cycle",
                            "代码块": "block"
                        }],
                        onchange: "chuangeFunFragmentType"
                    }
                }
                var useType = type || fragmentObj.type;
                var _this = this;
                //branchBlock时类型是只读的
                //--如果是分支，这里不能选择，只能readOnly
                if (useType == "branchBlock") {
                    dataDesc.type = {
                        name: "类型",
                        type: "ReadOnly"
                    }
                }
                //block(块){设定注释部分的数据描述
                //if(normal或者block情况){处理normal
                if (useType == "normal" || useType == "block") {
                    dataDesc.command = {
                        name: "做什么",
                        type: "Text"
                    }
                    dataDesc.subCommand = {
                        name: "子注释",
                        type: "List",
                        valueRange: [{
                            type: {
                                name: "类型",
                                type: "Select",
                                valueRange: [{
                                    tuDo: "++",
                                    "完成": "--"
                                }]
                            },
                            content: {
                                name: "注释内容",
                                type: "Text"
                            }
                        }]
                    }
                }
                //}
                //else if(循环情况或分支
                if (useType == "branchBlock" || useType == "cycle") {
                    dataDesc.condiction = {
                        name: "循环条件",
                        type: "Text"
                    }
                    dataDesc.command = {
                        name: "做什么",
                        type: "Text"
                    }
                    dataDesc.subCommand = {
                        name: "子注释",
                        type: "List",
                        valueRange: [{
                            type: {
                                name: "类型",
                                type: "Select",
                                valueRange: [{
                                    toDo: "++",
                                    "完成": "--"
                                }]
                            },
                            content: {
                                name: "注释内容",
                                type: "Text"
                            }
                        }]
                    }
                }
                //}
                //}
                //block(块){根据不同类型处理要显示数据
                //赋基本值
                var showData = {};
                showData.type = useType;
                showData.command = fragmentObj.command;
                showData.subCommand = fragmentObj.subCommand;
                //if(分支或循环情况){
                if (useType == "branchBlock" || useType == "cycle") {
                    //分解原来的注释部分
                    var orgData = showData.command;
                    var execResult = /\w\s*\((.*)\)\s*\{\s*(.*)/i.exec(orgData);
                    if (execResult != null) {
                        showData.command = execResult[1];
                        showData.condiction = execResult[2];
                    } else {
                        execResult = /else\{(\w*)/i.exec(orgData);
                        if (execResult != null) {
                            showData.command = "else";
                            showData.condiction = execResult[1];
                        }
                    }
                }
                //}
                //else if(代码块情况){
                else if (useType == "block") {
                    //分解原来的注释部分
                    var orgData = showData.command;
                    var execResult = /block\((.*)\)/i.exec(orgData);
                    if (execResult != null) {
                        showData.command = execResult[1];

                    }
                }
                //}
                //}
                //获取函数代码头部
                var funHead = this.MY.java2code.decodeFunHead(this.MY.java.functionFragment[idx]);
                //调用显示方法显示
                //--这里没有办法，只能将数据记录到对应的dom节点上
                this.API.show("funFragmentBase", {
                    i: idx,
                    funHead: funHead
                },
                "mainContent");

                var pageObj = $("#funFragmentBase")[0];
                pageObj.fragmentObj = fragmentObj;
                pageObj.parent = parent;
                pageObj.idx = idx;
                this.MY.formOper.showForm("funFragmentBase", dataDesc, showData);
                //block(块){显示片段代码
                //获取片段代码文本
                var code = this.MY.java2code.decodeFunFagment(fragmentObj);
                code = textformat.js_beautify(code);
                //初始化回调函数
                var fun = {
                    getBaseTipsStruct: function() {
                        return _this.API.private('getBaseTipsStruct');
                    },
                    getVar: function() {
                        var funObj = _this.MY.java.functionFragment[idx];
                        var funCode = _this.MY.java2code.decodeFun(funObj);
                        return _this.API.private('getVar', funCode);
                    },
                    getOneTipsStruct: function(objPath) {

                        return _this.API.private('getOneTipsStruct', objPath);
                    },
                    save: function() {
                        _this.API.private('saveFunFragment');
                    }
                }
                //显示代码
                this.MY.codeEditor.showText(code, "javascript", fun);
                //}
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showAllCode
            *@description [功能]显示源代码
            *[思路]调用java2code组件反向解析获取所有源代码，然后调用codeeditor显示
            */
            "showAllCode": function() {
                //显示基本视图
                this.API.show("codeView");
                //获取源代码
                //--注意要求格式化
                var code = this.MY.java2code.decodeJava(this.MY.java);

                code = textformat.js_beautify(code);
                //用于显示到页面
                var _this = this;
                var fun = {
                    getBaseTipsStruct: function() {
                        return _this.API.private('getBaseTipsStruct');
                    },
                    getVar: function() {
                        return _this.API.private('getVar', code);
                    },
                    getOneTipsStruct: function(objPath) {
                        return _this.API.private('getOneTipsStruct', objPath);
                    },
                    save: function() {
                        var text = _this.MY.codeEditor.getText();
                        _this.initNew(text);
                        _this.save();
                        FW.alert("保存成功");
                        _this.showAllCode();
                    }
                }
                //显示代码
                this.MY.codeEditor.showText(code, "javascript", fun);
                //显示控制器
                this.showCodeCompile({
                    msg: "信息窗口...."
                });
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showFunBase
            *@description [功能]显示函数的基本信息
            *[思路]函数有三个信息，基本信息，流程图，和代码。这里默认基本信息是一定必显示的，而且是三个的入口
            *注意：注释部分的结构要展开到上级对象中
            *[接口.this.MY.java]java代码的内部结构体，其中和本函数相关的结构如下：
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
            *@param idx 第几个函数的索引
            */
            "showFunBase": function(idx) {
                //整理元数据
                var metadata = {
                    name: {
                        name: "函数名",
                        type: "Text"
                    },
                    type: {
                        name: "访问类型",
                        type: "Select",
                        valueRange: [{
                            "public": "public",
                            "protected": "protected",
                            "defalut": "default",
                            "private": "private"
                        }]
                    },
                    comments: {
                        name: "注释说明",
                        type: "TextArea"
                    },
                    prifix: {
                        name: "前缀信息",
                        type: "Text",
                        desc: "变量前的一些前缀修饰符，比如static final"
                    },
                    "@": {
                        name: "@标注",
                        type: "Text",
                        desc: "如:Override"
                    },
                    parameters: {
                        name: "参数",
                        type: "List",
                        valueRange: [{
                            name: {
                                name: "参数名",
                                type: "Text"
                            },
                            type: {
                                name: "参数类型",
                                type: "Text"
                            },
                            desc: {
                                name: "参数说明",
                                type: "TextArea"
                            }
                        }]
                    },
                    throws: {
                        name: "抛出异常",
                        type: "List",
                        valueRange: ["Text"]
                    },
                    returnDesc: {
                        name: "返回值说明",
                        type: "Text"
                    },
                    "return": {
                        name: "返回值类型",
                        type: "Text"
                    },
                    example: {
                        name: "样例说明",
                        type: "Text"
                    }

                }
                //获取实际的值
                //--要将注释部分展开出来
                var data = {};
                for (var n in this.MY.java.functionFragment[idx]) {
                    data[n] = this.MY.java.functionFragment[idx][n];
                }
                for (var n in this.MY.java.functionFragment[idx].comments) {
                    if (n == "description") {
                        data.comments = this.MY.java.functionFragment[idx].comments[n];
                        continue;
                    }
                    if (n == "return") {
                        data.returnDesc = this.MY.java.functionFragment[idx].comments[n];
                        continue;
                    }
                    data[n] = this.MY.java.functionFragment[idx].comments[n];
                }
                var paramComments = this.MY.java.functionFragment[idx].comments && this.MY.java.functionFragment[idx].comments.param;
                if (paramComments) {
                    for (var i = 0; data.parameters && i < data.parameters.length; i++) {
                        data.parameters[i].desc = paramComments[data.parameters[i].name];
                    }
                }
                //显示出来
                this.API.show("baseFun", {
                    i: idx,
                    d: data
                },
                "mainContent");
                this.MY.formOper.showForm("funBase", metadata, data);
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$compileJava
            *@description [功能]编译java
            *[思路]先保存，再编译，编译完后，将编译结果返回
            */
            "compileJava": function() {
                //保存代码
                var code = this.save();
                if (code != 0) {
                    FW.alert("代码保存失败");
                }
                //编译代码
                var param = {
                    java: this.MY.java.name,
                    package: this.MY.java.package
                }
                FW.alert("开始编译");
                var result = this.API.doServer("javac", "class", param);
                //显示编译结果
                var data = "编译成功";
                if (result.code != 0) {
                    data = result.data;
                }
                this.showCodeCompile({
                    msg: data
                });
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showCodeCompile
            *@description [功能]显示代码调试框
            *[思路]直接显示内容即可
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param tips 编译的表述数据
            */
            "showCodeCompile": function(tips) {
                //显示视图
                var data = tips || "";
                this.API.show("codeCompile", data, "codeTools");
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$loadJava
            *@description [功能]和后台配合将编译好的类加载到后台
            *[思路]调用后台实现
            */
            "loadJava": function() {
                //加载代码
                var param = {
                    java: this.MY.java.name,
                    package: this.MY.java.package
                }
                FW.alert("开始加载");
                var result = this.API.doServer("loadClass", "class", param);
                //显示加载结果
                var data = "加载成功";
                if (result.code != 0) {
                    data = result.data;
                }
                this.showCodeCompile({
                    msg: data
                });
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showAllTemplate
            *@description [功能]这显示所有可用魔板
            */
            "showAllTemplate": function() {
                //直接显示
                var data = ["workflowunit.java", "btlfun.java", "singlecheck.java", "workflowtemplate.java"];
                this.API.show("allTemplate", data, "mainContent");
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showContent
            *@description [功能]对外文件管理器的接口，显示主体内容
            *[思路]这里描述实现的基本思路，如果模板名称参数为空，就是正常调用，否则就是强制的使用模板加载了
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param templateName 模板名称
            */
            "showContent": function(templateName) {
                //设置存储key值
                var savekey = "editJava" + this.param.idx;
                //if(有参数){就保存到本地存储
                if (this.param.fileUrl) {
                    FW.use().save(savekey, this.param.fileUrl, true);
                }
                //}
                //else{从本地存储中取参数
                else {
                    this.param.fileUrl = FW.use().load(savekey, true);
                }
                //}
                //调用框架，处理文件系统
                var settingdata = {
                    gadget: "editJava",
                    url: this.param.fileUrl
                }
                this.param.main.setFileHash(FW.use().toJSONString(settingdata));
                //加载文件
                //--原来没有加载过，即没缓存就加载
                //--要加载模板时，强制重新加载
                if (!this.MY.java || templateName != null) {
                    this.initByFileParam(templateName);
                }
                this.API.show("main");
                this.showTree();
                this.API.private('clickEvent');
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$initByFileParam
            *@description [功能]用输入的公有的文件参数进行初始化工作
            *[思路]用默认的参数进行初始化
            *@param templateName 模板名称，如果有输入，强制用模板加载
            */
            "initByFileParam": function(templateName) {
                //获取url参数
                var fileUrl = this.param.fileUrl;
                //block(代码块){构造文件对象读取对象
                var text = null;
                var fileName = null;
                var fileDir = null;
                //if (传入的url不为空){获取文件信息
                if (fileUrl != null) {
                    //读取文件
                    var fileArr = fileUrl.split("/");
                    fileName = fileArr.pop();
                    this.MY.fileName = fileName;
                    fileDir = "/" + fileArr.join("/");
                    this.MY.fileDir = fileDir;

                    if (templateName == null) {
                        this.MY.fileOper.setFileName(fileName);
                        this.MY.fileOper.setPath(fileDir);
                        text = this.MY.fileOper.queryFileContent();
                    }
                }
                //}
                //else{就是url不存在了，要退货
                else {
                    return;
                }
                //}
                //if(文件本身不存在){读取默认文件
                if (text == null) {
                    //检查template是否存在，存在就读取此为模板
                    var template = FW.use().getParameter();
                    //读取文件
                    fileName = templateName || "workflowunit.java";
                    fileDir = "/manager_auxiliary/template/java";
                    this.MY.fileOper.setFileName(fileName);
                    this.MY.fileOper.setPath(fileDir);
                    text = this.MY.fileOper.queryFileContent();
                }
                //}
                //if (文件还是为空){这说明出错了
                if (text == null) {
                    //alert错误并退出
                    alert("文件读取失败");
                    return;
                }
                //}
                //}
                this.initNew(text);
            },
            /**
            *@function
            *@memberOf editJava
            *@name public$showCodeDebug
            *@description [功能]显示和后台交互的调试信息
            *[思路]吧调试信息记录到一个数组中，放入内存，实时的显示这个信息，注意，显示时，是要发送请求，获取内容的，这个将会阻塞住页面
            *[this.MY.debugMsg]=["调试信息"]
            *[service.class.getDegugMsg]
            *param={
            *   threadSignal:xxxx
            *}
            *return={
            *   code:0,
            *   data:[
            *      [
            *           "内容",
            *           "类名",,
            *           "行号"
            *     ]
            *  ]
            *}
            *@param threadSignal 日志调试标识，如果没有，直接显示内容，否则先发请求获取信息，获取完后再把数据加入内存中，然后显示
            */
            "showCodeDebug": function(threadSignal) {
                //如果传入了参数则请求信的数组内容
                if (threadSignal != null) {
                    var result = this.API.doServer("getDegugMsg", "class", {
                        threadSignal: threadSignal
                    });
                    if (result.code != 0) {
                        FW.alert("获取调试信息失败");
                        return;
                    }
                    if (!result.data) {
                        FW.alert("没有更多信息了，请重新设置");
                        return;
                    }
                    if (this.MY.debugMsg == null) {
                        this.MY.debugMsg = [];
                    }
                    this.MY.debugMsg.push(result.data);
                    if (this.MY.java.package + "." + this.MY.java.name == result.data[1]) {
                        this.MY.codeEditor.spesLine(result.data[2], "yellow", true);
                    }
                }
                //显示页面
                this.API.show("codeDebug", {
                    threadSignal: threadSignal || this.MY.threadSignal || "",
                    msgArr: this.MY.debugMsg || [],
                    cs: this.MY.java.package + "." + this.MY.java.name
                },
                "codeTools");
            }
        },
        "private": {
            /**
            *@function
            *@memberOf editJava
            *@name private$clickEvent
            *@description [功能]树的点击调用方法
            *[思路]这里调用treeview里面的setEventCall方法设定回调的函数
            *@param o 对象
            */
            "clickEvent": function(o) {
                //清空对应对象
                this.MY.graphyMod.destroy();
                //获取节点信息
                var clickInfo = null;
                if (o && o.clickInfo) {
                    clickInfo = o.clickInfo;
                    this.MY.clickInfo = o.clickInfo;
                } else {
                    clickInfo = this.MY.clickInfo;
                }
                if (clickInfo == null) {
                    clickInfo = this.MY.clickInfo = "base";
                }
                //分解点击信息
                var execresult = /(\w+)\[?(\d*)\]?/i.exec(clickInfo);
                if (execresult == null) {
                    return;
                }
                var w = execresult[1];
                var idx = execresult[2];
                //if (是baseinfo){
                if (w == "base") {
                    this.showBaseInfo();
                    return;
                }
                //}
                //else if(是属性情况){处理属性情况
                else if (w == "attribute") {
                    this.showAttribute(idx);
                    return;
                }
                //}
                //else if(是函数情况){处理函数
                else if (w == "function") {
                    this.showFunBase(idx);
                    return;
                }
                //}
            },
            /**
            *@function
            *@memberOf editJava
            *@name private$getVar
            *@description [功能]获取变量对象，给codeMirror组件使用的
            *[思路]把函数弄过来，然后正则表达式判断一下函数内部的变量，再获取内部attribute结构一把
            *@param code 代码内容
            */
            "getVar": function(code) {
                //声明结果变量
                var result = {};
                //获取函数级别的成员变量
                var code = /\([\s\S]*$/i.exec(code)[0];
                code = code.replace(/\\"/g, "").replace(/"[^"]*"/g, "");
                var exp1 = /(\w+)\s+(\w+)\s*=?[^,;\)\}]*[,;\)\}]/ig
                while (true) {
                    var execResult = exp1.exec(code);
                    if (execResult == null) {
                        break;
                    }
                    if (execResult[1] == "return" || execResult[1] == "public" || execResult[1] == "private" || execResult[1] == "new") {
                        continue;
                    }
                    result[execResult[2]] = execResult[1];
                    if (this.MY.tips[execResult[1]] == null) {
                        this.MY.tips[execResult[1]] = false;
                    }
                }

                var exp2 = /(\w+)\s*\[[^=]*\]\s*(\w*)\s*=?[^,;\)\}]*[,;\)\}]/ig
                while (true) {
                    var execResult = exp2.exec(code);
                    if (execResult == null) {
                        break;
                    }
                    if (execResult[1] == "return" || execResult[1] == "public" || execResult[1] == "private" || execResult[1] == "new") {
                        continue;
                    }
                    result[execResult[2]] = "+Array";
                }

                var exp3 = /(\w+)\s*<[^=]+>\s*(\w+)\s*=?[^,;\)\}]*[,;\)\}]/ig
                while (true) {
                    var execResult = exp3.exec(code);
                    if (execResult == null) {
                        break;
                    }
                    if (execResult[1] == "return" || execResult[1] == "public" || execResult[1] == "private" || execResult[1] == "new") {
                        continue;
                    }
                    result[execResult[2]] = execResult[1];
                    if (this.MY.tips[execResult[1]] == null) {
                        this.MY.tips[execResult[1]] = false;
                    }
                }
                return result;
            },
            /**
            *@function
            *@memberOf editJava
            *@name private$saveOneFun
            *@description [功能]保存一个函数
            *[思路]从editor对象中获取代码，然后通过函数将其反射到内存结构中，然后再保存
            *@param idx 函数索引
            */
            "saveOneFun": function(idx) {
                //获取函数文本
                var code = this.MY.codeEditor.getText();
                //获取函数对象
                var funStruct = this.MY.javaParser.parserJavaFun(code);
                //获取原函数对象
                var orgFun = this.MY.java.functionFragment[idx];
                console.log(funStruct);
                console.log(orgFun);
                //block(块){更新内存信息
                //更新函数片段
                orgFun.fragments = funStruct.fragments;
                //更新名字
                var nameChanged = false;
                if (orgFun.name != funStruct.name) {
                    orgFun.name = funStruct.name;
                    nameChanged = true;
                }
                //更新参数
                //--先将原来信息保存到对象中
                //--再将数组赋值到新的部分
                //--然后遍历新数组，将原来相等的部分反向覆盖回去
                //--这样做，主要是考虑新的变量可能有插入，变更等调整，而就的参数保留了注释信息
                var oldParam = {};
                for (var i = 0; orgFun.parameters && i < orgFun.parameters.length; i++) {
                    oldParam[orgFun.parameters[i].name] = orgFun.parameters[i];
                }
                orgFun.parameters = funStruct.parameters;
                for (var i = 0; orgFun.parameters && i < orgFun.parameters.length; i++) {
                    var name = orgFun.parameters[i].name;
                    if (oldParam[name]) {
                        var type = orgFun.parameters[i].type;
                        orgFun.parameters[i] = oldParam[name];
                        orgFun.parameters[i].type = type;
                    }
                }
                //更新前缀
                orgFun.prifix = funStruct.prifix;
                //处理return 
                orgFun["return"] = funStruct["return"];
                //处理type
                if (orgFun.type != funStruct.type) {
                    nameChanged = true;
                    orgFun.type = funStruct.type;
                }
                //}
                //根据情况，决定是否刷新菜单树
                if (nameChanged) {
                    this.showTree();
                }
                this.save();
                alert("保存成功 ");
            },
            /**
            *@function
            *@memberOf editJava
            *@name private$saveFunFragment
            *@description [功能]保存一个函数的片段
            *[思路]将这个片段转换成对象，然后在父亲处获取子数组，获取完后，在子数组中找到原来的对象，然后用新的对象替换掉原来的
            */
            "saveFunFragment": function() {
                //获取原来的代码对象信息
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                var idx = pageObj.idx;
                //获取代码片段转换成对象
                var code = this.MY.codeEditor.getText();
                var newCodeObjs = this.MY.javaParser.parserJavaFunFragment(code);
                console.log(newCodeObjs);
                //找到原来片段的位置
                var arr = this.MY.java.functionFragment[idx].fragments;
                if (myParent != null) {
                    arr = myParent.subList;
                }
                var fidx = 0;
                for (var i = 0; i < arr.length; i++) {
                    if (arr[i] == fragmentObj) {
                        fidx = i;
                        break;
                    }
                }
                //block(块){替换成新的代码片段
                //if(父亲是分支类型且新解析也是分支类型){把分支类型下面部分替换掉
                if (myParent && myParent.type == "branchList" && newCodeObjs[0].type == "branchList") {
                    //替换的是儿子
                    arr.splice(fidx, 1);
                    for (var i = newCodeObjs[0].subList.length - 1; i >= 0; i--) {
                        arr.splice(fidx, 0, newCodeObjs[0].subList[i]);
                    }
                }
                //}
                //else if(父亲是分支类型，自己不是分支类型){报错，终止替换
                else if (myParent && myParent.type == "branchList" && newCodeObjs[0].type != "branchList") {
                    //报错并终止
                    alert("必须是if语句");
                    return;
                }
                //}
                //else{正常替换
                else {
                    //正常替换
                    arr.splice(fidx, 1);
                    for (var i = newCodeObjs.length - 1; i >= 0; i--) {
                        arr.splice(fidx, 0, newCodeObjs[i]);
                    }
                }
                //}
                //}
                //结束保存
                this.save();
                alert("保存成功");
                //重新显示
                //--fragmentObj要改成编辑后的数组第一个东西
                this.showFunFragmentBase(newCodeObjs[0], parent, idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name private$getOneTipsStruct
            *@description [功能]获取其中一个Tips结构，被codeEditor调用，提示会有一些基本类型，而这里就是返回基本类型还没有补充的结构。这里要远程调用后台，由后台通过反射把提示信息获取到
            *[思路]获取其中一个tips结构信息
            *@param objPath 对象路径
            */
            "getOneTipsStruct": function(objPath) {
                //设置基本变量
                var param = {
                    path: objPath
                };
                //获取import信息
                param.include = this.MY.java.include;
                //获取包信息
                param.package = this.MY.java.package;
                //远程调用获取内容
                var result = this.API.doServer("getTipsStruct", "class", param);
                //查找要找的提示对象
                var pathArr = objPath.split('.');
                var oneTips = this.MY.tips;
                for (var i = 0; i < pathArr.length - 1; i++) {
                    if (oneTips == null) {
                        break;
                    }
                    oneTips = oneTips[pathArr[i]];
                }
                if (oneTips == null) {
                    alert('无法将提示信息加入缓存');
                    return result.data;
                }
                //将结果挂到内存中，下次请求就不用远端取
                if (result.code != 0) {
                    oneTips[pathArr[pathArr.length - 1]] = null;
                    return {};
                }

                oneTips[pathArr[pathArr.length - 1]] = result.data;
                return result.data;
            },
            /**
            *@function
            *@memberOf editJava
            *@name private$getBaseTipsStruct
            *@description [功能]辅助，获取整个提示的结构数据，这个是给showFunCode的codeMirror组件回调用的
            *[思路]硬编码上去
            *将信息保存在
            *[参数.this.MY.tips]中，如果没有就创建新的
            *每个值中的类型，true表示已经到头了，String，表示可以继续，且值是可以转换的，false表示要远程请求
            *结构如下：
            *{
            *   obj:true,
            *   class1:{
            *       member1:true
            *       member2:false,
            *       member3:"Context"
            *   }
            *}
            *这里true，表示提示已经结束，false表示这个分支还未结束，要到后台继续获取，字符串表示，这个字符串的内容是另外一个类型，应该重新到这个提示对象的顶级节点继续获取。
            */
            "getBaseTipsStruct": function() {
                //如果内存中已经存在，直接返回
                if (this.MY.tips) {
                    return this.MY.tips;
                }
                //声明结果变量
                var result = {};
                //设定关键字
                result["new"] = true;
                result["synchronized(Object){}"] = true;
                result["break"] = true;
                result["continue"] = true;
                result["return"] = true;
                result["while(true){}"] = true;
                result["else"] = true;
                result["else if"] = true;
                result["for"] = true;
                result["instanceof"] = true;
                result["try{}"] = true;
                result["catch(Exception e){}"] = true;
                result["finally{}"] = true;
                result["boolean"] = true;
                result["byte"] = true;
                result["char"] = true;
                result["double"] = true;
                result["float"] = true;
                result["int"] = true;
                result["long"] = true;
                result["short"] = true;
                result["null"] = true;
                result["true"] = true;
                result["false"] = true;
                //特殊内容
                result["+Array"] = {
                    length: true,
                    "toString()": true
                }
                //本类的对象方法
                //--注意，函数有过载情况，所以可能有同名的函数
                //--注意静态方法和非静态方法是分离的
                var nat = result["this"] = {};
                for (var i = 0; this.MY.java.attributeFragment && i < this.MY.java.attributeFragment.length; i++) {
                    if (this.MY.java.attributeFragment[i].prifix && /static/.test(this.MY.java.attributeFragment[i].prifix)) {
                        continue;
                    }
                    nat[this.MY.java.attributeFragment[i].name] = this.MY.java.attributeFragment[i].attType;
                    if (result[this.MY.java.attributeFragment[i].attType] == null) {
                        result[this.MY.java.attributeFragment[i].attType] = false;
                    }
                }
                for (var i = 0; this.MY.java.functionFragment && i < this.MY.java.functionFragment.length; i++) {
                    if (this.MY.java.functionFragment[i].prifix && /static/.test(this.MY.java.functionFragment[i].prifix)) {
                        continue;
                    }
                    if (nat[this.MY.java.functionFragment[i].name] == null) {
                        nat[this.MY.java.functionFragment[i].name] = {};
                    }
                    var param = "(";
                    for (var j = 0; this.MY.java.functionFragment[i].parameters && j < this.MY.java.functionFragment[i].parameters.length; j++) {
                        if (j > 0) {
                            param += ',';
                        }
                        param += this.MY.java.functionFragment[i].parameters[j].name;
                    }
                    param += ")";
                    nat[this.MY.java.functionFragment[i].name][param] = true;
                }
                for (var i = 0; this.MY.java.attributeFragment && i < this.MY.java.attributeFragment.length; i++) {
                    result[this.MY.java.attributeFragment[i].name] = this.MY.java.attributeFragment[i].attType;
                    result[this.MY.java.attributeFragment[i].attType.attType] = false;
                    if (!this.MY.java.attributeFragment[i].prifix || !/static/.test(this.MY.java.attributeFragment[i].prifix)) {
                        nat[this.MY.java.attributeFragment[i].name] = this.MY.java.attributeFragment[i].attType;
                    }

                }
                //本类的静态方法
                //--注意，函数有过载情况，所以可能有同名的函数
                //--注意静态方法和非静态方法是分离的
                var nat = result[this.MY.java.name] = {};
                for (var i = 0; this.MY.java.attributeFragment && i < this.MY.java.attributeFragment.length; i++) {
                    if (!this.MY.java.attributeFragment[i].prifix || !/static/.test(this.MY.java.attributeFragment[i].prifix)) {
                        continue;
                    }
                    nat[this.MY.java.attributeFragment[i].name] = this.MY.java.attributeFragment[i].attType;
                    if (result[this.MY.java.attributeFragment[i].attType] == null) {
                        result[this.MY.java.attributeFragment[i].attType] = false;
                    }
                }
                for (var i = 0; this.MY.java.functionFragment && i < this.MY.java.functionFragment.length; i++) {
                    if (!this.MY.java.functionFragment[i].prifix || !/static/.test(this.MY.java.functionFragment[i].prifix)) {
                        continue;
                    }
                    if (nat[this.MY.java.functionFragment[i].name] == null) {
                        nat[this.MY.java.functionFragment[i].name] = {};
                    }
                    var param = "(";
                    for (var j = 0; this.MY.java.functionFragment[i].parameters && j < this.MY.java.functionFragment[i].parameters.length; j++) {
                        if (j > 0) {
                            param += ',';
                        }
                        param += this.MY.java.functionFragment[i].parameters[j].name;
                    }
                    param += ")";
                    nat[this.MY.java.functionFragment[i].name][param] = true;
                }
                this.MY.tips = result;
                return result;
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$setBaseInfo
            *@description [功能]从表单数据中获取真正的基础数据信息
            *[思路]从表单中获取后，要遍历成员逐个赋值
            */
            "setBaseInfo": function() {
                //获取表单数据
                var　data = this.MY.formOper.getFormData();
                for (var n in data) {
                    this.MY.java[n] = data[n];
                }
                //保存
                var result = this.save();
                if (result == 0) {
                    FW.alert("基本信息保存成功");
                } else {
                    FW.alert("基本信息保存失败");
                }
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$setAttribute
            *@description [功能]设置一个属性值
            *[思路]获取内存值并改变之，然后重新显示
            *@param idx 属性索引
            */
            "setAttribute": function(idx) {
                //获取原来的属性值
                var attr = this.MY.java.attributeFragment[idx];
                if (attr == null) {
                    return;
                }
                //获取页面上的值
                var　data = this.MY.formOper.getFormData();
                for (var n in data) {
                    attr[n] = data[n];
                }
                var result = this.save();
                if (result == 0) {
                    FW.alert("基本信息保存成功");
                } else {
                    FW.alert("基本信息保存失败");
                }
                //重新显示左侧树
                //--可能会修改名称
                this.showTree();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$delAttribute
            *@description [功能]删除一个属性，左边菜单则重新回到标准的页面去
            *@param idx 要删除的索引号
            */
            "delAttribute": function(idx) {
                //直接删除
                this.MY.java.attributeFragment.splice(idx, 1);
                //重新显示
                this.MY.clickInfo = null;
                this.showTree();
                this.save();
                this.API.private('clickEvent');
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$setFunBase
            *@description [功能]设置函数基本信息
            *[思路]从表单中读取信息，然后设置，最后左侧菜单重新刷新显示
            *[接口.this.MY.java]这里描述java代码的所有内部结构
            *@param idx 函数的索引
            */
            "setFunBase": function(idx) {
                //获取原来的属性值
                var funBase = this.MY.java.functionFragment[idx];
                if (funBase == null) {
                    return;
                }
                //获取页面上的值
                var　data = this.MY.formOper.getFormData();
                if (!funBase.comments) {
                    funBase.comments = {};
                }
                for (var n in data) {
                    if (n == "comments") {
                        funBase.comments.description = data[n];
                        continue;
                    }
                    if (n == "example") {
                        funBase.comments.example = data[n];
                        continue;
                    }
                    if (n == "returnDesc") {
                        funBase.comments["return"] = data[n];
                        continue;
                    }
                    if (n == "parameters") {
                        for (var i = 0; i < data[n].length; i++) {
                            if (data[n][i].desc) {
                                if (!funBase.comments.param) {
                                    funBase.comments.param = {}
                                }
                                funBase.comments.param[data[n][i].name] = data[n][i].desc;
                            }
                        }
                    }
                    funBase[n] = data[n];
                }
                var result = this.save();
                if (result == 0) {
                    FW.alert("基本信息保存成功");
                } else {
                    FW.alert("基本信息保存失败");
                }
                //重新显示左侧树
                //--可能会修改名称
                this.showTree();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$delFun
            *@description [功能]删除函数
            *[思路]从数据源里面删除，然后重新显示
            *[接口.this.MY.java]java的内部结构体
            *[接口.this.MY.clickInfo]点击信息记录体，要清除掉
            *@param idx 函数的索引
            */
            "delFun": function(idx) {
                //直接删除
                this.MY.java.functionFragment.splice(idx, 1);
                //重新显示
                this.MY.clickInfo = null;
                this.showTree();
                this.save();
                this.API.private('clickEvent');
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$showFunGraphy
            *@description [功能]调用公有方法去显示图形
            *[思路]这里调用公有方法去显示图形
            *@param idx 索引
            */
            "showFunGraphy": function(idx) {
                //调用公有函数实现
                this.showFunGraphy(idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$setFunFragmentBase
            *@description [功能]设置函数片段信息
            *[思路]因为图形页面没有记录对应函数索引信息，所以这里，只能用dom节点记录这些信息
            */
            "setFunFragmentBase": function() {
                //获取页面对象的值
                var　data = this.MY.formOper.getFormData();
                //从页面dom中获取要设置的值
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                console.log(fragmentObj);
                //block(整理这个值){
                //if(类型是分支情况){处理分支
                if (data.type == "branchBlock") {
                    //接收参数
                    var command = data.command;
                    var condiction = data.condiction;
                    delete data.condiction;
                    //获取自己的排位
                    //--入股是第一个，一定是 if
                    //--否则就是else if
                    //--填入的是空，或者是else或者是其他，则为else
                    var pos = 0;
                    for (var i = 0; i < myParent.subList.length; i++) {
                        if (fragmentObj == myParent.subList[i]) {
                            pos = i;
                            break;
                        }
                    }
                    //重新设置值
                    if (pos == 0) {
                        data.command = "if(" + condiction + "){" + command;

                    } else if (pos == myParent.subList.length - 1 && (condiction == null || condiction == "" || condiction == "else" || condiction == "其他")) {
                        data.command = "else{" + command;

                    } else {
                        data.command = "else if(" + condiction + "){" + command;
                    }
                }
                //}
                //else if(类型是循环情况){
                else if (data.type == "cycle") {
                    //获取基本值
                    var command = data.command;
                    var condiction = data.condiction;
                    delete data.condiction;
                    data.command = "while(" + condiction + "){" + command;
                }
                //}
                //else if(类型是代码块情况){
                else if (data.type == "block") {
                    //直接设置值
                    var command = data.command;
                    data.command = "block(" + command + "){";
                }
                //}
                //}
                //设置这个值
                for (var n in data) {
                    fragmentObj[n] = data[n];
                }

                this.save();
                FW.alert("修改成功");
                //重新显示
                this.showFunFragmentBase(fragmentObj, myParent, pageObj.idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$inserFragmentBefore
            *@description [功能]前插一个函数片段
            *[思路]数据结构上前插入，当然要配合parent处理
            *相关的数据都记录在dom节点上了
            *var pageObj = $("#funFragmentBase")[0];
            *    pageObj.fragmentObj = fragmentObj;
            *    pageObj.parent = parent;
            *    pageObj.idx = idx;
            */
            "inserFragmentBefore": function() {
                //找到要处理的数组
                //--如果是顶级节点，就是一级数组，否则从parent中获取这个数组
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                var arr = [];
                if (myParent == null) {
                    arr = this.MY.java.functionFragment[pageObj.idx].fragments;
                } else {
                    arr = myParent.subList;
                }
                //找到要移动节点的位置
                var idx = -1;
                for (i = 0; i < arr.length; i++) {
                    if (fragmentObj == arr[i]) {
                        idx = i;
                        break;
                    }
                }
                //用户js数组函数splice进行移动
                if (idx == -1) {
                    FW.alert("错误，节点未找到");
                    return;
                }
                var newObj = {
                    type: "normal",
                    command: "这里描述注释内容"
                };
                arr.splice(idx, 0, newObj);
                //重新显示
                this.showFunFragmentBase(newObj, myParent, pageObj.idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$appendFragmentAfter
            *@description [功能]在图形的后面追加片段
            *[思路]在数据结构中添加即可
            */
            "appendFragmentAfter": function() {
                //找到要处理的数组
                //--如果是顶级节点，就是一级数组，否则从parent中获取这个数组
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                var arr = [];
                if (myParent == null) {
                    arr = this.MY.java.functionFragment[pageObj.idx].fragments;
                } else {
                    arr = myParent.subList;
                }
                //找到要移动节点的位置
                var idx = -1;
                for (i = 0; i < arr.length; i++) {
                    if (fragmentObj == arr[i]) {
                        idx = i;
                        break;
                    }
                }
                //用户js数组函数splice进行移动
                if (idx == -1) {
                    FW.alert("错误，节点未找到");
                    return;
                }
                var newObj = {
                    type: "normal",
                    command: "这里描述注释内容"
                };
                //处理如果上层是分支的情况
                if (myParent.type == "branchList") {
                    newObj.type = "branchBlock";
                }
                arr.splice(idx + 1, 0, newObj);
                //重新显示
                this.showFunFragmentBase(newObj, myParent, pageObj.idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$insertFragmentInside
            *@description [功能]在里头插入儿子
            *[思路]直接操作内存数据，然后重新显示
            */
            "insertFragmentInside": function() {
                //找到要处理的数组
                //--如果是顶级节点，就是一级数组，否则从parent中获取这个数组
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                var arr = [];
                if (fragmentObj.type == "normal") {
                    FW.alert("该类型不能够添加");
                    return;
                }
                if (fragmentObj.subList) {
                    arr = fragmentObj.subList;
                } else {
                    fragmentObj.subList = arr;
                }
                //往数组里头加数据
                //--这里要根据添加的是否是分支，如果是分支，要实际的变成branchBlock
                var newType = "normal";
                if (fragmentObj.type == "branchList") {
                    newType = "branchBlock";
                }
                var newObj = {
                    type: newType,
                    command: "这里描述注释内容"
                };
                arr.push(newObj);
                //重新显示
                this.showFunFragmentBase(newObj, fragmentObj, pageObj.idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$deleteFragment
            *@description [功能]删除其中一个片段
            *[思路]将内存删除，然后返回图形
            */
            "deleteFragment": function() {
                //要处理的数组节点
                //--如果是顶级节点，就是一级数组，否则从parent中获取这个数组
                var pageObj = $("#funFragmentBase")[0];
                var fragmentObj = pageObj.fragmentObj;
                var myParent = pageObj.parent;
                var arr = [];
                if (myParent == null) {
                    arr = this.MY.java.functionFragment[pageObj.idx].fragments;
                } else {
                    myParent.subList;
                }
                //找到要移动节点的位置
                var idx = -1;
                for (i = 0; i < arr.length; i++) {
                    if (fragmentObj == arr[i]) {
                        idx = i;
                        break;
                    }
                }
                //用户js数组函数splice进行移动
                if (idx == -1) {
                    FW.alert("错误，节点未找到");
                    return;
                }
                arr.splice(idx, 1);
                //重新显示
                this.showFunGraphy(pageObj.idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$showFunBase
            *@description [功能]显示函数的基本信息
            *[思路]调用公有方法实现即可
            *@param idx 函数的索引
            */
            "showFunBase": function(idx) {
                //调用公有方法实现
                this.showFunBase(idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$showFunCode
            *@description [功能]显示代码文本信息
            *[思路]调用公有方法实现调用
            *@param idx idx
            */
            "showFunCode": function(idx) {
                //调用公有方法显示
                this.showFunCode(idx);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$showAllCode
            *@description [功能]显示全部代码
            *[思路]调用对应的公有函数实现显示
            */
            "showAllCode": function() {
                //调用公有函数实现
                this.showAllCode();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$showBaseInfo
            *@description [功能]显示基本函数信息
            *[思路]调用对应的公有方法显示
            */
            "showBaseInfo": function() {
                //还原所有内容
                this.API.show("main");
                this.showTree();
                //调用公有方法
                this.showBaseInfo();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$compileJava
            *@description [功能]调用公有方法进行编译
            *[思路]调用公有方法进行编译
            */
            "compileJava": function() {
                //调用公有方法编译
                this.compileJava();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$loadJava
            *@description [功能]加载编译好的类到内存中实时生效
            *[思路]编译好的类实时生效
            */
            "loadJava": function() {
                //调用公有方法实现
                this.loadJava();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$newAttr
            *@description [功能]创建一个新属性
            *[思路]直接操作内存，创建一个新属性，然后在页面上修改
            */
            "newAttr": function() {
                //创建新方法
                var one = {
                    name: "newAttr",
                    type: "public",
                    attType: "String",
                    comments: "",
                    prifix: "",
                    content: ""
                }
                if (!this.MY.java.attributeFragment) {
                    this.MY.java.attributeFragment = [];
                }
                this.MY.java.attributeFragment.push(one);
                //重新显示左侧菜单
                this.showTree();
                //显示属性编辑页面
                this.showAttribute(this.MY.java.attributeFragment.length - 1);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$newJavaFun
            *@description [功能]创建一个新的java方法
            *[思路]创建java方法对象，然后插入到内存中，然后重新显示
            */
            "newJavaFun": function() {
                //创建新方法
                var one = {
                    name: "newFun",
                    parameters: [],
                    type: "public",
                    comments: {},
                    prifix: "",
                    fragments: [{
                        type: "normal",
                        command: "toDo"
                    }],
                    "return": "void"
                }
                if (!this.MY.java.functionFragment) {
                    this.MY.java.functionFragment = [];
                }
                this.MY.java.functionFragment.push(one);
                //重新显示左侧菜单
                this.showTree();
                //显示属性编辑页面
                this.showFunBase(this.MY.java.functionFragment.length - 1);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$showAllTemplate
            *@description [功能]显示所有可用的魔板
            *[思路]正常的处理逻辑应该是到后端文件系统获取，但这里土一点，直接在页面上写死，以后再优化
            */
            "showAllTemplate": function() {
                //直接调用公有方法进行显示
                this.showAllTemplate();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$importTemplate
            *@description [功能]导入新的魔板
            *[思路]提示一下，然后直接导入重新刷新页面
            *@param tmpName 魔板名称
            */
            "importTemplate": function(tmpName) {
                //判断是否导入
                if (!confirm("是否真的导入模板，导入模板将覆盖掉当前文件所有内容")) {
                    return;
                }
                //导入模板，并显示
                this.showContent(tmpName);
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$debugJava
            *@description [功能]调试java
            *[思路]这里要弹出一个模式对话框，填写threadSignal的信息
            */
            "debugJava": function() {
                //toDo
                this.showCodeDebug();
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$setThreadSignal
            *@description [功能]向服务器发起请求进行日志设置
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            */
            "setThreadSignal": function() {
                //获取值
                var threadSignal = $("#threadSignal").val();
                var result = this.API.doServer("setLog", "class", {
                    threadSignal: threadSignal
                });
                if (result.code == 0) {
                    this.MY.debugMsg = [];
                    this.MY.threadSignal = threadSignal;
                    FW.alert("日志标识设置成功");
                    this.showCodeDebug();
                } else {
                    FW.alert("日志标识设置失败");
                }
            },
            /**
            *@function
            *@memberOf editJava
            *@name FireEvent$nextDebug
            *@description [功能]显示下一个日志信息
            *[思路]这里显示下条日志信息
            */
            "nextDebug": function() {
                //获取日志信息
                var threadSignal = this.MY.threadSignal;
                //调用公有方法显示
                this.showCodeDebug(threadSignal);
            }
        },
        view: {
            'test': require("./resource/editJava/test.tpl"),
            'main': require("./resource/editJava/main.tpl"),
            'baseInfo': require("./resource/editJava/baseInfo.tpl"),
            'attrView': require("./resource/editJava/attrView.tpl"),
            'baseFun': require("./resource/editJava/baseFun.tpl"),
            'funGraphy': require("./resource/editJava/funGraphy.tpl"),
            'funFragmentBase': require("./resource/editJava/funFragmentBase.tpl"),
            'funCode': require("./resource/editJava/funCode.tpl"),
            'codeView': require("./resource/editJava/codeView.tpl"),
            'codeCompile': require("./resource/editJava/codeCompile.tpl"),
            'allTemplate': require("./resource/editJava/allTemplate.tpl"),
            'codeDebug': require("./resource/editJava/codeDebug.tpl")
        }

    },
    module);
    return FW;
});