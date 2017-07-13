/**
* @namespace
* @name testFunParser 
* @description  undefined 
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../javalv2");
    FW.register({
        "name": "testFunParser",
        /**
        *@function
        *@memberOf testFunParser
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
            this.MY.javalv2 = FW.createApp("javalv2", "javalv2", this);
            this.API.show("main");
        },
        "public": {
            /**
            *@function
            *@memberOf testFunParser
            *@name public$testParserJavaClassFunctionBody
            *@description [功能]完成主测试
            *@param testObj 被测试的对象
            */
            "testParserJavaClassFunctionBody": function(testObj) {
                //初始化并构造对象进行测试
                var destArray = [];
                var app = this.MY.javalv2.private.parserJavaClassFunctionBody.call(this.MY.javalv2, testObj, 0, destArray);
                return FW.use().toJSONString(destArray);
            },
            /**
            *@function
            *@memberOf testFunParser
            *@name public$testParserJavaClassFunction
            *@description [功能]测试解析clas内容
            *@param obj toDo
            *@return toDo
            *@example toDO
            */
            "testParserJavaClassFunction": function(obj) {
                //toDo
                //初始化并构造对象进行测试
                var app = this.MY.javalv2.private.parserJavaClassFunction.call(this.MY.javalv2, obj, 0);
                return FW.use().toJSONString(app);
            },
            /**
            *@function
            *@memberOf testFunParser
            *@name public$testParserJavaClassFunctionComment
            *@description
            *@param str toDo
            */
            "testParserJavaClassFunctionComment": function(str) {
                //toDo
                var app = this.MY.javalv2.private.parserJavaClassFunctionComment.call(this.MY.javalv2, str);
                return FW.use().toJSONString(app);
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf testFunParser
            *@name FireEvent$parserJavaClassFunctionBody
            *@description
            *@param p1 domid号
            *@param p2 结果domid
            */
            "parserJavaClassFunctionBody": function(p1, p2) {
                //toDo
                var str = $("#" + p1).val();
                var obj = eval("(" + str + ")");
                var result = this.testParserJavaClassFunctionBody(obj);
                $("#" + p2).val(result);
            },
            /**
            *@function
            *@memberOf testFunParser
            *@name FireEvent$goParserJavaClassFunction
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            */
            "goParserJavaClassFunction": function() {
                //toDo
                this.API.show("parserJavaClassFunction");
            },
            /**
            *@function
            *@memberOf testFunParser
            *@name FireEvent$parserJavaClassFunction
            *@description
            *@param p1 入口文本
            *@param p2 出口文本
            *@return 、
            *@example 、
            */
            "parserJavaClassFunction": function(p1, p2) {
                //toDo
                //toDo
                var str = $("#" + p1).val();
                var obj = eval("(" + str + ")");
                var result = this.testParserJavaClassFunction(obj);
                $("#" + p2).val(result);
            },
            /**
            *@function
            *@memberOf testFunParser
            *@name FireEvent$parserJavaClassFunctionComment
            *@description
            *@param p1 toDo
            *@param p2 toDo
            */
            "parserJavaClassFunctionComment": function(p1, p2) {
                //toDo
                var str = $("#" + p1).val();
                var result = this.testParserJavaClassFunctionComment(str);
                $("#" + p2).val(result);
            },
            /**
            *@function
            *@memberOf testFunParser
            *@name FireEvent$goParserJavaClassFunctionComment
            *@description
            *@return toDo
            *@example toDO
            */
            "goParserJavaClassFunctionComment": function() {
                //toDo
                this.API.show("parserJavaClassFunctionComment");
            }
        },
        view: {
            'main': require("./resource/testFunParser/main.tpl"),
            'parserJavaClassFunction': require("./resource/testFunParser/parserJavaClassFunction.tpl"),
            'parserJavaClassFunctionComment': require("./resource/testFunParser/parserJavaClassFunctionComment.tpl")
        }

    },
    module);
    return FW;
});