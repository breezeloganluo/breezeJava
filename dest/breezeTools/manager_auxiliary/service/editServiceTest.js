/**
* @namespace
* @name editServiceTest 
* @version 0.01 罗光瑜 初始第一个个版本
* @description  这是一个service针对service的单元测试编辑器，这个编辑器实现了service的编辑以及对数据库的导入导出的功能。                     
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("./fileselect");
    var formatJS = require("../../breeze/framework/js/tools/formatJS");
    FW.register({
        "name": "editServiceTest",
        "param": {
            /**
            *@memberOf editServiceTest
            *@name fileUrl
            *@description 默认文件url地址
            */
            "fileUrl": "./manager_auxiliary/template/templateTestService.js"
        },
        /**
        *@function
        *@memberOf editServiceTest
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //重新初始化this.MY
            this.MY = {};
            //创建文件对象
            var pageParam = {
                id: 'fileselect',
                dom: this.dom,
                param: {
                    viewid: "--"
                },
                //实际的参数
                view: {}
                //实际的视图内容
            }

            this.MY.fileSelect = FW.createApp("fileselect", "fileselect", pageParam);
            //获取目录树对象
            this.MY.treeView = FW.getApp(this.param.treeViewAppid);
            //读取文件
            this.initText();
            this.initShow();
        },
        "public": {
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$initShow
            *@description [功能]在已经获取完文本数据这些都准备好后，进行最原始的显示
            */
            "initShow": function() {
                //显示左边菜单
                this.showMenu(this.MY.content);
                //显示最基本的右边内容
                this.showBase();
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$initText
            *@description [功能]根据url的hash中的参数读取测试文件,并放入内存中
            *[思路]显示的时候，实际显示的具体内容
            *[接口this..MY.fileSelect.setFileName]=设置要查询的文件名
            *[接口this..MY.fileSelect.setPath]=设置要查询的文件名的路径
            *[接口this..MY.fileSelect.queryFileContent]=进行文件内容查询
            *[接口this..MY.content]=实际测试文件内容的内侧存放地址。
            *[接口.hash的json格式]{
            *  gadget:"gadget名称",
            *  file:"url地址"
            *}
            */
            "initText": function() {
                //获取url参数
                var allHash = window.location.hash;
                if (allHash == null || allHash == "" || allHash == "#") {
                    FW.alert("请输入文件路径");
                    return;
                }
                allHash = allHash.replace(/^#/i, "");
                allHash = decodeURIComponent(allHash);
                var urlObj = eval("(" + allHash + ")");
                var fileUrl = urlObj.file;
                //block(代码块){构造文件对象读取对象
                var text = null;
                //if (传入的url不为空){获取文件信息
                if (fileUrl != null) {
                    //读取文件
                    var fileArr = fileUrl.split("/");
                    var fileName = fileArr.pop();
                    var fileDir = "/" + fileArr.join("/");
                    this.MY.fileSelect.setFileName(fileName);
                    this.MY.fileSelect.setPath(fileDir);
                    this.MY.jspFileName = fileName;
                    this.MY.jspPath = fileDir;
                    text = this.MY.fileSelect.queryFileContent();
                    //$("#aliasTitle").html("[" + fileName + "]");
                }
                //}
                //else{就是url不存在了，要退货
                else {
                    //退出
                    alert("文件地址为空");
                    return;
                }
                //}
                //if(文件本身不存在){读取默认文件
                if (text == null) {
                    //重新设置文件
                    var dfileArr = this.param.fileUrl.split("/");
                    var dfileName = dfileArr.pop();
                    var dfileDir = "/" + dfileArr.join("/");
                    this.MY.fileSelect.setFileName(dfileName);
                    this.MY.fileSelect.setPath(dfileDir);
                    text = this.MY.fileSelect.queryFileContent();
                    //文件设置复位，便于后续保存
                    this.MY.fileSelect.setFileName(fileName);
                    this.MY.fileSelect.setPath(fileDir);
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
                this.MY.content = eval("(" + text + ")");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$showBase
            *@description [功能]显示基本内容，就是service和package的编辑框
            */
            "showBase": function() {
                //直接显示
                this.API.show("baseView", this.MY.content);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$showMenu
            *@description [功能]直接初始化父亲部分的目录树
            *[思路]用父亲本身的目录树进行初始化
            *[接口.目录树初始化的数据结构]
            *            [
            *            {
            *            name:"显示名称",
            *            type:"item/folder",
            *            itemid:"点击标识"
            *            children:[儿子的内容，循环上面父亲的结构
            *            ]
            *            }
            *            ]
            *[接口.目录树初始化的数据]{
            *      基本信息:"存放被测试的service和package,itemid是base",
            *      数据库设置:"链接到基本数据库设置页面,itemid是database"
            *      用例:[
            *           case1:"case1的数据，itemid是case+id"
            *      ]
            *}
            *[接口.this.MY.treeView]左边treeView的数据
            *@param data 完整的结构数据，即实际的测试记录文件
            */
            "showMenu": function(data) {
                //整理目录树数据
                var treeData = [{
                    name: "基本信息",
                    type: "item",
                    itemid: "base"
                },
                {
                    name: "数据库设置",
                    type: "item",
                    itemid: "database"
                },
                {
                    name: "用例",
                    type: "folder",
                    children: []
                }

                ];

                for (var i = 0; i < data.testcase.length; i++) {
                    treeData[2].children.push({
                        name: data.testcase[i].caseName,
                        type: "item",
                        itemid: "case_" + i
                    });
                }
                //目录树初始化
                this.MY.treeView.init(treeData);
                //绑定点击事件
                var _this = this;
                this.MY.treeView.setEventCall(function(ex, info) {
                    _this.treeMenuClick(info);
                });
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$treeMenuClick
            *@description [功能]给左边树点击后回调的函数
            *@param nodedata 这里传入的是treeview传入的数据结构，如下：
            {
            info[//这里表示被选中的节点，通常只有一个
            {
            name:名称，用于显示，可能会加入前缀的i标签,
            type:"item/folder",
            itemid:"被选中的节点id，其值，参见showMenu函数"
            additionalParameters:{有儿子就有这个节点
            children:{
            data0:{
            //这里开始就是一个独立儿子的结构
            }
            }
            }
            }
            ]
            }
            如果这个数据没有，则表示用this.MY.curContentList
            */
            "treeMenuClick": function(nodedata) {
                //获取itemid值
                var itemid = nodedata.info[0].itemid;
                //if(基本信息情况){显示基本情况
                if (itemid == "base") {
                    //显示基本信息
                    this.showBase();
                }
                //}
                //else if(点击数据库情况){显示数据库部分
                else if (itemid == "database") {
                    //显示数据库部分
                    this.showDatabase();
                }
                //}
                //else if(case情况){显示case
                else if (/case_\d+/i.test(itemid)) {
                    //显示这个case
                    this.showCase(itemid);
                }
                //}
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$showDatabase
            *@description [功能]显示当前这个service当前所需要的记录的数据库信息，包括数据编辑功能
            *[思路]这是主显示窗口，给出主架构，具体显示的内容，调用子方法实现
            */
            "showDatabase": function() {
                //显示主框架
                this.API.show("databaseMain", null);
                //显示主框架下缺省的内容
                this.API.private('showDatabaseSelected');
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$showCase
            *@description [功能]显示某个case的基本信息
            *[思路]这里只显示基本的架构，也是tag分页方式显示，具体每个tag由私有方法决定
            *[接口.this.MY.content.testcase]总的内部数据在这里
            *[接口.view.caseView]总的内部数据在这里
            *@param caseitem 这是在showMenu函数中定义的每个case的id方式，详细请参考showMenu函数
            */
            "showCase": function(caseitem) {
                //获取本case数据
                var execResult = /case_(\d+)/i.exec(caseitem);
                if (execResult == null) {
                    FW.alert("没找到用例：" + caseitem);
                    return;
                }
                var idx = execResult[1];
                var caseData = this.MY.content.testcase[idx];

                if (caseData == null) {
                    FW.alert("没找到用例：" + caseitem);
                }
                caseData.idx = idx;
                caseData.tables = this.MY.content.tables;
                //显示基本信息
                this.API.show("caseView", caseData);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$saveAll
            *@description [功能]保存所有文件内容
            *[思路]将内存对象转为字符串，然后格式化，然后调用fileoper对象实例进行文件处理，注意，在实际的代码中，添加了很多辅助变量，这些变量是要祛除的。
            *[接口.this.MY.content]实际的文件保存地址
            *[接口.this.MY.fileSelect]文件操作对象
            */
            "saveAll": function() {
                //祛除内存多余内容
                //将内存内容转换成字符串
                var fileStr = FW.use().toJSONString(this.MY.content);
                //格式化
                var formatFileStr = formatJS.js_beautify(fileStr);
                //保存文件
                this.MY.fileSelect.setFileName(this.MY.jspFileName);
                this.MY.fileSelect.setPath(this.MY.jspPath);
                this.MY.fileSelect.saveFile(formatFileStr);
                alert("文件保存成功");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name public$goTest
            *@description [功能]发消息进行一次测试
            *[思路]发消息到服务器执行，然后调用私有方法进行结果判断
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param idx 测试第idx个case
            */
            "goTest": function(idx) {
                //获取测试对象
                var testCase = this.MY.content.testcase[idx];
                //整理发送的消息参数
                var param = {
                    "servicename": this.MY.content.servicename,
                    "package": this.MY.content.package,
                    "case": testCase
                }
                //同步方法调用服务端的测试
                var testResult = this.API.doServer("TestService", "service", param);
                //处理结果
                var result = this.API.private('analyzeResult', idx, testResult);
                this.API.append("testResult", result, "testresultgroup");
            }
        },
        "private": {
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showDatabaseSelected
            *@description [功能]显示基本的被选中的表
            */
            "showDatabaseSelected": function() {
                //直接将当前的数据显示出去
                this.API.show("databaseSelected", this.MY.content.tables, "database_home_show");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showDatabaseAll
            *@description [功能]到后台获取当前所有表的表名，并将表和当前已选中表整合显示到页面上
            *[接口.service.db.getAllTable.return]={
            *   code:0,
            *   data:[
            *       {
            *            name:'表名'
            *       }
            *   ]
            *}
            *[接口.showdata][
            *   {
            *          name:"表名",
            *          selected:"是否选中"
            *   }
            *]
            */
            "showDatabaseAll": function() {
                //获取所有数据
                //--用同步方法
                var doServerResult = this.API.doServer("getAllTable", "db");
                if (doServerResult == null || doServerResult.code != 0 || !doServerResult.data || doServerResult.data.length == 0) {
                    FW.alert("查询表名失败");
                    return;
                }
                var allTable = doServerResult.data;
                //排序
                allTable.sort(function(a, b) {
                    return (a.name > b.name) ? 1 : -1;
                });
                //block(块){合成显示数据
                //将选中表名合成一个","为间隔符的字符串，为选中字符串
                var selectedTable = this.MY.content.tables.join(",");
                //while(所有表){
                for (var i = 0; i < allTable.length; i++) {
                    //if(属于选中字符的子串){标识出选中
                    if (selectedTable.indexOf(allTable[i].name) >= 0) {
                        //设置成选中
                        allTable[i].selected = true;
                    }
                    //}
                }
                //}
                //}
                //显示内容
                this.API.show("databaseAllTable", allTable, "database_all_show");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showDatabaseTable
            *@description [功能]显示其中一个表的所有数据
            *[接口.service.db.getOneTable.param]{
            *  name:"表名"
            *}
            *[接口.service.db.getOneTable.return]{
            *  code:0.
            *  data:[
            *      {
            *          字段名:'字段值'
            *      }
            *  ]
            *}
            *@param tableName 显示表名
            */
            "showDatabaseTable": function(tableName) {
                //获取表数据
                var tableData = this.API.doServer("getOneTable", "db", {
                    name: tableName
                });
                if (!tableData || tableData.code == null || tableData.code != 0) {
                    FW.alert("获取表数据失败");
                    return;
                }
                if (!tableData.data) {
                    tableData.data = [];
                }
                //显示到页面上
                this.API.show("databaseOneTable", tableData.data, "database_home_show");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showCaseMemSession
            *@description [功能]显示测试用例的内存和session内容
            *[接口.this.MY.content.testcase]某一个测试用例
            *[接口.view.caseMemSession]基础的视图名
            *@param idx 要显示的case数组索引
            */
            "showCaseMemSession": function(idx) {
                //获取数据
                var displayCase = this.MY.content.testcase[idx];
                //显示出来
                this.API.show("caseMemSession", displayCase, "case_session_show");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showCaseDB
            *@description [功能]显示某一个DB的数据用例信息，或期望结果信息，如果有输入行和colName的名称，则会在该处显示成一个输入框。如果原图中已经有一个输入框，则是做提交操作，而不是显示输入框
            *[思路]这里先到后天将这个数据表的结构获取到，然后再一个个显示
            *[接口.this.MY.content]这里是总结构
            *@param showData 要处理的数据，可能是输入数据，也可能是期望结果数据
            *@param idx 当前第几个case
            *@param dbTableName 数据库的表名
            
            *@param row 第几行
            *@param colName 字段名
            */
            "showCaseDB": function(showData, idx, dbTableName, row, colName) {
                //获取DB的结构
                var dbStruct = this.API.doServer("getOneTableStruts", "db", {
                    name: dbTableName
                });
                if (dbStruct == null || !dbStruct.data || dbStruct.data.length == 0) {
                    alert("数据结构获取失败");
                    return;
                }
                //整合显示数据
                var showData = showData || [];
                showData.struct = dbStruct.data;
                showData.name = dbTableName;
                showData.idx = idx;
                //if(输入了行和字段名){进行字段操作
                if (row != null && colName != null) {
                    //获取页面的输入框
                    var inputDom = this.API.find("#dbinput");
                    //if(页面输入框存在){提交这个值
                    if (inputDom != null && inputDom.length != 0) {
                        //获取原来的坐标
                        var row = inputDom.attr("idx");
                        var colName = inputDom.attr("name");
                        var value = inputDom.val();
                        //设置内存的这个值
                        showData[Number(row)][colName] = value;
                        //将显示数据的显示坐标信息去掉
                        delete showData.row;
                        delete showData.colName;
                    }
                    //}
                    //else if(设置数据将输入框的信息显示到页面上){写入要显示输入框的位置
                    else {
                        //设置显示值
                        showData.row = row;
                        showData.colName = colName;
                    }
                    //}
                }
                //}
                //else{删除掉标记字段
                else {
                    //将显示数据的显示坐标信息去掉
                    delete showData.row;
                    delete showData.colName;
                }
                //}
                //显示到页面上
                this.API.show("caseInitDB", showData, "tablescontent");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showExpMsgResult
            *@description [功能]显示期待的函数调用结果
            *[思路]这个函数很简单，就是从数据中获取对应的结构，然后转换成文本，最后写入textarea中，注意，文本要格式化
            *[接口.this.MY.content[].expMsgResult]包含某个索引的testcase下面的结果设置
            *@param idx 索引
            */
            "showExpMsgResult": function(idx) {
                //获取对象值
                var expResultObj = this.MY.content.testcase[idx].expMsgResult;
                //转换成字符串
                var str = FW.use().toJSONString(expResultObj);
                //格式化
                var lastStr = formatJS.js_beautify(str);
                //show到页面上
                this.API.show("caseExpMsgResult", {
                    str: lastStr,
                    idx: idx
                },
                "expresultcontent");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$showCaseParam
            *@description [功能]显示某个用例下的单一测试参数
            *[思路]直接获取参数内容，然后格式化，注意对非空判断的处理
            *[接口.this.MY.content]所有的测试内容都在这里
            *@param idx 要显示的第几个用例
            */
            "showCaseParam": function(idx) {
                //获取参数内容
                var param = this.MY.content.testcase[idx].param;
                //格式化字符串
                var showText = "";
                if (param != null) {
                    showText = FW.use().toJSONString(param);
                    showText = formatJS.js_beautify(showText);
                }
                //显示到页面上
                this.API.show("caseParam", {
                    text: showText,
                    idx: idx
                },
                "case_param_show");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$analyzeResult
            *@description [功能]分析返回来的测试结果，比对和预期结果是否一致
            *[思路]返回结果，数据库逐个比较
            *[接口.this.MY.content]整个的测试预置数据
            *@param idx 第几个测试用例
            *@param result 要比对的结果对象
            {
            code:xxx
            data:结果数据
            dbData:数据库的结果数据
            }
            *@return {
            code:0表示正确，否则是错误,
            casename:"",
            error:{
            obj:"错误对象",
            expect:"期待对象",
            result:"实际结果"
            }
            }
            */
            "analyzeResult": function(idx, result) {
                //获取测试对象
                var testObj = this.MY.content.testcase[idx];
                //获取结果数据库对象
                var resultDBObj = result.dbData;
                //整理结果对象
                delete result.dbData;
                //获取用例名
                var casename = testObj.caseName;
                //block(块){比较结果
                //获取期待值
                var　expData = testObj.expMsgResult;
                //获取结果值
                var resultData = result;
                //比较结果
                var cr = this.API.private('cmpObj', expData, resultData);
                if (!cr) {
                    return {
                        code: 11,
                        casename: casename,
                        error: {
                            "obj": "消息结果错误",
                            "expect": FW.use().toJSONString(expData),
                            "result": FW.use().toJSONString(resultData)
                        }
                    }
                }
                //}
                //block(块){比较数据库
                //获取期望值
                for (var i = 0; i < this.MY.content.tables.length; i++) {
                    var tbName = this.MY.content.tables[i];
                    var expData = testObj.expDBResult[tbName];
                    //获取实际结果
                    var resultData = resultDBObj[tbName];
                    //实际比较
                    var cr = this.API.private('cmpObj', expData, resultData);
                    if (!cr) {
                        return {
                            code: 12,
                            casename: casename,
                            error: {
                                "obj": "数据库" + tbName + "失败",
                                "expect": FW.use().toJSONString(expData),
                                "result": FW.use().toJSONString(resultData)
                            }
                        }
                    }
                }
                //}
                //处理结果
                return {
                    code: 0,
                    casename: casename
                }
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name private$cmpObj
            *@description [功能]比较两个对象是否相等
            *[思路]递归方式比较
            *
            *@param obj1 对象1
            *@param obj2 对象2
            *@return true表示相同，否则不同
            */
            "cmpObj": function(obj1, obj2) {
                //if(是对象){展开病对比每个值
                if (/object/i.test(typeof(obj1)) && obj1.length == null) {
                    //while(所有对象内部){递归比较
                    for (var n in obj1) {
                        //获取第一个对象
                        var f = obj1[n];
                        //获取第二个对象
                        var s = obj2[n];
                        //递归比较
                        if (f == null || s == null) {
                            return false;
                        }
                        var cr = this.API.private('cmpObj', f, s);
                        if (!cr) {
                            return false;
                        }
                    }
                    //}
                }
                //}
                //else if(是数组){循环展开对比每个成员
                else if (/object/i.test(typeof(obj1)) && obj1.length) {
                    //while(遍历数组){遍历每一个值进行比较
                    for (var i = 0; i < obj1.length; i++) {
                        //获取第一个值
                        var f = obj1[i];
                        //获取第二个值
                        var s = obj2[i];
                        //比较并返回结果
                        if (s == null || f == null) {
                            return false;
                        }
                        var cr = this.API.private('cmpObj', f, s);
                        if (!cr) {
                            return false;
                        }
                    }
                    //}
                }
                //}
                //else{直接比较结果
                else {
                    //否则直接比较结果
                    return obj1 == obj2;
                }
                //}
                //返回真
                return true;
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$saveBase
            *@description [功能]保存基本信息
            *[思路]用最基本的FW.use().getFormValue去获取表单的值
            *@param formid 要读取的表单的id值
            */
            "saveBase": function(formid) {
                //获取表单的值
                var formValue = FW.use().getFormValue(formid);
                //修改值
                if (formValue.servicename == "" || formValue.servicename == null) {
                    FW.alert("请填写servicename");
                }
                if (formValue.package == "" || formValue.package == null) {
                    FW.alert("请填写包名");
                }
                this.MY.content.servicename = formValue.servicename;
                this.MY.content.package = formValue.package;
                //重新显示
                this.showBase();
                FW.alert("基本信息设置成功");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showDatabaseAll
            *@description [功能]显示所有表
            */
            "showDatabaseAll": function() {
                //调用私用方法显示所有表
                this.API.private('showDatabaseAll');
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$addDBSelected
            *@description [功能]添加关注的数据库表名
            *@param name 要关注的表名称
            */
            "addDBSelected": function(name) {
                //添加关注
                this.MY.content.tables.push(name);
                //重新显示
                this.API.private('showDatabaseAll');
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showDatabaseSelected
            *@description [功能]显示选中信息
            */
            "showDatabaseSelected": function() {
                //显示
                this.API.private('showDatabaseSelected');
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$removeDBSelected
            *@description [功能]取消关注
            *@param idx 被删除的索引
            *@param refresh 是否刷新，true刷新，否则不刷新
            */
            "removeDBSelected": function(idx, refresh) {
                //查找数据并删除
                var dbName = this.MY.content.tables[idx];
                this.MY.content.tables.splice(idx, 1);
                for (var i = 0; i < this.MY.content.testcase.length; i++) {
                    var onecase = this.MY.content.testcase[i];
                    delete onecase.initData[dbName];
                    delete onecase.expDBResult[dbName];
                }
                //重新显示
                if (refresh) {
                    this.API.private('showDatabaseSelected');
                }
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showDatabaseTable
            *@description [功能]调用私有方法，显示一个具体的表信息
            *@param tableName 表名
            */
            "showDatabaseTable": function(tableName) {
                //toDo
                this.API.private('showDatabaseTable', tableName);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$modifyCaseMem
            *@description [功能]修改某个case的内存信息
            *[原理]页面的key和value是用id值memset[原来的key]key和memset[原来的key]value来标识，在事件处理中直接用jquery去获取对应的值。
            *[接口.this.MY.content.testcase]这里描述内部全局变量定义
            *@param idx 所在case的索引
            *@param oldKey 原来的key
            */
            "modifyCaseMem": function(idx, oldKey) {
                //获取要修改的testcase
                var thisCase = this.MY.content.testcase[idx];
                //从页面上获取新的key和新的value
                var newKey = $("#memset" + oldKey + "key").val();
                if (newKey == null) {
                    FW.alert("key不允许为空");
                    return;
                }
                newKey = newKey.replace(/(^\s*)|(\s*$)/i, "");
                if (newKey == "") {
                    FW.alert("key不允许为空");
                    return;
                }

                var newValue = $("#memset" + oldKey + "value").val();
                if (newValue == null) {
                    FW.alert("value不允许为空");
                    return;
                }
                newValue = newValue.replace(/(^\s*)|(\s*$)/i, "");
                if (newValue == "") {
                    FW.alert("value不允许为空");
                    return;
                }
                //if(key没有修改){直接找出原来的值进行修改
                if (oldKey == newKey) {
                    //直接找出原来的值进行修改
                    thisCase.globel[oldKey] = newValue;
                }
                //}
                //else{删除原来的值然后再添加新值
                else {
                    //删除原来的值然后再添加新值
                    delete thisCase.globel[oldKey];
                    thisCase.globel[newKey] = newValue;
                }
                //}
                //重新显示
                FW.alert("全局变量修改成功");
                this.API.private('showCaseMemSession', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showCaseMemSession
            *@description [功能]触发显示内存部分设置
            *[思路]调用私有方法实现
            *@param idx 要显示的case索引
            */
            "showCaseMemSession": function(idx) {
                //toDo
                this.API.private('showCaseMemSession', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$modifyCaseSession
            *@description [功能]修改某个case的内存信息
            *[原理]页面的key和value是用id值sessionset[原来的key]key和sessionset[原来的key]value来标识，在事件处理中直接用jquery去获取对应的值。
            *[接口.this.MY.content.testcase]这里描述内部全局变量定义
            *@param idx 所在case的索引
            *@param oldKey 原来的key
            */
            "modifyCaseSession": function(idx, oldKey) {
                //获取要修改的testcase
                var thisCase = this.MY.content.testcase[idx];
                //从页面上获取新的key和新的value
                var newKey = $("#sessionset" + oldKey + "key").val();
                if (newKey == null) {
                    FW.alert("key不允许为空");
                    return;
                }
                newKey = newKey.replace(/(^\s*)|(\s*$)/i, "");
                if (newKey == "") {
                    FW.alert("key不允许为空");
                    return;
                }

                var newValue = $("#sessionset" + oldKey + "value").val();
                if (newValue == null) {
                    FW.alert("value不允许为空");
                    return;
                }
                newValue = newValue.replace(/(^\s*)|(\s*$)/i, "");
                if (newValue == "") {
                    FW.alert("value不允许为空");
                    return;
                }
                newValue = newValue.replace(/"/ig, "'");
                //if(key没有修改){直接找出原来的值进行修改
                if (oldKey == newKey) {
                    //直接找出原来的值进行修改
                    thisCase.session[oldKey] = newValue;
                }
                //}
                //else{删除原来的值然后再添加新值
                else {
                    //删除原来的值然后再添加新值
                    delete thisCase.session[oldKey];
                    thisCase.session[newKey] = newValue;
                }
                //}
                //重新显示
                FW.alert("session设置修改成功");
                this.API.private('showCaseMemSession', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$addCaseMemSession
            *@description [功能]添加一个新的内存或者session设置
            *[思路]是添加session还是添加mem通过传入的参数决定
            *@param idx 要添加的索引
            *@param name globel/session
            */
            "addCaseMemSession": function(idx, name) {
                //添加数据
                this.MY.content.testcase[idx][name]["newone"] = "new one value"
                //重新显示
                this.API.private('showCaseMemSession', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$deleteCaseSession
            *@description [功能]删除某个特定的session值
            *[思路]通过idx获取到当前的内存，然后通过key值删除
            *[接口.this.MY.content]完整的内存变量
            *@param idx 索引
            *@param key 要删除的key值
            */
            "deleteCaseSession": function(idx, key) {
                //获取到case
                var caseData = this.MY.content.testcase[idx];
                //直接删除
                delete caseData.session[key];
                //重新显示
                this.API.private('showCaseMemSession', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$deleteCaseMem
            *@description [功能]删除内存预置信息
            *[思路]根据输入的idx和对象名称进行删除，删除完要重新显示
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param idx 要删除的case索引
            *@param key 要删除的key
            */
            "deleteCaseMem": function(idx, key) {
                //获取到case
                var caseData = this.MY.content.testcase[idx];
                //直接删除
                delete caseData.globel[key];
                //重新显示
                this.API.private('showCaseMemSession', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$newInitDBData
            *@description [功能]在内存中添加一条新的记录
            *[思路]这里添加，然后调用重新显示
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param idx case的索引
            *@param dbName 数据库名称
            */
            "newInitDBData": function(idx, dbName) {
                //获取数据
                var data = this.MY.content.testcase[idx][this.MY.caseDBObjType][dbName];
                if (data == null) {
                    data = this.MY.content.testcase[idx][this.MY.caseDBObjType][dbName] = [];
                }
                //添加记录
                data.push({});
                //重新显示
                this.API.private('showCaseDB', data, idx, dbName);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$deleteInitDB
            *@description [功能]删除一个选中的数据
            *[思路]在页面上获取输入框，获取里面的idx的值，然后进行数据删除，最后显示出来
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param idx 是第几个用例
            *@param dbName 数据库名
            */
            "deleteInitDB": function(idx, dbName) {
                //获取页面的那个输入框对象
                var inputDom = this.API.find("#dbinput");
                if (inputDom == null || inputDom.length == 0) {
                    alert("请选中要删除的数据");
                    return;
                }
                //获取数组对象
                var data = this.MY.content.testcase[idx][this.MY.caseDBObjType][dbName];
                var deletedix = inputDom.attr("idx");
                data.splice(deletedix, 1);
                //调用私有函数进行显示
                var showData = this.MY.content.testcase[idx][this.MY.caseDBObjType][dbName];
                this.API.private('showCaseDB', showData, idx, dbName);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showCaseDB
            *@description [功能]显示某个case的基本数据
            *[思路]调用私有方法实现显示
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param idx 索引id
            *@param dbName 数据库名
            *@param row 所在行号
            *@param colName 字段名
            *@param showObj initData缺省，表示初始数据
            expDBResult表示结果数据
            */
            "showCaseDB": function(idx, dbName, row, colName, showObj) {
                //确认调用对象
                if (showObj) {
                    this.MY.caseDBObjType = showObj;
                }
                //调用私有函数去实现
                var showData = this.MY.content.testcase[idx][this.MY.caseDBObjType][dbName];
                this.API.private('showCaseDB', showData, idx, dbName, row, colName);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showExpMsgResult
            *@description [功能]调用同名私有函数，完成内容显示功能
            *@param idx 要处理的case索引
            */
            "showExpMsgResult": function(idx) {
                //调用同名私有函数
                this.API.private('showExpMsgResult', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$saveExpMsgResult
            *@description [功能]将测试的期待的消息结果记录到内存中
            *[思路]检查一下格式（是否符合json要求）然后保存到内容中
            *[接口.this.MY.content.testcase[].expMsgResult]保存的地址内容
            *@param idx 消息结果
            */
            "saveExpMsgResult": function(idx) {
                //获取字符串
                var result = $("#expResultMsgEditContent").val();
                if (result == null) {
                    FW.alert("请填写正确的结果信息");
                    return;
                }
                //字符串校验，并写入内存
                try {
                    var resultObj = eval("(" + result + ")");
                    this.MY.content.testcase[idx].expMsgResult = resultObj;
                    FW.alert("期待结果信息修改成功");
                    this.API.private('showExpMsgResult', idx);
                } catch(e) {
                    FW.alert("json格式错误");
                    return;
                }
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$saveAll
            *@description [功能]保存所有内容
            *[思路]调用实际的公有函数进行实际的保存
            *[接口.this.MY.content]保存的内容对象
            */
            "saveAll": function() {
                //调用公有方法保存
                this.saveAll();
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$goTest
            *@description [功能]进行一个基本的测试，只测试一个用例
            *[思路]调用公有方法去处理
            *[接口.this.MY.content]调用公有方法去处理
            *@param idx 当前处理的第几个case
            */
            "goTest": function(idx) {
                //显示主测试框
                this.API.show("clear");
                //执行单个测试
                this.goTest(idx, true);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$showCaseParam
            *@description [功能]显示这个case要测试的参数
            *[思路]调用真正的私有方法实现
            *@param idx 当前是第几个case
            */
            "showCaseParam": function(idx) {
                //调用私有函数去实现
                this.API.private('showCaseParam', idx);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$saveParam
            *@description [功能]保存用例
            *[思路]直接保存到内存中
            *[接口.this.MY.content]内存的内容信息
            *@param idx 第n个用例
            */
            "saveParam": function(idx) {
                //获取文本内容
                var orgText = $("#caseParamText").val();
                try {
                    var obj = eval("(" + orgText + ")");
                    this.MY.content.testcase[idx].param = obj;
                    FW.alert("参数保存成功");
                    return;
                } catch(e) {
                    FW.alert("json 格式错误，请重试");
                    return;
                }
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$saveCaseName
            *@description [功能]保存用力名称
            *[思路]直接改写内存，然后重新刷新左边菜单树
            *[接口.this.MY.content]保存整个测试用例的内存信息
            *@param idx 第几个测试用例
            */
            "saveCaseName": function(idx) {
                //获取用例名
                var name = $("#casenametext").val();
                this.MY.content.testcase[idx].caseName = name;
                this.showMenu(this.MY.content);
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$newCase
            *@description [功能]直接创建一个新用例对象到内存中，然后刷新显示，用例名用默认的即可
            *[思路]直接创建一个新用例对象到内存中，然后刷新显示，用例名用默认的即可
            */
            "newCase": function() {
                //直接显示新表单
                this.MY.content.testcase.push({
                    caseName: "newCase",
                    globel: {},
                    session: {},
                    initData: {},
                    expDBResult: {},
                    expMsgResult: {}
                });
                //显示菜单
                this.showMenu(this.MY.content);
                FW.alert("新用例创建成功，请点击左边菜单进行修改以及维护");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$deleteCase
            *@description [功能]删除一个用例
            *[思路]在内存中实现删除
            *@param idx 要删除的用例的索引
            */
            "deleteCase": function(idx) {
                //直接从内存中删除
                this.MY.content.testcase.splice(idx, 1);
                //重新显示菜单
                this.showMenu(this.MY.content);
                FW.alert("用例删除成功");
            },
            /**
            *@function
            *@memberOf editServiceTest
            *@name FireEvent$testAll
            *@description [功能]运行所有测试用例
            *[思路]循环遍历所有的测试用例
            *[接口.this.MY.content]内存中的测试用例变量
            */
            "testAll": function() {
                //显示主测试框
                this.API.show("clear");
                //执行单个测试
                for (var i = 0; i < this.MY.content.testcase.length; i++) {
                    this.goTest(i);
                }
            }
        },
        view: {
            'baseView': require("./resource/editServiceTest/baseView.tpl"),
            'databaseMain': require("./resource/editServiceTest/databaseMain.tpl"),
            'databaseSelected': require("./resource/editServiceTest/databaseSelected.tpl"),
            'databaseAllTable': require("./resource/editServiceTest/databaseAllTable.tpl"),
            'databaseOneTable': require("./resource/editServiceTest/databaseOneTable.tpl"),
            'caseView': require("./resource/editServiceTest/caseView.tpl"),
            'caseMemSession': require("./resource/editServiceTest/caseMemSession.tpl"),
            'caseInitDB': require("./resource/editServiceTest/caseInitDB.tpl"),
            'caseExpMsgResult': require("./resource/editServiceTest/caseExpMsgResult.tpl"),
            'caseParam': require("./resource/editServiceTest/caseParam.tpl"),
            'clear': require("./resource/editServiceTest/clear.tpl"),
            'testResult': require("./resource/editServiceTest/testResult.tpl")
        }

    },
    module);
    return FW;
});