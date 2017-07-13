/**
* @namespace
* @name funsrs 
* @version 0.01 罗光瑜 初始化版本
* @description  树状脑图类。树状脑图是用脑图的思路，编写整个需求。每个节点可能都有分支，逐步下行，最后节点是落地功能，该功能可以被格式成预置条件，需求描述等内容 
*该gadget保存的数据格式为：
*{
*    actor:[//角色信息
*          {
*                 name:"角色名",
*                 desc:"角色说明"
*          }
*    ],
*    funsrs:[//需求点
*            {
*                sig:节点标识，用路径加节点的方式唯一的标识每一个节点，这个信息在保存的时候删除掉
*                name:"显示名称，就是标题",
*                actor:"使用角色",
*                scene：使用场景,
*                precondition:前置条件,
*                desc:需求描述,
*                exception:异常说明,
*                memoList:[备注信息
*                ],
*                prototype:[原型链接
*                ],
*                flow:[流程图链接
*                ],
*                type:"item/folder",
*                expanded:是否展开,保存文件时要删除
*                selected:是否选中，保存文件时要删除
*                children:[//儿子的内容，循环上面父亲的结构
*                ]
*            }
*    ]
*}        
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../../commtools/treeView");
    require("../../commtools/formOper");
    FW.register({
        "name": "funsrs",
        /**
        *@function
        *@memberOf funsrs
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
            this.MY.treeView = FW.createApp("javaTree", "treeView", {},
            true);
            //初始化表单操作
            this.MY.formOper = FW.createApp("formOper", "formOper", {});
            this.MY.formOper.owner = this;
        },
        "public": {
            /**
            *@function
            *@memberOf funsrs
            *@name public$showContent
            *@description [功能]接口方法，被框架调用，用于显示基本视图信息的
            *[思路]首先处理文件路径，有两种可能，一个是主框架把路径传入，这时要保存到本地，另外一种情况是主框架直接进入的，这时就要从本地文件中读取路径信息了。
            *保存完文件后，分别加载文件，显示，以及绑定事件
            *[接口.this.param.idx]分配给本实例的id名称便于区分和本地存储的键值
            *[接口.this.param.fileUrl]要编辑文件的文件路径
            */
            "showContent": function() {
                //设置存储key值
                var savekey = "funsrs" + this.param.idx;
                //if(有参数){就保存到本地存储
                if (this.param.fileUrl) {
                    FW.use().save(savekey, this.param.fileUrl, true);
                }
                //}
                //else{从本地存储中取参数
                else {
                    this.param.fileUrl = FW.use().load(savekey, true);
                }
                //}
                //调用框架，处理文件系统
                //--这个格式固定的给框架用的参数，当直接请求hash时会跳入到这里
                var settingdata = {
                    gadget: "funsrs",
                    url: this.param.fileUrl
                }
                this.param.main.setFileHash(FW.use().toJSONString(settingdata));
                //加载文件
                //--原来没有加载过，即没缓存就加载
                //--要加载模板时，强制重新加载
                if (!this.MY.data) {
                    this.initByFileParam();
                }
                //显示
                //--主窗口和树是分开显示的，先显示主窗口，绑定好树的id和内容部分id
                this.API.show("main");
                this.showTree();
                //处理事件
                this.API.private('clickEvent');
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$initByFileParam
            *@description [功能]加载文件，如果文件不存在就从魔板中读取
            *[思路]先从传入的url中读取文件，读不到就从模板中读取，读取后，调用公有函数对读入的这个文本进行实际的初始化动作
            *@param templateName 可指定的魔板名称，这个参数目前没用的，留作扩展
            */
            "initByFileParam": function(templateName) {
                //获取url参数
                var fileUrl = this.param.fileUrl;
                //block(代码块){构造文件对象读取对象
                var text = null;
                var fileName = null;
                var fileDir = null;
                //if (传入的url不为空){获取文件信息
                if (fileUrl != null) {
                    //读取文件
                    var fileArr = fileUrl.split("/");
                    fileName = fileArr.pop();
                    this.MY.fileName = fileName;
                    fileDir = "/" + fileArr.join("/");
                    this.MY.fileDir = fileDir;

                    if (templateName == null) {
                        this.MY.fileOper.setFileName(fileName);
                        this.MY.fileOper.setPath(fileDir);
                        text = this.MY.fileOper.queryFileContent();
                    }
                }
                //}
                //else{就是url不存在了，要退货
                else {
                    return;
                }
                //}
                //if(文件本身不存在){读取默认文件
                if (text == null) {
                    //检查template是否存在，存在就读取此为模板
                    var template = FW.use().getParameter();
                    //读取文件
                    fileName = templateName || "ex1.frs";
                    fileDir = "/manager_auxiliary/template/funsrs/";
                    this.MY.fileOper.setFileName(fileName);
                    this.MY.fileOper.setPath(fileDir);
                    text = this.MY.fileOper.queryFileContent();
                }
                //}
                //if (文件还是为空){这说明出错了
                if (text == null) {
                    //alert错误并退出
                    alert("文件读取失败");
                    return;
                }
                //}
                //}
                this.change2Struct(text);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$change2Struct
            *@description [功能]将文本内容转换成内部的数据结构
            *[思路]非常简单，就是eval就可以
            *@param text 输入的文本内容
            */
            "change2Struct": function(text) {
                //eval结构
                this.MY.data = eval("(" + text + ")");
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(node, path) {
                    node.sig = path;
                    return true;
                });
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$showTree
            *@description [功能]显示左边java的菜单树
            *[思路]使用treeView组件完成显示，主体结构中的funsrs已经是树状结构了，这里唯一处理的是节点选中方式
            *[接口.this.MY.clickInfo]当前选中节点的id信息，其结构是一个路径，另外要整理一个函数遍历这些个数据对象，把其他选中的节点去除，把选中状态的成员：expanded和selected选中为true
            *调用函数为：私有的eachsrs
            */
            "showTree": function() {
                //处理默认的点击状态，如果点击状态为空，默认就是基础信息了
                if (this.MY.clickInfo == null) {
                    this.MY.clickInfo = "base";
                }
                //设置数据
                //--重点是将扩展信息设置进去
                var _this = this;
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(node, path) {
                    if (path == _this.MY.clickInfo) {
                        node.expanded = true;
                        node.selected = true;
                    } else {
                        if (_this.MY.clickInfo.indexOf(path) == 0) {
                            node.expanded = true;
                            node.selected = false;
                        } else {
                            node.expanded = false;
                            node.selected = false;
                        }

                    }
                    return true;
                });
                //使用组件显示菜单树
                this.MY.treeView.init(this.MY.data.funsrs);
                //设定回调方法
                var _this = this;
                this.MY.treeView.setEventCall(function(e, o) {
                    var info = o.info && o.info[0];
                    _this.API.private('clickEvent', info);

                })
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$showOne
            *@description [功能]显示其中一个节点信息
            *[思路]用表单构造一个节点信息
            *@param node 其中一个节点信息
            {
            name:"显示名称，就是标题",
            actor:"使用角色",
            scene：使用场景,
            precondition:前置条件,
            desc:需求描述,
            exception:异常说明,
            prototype:[原型链接
            ],
            flow:[流程图链接
            ]
            }
            */
            "showOne": function(node) {
                //显示tag页
                this.API.private('showTag', "base");
                //整理表单数据
                //--根据是否是有子节点进行整理
                var datadesc = {
                    name: {
                        name: "需求标题",
                        type: "Text"

                    },
                    actor: {
                        name: "使用角色",
                        type: "Text"
                    },
                    scene: {
                        name: "使用场景",
                        type: "TextArea"
                    },
                    precondition: {
                        name: "前置条件",
                        type: "TextArea"
                    },
                    desc: {
                        name: "需求描述",
                        type: "TextArea"
                    },
                    exception: {
                        name: "异常说明",
                        type: "TextArea"
                    },

                    srspoint: {
                        name: "需求点",

                        type: "List",
                        valueRange: [{
                            point: {
                                name: "需求点",
                                type: "Text"
                            },
                            version: {
                                name: "版本号",
                                type: "Text"
                            }
                        }]
                    },
                    memoList: {
                        name: "备注说明",

                        type: "List",
                        valueRange: ["Text"]
                    },
                    "prototype": {
                        name: "原型链接",
                        type: "List",
                        valueRange: ["Text"]
                    },
                    flow: {
                        name: "流程图链接",
                        type: "List",
                        valueRange: ["Text"]
                    },
                    hardwork: {
                        name: "工作量评估",
                        type: "Text"
                    }
                }
                if (node != null && node.children != null) {
                    datadesc = {
                        name: {
                            name: "需求标题",
                            type: "Text"

                        },
                        flow: {
                            name: "流程图链接",
                            type: "List",
                            valueRange: ["Text"]
                        }
                    }
                }
                //显示字段
                this.API.show("oneNode", node, "mainContent");
                this.MY.formOper.showForm("oneNode", datadesc, node);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$save
            *@description [功能]将内存内容保存到磁盘上
            *[思路]获取内存结构，然后删除各种不需要保存内容，然后进行json化，然后保存
            */
            "save": function() {
                //形成字符串
                var saveText = FW.use().toJSONString(this.MY.data);
                //获取文件路径名
                var fileDir = this.MY.fileDir;
                var fileDir = fileDir.replace(/[\\\/]+/ig, "/");
                var fileFile = this.MY.fileName;
                //保存到网络端
                this.MY.fileOper.setPath(fileDir);
                this.MY.fileOper.setFile(fileFile);
                this.MY.fileOper.saveFile(saveText);
                //返回结果
                return 0;
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$showActor
            *@description [功能]显示角色处理列表
            *[思路]显示角色列表
            */
            "showActor": function() {
                //构造表单说明
                var formDesc = {
                    actor: {
                        name: "角色定义",
                        type: "List",
                        valueRange: [{
                            name: {
                                name: "角色名称",
                                type: "Text"
                            },
                            desc: {
                                name: "角色描述",
                                type: "Text"
                            }
                        }

                        ]
                    }
                }
                //显示到页面上tag
                this.API.private('showTag', "actor");
                //显示字段
                this.API.show("actor", null, "mainContent");
                this.MY.formOper.showForm("actor", formDesc, this.MY.data);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name public$showBaseInfo
            *@description [功能]文档的基本属性信息
            *[思路]调用基本formoper去实现
            */
            "showBaseInfo": function() {
                //构造表单说明
                var formDesc = {
                    project: {
                        name: "文档名称",
                        type: "Text",
                    },
                    corporate: {
                        name: "公司名称",
                        type: "Text",

                    },
                    date: {
                        name: "文档日期",
                        type: "Text"
                    },
                    record: {
                        name: "修订记录",
                        type: "List",
                        valueRange: [{
                            date: {
                                name: "修订时间",
                                type: "Text"
                            },
                            version: {
                                name: "版本号",
                                type: "Text"
                            },
                            desc: {
                                name: "描述",
                                type: "Text"
                            },
                            auth: {
                                name: "作者",
                                type: "Text"
                            }
                        }]
                    },
                    backdrop: {
                        name: "项目背景",
                        type: "TextArea"
                    },
                    purpose: {
                        name: "项目目标",
                        type: "TextArea"
                    }
                }
                //显示到页面上tag
                this.API.private('showTag', "info");
                //显示字段
                this.API.show("baseInfo", null, "mainContent");
                this.MY.formOper.showForm("baseInfo", formDesc, this.MY.data.docinfo);
            }
        },
        "private": {
            /**
            *@function
            *@memberOf funsrs
            *@name private$eachsrs
            *@description [功能]遍历每一个功能节点树
            *[思路]普通的遍历用递归的方法，数据结构参见nodeArr参数,是被分析的一层节点数组，path是遍历时的上级路径，比如上级路径已经是a.b当前遍历的节点为c，那么给下一级遍历就是a.b.c。
            *该函数每递归处理一个节点就调用一次callback的回调函数，进行处理
            *该函数因为是递归的，即有子节点就调自己递归下去，没有就结束。
            *@param nodeArr 节点对象[{
            name:节点的名称
            type:"有子节点就是folder/没有就是item",
            children:[
            ...继续儿子
            ]
            }
            ]
            *@param callback 回调函数,每遍历一次节点，就调用一次，存在的情况，如果为空就不调用。
            function(node,path)，注意回调函数里头的path已经是包含这个节点的名字name的路径信息
            *@param parentPath 上级节点路径供递归使用
            */
            "eachsrs": function(nodeArr, callback, parentPath) {
                //处理上级路径问题
                //--如果为空则变成""
                if (parentPath == null) {
                    parentPath = "";
                } else {
                    parentPath += ".";
                }
                var canEach = true;
                //while(所有节点){逐个遍历
                for (var i = 0; canEach && i < nodeArr.length; i++) {
                    var one = nodeArr[i];
                    //if(是节点){遍历操作
                    if (!one.children) {
                        //做节点的操作
                        if (callback) {
                            canEach = callback(one, parentPath + one.name);
                        }
                    }
                    //}
                    //else{递归操作
                    else {
                        //递归操作，广度遍历，先访问再递归
                        if (callback) {
                            canEach = callback(one, parentPath + one.name);
                        }
                        if (canEach) {
                            canEach = this.API.private('eachsrs', one.children, callback, parentPath + one.name);
                        }
                    }
                    //}
                }
                //}
                return canEach;
            },
            /**
            *@function
            *@memberOf funsrs
            *@name private$clickEvent
            *@description [功能]树的点击调用方法
            *[思路]这里调用treeview里面的setEventCall方法设定回调的函数
            *@param o 对象
            */
            "clickEvent": function(o) {
                //获取节点信息
                var clickInfo = null;
                if (o) {
                    clickInfo = o.sig;
                    this.MY.clickInfo = clickInfo;
                } else {
                    clickInfo = this.MY.clickInfo;
                }
                if (clickInfo == null) {
                    clickInfo = this.MY.clickInfo = "base";
                }
                //显示基本节点信息
                this.showOne(o);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name private$showTag
            *@description [功能]根据类型显示tag的信息
            *[思路]直接show就是了
            *@param type base、actor
            */
            "showTag": function(type) {
                //直接显示
                this.API.show("tag", type, "tag");
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$setOne
            *@description [功能]触发事件，设置其中一个节点的内容
            *[思路]遍历，根据sig找到要设置的对象，从表单中获取值，然后赋值，最后重新显示一遍
            *@param sig 节点标识
            */
            "setOne": function(sig) {
                //找到节点对象
                var node = null;
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(_n, path) {
                    if (sig == path) {
                        node = _n;
                        return false;
                    }
                    return true;
                });
                //页面上获取对象值
                var　data = this.MY.formOper.getFormData();
                //遍历对象进行赋值
                for (var n in data) {
                    node[n] = data[n];
                }
                //重新设置节点标识
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(node, path) {
                    node.sig = path;
                    return true;
                });
                //重新显示
                this.save();
                FW.alert("设置成功");
                this.showTree();
                this.showOne(node);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$newSub
            *@description [功能]在一个节点下创建子节点
            *[思路]首先找到当前的节点，然后操作后台创建一个空节点，然后显示出来
            */
            "newSub": function() {
                //获取当前被选中节点
                var node = null;
                var _this = this;
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(_n, path) {
                    if (_this.MY.clickInfo == path) {
                        node = _n;
                        return false;
                    }
                    return true;
                });
                if (node == null) {
                    FW.alert("请先选中需求");
                    return;
                }
                //修改为folder属性
                node.type = "folder";
                if (node.children == null) {
                    node.children = [];
                }
                //添加新节点到children中
                var one = {
                    name: "新功能点",
                    type: "item"
                };
                node.children.push(one);
                //重新刷新标识
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(node, path) {
                    node.sig = path;
                    return true;
                });
                //重新显示
                this.showOne(one);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$newTop
            *@description [功能]添加顶级节点
            *[思路]往总信息里面加，加完内存显示出来
            */
            "newTop": function() {
                //添加新节点到children中
                var one = {
                    name: "新功能点",
                    type: "item"
                };
                this.MY.data.funsrs.push(one);
                //重新刷新标识
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(node, path) {
                    node.sig = path;
                    return true;
                });
                //重新显示
                this.showOne(one);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$delFun
            *@description [功能]删除功能
            *[思路]比较麻烦，要找到标识节点的上层，然后对儿子进行遍历，找到后，把儿子移除掉，然后判断是否是最后一个，最后一个还要变成item形式
            */
            "delFun": function() {
                //找到父亲
                if (this.MY.clickInfo == null) {
                    FW.alert("请选中节点");
                }
                var fatherArr = this.MY.data.funsrs;
                var father = null;
                var parhArr = this.MY.clickInfo.split(".");
                if (parhArr.length > 1) {
                    parhArr.splice(parhArr.length - 1, 1);
                    var clickinfo = parhArr.join('.');
                    this.API.private('eachsrs', this.MY.data.funsrs,
                    function(_n, path) {
                        if (path == clickinfo) {
                            fatherArr = _n.children;
                            father = _n;
                            return false;
                        }
                        return true;
                    });
                }
                //在父亲中找儿子
                //--找到后移除
                for (var i = 0; i < fatherArr.length; i++) {
                    var one = fatherArr[i];
                    if (one.sig == this.MY.clickInfo) {
                        fatherArr.splice(i, 1);
                        break;
                    }
                }
                //判断是否要改成item
                if (fatherArr.length == 0 && father != null) {
                    father.type = "item";
                    father.children = null;
                }
                //重新显示
                this.showTree();
                this.showOne(null);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$showActor
            *@description [功能]显示角色定义
            *[思路]调用公有方法实现
            */
            "showActor": function() {
                //调用公有方法实现
                this.showActor();
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$showOne
            *@description [功能]调用公有方法实现
            */
            "showOne": function() {
                //找到节点对象
                var node = null;
                var sig = this.MY.clickInfo;
                this.API.private('eachsrs', this.MY.data.funsrs,
                function(_n, path) {
                    if (sig == path) {
                        node = _n;
                        return false;
                    }
                    return true;
                });
                //重新显示
                this.showOne(node);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$setActor
            *@description [功能]设置角色属性
            *[思路]从表单中获取直接设置进去即可
            */
            "setActor": function() {
                //获取表单的值
                var　data = this.MY.formOper.getFormData();
                this.MY.data.actor = data.actor;
                //保存
                this.save();
                FW.alert("角色信息设置成功");
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$showDoc
            *@description [功能]显示当前的文档内容
            *[思路]把文件地址传入到目标文件中，目标文件是一个独立的jsp，那个文件中处理这个内容
            */
            "showDoc": function() {
                //合成文件路径
                var fileDir = this.MY.fileDir;
                var fileFile = (fileDir + "/" + this.MY.fileName).replace(/[\\\/]+/ig, "/");
                //合成url
                var url = "../funsrs/doc.jsp?file=" + encodeURIComponent(fileFile);
                //打开浏览器
                window.open(url);
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$showBaseInfo
            *@description [功能]显示文档的基本信息
            *[思路]调用公有方法实现显示
            */
            "showBaseInfo": function() {
                //调用公有方法实现
                this.showBaseInfo();
            },
            /**
            *@function
            *@memberOf funsrs
            *@name FireEvent$setBaseInfo
            *@description [功能]保存基本属性信息
            *[思路]调用fw的基本方法即可从表单中获取对应的值
            */
            "setBaseInfo": function() {
                //获取表单的值
                var　data = this.MY.formOper.getFormData();
                this.MY.data.docinfo = data;
                //保存
                this.save();
                FW.alert("文档基本信息设置成功");
            }
        },
        view: {
            'main': require("./resource/funsrs/main.tpl"),
            'tag': require("./resource/funsrs/tag.tpl"),
            'oneNode': require("./resource/funsrs/oneNode.tpl"),
            'actor': require("./resource/funsrs/actor.tpl"),
            'baseInfo': require("./resource/funsrs/baseInfo.tpl")
        }

    },
    module);
    return FW;
});