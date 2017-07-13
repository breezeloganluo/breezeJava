/**
* @namespace
* @name test 
* @description  undefined 
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "test",
        /**
        *@function
        *@memberOf test
        *@name onCreate$onCreate
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf test
            *@name public$testA
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "testA": function(p1) {
                //toDo
                //if(分支1){做分支1的事情
                //toDo
                //}
                //else if(分支2){做分支2的事情
                //toDo
                //}
            }
        }
    },
    module);
    return FW;
});