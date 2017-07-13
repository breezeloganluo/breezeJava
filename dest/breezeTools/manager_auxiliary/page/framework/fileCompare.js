/**
* @namespace
* @name fileCompare 
* @version 0.01 罗光瑜 比较工具
* @description  文件比较工具                          
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "fileCompare",
        /**
        *@function
        *@memberOf fileCompare
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf fileCompare
            *@name public$showContent
            *@description [功能]显示文件比较的主体框架
            *[思路]
            */
            "showContent": function() {
                //toDo
                this.API.show("main");
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name public$showBCResult
            *@description [功能]显示比较结果
            */
            "showBCResult": function() {
                //设置头部工具按钮
                this.showTools();
                // 获取左边选择项
                var data = FW.use().getFormValue("singleFormDataLeft");
                this.API.mask("wait");

                if (data.ctype == "历史文件") {
                    this.API.doServer("getFileRecord", "file", data,
                    function(code, rs) {
                        if (code == 0) {
                            this.MY.lf = rs || {};
                            this.API.private('proecessBCResult');
                        }
                    });
                } else if (data.ctype == "当前文件") {
                    lf = this.API.doServer("getFileList", "file", data,
                    function(code, rs) {
                        if (code == 0) {
                            this.MY.lf = rs || {};
                            this.API.private('proecessBCResult');
                        }
                    });
                }
                // 获取右边选择项
                data = FW.use().getFormValue("singleFormDataRight");
                value = {};
                if (data.ctype == "历史文件") {
                    this.API.doServer("getFileRecord", "file", data,
                    function(code, rs) {
                        if (code == 0) {
                            this.MY.rf = rs || {};
                            this.API.private('proecessBCResult');
                        }
                    });
                } else if (data.ctype == "当前文件") {
                    this.API.doServer("getFileList", "file", data,
                    function(code, rs) {
                        if (code == 0) {
                            this.MY.rf = rs || {};
                            this.API.private('proecessBCResult');
                        }
                    });
                }
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name public$showTools
            *@description [功能]显示头部按钮信息
            *[思路]显示头部按钮列表
            */
            "showTools": function() {
                //直接显示
                this.API.show("tools", null, "buttonHead");
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name public$showResult
            *@description [功能]显示结果，之所以要这个，是因为支持过滤，过滤的原理是用原来已经有的比较结果基础上过滤的，所以要有一个单独显示结果的函数
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param filter 过滤函数
            */
            "showResult": function(filter) {
                //toDo
                this.MY.llf.filter = filter;
                this.MY.rrf.filter = filter;
                this.API.show("bcResult", this.MY.llf, "leftsite");
                this.API.show("bcResult", this.MY.rrf, "rightsite");
            }
        },
        "private": {
            /**
            *@function
            *@memberOf fileCompare
            *@name private$bcFile
            *@description [功能]整理左侧文件
            *整合比较文件
            *@param baseFile 基本文件
            *@param destFile 对比文件
            */
            "bcFile": function(baseFile, destFile) {
                //直接比较
                for (n in destFile) {
                    var src = baseFile[n];
                    var dest = destFile[n];

                    if (src == null) {
                        baseFile[n] = {
                            style: "alert-default"
                        }

                        dest.style = "alert-danger";
                        continue;
                    }

                    if (dest.length == null && dest.lastModify == null) {
                        src.style = "alert-danger";
                        continue;
                    }

                    if (src.length == dest.length) {
                        src.style = "alert-success";
                        continue;
                    }

                    if (src.lastModify < dest.lastModify) {
                        src.style = "alert-warning";
                        continue
                    }

                    if (src.lastModify >= dest.lastModify) {
                        src.style = "alert-info";
                        continue;
                    }

                }
                return baseFile;
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name private$obj2array
            *@description [功能]将对象比数组
            *@param obj 对象
            */
            "obj2array": function(obj) {
                //将对象转换
                var result = [];
                for (var n in obj) {
                    var one = obj[n];
                    one.name = n;
                    result.push(one);
                }
                //排序
                result.sort(function(a, b) {
                    if (a.name > b.name) {
                        return - 1;
                    }
                    if (a.name < b.name) {
                        return 1;
                    }
                    return 0;
                });

                return result;
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name private$proecessBCResult
            *@description [功能]显示结果
            */
            "proecessBCResult": function() {
                //判断是否可以
                if (this.MY.lf == null || this.MY.rf == null) {
                    return;
                }
                //赋值
                FW.unblockUI();
                var lf = this.MY.lf;
                var rf = this.MY.rf;
                //整理比较文件
                var llf = this.API.private('bcFile', lf, rf);
                this.MY.llf = this.API.private('obj2array', llf);
                var rrf = this.API.private('bcFile', rf, lf);
                this.MY.rrf = this.API.private('obj2array', rrf);
                //显示比较结果
                this.showResult();
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name private$filterShowOne
            *@description [功能]根据过滤条件决定是否显示内容
            *@param filterType 过滤条件
            *@param idx 文件索引
            */
            "filterShowOne": function(filterType, data, idx) {
                data.idx = idx;
                //根据不同过滤情况显示内容
                if (filterType == null) {
                    return this.API.show("oneFile", data, "_");
                }
                if (filterType == "忽略缺失") {
                    if (data.style == "alert-default" || data.style == "alert-danger") {
                        return "";
                    }
                    return this.API.show("oneFile", data, "_");
                }
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf fileCompare
            *@name FireEvent$selectType
            *@description [功能]选择文件类型
            *[思路]根据单选按钮判断情况作出不同的显示处理
            *@param type 左侧或右侧类型
            */
            "selectType": function(type) {
                //根据左侧还是右侧找出节点
                var value = FW.use().getFormValue("singleFormData" + type);
                var tv = value.ctype;
                if (tv == "当前文件") {
                    this.API.show("", null, "moreSet" + type);
                } else if (tv = "历史文件") {
                    this.API.show("historyForm", null, "moreSet" + type);
                    try {
                        $('.datetimepickerset').datetimepicker({
                            format: "yyyy-mm-dd hh:ii:ss",
                            autoclose: true,
                            startView: 2,
                            minView: 0
                        });

                        $('.datetimepickerset').datetimepicker().on('changeDate',
                        function(ev) {
                            $(this).parent().find(".realValue").val(ev.date.getTime());
                        });
                    } catch(e) {
                        alert("ace模版暂不支持DateTimePicker类型");
                        console.log(e);
                    }
                }
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name FireEvent$beginBC
            *@description [功能]获取信息
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "beginBC": function(p1) {
                //toDo
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name FireEvent$loadFiles
            *@description [功能]读取文件
            *[思路]根据选择情况读取文件列表
            */
            "loadFiles": function() {
                // 显示比较信息
                this.showBCResult();
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name FireEvent$reset
            *@description [功能]重置
            */
            "reset": function() {
                //toDo
                this.showContent();
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name FireEvent$filterNormal
            *@description [功能]忽略缺失文件
            *@param filter 过滤名称
            */
            "filterNormal": function(filter) {
                //toDo
                this.showResult(filter);
            },
            /**
            *@function
            *@memberOf fileCompare
            *@name FireEvent$compareOne
            *@description [功能]比较其中一个文件
            *@param idx 文件索引
            */
            "compareOne": function(idx) {
                //获取比较数据
                fileObj = {
                    lfDir: this.MY.llf[idx].orgUrl || this.MY.llf[idx].url,
                    rfDir: this.MY.rrf[idx].orgUrl || this.MY.rrf[idx].url
                }
                var file = FW.use().toJSONString(fileObj);
                //打开信息
                var gadgetName = "oneFileCompare";
                var app = FW.createApp(gadgetName, gadgetName, {
                    dom: "m_content"
                });
                app.param.fileUrl = file;
                //显示到页面
                this.param.main && this.param.main.addTopApp && this.param.main.addTopApp(app, "单文件比较", [gadgetName, gadgetName, {
                    dom: "m_content"
                }]);
            }
        },
        view: {
            'main': require("./resource/fileCompare/main.tpl"),
            'historyForm': require("./resource/fileCompare/historyForm.tpl"),
            'bcResult': require("./resource/fileCompare/bcResult.tpl"),
            'wait': require("./resource/fileCompare/wait.tpl"),
            'tools': require("./resource/fileCompare/tools.tpl"),
            'oneFile': require("./resource/fileCompare/oneFile.tpl")
        }

    },
    module, '0.01');
    return FW;
});