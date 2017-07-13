/**
* @namespace
* @name debugservice 
* @version 0.01 罗光瑜 初始版本
* @description  类编辑器 
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "debugservice",
        /**
        *@function
        *@memberOf debugservice
        *@name onCreate$onCreate
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf debugservice
            *@name public$showContent
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "showContent": function(p1) {
                //显示主页面
                this.API.show("main", [{}]);
            }
        },
        view: {
            'main': require("./resource/debugservice/main.tpl")
        }

    },
    module);
    return FW;
});