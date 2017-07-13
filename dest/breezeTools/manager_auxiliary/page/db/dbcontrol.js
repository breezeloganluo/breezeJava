/**
* @namespace
* @name dbcontrol 
* @version 0.01 罗光瑜 初始版本
0.02 罗光瑜 显示备注信息的时候，备注信息加上空格替换
* @description  这是一个数据库的控制类，前期就只是把数据库简单的显示出来
*外部传入的参数即url传入的 值是一个json字符串:
*{
*    auth:['dbview'],
*    viewdb:['实际可见的数据库表名,不传就是看所有表']
*}      
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "dbcontrol",
        /**
        *@function
        *@memberOf dbcontrol
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf dbcontrol
            *@name public$showContent
            *@description [功能]显示入口函数
            *[思路]从外部传入参数进行控制
            */
            "showContent": function() {
                //处理参数
                var param = this.param.fileUrl;
                if (param != null) {
                    FW.use().save("_dbcontrol", param, true);
                } else {
                    param = FW.use().load("_dbcontrol", true);
                }
                var control = {
                    auth: ['dbview'],
                    viewdb: []
                }

                if (param != null) {
                    try {
                        control = eval("(" + param + ")");
                    } catch(e) {
                        control(e);
                    }
                }
                if (control.viewdb == null) {
                    control.viewdb = [];
                }
                //查询表信息
                var tableData = this.API.private('getAllTable', control.viewdb);
                //显示数据库表
                this.API.show("main", tableData);
            }
        },
        "private": {
            /**
            *@function
            *@memberOf dbcontrol
            *@name private$memo
            *@description [功能]处理memo字段，如果null则转换成""如果过长就截长并，显示成a标签，点击后显示完整的内容
            *@param text 文本内容
            *@param alias 那个alias
            *@param idx 所在alias的字段索引
            *@return html片段
            */
            "memo": function(text, alias, idx) {
                //如果为空的情况直接处理
                if (text == null) {
                    return "";
                }
                //如果长度小于20则正常显示
                if (text.length < 20) {
                    return text;
                }
                //如果超过20就截长处理
                var showData = {
                    text: text.substr(0, 17) + "...",
                    alias: alias,
                    idx: idx

                }
                return this.API.show("memo", showData, "_");
            },
            /**
            *@function
            *@memberOf dbcontrol
            *@name private$showRelation
            *@description [功能]被页面反向调用的，显示表的所有关系表
            *[思路]主要是显示两个，一个是挂接关系，一个里面所有的外链
            *中间显示时的显示对象数据结构为：
            *[
            *   {
            *        table:"表名"
            *        type:"挂接/外链",
            *        alias:"alias名称"
            *   }
            *]
            *注意啊，第一个参数table是当前表，而alias是当前表要外链的外表的alias，这两个所指不是相同表
            *@param oneTableObj 一个表对象：
            {
            displayName:"显示名称",
            tableName:"数据库表名",
            parentAlias:"挂接信息",
            dataDesc:[
            {
            fieldname:"字段名",
            title:"字段名称",
            fieldType:"字段类型",
            ourterLink:"外链描述",
            fieldMemo:"备注说明",
            fieldLen:"字段长度"
            }
            ]
            }
            *@return 要显示的内容
            */
            "showRelation": function(oneTableObj) {
                //初始化参数
                var showData = [];
                //处理挂接情况
                if (oneTableObj.parentAlias != null && !/^\s*$/.test(oneTableObj.parentAlias)) {
                    showData.push({
                        table: oneTableObj.tableName,
                        type: "挂接",
                        alias: oneTableObj.parentAlias
                    });
                }
                //处理外链情况
                var tmp = {};
                var hasValue = false;
                for (var i = 0; i < oneTableObj.dataDesc.length; i++) {
                    var one = oneTableObj.dataDesc[i];
                    if (one.ourterLink != null && !/^\s*$/.test(one.ourterLink)) {
                        var aliasName = one.ourterLink.split(".")[0];
                        hasValue = true;
                        tmp[aliasName] = true;
                    }
                }
                if (hasValue) {
                    for (var n in tmp) {
                        showData.push({
                            table: oneTableObj.tableName,
                            type: "外链",
                            alias: n
                        });
                    }
                }

                var result = this.API.show("relation", showData, "_");
                return result;
            },
            /**
            *@function
            *@memberOf dbcontrol
            *@name private$getAllTable
            *@description [功能]从后台获取所要查询的表信息
            *[思路]doserver查询
            *[接口.getDBDesc.db.param]{
            *     queryType:"table/alias",
            *     condiction:[
            *           "alias1"，。。。。
            *     ]
            *}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param tablelilst 数据库表列表
            *@param method 查询的方法table/alias默认是table
            *@return {
            "alias":{
            displayName:"显示名称",
            tableName:"数据库表名",
            parentAlias:"挂接信息",
            dataMemo:"备注信息",
            dataDesc:[
            {
            fieldname:"字段名",
            title:"字段名称",
            fieldType:"字段类型",
            ourterLink:"外链描述",
            fieldMemo:"备注说明",
            fieldLen:"字段长度"
            }
            ]
            }
            }
            */
            "getAllTable": function(tablelilst, method) {
                //到数据库中查询
                var param = {
                    queryType: method || "table"
                }

                if (tablelilst != null && tablelilst.length > 0) {
                    param.condiction = tablelilst;
                }
                //如果是用alias查询，那么看看是否已经存在有值
                if (this.MY.data != null && method == "alias" && tablelilst != null && tablelilst.length > 0) {
                    var tmpRes = {};
                    var needSerfer = false;
                    for (var i = 0; i < tablelilst.length; i++) {
                        if (this.MY.data[tablelilst[i]] == null) {
                            needSerfer = true;
                            break;
                        } else {
                            tmpRes[tablelilst[i]] = this.MY.data[tablelilst[i]];
                        }
                    }
                    if (!needSerfer) {
                        return tmpRes;
                    }
                }

                var data = this.API.doServer("getDBDesc", "db", param);
                //block(块){整理结果
                //初始化
                var result = this.MY.data || {};
                if (data.code != 0 || data.data == null) {
                    return {};
                }
                //for (所有表){
                for (var i = 0; i < data.data.length; i++) {
                    //处理普通字段
                    var one = data.data[i];
                    var tmp = result[one.alias] = {
                        displayName: one.displayName,
                        tableName: one.tableName,
                        parentAlias: one.parentAlias,
                    }
                    //处理dataDesc
                    tmp.dataDesc = [];
                    for (var n in data.data[i].dataDesc) {
                        var field = data.data[i].dataDesc[n];
                        tmp.dataDesc.push({

                            fieldname: n,
                            title: field.title,
                            fieldType: field.fieldType,
                            ourterLink: field.ourterLink,
                            fieldMemo: field.fieldMemo,
                            fieldLen: field.fieldLen
                        });
                    }
                    //处理备注字段
                    var orgMemo = one.dataMemo;
                    if (orgMemo != null && !/^\s*$/i.test(orgMemo)) {
                        try {
                            var memoObj = eval("(" + orgMemo + ")");
                            for (var j = 0; j < memoObj.length; j++) {
                                if (memoObj[j].type == "desc") {
                                    tmp.dataMemo = memoObj[j].desc;
                                    break;
                                }
                            }
                        } catch(e) {}
                    }
                }
                //}
                //赋值
                this.MY.data = result;
                //}
                //如果是用alias查询，则用alias过滤结果
                if (method == "alias" && tablelilst != null && tablelilst.length > 0) {
                    var newResult = {};
                    for (var i = 0; i < tablelilst.length; i++) {
                        newResult[tablelilst[i]] = result[tablelilst[i]];
                    }
                    return newResult;
                }
                //返回结果
                return result;
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf dbcontrol
            *@name FireEvent$showMemo
            *@description [功能]用蒙板层显示备注信息
            *[思路]先获取数据，然后展示
            *@param alias 别名
            *@param idx 所在别名的第几条字段描述
            */
            "showMemo": function(alias, idx) {
                //获取数据
                if (this.MY.data[alias] == null) {
                    FW.alert("数据库" + alias + "没找到");
                    return;
                }
                var data = this.MY.data[alias].dataDesc[idx].fieldMemo;
                var data = data.replace(/[\r\n]+/ig, "<br/>").replace(/\s/g, "&nbsp;&nbsp;").replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
                this.API.mask("memoMask", data, 500);
            },
            /**
            *@function
            *@memberOf dbcontrol
            *@name FireEvent$clickRelation
            *@description [功能]点击了外部表后要显示
            *[思路]要判断显示状态，如果原来是显示的那么关闭，否则显示。
            *而显示时也要查询看看缓存中是否有相关表，如果没有就上服务器上查询
            *@param table 当前表名
            *@param alias 要展开的关系表
            */
            "clickRelation": function(table, alias) {
                //获取对应页面装id
                var dom = $("#relation" + table + alias);
                var isShow = (dom.css("display") != "none");
                //if (如果是显示状态){隐藏
                if (isShow) {
                    dom.css("display", "none");
                    return;
                }
                //}
                //else {走显示流程
                else {
                    //查询结果值
                    var tableData = this.API.private('getAllTable', [alias], "alias")[alias];
                    this.API.show("oneRelationTable", tableData, "relation" + table + alias);
                    dom.css("display", "block");
                }
                //}
            },
            /**
            *@function
            *@memberOf dbcontrol
            *@name FireEvent$showTabMemo
            *@description [功能]显示整个表的备注信息，当然是用蒙板层显示的
            *[思路]就是获取数据然后显示
            *@param alias 表的alias
            */
            "showTabMemo": function(alias) {
                //获取对象
                var oneTab = this.MY.data[alias];
                var showData = "没有备注描述";
                if (oneTab.dataMemo && !/^\s*$/i.test(oneTab.dataMemo)) {
                    showData = oneTab.dataMemo;
                }
                var data = showData.replace(/[\r\n]+/ig, "<br/>").replace(/\s/g, "&nbsp;&nbsp;").replace(/\t/g, "&nbsp;&nbsp;&nbsp;&nbsp;");
                this.API.mask("memoMask", data, 500);
            }
        },
        view: {
            'relation': require("./resource/dbcontrol/relation.tpl"),
            'oneRelationTable': require("./resource/dbcontrol/oneRelationTable.tpl"),
            'memo': require("./resource/dbcontrol/memo.tpl"),
            'memoMask': require("./resource/dbcontrol/memoMask.tpl"),
            'main': require("./resource/dbcontrol/main.tpl")
        }

    },
    module, '0.02');
    return FW;
});