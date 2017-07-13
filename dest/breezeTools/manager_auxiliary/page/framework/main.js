/**
* @namespace
* @name main 
* @version v0.01 罗光瑜 初始版本
* @description  1.在用法上，支持通过hash直接访问和打开，很简单，用类似下面格式:
*{"gadget":"editJava","url":"/aaa.java"}
*然后编码，把编码结果放到hash中即可
*2.挂接到下面的子应用只要实现：showContent函数，即入口函数即可。
*  在首次进入后，从this.param.fileUrl中可以获取从主框架中传入的URL参数。
*  另外，而加载这个gadget则要修改main.jsp里面的配置和use中加载这个类。
*  
*主控制类，负责加载下面各个子页面的逻辑，主加载逻辑就是维护一个框架结构:
*[
*    {
*       name:"标签名称",
*       type:"app/container",//app表示一个对象标签实例，container表示继续还是一个容器
*       selected:true,//表示是否选中
*       content:{}//"根据类型这里可能就是一个对象"
*    },
*    {
*       name:"标签名称",
*       type:"container",//obj表示一个对象标签实例，container表示继续还是一个容器
*       selected:true,//表示是否选中
*       content:[//根据类型这里可能就是一个对象
*              {....重复对应的内容了}
*       ]
*    }
*]
*对任何一层来说，就是两个概念，画标题，画对应的某个具体内容，即使嵌套逻辑也是如此 
*其中，每一个子对象，都被框架对象设置了对应的属性：
*one.content.param.main = this;
*one.content.param.dataArr = objArr;
*one.content.param.idx = i;
*one.content.param.tagIdx = tagIdx;
*one.content.param.tagPath = tagPath;     
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("./fileView");
    FW.register({
        "name": "main",
        /**
        *@function
        *@memberOf main
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //block(块)初始化数据
            //有hash设置，直接跳转hash应用
            var hashData = location.hash;
            if (hashData != null && hashData != "") {
                hashData = hashData.replace(/^#/ig, "");
                if (hashData != "" && !/^none$/i.test(hashData)) {
                    var hashObj = eval("(" + decodeURI(hashData) + ")");
                    var fileNameArr = hashObj.url.split(/[\\\/]/);
                    var app = FW.createApp(hashObj.gadget, hashObj.gadget, {
                        "dom": "m_content"
                    });
                    app.param.fileUrl = hashObj.url;

                    this.MY.allData = [{
                        name: fileNameArr[fileNameArr.length - 1],
                        type: "app",
                        selected: true,
                        appParam: ["editJava", "editJava", {
                            "dom": "m_content"
                        }],
                        content: app

                    }];
                    this.MY.allData.isOnly = true;
                }

            }
            //有缓存，从缓存读取
            if (this.MY.allData == null && !this.API.private('loadAllData')) {
                this.MY.allData = [{
                    name: "文件管理",
                    type: "container",
                    selected: true,
                    content: [[{
                        name: "/",
                        type: "app",
                        selected: true,
                        appParam: ["[0].content[0][0]", "fileView", {
                            dom: "m0_content"
                        }],
                        content: FW.createApp("[0].content[0][0]", "fileView", {
                            dom: "m0_content"
                        })
                    }], [{
                        name: "/",
                        type: "app",
                        selected: true,
                        appParam: ["[0].content[1][0]", "fileView", {
                            dom: "m1_content"
                        }],
                        content: FW.createApp("[0].content[1][0]", "fileView", {
                            dom: "m1_content"
                        })
                    }], [{
                        name: "/",
                        type: "app",
                        selected: true,
                        appParam: ["[0].content[2][0]", "fileView", {
                            dom: "m2_content"
                        }],
                        content: FW.createApp("[0].content[2][0]", "fileView", {
                            dom: "m2_content"
                        })
                    }], [{
                        name: "/",
                        type: "app",
                        selected: true,
                        appParam: ["[0].content[3][0]", "fileView", {
                            dom: "m3_content"
                        }],
                        content: FW.createApp("[0].content[3][0]", "fileView", {
                            dom: "m3_content"
                        })
                    }]]
                }];
                this.API.private('saveAllData');
            }
            //}
            this.API.private('setApp', this.MY.allData);
            //显示主窗口
            this.API.show("main", this.MY.allData.isOnly);
            //初始化顶部图标
            this.showTopIcon();
            //初始化界面
            this.showTags(this.MY.allData);
            this.showContent(this.MY.allData);
        },
        "public": {
            /**
            *@function
            *@memberOf main
            *@name public$showTags
            *@description [功能]显示标签页，支持显示子标签
            *[思路]用id去查找显示标签的目标标签，id格式如下m[id]_tag,其中id表示第几个定标签下的第几个子标签，不存在就是""
            *@param tags 标签数组
            *@param id 如果是顶级就空
            *@param path 从顶节点访问下来的路径
            */
            "showTags": function(tags, id, path) {
                //初始化参数
                if (id == null) {
                    id = "";
                }
                if (path == null) {
                    path = "";
                }
                var target = "m" + id + "_tag";
                //将结果显示到页面上
                this.API.show("tag", {
                    tags: tags,
                    path: path
                },
                target);
            },
            /**
            *@function
            *@memberOf main
            *@name public$showContent
            *@description [功能]显示基本的页面内容
            *[思路]如果类型四obj就直接调用内容的showContent方法，如果是container类型，则调用showSubContent方法。id格式如下m[id1]_[id2]_content,其中id表示第几个定标签下的第几个子标签，不存在就是""
            *@param objArray 对应对象
            [
            {
            name:"标签名称",
            type:"obj/container",//obj表示一个对象标签实例，container表示继续还是一个容器
            selected:true,//表示是否选中
            content:{}//"根据类型这里可能就是一个对象"
            }
            ]
            *@param path 从定节点访问下来的路径
            */
            "showContent": function(objArray, path) {
                //查找出选中的元素
                var app = null;
                if (path == null) {
                    path = "";
                }
                var idx = 0;
                for (var i = 0; i < objArray.length; i++) {
                    if (objArray[i].selected) {
                        app = objArray[i];
                        idx = i;
                        break;
                    }
                }
                //if (是复合类型){调用私有的方法进行初始化
                if (app.type == "container") {
                    this.API.private('showSubContent', app, idx, path + "[" + i + "]");
                }
                //}
                //else{
                else {
                    app.content.showContent();
                }
                //}
            },
            /**
            *@function
            *@memberOf main
            *@name public$setTitle
            *@description [功能]设置标题的名字
            *@param objArray 对象
            *@param idx 索引
            *@param title 标题内容
            *@param tagIdx showTag的第二个参数
            *@param tagPath showTag的第三个参数
            */
            "setTitle": function(objArray, idx, title, tagIdx, tagPath) {
                //设置
                objArray[idx].name = title;
                //刷新标题
                this.showTags(objArray, tagIdx, tagPath);
            },
            /**
            *@function
            *@memberOf main
            *@name public$showTopIcon
            *@description [功能]显示顶部的类型为空的菜单
            *[思路]fileGlobleSetting是全局变量，在main.jsp中，参考这个段声明就知道
            *[接口.fileGlobleSetting]fileGlobleSetting = [
            *							{
            *								  name:"gadget编辑器",
            *								  exp:".js",
            *								  icon:"./img/icon/editgadget.png",
            *								  type:"file",
            *								  initDir: "/",
            *								  clickSetting: {
            *									  "link": "点击自身的事件",
            *									  'newone':"./gadgetCreator.jsp?fileUrl=[fileUrl]",
            *									  "编辑": "./gadgetCreator.jsp?fileUrl=[fileUrl]"
            *								  }
            *							},   {
            *								name:"需求管理",
            *								icon:"./img/icon/srsview.png",
            *								exp:".jsp",
            *								initDir: "/design/srs/",
            *								type:"file",
            *								clickSetting: {
            *									"link": "点击自身的事件",
            *									'newone':"./SRSCreator.jsp?fileUrl=[fileUrl]",
            *									"编辑": "./SRSCreator.jsp?fileUrl=[fileUrl]"
            *								}
            *							},
            *                                                        {
            *								  name:"Service测试",
            *								  exp:".js",
            *								  initDir: "/",
            *								  icon:"./img/icon/servicetest.png",
            *								  type:"selfedit",
            *								  gadget:"editServiceTest"
            *							}
            *                                                        ]
            */
            "showTopIcon": function() {
                //过滤出头部对象
                var data = [];
                for (var i = 0; i < fileGlobleSetting.length; i++) {
                    var one = fileGlobleSetting[i];
                    if (one.type == null || one.type == "self") {
                        data.push(one);
                        one.idx = i;
                    }
                }
                //显示到头部
                this.API.show("topIcon", data, "topIcon");
            },
            /**
            *@function
            *@memberOf main
            *@name public$addTopApp
            *@description [功能]添加一个顶级窗口
            *[思路]设置对象，然后显示页面
            *注意，类型要在这个类里面自行判断，直接用对象判断是否是gadget即可
            *{
            *       name:"标签名称",
            *       type:"app/container",//app表示一个对象标签实例，container表示继续还是一个容器
            *       selected:true,//表示是否选中
            *       content:{}//"根据类型这里可能就是一个对象"
            *    }
            *@param newObj 新窗口对象
            *@param name 名称
            *@param appParam app的参数
            */
            "addTopApp": function(newObj, name, appParam) {
                //添加顶级节点事件
                for (var i = 0; i < this.MY.allData.length; i++) {
                    this.MY.allData[i].selected = false;
                }
                //添加一个新的
                var type = "container";
                if (newObj.API) {
                    type = "app";
                }
                var one = {
                    name: name,
                    type: type,
                    selected: true,
                    content: newObj,
                    appParam: appParam
                };
                this.MY.allData.push(one);
                //设置app数据
                this.API.private('setApp');
                this.API.private('saveAllData');
                //显示出来
                this.showTags(this.MY.allData);
                this.showContent(this.MY.allData);
            },
            /**
            *@function
            *@memberOf main
            *@name public$setFileHash
            *@description [功能]设置文件hash链接
            *[思路]根据输入的url信息，进行页面设置
            *@param url 要设置的url
            */
            "setFileHash": function(url) {
                //转成encodeing
                var enUrl = encodeURI(url);
                //设置hash
                location.hash = enUrl;
            }
        },
        "private": {
            /**
            *@function
            *@memberOf main
            *@name private$showSubContent
            *@description [功能]显示子窗口结构内容
            *[思路]使用窗口结构方法去显示
            *@param p1 toDo
            *@param idx 第几个窗口
            *@param path 从顶结点访问下来的路径
            */
            "showSubContent": function(p1, idx, path) {
                //显示基础框架
                //--这里默认是四个，以后考虑兼容多个自动分n个的处理
                this.API.show("submain", {
                    content: p1.content,
                    path: path
                },
                "m_content");
                //显示每个子任务的儿子
                for (var i = 0; i < p1.content.length; i++) {
                    this.showTags(p1.content[i], i, path + ".content[" + i + "]");
                    this.showContent(p1.content[i], path + ".content[" + i + "]");
                }
            },
            /**
            *@function
            *@memberOf main
            *@name private$setApp
            *@description [功能]挂入数据的app实际和本类是有调用关系的，这个函数用于设置其关系，这个函数在所有设置变量的时候都要求调用一次，设置的参数分别是：
            *one.content.param.main = this;
            *one.content.param.dataArr = objArr;
            *one.content.param.idx = i;
            *[思路]递归遍历所有的数据，然后进行设置
            *[接口.this.MY.allData]所有的数据
            *@param objArr 对象数组
            *@param tagIdx showTags时的第二个参数，递归用参数
            *@param tagPath showTags的第三个参数,递归用参数
            */
            "setApp": function(objArr, tagIdx, tagPath) {
                //处理输入参数
                if (objArr == null) {
                    objArr = this.MY.allData;
                }
                //while(数组中所有元素){遍历后循环
                for (var i = 0; i < objArr.length; i++) {
                    //获取其中一个元素
                    var one = objArr[i];
                    //if(app情况){设置
                    if (one.type == "app") {
                        //设置这个app
                        one.content.param.main = this;
                        one.content.param.dataArr = objArr;
                        one.content.param.idx = i;
                        one.content.param.tagIdx = tagIdx;
                        one.content.param.tagPath = tagPath;
                    }
                    //}
                    //else{递归调用
                    else {
                        //toDo
                        for (var j = 0; j < one.content.length; j++) {
                            this.API.private('setApp', one.content[j], j, "[" + i + "].content[" + j + "]");
                        }
                    }
                    //}
                }
                //}
            },
            /**
            *@function
            *@memberOf main
            *@name private$saveAllData
            *@description [功能]将所有数据记录到本地存储
            *[思路]递归取数据，对象是不能放入的
            *[接口.this.MY.allData]全局对象
            */
            "saveAllData": function() {
                //声明一个结果集合
                var sData = [];
                //for(第一从循环){顶层循环
                for (var i = 0; i < this.MY.allData.length; i++) {
                    //初始化内部变量
                    var sOne = this.MY.allData[i];
                    var dOne = {};
                    sData.push(dOne);
                    //基本赋值
                    for (var n in sOne) {
                        if (n == "content") {
                            continue;
                        }
                        dOne[n] = sOne[n];
                    }
                    //if (content不是app){进行深度循环赋值
                    if (sOne.type != "app") {
                        dOne.content = [];
                        for (var j = 0; j < sOne.content.length; j++) {
                            var ssOne = sOne.content[j];
                            var ddOne = [];
                            dOne.content.push(ddOne);
                            for (var k = 0; k < ssOne.length; k++) {
                                var sssOne = ssOne[k];
                                var dddOne = {};
                                ddOne.push(dddOne);
                                for (var n in sssOne) {
                                    if (n == "content") {
                                        continue;
                                    }
                                    dddOne[n] = sssOne[n];

                                }
                            }
                        }
                    }
                    //}
                }
                //}
                //保存到本地存储中
                FW.use().save("auxMain", sData, true);
            },
            /**
            *@function
            *@memberOf main
            *@name private$loadAllData
            *@description [功能]从内存中读取对应的值，并且如果是app再将app实例化
            *[思路]实例化app时，app的参数已经记录下来的
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@return 如果存储中存在值，则返回true，否则返回false
            */
            "loadAllData": function() {
                //从本地存储中取值
                var sData = FW.use().load("auxMain", true);
                if (!sData) {
                    return false;
                }
                //声明一个结果集合
                this.MY.allData = [];
                //for(第一从循环){顶层循环
                for (var i = 0; i < sData.length; i++) {
                    //初始化内部变量
                    var sOne = sData[i];
                    var dOne = {};
                    this.MY.allData.push(dOne);
                    //基本赋值
                    for (var n in sOne) {
                        if (n == "content") {
                            continue;
                        }
                        dOne[n] = sOne[n];
                    }
                    //if (content不是app){进行深度循环赋值
                    if (sOne.type != "app") {
                        dOne.content = [];
                        for (var j = 0; j < sOne.content.length; j++) {
                            var ssOne = sOne.content[j];
                            var ddOne = [];
                            dOne.content.push(ddOne);
                            for (var k = 0; k < ssOne.length; k++) {
                                var sssOne = ssOne[k];
                                var dddOne = {};
                                ddOne.push(dddOne);
                                for (var n in sssOne) {
                                    if (n == "content") {
                                        continue;
                                    }
                                    dddOne[n] = sssOne[n];

                                }
                                dddOne.content = FW.createApp(dddOne.appParam[0], dddOne.appParam[1], dddOne.appParam[2]);
                            }
                        }
                    }
                    //}
                    //else{给app赋值
                    else {
                        dOne.content = FW.createApp(dOne.appParam[0], dOne.appParam[1], dOne.appParam[2]);
                    }
                    //}
                }
                //}
                //返回true
                return true;
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf main
            *@name FireEvent$addSubFrame
            *@description [功能]添加子窗体
            *[思路]改数据，然后重新show出来
            *@param path 对应的路径
            *@param idx 第几个窗口
            */
            "addSubFrame": function(path, idx) {
                //取消原来数据的当前状态
                var arr = eval("(this.MY.allData" + path + ")");
                for (var i = 0; i < arr.length; i++) {
                    arr[i].selected = false;
                }
                //添加数据
                //--注意，使用前一个app的id值
                var resource = {
                    dom: arr[arr.length - 1].content.dom
                };
                var appName = path + "[" + arr.length + "]";
                var one = {
                    name: "/",
                    type: "app",
                    selected: true,
                    content: FW.createApp(appName, "fileView", resource),
                    appParam: [appName, "fileView", resource]
                }
                arr.push(one);
                //设置数据
                this.API.private('setApp');
                this.API.private('saveAllData');
                //显示标题
                this.showTags(arr, idx, path);
                this.showContent(arr, path);
            },
            /**
            *@function
            *@memberOf main
            *@name FireEvent$changeTag
            *@description [功能]切换tag函数
            *[思路]先将数据取出，然后获取原来所有的数据变成非激活，然后把传入数据变成激活
            *[接口.this.MY.allData]保存着框架的整体数据
            *@param path 当前结构的路径
            *@param idx 索引
            */
            "changeTag": function(path, idx) {
                //获取原来对象
                var orgObj = eval("(this.MY.allData" + path + ")");
                //for(所有的对象){
                for (var i = 0; i < orgObj.length; i++) {
                    //if(是选中的索引){激活
                    if (i == idx) {
                        orgObj[i].selected = true;
                    }
                    //}
                    //else{
                    else {
                        orgObj[i].selected = false;
                    }
                    //}
                }
                //}
                //重新显示
                location.hash = "";
                var execResult = /content\[(\d)\]/i.exec(path);
                if (execResult) {
                    this.showTags(orgObj, execResult[1], path);
                    this.showContent(orgObj, path);
                } else {
                    this.showTags(orgObj);
                    this.showContent(orgObj);
                }
            },
            /**
            *@function
            *@memberOf main
            *@name FireEvent$addTopFrame
            *@description [功能]添加顶级窗口
            *[思路]添加一个顶级窗体，因为是顶级窗体，所以不需要什么参数
            *[接口.this.MY.allData]所有的窗体数据
            */
            "addTopFrame": function() {
                //添加一个新的
                var curStr = "[" + this.MY.allData.length + "]";
                var newObj = [[{
                    name: "top",
                    type: "app",
                    selected: true,
                    content: FW.createApp(curStr + ".content[0][0]", "fileView", {
                        dom: "m0_content"
                    }),
                    appParam: [curStr + ".content[0][0]", "fileView", {
                        dom: "m0_content"
                    }]
                }], [{
                    name: "top",
                    type: "app",
                    selected: true,
                    content: FW.createApp(curStr + ".content[1][0]", "fileView", {
                        dom: "m1_content"
                    }),
                    appParam: [curStr + ".content[1][0]", "fileView", {
                        dom: "m1_content"
                    }]
                }], [{
                    name: "top",
                    type: "app",
                    selected: true,
                    content: FW.createApp(curStr + ".content[2][0]", "fileView", {
                        dom: "m2_content"
                    }),
                    appParam: [curStr + ".content[2][0]", "fileView", {
                        dom: "m2_content"
                    }]
                }], [{
                    name: "top",
                    type: "app",
                    selected: true,
                    content: FW.createApp(curStr + ".content[3][0]", "fileView", {
                        dom: "m3_content"
                    }),
                    appParam: [curStr + ".content[3][0]", "fileView", {
                        dom: "m3_content"
                    }]
                }]];
                this.addTopApp(newObj, "文件管理");
            },
            /**
            *@function
            *@memberOf main
            *@name FireEvent$openApp
            *@description [功能]打开一个全局app
            *[思路]获取参数创建app实例，然后调用addTopApp方法创建
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param name app的名字
            *@param idx 索引
            */
            "openApp": function(name, idx) {
                //获取gadget
                var data = fileGlobleSetting[idx];
                //创建app
                var app = FW.createApp(data.gadget + this.MY.allData.length, data.gadget, {
                    dom: "m_content"
                });
                //打开app
                this.addTopApp(app, name, [data.gadget + this.MY.allData.length, data.gadget, {
                    dom: "m_content"
                }]);
            },
            /**
            *@function
            *@memberOf main
            *@name FireEvent$closeSubFrame
            *@description [功能]关闭子窗口
            *[思路]关闭掉其中激活的子窗口，如果是最后一个窗口，那么不关闭
            *[接口.this.MY.allData]全局窗口变量
            *@param path 当前窗口的路径
            */
            "closeSubFrame": function(path, idx) {
                //获取数据
                var subData = eval("(this.MY.allData" + path + ")");
                //最后一个就不允许关闭
                if (subData.length == 1) {
                    return;
                }
                //找出激活状态，并在内存中取消
                for (var i = 0; i < subData.length; i++) {
                    if (subData[i].selected) {
                        subData.splice(i, 1);
                        break;
                    }
                }
                subData[0].selected = true;
                this.API.private('saveAllData');
                //重新显示
                this.showTags(subData, idx, path);
                this.showContent(subData, path);
            },
            /**
            *@function
            *@memberOf main
            *@name FireEvent$closeTopFrame
            *@description [功能]关闭当前激活状态的主窗口
            *[思路]改数据，然后重新显示页面
            *[接口.this.MY.allData]全部数据对象
            */
            "closeTopFrame": function() {
                //获取数据
                var subData = this.MY.allData;
                //最后一个就不允许关闭
                if (subData.length == 1) {
                    return;
                }
                //找出激活状态，并在内存中取消
                for (var i = 0; i < subData.length; i++) {
                    if (subData[i].selected) {
                        subData.splice(i, 1);
                        break;
                    }
                }
                subData[0].selected = true;
                this.API.private('saveAllData');
                //重新显示
                this.showTags(subData);
                this.showContent(subData);
            }
        },
        view: {
            'main': require("./resource/main/main.tpl"),
            'tag': require("./resource/main/tag.tpl"),
            'submain': require("./resource/main/submain.tpl"),
            'topIcon': require("./resource/main/topIcon.tpl")
        }

    },
    module);
    return FW;
});