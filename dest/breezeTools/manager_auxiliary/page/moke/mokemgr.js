/**
* @namespace
* @name mokemgr 
* @version 0.01 罗光瑜 初始版本
* @description  模拟桩数据管理类
*负责对模拟数据管理，模拟数据的所在路径为data/moke下面     
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../../commtools/treeView");
    var textformat = require("../../../breeze/framework/js/tools/formatJS");
    FW.register({
        "name": "mokemgr",
        /**
        *@function
        *@memberOf mokemgr
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
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
            //初始化左边菜单树
            this.MY.treeView = FW.createApp("myTree", "treeView", {},
            true);
        },
        "public": {
            /**
            *@function
            *@memberOf mokemgr
            *@name public$showContent
            *@description [功能]入口函数，显示整体的管理界面
            *[思路]显示左边的菜单，然后根据菜单的点击选择选择右边内容进行展示
            */
            "showContent": function() {
                //获取基本的编辑的文件名
                var searchFile = this.param.fileUrl;
                if (searchFile == null) {
                    searchFile = FW.use().load("mokeurl", true);
                } else {
                    FW.use().save("mokeurl", searchFile, true);
                }
                this.MY.fileUrl = searchFile;
                //调用框架，处理文件系统
                var settingdata = {
                    gadget: "mokemgr",
                    url: searchFile
                }
                this.param.main.setFileHash(FW.use().toJSONString(settingdata));
                //显示总体框架
                this.API.show("main");
                //显示树
                this.showTree();
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name public$showTree
            *@description [功能]显示树
            *[思路]调用树组件实现
            */
            "showTree": function() {
                //处理默认的点击状态，如果点击状态为空，默认就是基础信息了
                if (this.MY.clickInfo == null) {
                    this.MY.clickInfo = "base";
                }
                //获取数据
                var treeData = this.API.private('getData');
                //使用组件显示菜单树
                this.MY.treeView.init(treeData);
                //设定回调方法
                var _this = this;
                this.MY.treeView.setEventCall(function(e, o) {
                    var info = o.info && o.info[0];
                    _this.API.private('clickEvent', info);

                })
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name public$showOneMethod
            *@description [功能]显示其中一个具体的方法节点
            *[思路]直接show
            *[this.MY.currentClick]=当前点击的节点
            */
            "showOneMethod": function() {
                //显示
                this.API.show("content", this.MY.currClick.content || [], "content");
            }
        },
        "private": {
            /**
            *@function
            *@memberOf mokemgr
            *@name private$getData
            *@description [功能]获取远程的数据
            *[思路]要有缓存，如果缓存数据不在就到远程获取
            *[接口.this.MY.tree]远程数据
            *这个数据结构为：
            *[
            *            {
            *            name:"显示名称",
            *            type:"item/folder",
            *            icon-img:"图标地址本接口无用",
            *            icon:"icon的样式内容，比如icon-music blue"，
            *            selectedIcon:"选中的图标",
            *            expanded:是否展开
            *            selected:是否选中
            *            其他自定义属性,
            *            children:[儿子的内容，循环上面父亲的结构
            *            ]
            *            }
            *            ]
            *这是目录树那个组件所需要的结构
            *而远程的方法为:
            *[service.moke.getAllData.param]={
            *    url:"当前的url文件，将http头部去掉，剩余的应用前缀去掉，然后用_将/替换掉"
            *}
            *[service.moke.getAllData.return]=[
            *    {
            *         name:"文件或目录名",
            *         type:"item/folder"
            *         children:[子目录],
            *         content:"文件内容"
            *    }
            *]
            *文件内容是这个gadget的方法，这层也要把文件解析成节点，里面的每个方法解析成对应叶子，文件内容结构为：
            *{
            *    "方法名":[{"input":[{"a":"b"}],"output":{"c":"d"}}]
            *}
            */
            "getData": function() {
                //先判断是否有缓存，有就直接返回
                if (this.MY.tree) {
                    return this.MY.tree;
                }
                //整理url
                var myurl = this.MY.fileUrl;
                var url = myurl.replace(/^http[s]?:\/\/[^\/]+/i, "");
                //去掉尾部?部分
                url = url.replace(/[#\?][\s\S]*$/i, "");
                //去掉应用名部分
                if (/undefined/.test(typeof(Cfg))) {
                    alert("Cfg.baseUrl必须被定义，在/config/config.jsp中定义按照js方式加载");
                    return;
                }
                if (!/undefined/.test(typeof(Cfg)) && url.indexOf(Cfg.baseUrl) == 0) {
                    url = url.substr(Cfg.baseUrl.length);
                } else {
                    url = url.replace(/^.*(\/page\/)/i,
                    function(a, b, c, d, e) {
                        return b;
                    })
                }
                //变成可用符号
                url = ("/" + url).replace(/[\\\/]+/ig, "_");
                //发送请求
                var result = this.API.doServer("getAllData", "moke", {
                    url: url
                });
                if (result.code != 0) {
                    FW.alert("获取数据失败");
                    return [];
                }
                var data = result.data;
                if (data == null || data.length == 0) {
                    return [];
                }
                //for(遍历所有目录{第一层循环所有场景
                for (var i = 0; i < data.length; i++) {
                    var oneData = data[i];
                    //for(第二层循环){循环某个场景的所有文件
                    for (var j = 0; oneData.children != null && j < oneData.children.length; j++) {
                        //初始化单个文件内容
                        var fileData = oneData.children[j];
                        fileData.children = [];
                        //block(块){文件块
                        //eval文件
                        var fConentObj = {};
                        if (fileData) {
                            fConentObj = eval("(" + fileData.content + ")");
                        }
                        //重整理文件数据
                        fileData && delete fileData.content;
                        fileData.type = "folder";
                        fileData.name = fileData.name.replace(/\.mok$/i, "");
                        //将文件内容数据记录进去
                        for (var n in fConentObj) {
                            var oneMethodName = {
                                name: n,
                                type: "item",
                                content: fConentObj[n],
                                url: url,
                                scene: oneData.name,
                                gadget: fileData.name
                            }
                            fileData.children.push(oneMethodName);
                        }
                        //}
                    }
                    //}
                }
                //}
                //返回数据
                this.MY.tree = data;
                return data;
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name private$clickEvent
            *@description [功能]点击事件
            *[思路]点击某个节点后的操作处理
            *[参数.this.MY.contenArray]=点击后的对象缓存记录起来
            *@param info 点击信息
            */
            "clickEvent": function(info) {
                //过滤非节点情况
                if (!/item/i.test(info.type)) {
                    return;
                }
                //打开显示当前
                info.content = info.content || [];
                this.MY.currClick = info;
                this.showOneMethod();
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name private$parserContent
            *@description [功能]显示的时候需要把要显示的内容解析成json
            *[思路]判断类型，然后json化，然后再格式化
            *@param obj obj
            */
            "parserContent": function(obj) {
                //if (是空){返回空字符串
                if (obj == null) {
                    return "";
                }
                //}
                //else if (是字符串){直接返回
                else if (/string/i.test(typeof(obj))) {
                    return obj;
                }
                //}
                //else{
                else {
                    var json = "错误json格式";
                    try {
                        json = FW.use().toJSONString(obj);
                    } catch(e) {
                        return json;
                    }
                }
                //}
                return textformat.js_beautify(json);
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf mokemgr
            *@name FireEvent$open
            *@description [功能]打开目录
            *[思路]打开原来的目录
            *@param idx 被打开的目录id
            */
            "open": function(idx) {
                //打开目录
                var currrObj = this.MY.currClick.content;
                //重新设置信息
                for (var i = 0; i < currrObj.length; i++) {
                    if (i != idx) {
                        currrObj[i].isOnpen = false;
                    } else {
                        currrObj[i].isOnpen = true;
                    }
                }
                this.showOneMethod();
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name FireEvent$close
            *@description [功能]点击关闭按钮，收起当前编辑
            *[思路]设置数据结构，然后show出去
            *@param idx 要show的索引
            */
            "close": function(idx) {
                //打开目录
                var currrObj = this.MY.currClick.content;
                currrObj[idx].isOnpen = false;

                this.showOneMethod();
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name FireEvent$saveInput
            *@description [功能]保存输入
            *[思路]获取到节点后，直接提交到后台完成文件保存工作
            *[参数.this.MY.currClick]
            *[service.moke.saveOne.param]={
            *      ur:所在url信息
            *      scene:所属的场景
            *      gadget:gadget的名称
            *      method:函数名
            *      idx:第几个函数
            *      type:"input/output",
            *      content:"内容"
            *}
            *@param idx 当前的索引
            */
            "saveInput": function(idx) {
                //获取数据
                var clickObj = this.MY.currClick;
                var param = {
                    url: clickObj.url,
                    scene: clickObj.scene,
                    gadgetName: clickObj.gadget,
                    funName: clickObj.name,
                    idx: idx,
                    type: "input",
                    content: $("#inputtext").val()
                }
                try {
                    param.content = eval("(" + param.content + ")");
                } catch(e) {}
                //发送请求，记录文件
                var result = this.API.doServer("saveOne", "moke", param);
                if (result.code == 0) {
                    this.MY.tree = null;
                    this.showTree();
                    FW.alert("操作成功");
                } else {
                    FW.alert("保存失败");
                }
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name FireEvent$addOne
            *@description [功能]新建一组输入输出内容
            *[思路]从节点中获取对应参数，然后发消息
            *[接口.service.moke.addOne.param]{
            *      url:请求url
            *      scene:场景
            *      gadget:gadget名
            *      method:方法名
            *}
            */
            "addOne": function() {
                //获取数据
                var clickObj = this.MY.currClick;
                var param = {
                    url: clickObj.url,
                    scene: clickObj.scene,
                    gadgetName: clickObj.gadget,
                    funName: clickObj.name
                }
                //发送请求，记录文件
                var result = this.API.doServer("addOne", "moke", param);
                if (result.code == 0) {
                    this.MY.tree = null;
                    this.showTree();
                    //显示
                    FW.alert("操作成功");
                    this.API.show("请重新选择", null, "content");
                } else {
                    FW.alert("保存失败");
                }
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name FireEvent$saveOutput
            *@description [功能]保存输入
            *[思路]获取到节点后，直接提交到后台完成文件保存工作
            *[参数.this.MY.currClick]
            *[service.moke.saveOne.param]={
            *      ur:所在url信息
            *      scene:所属的场景
            *      gadget:gadget的名称
            *      method:函数名
            *      idx:第几个函数
            *      type:"input/output",
            *      content:"内容"
            *}
            *@param idx 当前的索引
            */
            "saveOutput": function(idx) {
                //获取数据
                var clickObj = this.MY.currClick;
                var param = {
                    url: clickObj.url,
                    scene: clickObj.scene,
                    gadgetName: clickObj.gadget,
                    funName: clickObj.name,
                    idx: idx,
                    type: "output",
                    content: $("#outputtext").val()
                }
                try {
                    param.content = eval("(" + param.content + ")");
                } catch(e) {}
                //发送请求，记录文件
                var result = this.API.doServer("saveOne", "moke", param);
                if (result.code == 0) {
                    this.MY.tree = null;
                    this.showTree();
                    FW.alert("操作成功");
                } else {
                    FW.alert("保存失败");
                }
            },
            /**
            *@function
            *@memberOf mokemgr
            *@name FireEvent$delOne
            *@description [功能]删除其中一个
            *[思路]整理参数，发后台请求
            *[接口.service.moke.delOne.param]{
            *      url:请求url
            *      scene:场景
            *      gadget:gadget名
            *      method:方法名
            *      idx:索引号
            *}
            *@param idx 索引号
            */
            "delOne": function(idx) {
                //获取数据
                var clickObj = this.MY.currClick;
                var param = {
                    url: clickObj.url,
                    scene: clickObj.scene,
                    gadgetName: clickObj.gadget,
                    funName: clickObj.name,
                    idx: idx
                }
                //发送请求，记录文件
                var result = this.API.doServer("delOne", "moke", param);
                if (result.code == 0) {
                    this.MY.tree = null;
                    this.showTree();
                    FW.alert("操作成功");
                    this.API.show("请重新选择", null, "content");
                } else {
                    FW.alert("保存失败");
                }
            }
        },
        view: {
            'main': require("./resource/mokemgr/main.tpl"),
            'content': require("./resource/mokemgr/content.tpl")
        }

    },
    module);
    return FW;
});