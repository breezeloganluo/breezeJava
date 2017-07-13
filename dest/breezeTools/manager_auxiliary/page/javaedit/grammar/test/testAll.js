/**
* @namespace
* @name testAll 
* @description   
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../javalv2");
    FW.register({
        "name": "testAll",
        /**
        *@function
        *@memberOf testAll
        *@name onCreate$onCreate
        */
        "onCreate": function() {
            //toDO
            this.MY.javalv2 = FW.createApp("javalv2", "javalv2", this);
            this.API.show("main");
        },
        "public": {
            /**
            *@function
            *@memberOf testAll
            *@name public$testJavalv2
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param javatext java的原文件
            */
            "testJavalv2": function(javatext) {
                //toDo
                var app = this.MY.javalv2.parserJava(javatext);
                return FW.use().toJSONString(app);
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf testAll
            *@name FireEvent$parserJava
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@param p2
            *@return toDo
            *@example toDO
            */
            "parserJava": function(p1, p2) {
                //toDo
                var str = $("#" + p1).val();

                var result = this.testJavalv2(str);
                $("#" + p2).val(result);
            }
        },
        view: {
            'main': require("./resource/testAll/main.tpl")
        }

    },
    module);
    return FW;
});