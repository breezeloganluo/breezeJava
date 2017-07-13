/**
* @namespace
* @name package 
* @version 0.01 罗光瑜 初始版本
* @description  这是一个大包的工具，
*先实现简单的将文件分离，找依赖关系，然后合并 
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "package",
        /**
        *@function
        *@memberOf package
        *@name onCreate$onCreate
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf package
            *@name public$showContent
            *@description [功能]
            */
            "showContent": function() {
                //toDo
                this.API.show("main");
            },
            /**
            *@function
            *@memberOf package
            *@name public$showResultList
            *@description [功能]显示结果页面
            *[思路]
            *@param data
            */
            "showResultList": function(data) {
                this.API.show("result", data);
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf package
            *@name FireEvent$package
            *@description [功能]触发进行打包服务
            *[思路]
            */
            "package": function() {
                this.API.mask("packing", null, 500);
                this.API.doServer("package", "package", {},
                function(code, data) {
                    if (code == 0) {
                        FW.alert("打包成功");
                        FW.unblockUI();
                        this.showResultList(data);
                    } else {
                        FW.alert("打包失败");
                        FW.unblockUI();
                    }

                });
            }
        },
        view: {
            'main': require("./resource/package/main.tpl"),
            'packing': require("./resource/package/packing.tpl"),
            'result': require("./resource/package/result.tpl")
        }

    },
    module, '0.01');
    return FW;
});