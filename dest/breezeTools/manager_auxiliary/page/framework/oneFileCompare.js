/**
* @namespace
* @name oneFileCompare 
* @version 0.01 罗光瑜 初始版本
* @description  单个文件比较           
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../../commtools/fileselect");
    FW.register({
        "name": "oneFileCompare",
        /**
        *@function
        *@memberOf oneFileCompare
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
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
        },
        "public": {
            /**
            *@function
            *@memberOf oneFileCompare
            *@name public$showContent
            *@description [功能]开始比较单个文件
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            */
            "showContent": function() {
                //处理参数
                var param = this.param.fileUrl;
                if (param != null) {
                    FW.use().save("_onefilecompare", param, true);
                } else {
                    param = FW.use().load("_onefilecompare", true);
                }

                var control = {
                    lfDir: "",
                    rfDir: ""
                }
                if (param != null) {
                    control = eval("(" + param + ")");
                }
                //读取文件
                this.MY.left = {
                    name: control.lfDir,
                    content: ""
                }
                if (control.lfDir) {
                    this.MY.left.content = this.MY.fileOper.queryFileContent(control.lfDir);
                }

                this.MY.right = {
                    name: control.rfDir,
                    content: ""
                }
                if (control.rfDir) {
                    this.MY.right.content = this.MY.fileOper.queryFileContent(control.rfDir);
                }
                //显示数据库表
                this.API.show("main", control);
                this.API.private('compare');
                this.showResult();
            },
            /**
            *@function
            *@memberOf oneFileCompare
            *@name public$showResult
            *@description [功能]显示结果
            */
            "showResult": function() {
                //toDo
                this.API.show("one", this.MY.left, "leftsite");
                this.API.show("one", this.MY.right, "rightsite");
            }
        },
        "private": {
            /**
            *@function
            *@memberOf oneFileCompare
            *@name private$compare
            *@description [功能]比较结果
            *比较算法，两边对比，如果左边和右边相等，就跳过
            *如果不登，先移动到右边，进行匹配，在用右边和移动左边匹配，取一个较好的
            */
            "compare": function() {
                //获取两个初始函数
                var larr = this.MY.left.arr = this.MY.left.content.split(/[\r\n]/);
                var rarr = this.MY.right.arr = this.MY.right.content.split(/[\r\n]/);
                var lidx = 0;
                var ridx = 0;
                //for (循环两个数组比较){
                while (ridx < larr.length && ridx < rarr.length) {
                    //获取左右数据
                    var lone = larr[lidx] = (function(str) {
                        return {
                            toString: function() {
                                return str;
                            }
                        }
                    })(larr[lidx]);

                    var rone = rarr[ridx] = (function(str) {
                        return {
                            toString: function() {
                                return str;
                            }
                        }

                    })(rarr[ridx])
                    //比较两行相等就下一轮
                    if (this.API.private('compareLine', lone, rone)) {
                        lidx++;
                        ridx++;
                        continue;
                    }
                    //获取左右跳跃比较结果
                    var lrs = this.API.private('moveCompare', lone, rarr, ridx);
                    var rrs = this.API.private('moveCompare', rone, larr, lidx);
                    //如果两个都没找到，就是不相等情况
                    //--两个变颜色，然后下一轮
                    if (lrs == 1000000 && rrs == 1000000) {
                        lone.style = "alert-warning";
                        rone.style = "alert-warning";
                        lidx++;
                        ridx++;
                        continue;
                    }
                    //如果左边最优，处理左边
                    if (lrs <= rrs) {
                        lone.move = lrs;
                        for (i = 0; i < lrs; i++) {
                            rone = rarr[ridx + i] = (function(str) {
                                return {
                                    toString: function() {
                                        return str;
                                    }
                                }

                            })("" + rarr[ridx + i])

                            rone.style = "alert-warning";
                        }
                        lidx++;
                        ridx = ridx + lrs + 1;
                        continue;
                    }
                    //如果右边最优，处理右边
                    if (lrs >= rrs) {
                        rone.move = rrs;
                        for (i = 0; i < rrs; i++) {
                            lone = larr[lidx + i] = (function(str) {
                                return {
                                    toString: function() {
                                        return str;
                                    }
                                }
                            })("" + larr[lidx + i]);

                            lone.style = "alert-warning";
                        }
                        lidx = lidx + rrs + 1;
                        ridx++;
                        continue;
                    }
                }
                //}
            },
            /**
            *@function
            *@memberOf oneFileCompare
            *@name private$compareLine
            *@description [功能]比较一行字符串
            *[思路]
            *@param lstr 左边字符串
            *@param rstr 右边字符串
            */
            "compareLine": function(lstr, rstr) {
                var l = ("" + lstr).replace(/(^\s+)|(\s+$)/ig, "");
                var r = ("" + rstr).replace(/(^\s+)|(\s+$)/ig, "");

                return l == r;
            },
            /**
            *@function
            *@memberOf oneFileCompare
            *@name private$moveCompare
            *@description [功能]已左边为基点移动比较的指针，看看哪个指针对应的字符相等
            *如果都不相等，就返回1000000
            *@param src 原来字符
            *@param destArr 目标数组
            *@param destIdx 目标数组索引
            */
            "moveCompare": function(src, destArr, destIdx) {
                //for(所有后续指针){
                for (var i = destIdx + 1; i < destArr.length; i++) {
                    //获取一个
                    var one = destArr[i];
                    //如果相同，计算结果并返回
                    if (this.API.private('compareLine', src, one)) {
                        return i - destIdx;
                    }
                }
                //}
                //直接返回失败
                return 1000000;
            },
            /**
            *@function
            *@memberOf oneFileCompare
            *@name private$parserStr
            *@description [功能]处理显示的字符串
            */
            "parserStr": function(idx, str) {
                //获取初始数据
                var len = str.move || 0;
                var style = str.style || "alert-default";
                var result = "";
                //处理空置跳跃的行
                for (var i = 0; i < len; i++) {
                    result += this.API.show("oneline", {
                        style: "alert-default",
                        str: "&nbsp;"
                    },
                    "_");
                }
                //处理要显示的字符串
                var linestr = (idx + 1) + ":" + ("" + str).replace(/\s/ig, "&nbsp;").replace(/</ig, "&lt;").replace(/>/ig, "&gt;");
                result += this.API.show("oneline", {
                    style: style,
                    str: linestr
                },
                "_");
                return result;
            }
        },
        view: {
            'main': require("./resource/oneFileCompare/main.tpl"),
            'one': require("./resource/oneFileCompare/one.tpl"),
            'oneline': require("./resource/oneFileCompare/oneline.tpl")
        }

    },
    module, '0.01');
    return FW;
});