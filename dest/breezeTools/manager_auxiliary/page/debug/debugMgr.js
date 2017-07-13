/**
* @namespace
* @name debugMgr 
* @version 0.01 罗光瑜 初始版本
* @description  主要的处理类，生产随机的信息然后控制本地的debug开关      
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "debugMgr",
        "param": {
            /**
            *@memberOf debugMgr
            *@name uuid
            *@description 默认的随机值
            */
            "uuid": "111111"
        },
        /**
        *@function
        *@memberOf debugMgr
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //显示主页面
            this.showMain();
            this.showGadgetInfo();
            this.showLog();
        },
        "public": {
            /**
            *@function
            *@memberOf debugMgr
            *@name public$showMain
            *@description [功能]显示主页面
            *[思路]直接显示主页面
            */
            "showMain": function() {
                //获取原来的值
                var data = FW.use().load("_threadSignal", true);
                var threadSignal = data && data.threadSignal;
                //判断时间
                this.MY.overTime = false;
                var retime = data && data.time;
                var now = (new Date()).getTime();
                if (threadSignal && retime + 5 * 60 * 1000 < now) {
                    this.MY.overTime = true;
                    FW.use().save("_threadSignal", null, true);
                    this.MY.overTime = true;
                    threadSignal = null;
                }
                this.API.show("main", {
                    s: threadSignal
                });
            },
            /**
            *@function
            *@memberOf debugMgr
            *@name public$showGadgetInfo
            *@description [功能]显示gadget的版本信息
            */
            "showGadgetInfo": function() {
                var gadgetInfo = FW.use().load("_allGadgetVersion", true);
                if (gadgetInfo) {
                    this.API.show("gadgetInfo", gadgetInfo, "gadgetInfo");
                    if (this.MY.overTime) {
                        FW.use().save("_allGadgetVersion", null, true);
                    }
                }
            },
            /**
            *@function
            *@memberOf debugMgr
            *@name public$showLog
            *@description [功能]显示日志
            */
            "showLog": function() {
                //获取数据
                var result = this.API.doServer("log", "debug", {
                    oper: "get"
                });
                if (!result || result.code != 0) {
                    return;
                }
                var logInfo = result.data;
                this.API.show("logInfo", logInfo, "logInfo");
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf debugMgr
            *@name FireEvent$newOne
            *@description [功能]设置新值
            *[思路]到后台发消息
            */
            "newOne": function() {
                var result = this.API.doServer("getUUID", "debug");
                var uuid = result.data.replace(/-/ig, "");

                uuid = uuid.substr(0, 3) + uuid.substr(5, 2) + uuid.substr(8, 3);

                FW.use().save("_threadSignal", {
                    threadSignal: uuid,
                    time: (new Date()).getTime()
                },
                true);
                this.showMain();
            },
            /**
            *@function
            *@memberOf debugMgr
            *@name FireEvent$stopOne
            *@description [功能]清除掉日志标识
            *[思路]
            */
            "stopOne": function() {
                //删除日志
                FW.use().save("_threadSignal", null, true);
                FW.use().save("_allGadgetVersion", null, true);
                FW.use().save("_pgLog", null, true);
                this.showMain();
            },
            /**
            *@function
            *@memberOf debugMgr
            *@name FireEvent$clickGadgetInfo
            *@description [功能]点击事件
            *[思路]点击gadget是否隐藏或显示
            */
            "clickGadgetInfo": function() {
                var dom = $("#clickGadgetInfo");
                if (dom.css("display") == "none") {
                    dom.fadeIn();
                } else {
                    dom.fadeOut();
                }
            },
            /**
            *@function
            *@memberOf debugMgr
            *@name FireEvent$clickLog
            *@description [功能]点击日志效果
            */
            "clickLog": function() {
                var dom = $("#innerLogInfo");
                if (dom.css("display") == "none") {
                    dom.fadeIn();
                } else {
                    dom.fadeOut();
                }
            },
            /**
            *@function
            *@memberOf debugMgr
            *@name FireEvent$clearLog
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            */
            "clearLog": function() {
                this.API.doServer("log", "debug", {
                    oper: "del"
                });
                this.showLog();
            }
        },
        view: {
            'main': require("./resource/debugMgr/main.tpl"),
            'gadgetInfo': require("./resource/debugMgr/gadgetInfo.tpl"),
            'logInfo': require("./resource/debugMgr/logInfo.tpl")
        }

    },
    module, '0.01');
    return FW;
});