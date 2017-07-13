/**
* @namespace
* @name editSequence 
* @version 0.01 罗光瑜 版本初始化
0.02 罗光瑜 修改getEditUrl支持DB类型
0.03 罗光瑜 备注字段增加scene场景字段
0.04 罗光瑜 单线条中增加场景跳转功能
* @description  编辑editSequence图的一个包，其格式如下：
*{
*        note:{
*               scene:"场景"
*               desc:"总体描述",
*               history:[
*                           {
*                                       auth:"作者",
*                                       date:"时间",
*                                       desc::"描述"
*                           }
*               ]
*        }
*	node:[
*		{
*			name:"first",
*			dir:"xxxx"
*		},
*		{
*			name:"second",
*			dir:"xxx"
*		}
*	],
*	line:[
*		{
*			start:"second",
*			end:"first",
*			dtype:"abc",
*			desc:"这是一个很重要的连线"
*		}
*	]
*}        
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("breeze/framework/js/tools/Graph");
    require("./fileselect");
    FW.register({
        "name": "editSequence",
        "param": {
            /**
            *@memberOf editSequence
            *@name graphview
            *@description 图形的视图id
            */
            "graphview": "graphview",
            /**
            *@memberOf editSequence
            *@name graphcontainer
            *@description 承载graph的标签的id名
            */
            "graphcontainer": "graphcontainer",
            /**
            *@memberOf editSequence
            *@name graphwidth
            *@description 图形的宽度
            */
            "graphwidth": "100%",
            /**
            *@memberOf editSequence
            *@name graphheight
            *@description 图形的高度
            */
            "graphheight": "900",
            /**
            *@memberOf editSequence
            *@name fileUrl
            *@description 默认路径
            */
            "fileUrl": "manager_auxiliary/template/templateSeq.js",
            /**
            *@memberOf editSequence
            *@name typeopen_jsp
            *@description comments
            */
            "typeopen_jsp": "./htmlCreator.jsp",
            /**
            *@memberOf editSequence
            *@name typeopen_js
            *@description gadget的编辑器
            */
            "typeopen_js": "./gadgetCreator.jsp",
            /**
            *@memberOf editSequence
            *@name typeopen_brz
            *@description 进行service编辑的
            */
            "typeopen_brz": "./editservice.jsp"
        },
        /**
        *@function
        *@memberOf editSequence
        *@name onCreate$onCreate
        *@description undefined
        */
        "onCreate": function() {
            //创建文件对象
            console.log("bdgin......");
            var pageParam = {
                id: 'fileselect',
                dom: this.dom,
                param: {
                    viewid: "--"
                },
                //实际的参数
                view: {}
                //实际的视图内容
            }
            //设定提示转向
            this.MY.parent = (parent != window);

            this.MY.fileSelect = FW.createApp("fileselect", "fileselect", pageParam);
            //读取文件
            this.showSequenceFromUrl();
        },
        "public": {
            /**
            *@function
            *@memberOf editSequence
            *@name public$showSequenceFromUrl
            *@description 显示序列图，从url中获取到对应的文件路径，并读取加载到内存中。
            *数据结构：
            *[this.MY.data]={
            *	node:[
            *		{
            *			name:"first",
            *			dir:"xxxx"
            *		},
            *		{
            *			name:"second",
            *			dir:"xxx"
            *		}
            *	],
            *	line:[
            *		{
            *			start:"first",
            *			end:"second",
            *			dtype:"abc",
            *			desc:"这是一个很重要的连线"
            *		}
            *	]
            *}
            */
            "showSequenceFromUrl": function() {
                //获取url参数
                var fileUrl = FW.use().getParameter("fileUrl");
                //block(代码块){构造文件对象读取对象
                var text = null;
                //if (传入的url不为空){获取文件信息
                if (fileUrl != null) {
                    //读取文件
                    var fileArr = fileUrl.split("/");
                    var fileName = fileArr.pop();
                    var fileDir = "/" + fileArr.join("/");
                    this.MY.fileSelect.setFileName(fileName);
                    this.MY.fileSelect.setPath(fileDir);
                    text = this.MY.fileSelect.queryFileContent();

                    $("#aliasTitle").html("[" + decodeURI(fileName.replace(/@/ig, "%")) + "]");
                }
                //}
                //else{就是url不存在了，要退货
                else {
                    //退出
                    alert("文件地址为空");
                    return;
                }
                //}
                //if(文件本身不存在){读取默认文件
                if (text == null) {
                    //重新设置文件
                    var dfileArr = this.param.fileUrl.split("/");
                    var dfileName = dfileArr.pop();
                    var dfileDir = "/" + dfileArr.join("/");
                    this.MY.fileSelect.setFileName(dfileName);
                    this.MY.fileSelect.setPath(dfileDir);
                    text = this.MY.fileSelect.queryFileContent();
                    //文件设置复位，便于后续保存
                    this.MY.fileSelect.setFileName(fileName);
                    this.MY.fileSelect.setPath(fileDir);
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
                //解析文本内容
                this.API.private('contructData', text);
                //显示到页面上
                this.API.private('showGraph');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name public$updateNode
            *@description 根据输入的内容修改编辑内容
            *@param oldName 原来的对象索引
            *@param newNode 新的对象内容
            */
            "updateNode": function(oldName, newNode) {
                //查找原来的对象并修改
                for (var i = 0; i < this.MY.data.node.length; i++) {
                    if (this.MY.data.node[i].name == oldName) {
                        this.MY.data.node[i] = newNode;
                        break;
                    }
                }
                //if (名字有改动){将所有线条的名字改一次
                if (oldName != newNode.name) {
                    for (var i = 0; i < this.MY.data.line.length; i++) {
                        var oneLine = this.MY.data.line[i];
                        if (oneLine.start == oldName) {
                            oneLine.start = newNode.name;
                        }
                        if (oneLine.end == oldName) {
                            oneLine.end = newNode.name;
                        }
                    }
                }
                //}
            },
            /**
            *@function
            *@memberOf editSequence
            *@name public$deleteNode
            *@description 删除一个节点，注意要把line一起删除了
            *@param name 要删除的节点名
            */
            "deleteNode": function(name) {
                //for(所有node){删除节点
                for (var i = 0; i < this.MY.data.node.length; i++) {
                    //if (名字是要被删除的){删除
                    if (this.MY.data.node[i].name == name) {
                        //删除
                        this.MY.data.node.splice(i, 1);
                        break;
                    }
                    //}
                }
                //}
                //for(所有的line){
                //--注意删除数组要影响数组索引，所以要倒序遍历
                for (var i = this.MY.data.line.length - 1; i >= 0; i--) {
                    var ll = this.MY.data.line[i];
                    //if (起始或者终止有一个是){删除
                    if (ll.start == name || ll.end == name) {
                        //删除
                        this.MY.data.line.splice(i, 1);
                    }
                    //}
                }
                //}
            },
            /**
            *@function
            *@memberOf editSequence
            *@name public$updateLine
            *@description 更新连线信息
            *@param idx 要更新的连线索引
            *@param newObj 新的连线对象
            */
            "updateLine": function(idx, newObj) {
                //直接替换
                this.MY.data.line[idx] = newObj;
            },
            /**
            *@function
            *@memberOf editSequence
            *@name public$deleteLine
            *@description 删除一个指定的连线
            *@param idx 要删除的索引
            */
            "deleteLine": function(idx) {
                //直接删除
                this.MY.data.line.splice(idx, 1);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name public$showInfo
            *@description [功能]显示基本信息
            *@param data 要显示的数据
            */
            "showInfo": function(data) {
                //显示要显示的信息视图
                this.API.show("info", data);
            }
        },
        "private": {
            /**
            *@function
            *@memberOf editSequence
            *@name private$contructData
            *@description 利用输入的文本构造对应的文档结构
            *构造好的文档内存结构，就存放到内存 的一个结构中，参见showSequenceFromUrl方法
            *@param text 文档类型
            */
            "contructData": function(text) {
                //直接eval到内存变量中
                this.MY.data = eval("(" + text + ")");
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$showGraph
            *@description 将内存中的数据显示到页面上
            *生命线也用矩形表示，这个矩形分段，比实际连线多1倍，因为每个横面都留一个空位，可以画虚线，而这些虚线可以被当作插入部分处理
            */
            "showGraph": function() {
                //block(块){构造graph对象
                _this = this;
                this.API.show(this.param.graphview);
                var graphwidth = $("#" + this.param.graphcontainer).css("width");
                var graph = FW.use("Graph").createGraph(this.API.find("#" + this.param.graphcontainer)[0], graphwidth, this.param.graphheight);
                //}
                //block(块){画对象和生命线
                //--所有图形对象不必记录到全局
                //--每个图形对象自己带上序号，被点击后使用
                //获取总层数
                //--从序列图的实际连线的数量可获取到，count*2+1就是实际总层数
                var deep = this.MY.data.line.length * 2 + 1;
                //设定每个对象的总空间
                //--这个空间包括对象和对象之间的空隙
                var roomWidth = 150;
                //设定每个矩形的宽度和高度
                var oneObjWidth = 100;
                var oneObjHeitht = 50;
                //设定对象矩形形状
                var objShape = FW.use("Graph").Shapes.getRect(oneObjWidth, oneObjHeitht, true, {
                    "onclick": function(p, n) {
                        _this.API.private('showNodeEdit', n.data);
                    }
                });
                //设定生命线矩形形状
                var lifeWidht = 6;
                var lifeHeight = 18;
                var lifeShape = FW.use("Graph").Shapes.getRect(lifeWidht, lifeHeight);
                //for (每一个对象){
                //--从this.MY.data中获取，结构参见文件说明
                var allLifeArr = {};
                for (var i = 0; i < this.MY.data.node.length; i++) {
                    //画出当前对象矩形
                    //--难度在计算矩形的位置，利用前面的总空间和矩形宽度计算
                    //--矩形内容要增加成员id(表示对象索引)
                    var x = i * roomWidth + (roomWidth - oneObjWidth) / 2;
                    var y = 10;
                    var text = this.MY.data.node[i].name;
                    var node = graph.createNode(text, objShape, x, y, "#4444ff");
                    node.id = i;
                    node.data = this.MY.data.node[i];
                    allLifeArr[this.MY.data.node[i].name] = [];
                    //画出矩形下的生命线
                    //--循环一下就可以，循环数量为层数
                    //--每个生命线，增加成员id(表示第几个对象),deep(表示第几层)
                    for (var j = 0; j < deep; j++) {
                        var xx = x + (oneObjWidth - lifeWidht) / 2;
                        var yy = j * lifeHeight + oneObjHeitht + 10;
                        var life = graph.createNode(null, lifeShape, xx, yy, "#660000");
                        life.id = i;
                        life.deep = j;
                        allLifeArr[this.MY.data.node[i].name][j] = life;
                    }
                }
                //}
                //}
                //block(块){画序列箭头
                //block(块){画实际线
                //for (所有的线){
                for (var i = 0; i < this.MY.data.line.length; i++) {
                    //计算要画的层
                    //--因为是间隔开的，所以要画线的索引就是i*2+1
                    var dDeep = i * 2 + 1;
                    //获取要画的起始和终止节点
                    var oneLineInfo = this.MY.data.line[i];
                    var sNode = allLifeArr[oneLineInfo.start][dDeep];
                    var eNode = allLifeArr[oneLineInfo.end][dDeep];
                    //画线
                    //--记得带箭头，实线
                    var dText = oneLineInfo.dtype + ":" + oneLineInfo.desc;
                    var lineObj = graph.createEdge(dText, sNode, eNode, null, false, true, "black"); (function(_i, _oneLineInfo) {
                        lineObj.addEvent({
                            onclick: function(p, l) {
                                _this.API.private('showLineEdit', _i, _oneLineInfo);
                            }
                        });
                    })(i, oneLineInfo);
                }
                //}
                //}
                //block(块){画虚拟线
                //for (遍历所有层，步长是2){画线
                for (var i = 0; i < this.MY.data.line.length + 1; i++) {
                    var sNode = null;
                    //for(遍历所有对象){
                    for (var n in allLifeArr) {
                        if (sNode == null) {
                            sNode = allLifeArr[n][i * 2];
                            continue;
                        }
                        var eNode = allLifeArr[n][i * 2];
                        var lineObj = graph.createEdge(dText, sNode, eNode, null, true, false, "#dddddd"); (function(_i) {
                            lineObj.addEvent({
                                onclick: function() {
                                    var newOne = {
                                        start: _this.MY.data.node[0].name,
                                        end: _this.MY.data.node[1].name,
                                        dtype: "新增加",
                                        desc: "在第[" + (_i + 1) + "]序列，增加新连线"
                                    }
                                    _this.MY.data.line.splice(_i, 0, newOne);
                                    _this.API.private('showLineEdit', _i, newOne);
                                }
                            });
                        })(i);
                        sNode = eNode;
                    }
                    //}
                }
                //}
                //}
                //}
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$showNodeEdit
            *@description 显示编辑节点的表单
            *@param node node对象
            */
            "showNodeEdit": function(node) {
                //编辑url
                node.href = this.API.private('getEditUrl', node);
                //直接显示
                this.API.show("editForm", node);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$save2file
            *@description 保存到文件中
            */
            "save2file": function() {
                //将内存对象转换成文本
                var resultText = FW.use().toJSONString(this.MY.data);
                //将文本保存到文件
                this.MY.fileSelect.saveFile(resultText);
                FW.alert("保存文件成功");
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$showLineEdit
            *@description 显示编辑线的界面
            *@param idx 该线段的索引
            *@param line 线对象
            */
            "showLineEdit": function(idx, line) {
                //直接调用show方法显示
                var showObj = {
                    idx: idx,
                    start: line.start,
                    end: line.end,
                    dtype: line.dtype,
                    desc: line.desc,
                    detail: line.detail,
                    node: this.MY.data.node,
                    jumb: line.jumb
                }
                this.API.show("lineForm", showObj);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$moveup
            *@description 位置前移
            *@param name 要移动的那个节点名
            */
            "moveup": function(name) {
                //for (所有的节点){
                for (var i = 0; i < this.MY.data.node.length; i++) {
                    //if (是要移动的节点){移动
                    if (this.MY.data.node[i].name == name) {
                        //if (已经是第一个了){提示并退出
                        if (i == 0) {
                            //提示并退出
                            FW.alert("已经是第一个了");
                            return;
                        }
                        //}
                        //和前一个交换
                        var tmp = this.MY.data.node[i];
                        this.MY.data.node[i] = this.MY.data.node[i - 1];
                        this.MY.data.node[i - 1] = tmp;
                        this.API.private('showGraph');
                    }
                    //}
                }
                //}
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$movedown
            *@description 向后移动
            *@param name 要移动的节点名称
            */
            "movedown": function(name) {
                //for (所有的节点){
                for (var i = 0; i < this.MY.data.node.length; i++) {
                    //if (是要移动的节点){移动
                    if (this.MY.data.node[i].name == name) {
                        //if (已经是第一个了){提示并退出
                        if (i == this.MY.data.node.length - 1) {
                            //提示并退出
                            FW.alert("已经是第最后一个了");
                            return;
                        }
                        //}
                        //和前一个交换
                        var tmp = this.MY.data.node[i];
                        this.MY.data.node[i] = this.MY.data.node[i + 1];
                        this.MY.data.node[i + 1] = tmp;
                        this.API.private('showGraph');
                    }
                    //}
                }
                //}
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$getEditUrl
            *@description 根据点击的内容，页面导航到指定的编辑页面去
            *支持DB类型即DB或者xxx.DB，DB类型实际返回的是统一的新工具框架。
            *这个框架中要传入一个json字符串满足框架跳转用，json字符串为：
            *    {"gadget":"editJava","url":"dbJson"}
            *其中，dbjson又是一个json字符串，格式为：
            *{
            *    auth:['dbview'],
            *    viewdb:['实际可见的数据库表名,不传就是看所有表']
            *}
            *其中，viewdb中是传入的db表，这个信息是所有指向db表的的line信息，其格式为表名|表名|....
            *@param node 要编辑的node
            */
            "getEditUrl": function(node) {
                //先处理DB类型
                if (/.*DB$/i.test(node.name)) {
                    var url = "./page/framework/main.jsp#";
                    var dbNames = [];
                    for (var i = 0; i < this.MY.data.line.length; i++) {
                        if (this.MY.data.line[i].end == node.name) {
                            var oneDbs = this.MY.data.line[i].desc.split(/[\|,]/);
                            for (var ii = 0; oneDbs != null && ii < oneDbs.length; ii++) {
                                if (oneDbs[ii] != "") {
                                    dbNames.push(oneDbs[ii]);
                                }
                            }
                        }
                    }
                    var hashObj = {
                        auth: ['dbview'],
                        viewdb: dbNames
                    }
                    hashObj = {
                        "gadget": "dbcontrol",
                        "url": FW.use().toJSONString(hashObj)
                    }

                    var hashStr = decodeURI(FW.use().toJSONString(hashObj)).replace(/"/ig, "&quot;");
                    url = url + hashStr;
                    return url;
                }
                //获取类型
                var type = node.name.split(".").pop();
                //获取要打开的工具url地址
                var baseurl = this.param["typeopen_" + type];
                if (!baseurl) {
                    alert("注意：当前类型[" + type + "]不支持编辑");
                    return "#";
                }
                //整合url
                var url = baseurl + "?fileUrl=" + encodeURIComponent(node.dir + "/" + node.name);
                return url;
            },
            /**
            *@function
            *@memberOf editSequence
            *@name private$parserJumb
            *@description [功能]转换跳转字符串
            *[思路]
            *1.空处理
            *2.转换中文部分
            */
            "parserJumb": function(text) {
                var result = text;
                if (text == null || text == "") {
                    result = this.MY.fileSelect.getPath();
                }

                result = decodeURI(result.replace(/@/ig, "%"));
                result = result.replace(/[\\\/]+/g, "/");
                return result;
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$updateNode
            *@description 响应事件，点击后修改对应的值，并且返回到原始页面
            */
            "updateNode": function() {
                //获取表单值
                var oldName = this.API.find("#oldName").val();
                var newNode = {
                    name: this.API.find("#name").val(),
                    dir: this.API.find("#dir").val()
                }
                //修改值
                this.updateNode(oldName, newNode);
                //保存文件
                this.API.private('save2file');
                //返回图形
                this.API.private('showGraph');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$addNew
            *@description 添加新节点，这个方法只是在内存中添加一个新的对象，不保存，然后跳到编辑页面
            */
            "addNew": function() {
                //构造新节点对象
                var newData = {
                    name: "",
                    dir: ""
                }
                this.MY.data.node.push(newData);
                //跳转到编辑页面
                this.API.private('showNodeEdit', newData);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$goBack
            *@description 返回图形页面
            */
            "goBack": function() {
                //显示图形
                this.API.private('showGraph');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$deleteNode
            *@description 删除节点
            *@param name 要删除的节点名称
            */
            "deleteNode": function(name) {
                //删除节点
				if(confirm("确认删除该线段吗？")){
					this.deleteNode(name);
					//保存文件
					this.API.private('save2file');
					//返回图形
					this.API.private('showGraph');
				}
                
                
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$updateLine
            *@description 更新线条的事件
            *@param idx 索引
            */
            "updateLine": function(idx) {
                //合成新对象
                var newObj = {
                    start: this.API.find("#start").val(),
                    end: this.API.find("#end").val(),
                    dtype: this.API.find("#dtype").val(),
                    desc: this.API.find("#desc").val(),
                    detail: this.API.find("#detail").val(),
                    jumb: this.API.find("#jumb").val()
                }
                //调用方法设置新对象
                this.updateLine(idx, newObj);
                //保存文件
                this.API.private('save2file');
                //重新显示页面
                this.API.private('showGraph');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$deleteLine
            *@description 删除一条线
            *@param idx 要删除的索引
            */
            "deleteLine": function(idx) {
                //删除
				if(confirm("确认删除该线段吗？")){
					this.deleteLine(idx);
					//保存文件
					this.API.private('save2file');
					//返回
					this.API.private('showGraph');
				}
                
                
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$movenode
            *@description 向前移动一个节点
            *@param name 节点名称
            *@param direction 方向
            */
            "movenode": function(name, direction) {
                //向前移动
                this.API.private(direction, name);
                //保存文件
                this.API.private('save2file');
                //显示
                this.API.private('showGraph');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$save
            *@description toD保存
            */
            "save": function() {
                //保存
                this.API.private('save2file');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$showInfo
            *@description [功能]显示顺序图基本信息内容
            *[思路]直接显示编辑框即可
            */
            "showInfo": function() {
                //获取数据
                var data = this.MY.data.note || {
                    scene: "本顺序图的使用场景是....",
                    desc: "[需求来源]。。。\n[pagegadget]先showmain显示整体页面框架再分开显示/显示主框架内容\n[servicegadget]正常根据获取数据,然后做所有的数据转换\n[后端]：用正常的service查询/要写java代码实现了\n",
                    history: [{
                        auth: "[作者]",
                        date: "[时间]",
                        desc: "初始版本"
                    }]
                };
                //显示
                this.showInfo(data);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$addHistroyRecord
            *@description [功能]添加一条新的历史记录
            *[思路]从form中获取当前的值，然后添加数据，然后再show
            */
            "addHistroyRecord": function() {
                //获取数据
                var data = FW.use().getFormValue("infoform");
                //添加数据
                if (data.history == null) {
                    data.history = [];
                }
                data.history.push({
                    auth: "[作者]",
                    date: "[日期]",
                    desc: "[改动描述]"
                });
                //重新显示
                this.showInfo(data);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$delHistroyRecord
            *@description [功能]这里描述基本功能
            *[思路]从页面dom中获取到选中的id，然后从后往前逐个移除
            */
            "delHistroyRecord": function() {
                //获取要删除数组
                var delArray = [];
                var delArr = $("#infoform").find("input:checked").each(function() {
                    delArray.push($(this).attr("idx"));
                });
                //获取总数据
                var data = FW.use().getFormValue("infoform");
                //从后往前逐个删除
                for (var i = delArray.length - 1; i >= 0; i--) {
                    data.history.splice(delArray[i], 1);
                }
                //重新显示
                this.showInfo(data);
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$updateInfo
            *@description [功能]保存信息数据
            *[思路]用组件获取表单的值，并记录到内存中
            */
            "updateInfo": function() {
                //获取表单数据
                var data = FW.use().getFormValue("infoform");
                //保存数据
                this.MY.data.note = data;
                //保存到文件
                this.API.private('save2file');
                //显示图形
                this.API.private('showGraph');
            },
            /**
            *@function
            *@memberOf editSequence
            *@name FireEvent$jumb2scene
            *@description [功能]跳转到指定的url中
            *[思路]url从文本框中读取
            *@param p1 toDo
            */
            "jumb2scene": function(p1) {
                //获取url
                var url = this.API.find("#jumb").val();
                var urlArr = url.replace(/\\\//ig, "/").split("/");
                for (var i = 0; i < urlArr.length; i++) {
                    urlArr[i] = encodeURI(urlArr[i]).replace(/%/ig, "@");
                }
                url = urlArr.join("%2F");
                window.open("./sequenceCreator.jsp?fileUrl=" + url);
            }
        }
    },
    module);
    return FW;
});