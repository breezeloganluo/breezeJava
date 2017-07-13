/**
* @namespace
* @name graphyFunMod 
* @version 0.01 罗光瑜 初始版本
0.02 罗光瑜 Branchlist分支函数私有函数调用的一个问题
0.03 罗光瑜 解决cuycle分支的收起后的一系列错误
* @description  这个就是从原来sevice下面graphyFunMod移植过来的，因为原来这个是做gadget编辑器的时候做的，那个时候因为编辑器没有出来，所以很多这些内容都是不规范无法方便管理，包括整理视图，这些都不方便修改，所以干脆用编辑器重新写一个        
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("breeze/framework/js/tools/Graph");
    FW.register({
        "name": "graphyFunMod",
        "param": {
            /**
            *@memberOf graphyFunMod
            *@name mainView
            *@description 主视图id
            */
            "mainView": "mainView",
            /**
            *@memberOf graphyFunMod
            *@name canvas
            *@description 图形canvasid
            */
            "canvas": "infoForm"
        },
        /**
        *@function
        *@memberOf graphyFunMod
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf graphyFunMod
            *@name public$setCanvas
            *@description [功能]设置canvas对象
            *[思路]
            *@param canvas canvas
            */
            "setCanvas": function(canvas) {
                //设置
                this.MY.canvas = canvas;
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name public$showFun
            *@description [功能]显示图形
            *[思路]移植原来的函数实现
            *@param funObj 函数对象
            *@param editCallback 回调函数
            */
            "showFun": function(funObj, editCallback) {
                //做基本的初始化工作
                var _this = this;

                if (funObj == null) {
                    alert("function  not found!");
                }
                if (!this.MY.canvas) {
                    this.API.show(this.param.mainView);
                    this.MY.canvas = $("#" + this.param.canvas);
                }

                var canvas = this.MY.canvas[0];
                //计算各种坐标
                //--顶部第一个位置坐标值，并设置每个递进节点的长度
                this.MY.funObj = funObj;
                this.MY.editCallback = editCallback;
                var cwidth = this.MY.cwidth = canvas.clientWidth;
                var cheight = this.MY.cheight = canvas.clientHeight;
                var graph = this.MY.graph = FW.use("Graph").createGraph(canvas, cwidth, cheight);

                var oneBaseH = this.MY.oneBaseH = 60;
                var oneBaseW = this.MY.oneBaseW = 180;
                var stepW = this.MY.stepW = 30;
                var oneStep = this.MY.oneStep = oneBaseH + 20;

                var startX = this.MY.startX = cwidth / 2 - oneBaseW;
                var startY = this.MY.startY = oneStep;

                var nodeEvent = {
                    onclick: function(p, obj) {
                        if (obj.fragment.isClose) {
                            obj.fragment.isClose = false;
                            _this.showFun(funObj, editCallback);
                        } else {
                            editCallback && editCallback(obj.fragment, obj.parentFragment);
                        }
                    }
                }
                //设定基本图形样式
                var NORMAL = this.MY.NORMAL = FW.use("Graph").Shapes.getRect(oneBaseW, oneBaseH, true, nodeEvent);
                var BRANCH = this.MY.BRANCH = FW.use("Graph").Shapes.getDiamond(oneBaseW, oneBaseH / 2, true, nodeEvent);
                var BRANCHLIST = this.MY.BRANCHLIST = FW.use("Graph").Shapes.getHexagon(oneBaseW, oneBaseH / 2, true, nodeEvent);
                var CYCLE = this.MY.CYCLE = FW.use("Graph").Shapes.getParallelogram(oneBaseW, oneBaseH, true, nodeEvent);
                var CYCLEEND = this.MY.CYCLEEND = FW.use("Graph").Shapes.getParallelogram(oneBaseW, oneBaseH, true, nodeEvent);
                var BLOCK = this.MY.BLOCK = FW.use("Graph").Shapes.getHexagon(oneBaseW, oneBaseH, false, nodeEvent);

                var BLUE = this.MY.BLUE = "#5555bb";
                var YELLOW = this.MY.YELLOW = "#bbbb11";
                var RED = this.MY.RED = "#bb5555";
                var GRAY = this.MY.GRAY = "#555";
                var BLACK = this.MY.BLACK = "#000000";
                this.API.private('drawAny', funObj.fragments, 0, startX, startY, null);
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name public$destroy
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@return toDo
            *@example toDO
            */
            "destroy": function() {
                this.MY.canvas = null;
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name public$newFun
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "newFun": function(p1) {
                //block(){
                //}
                //toDo
                //toDo
            }
        },
        "private": {
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawNode
            *@description [功能]这是由原来的drawNode函数演变过来的
            *@param p1 toDo
            */
            "drawNode": function(txt, cx, cy, shapes, clolor) {
                var inColor = clolor || this.MY.BLUE;
                var node = this.MY.graph.createNode(txt, shapes, cx, cy, inColor);
                return node;
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawLine
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            */
            "drawLine": function(f, e, txt, lineArr) {
                var drawTxt = (txt) ? txt: "";
                var edge = this.MY.graph.createEdge(drawTxt, f, e, lineArr, null, null, "black");
                return edge;
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$getFunFragmentWidth
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "getFunFragmentWidth": function(funFragment) {
                //计算自己的宽度
                var selfWidth = this.MY.oneBaseW + this.MY.stepW * 2;
                //if(normal情况){直接返回自己的宽度为最终结果
                if (funFragment.type == "normal") {
                    return selfWidth;
                }
                //}
                //else if(分支情况){循环自己的儿子，递归调用每个儿子的宽度加在一起
                else if (funFragment.type == "branchList") {
                    if (!funFragment.subList || funFragment.isClose == true) {
                        return selfWidth;
                    }
                    var total = 0;
                    for (var i = 0; funFragment.subList && i < funFragment.subList.length; i++) {
                        total += this.API.private("getFunFragmentWidth", funFragment.subList[i]);
                    }
                    return total;
                }
                //}
                //else{递归调用直接返回儿子中最大的那个
                else {
                    var max = selfWidth;
                    for (var i = 0; ! funFragment.isClose && funFragment.subList && i < funFragment.subList.length; i++) {
                        var CW = this.API.private('getFunFragmentWidth', funFragment.subList[i]);
                        if (CW > max) {
                            max = CW;
                        }
                    }
                    return max;
                }
                //}
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawNormal
            *@description [功能]设定普通节点的画图函数,idx是函数片段序号，cx,cy是画图中心坐标，lastNode是上一个坐标，有连线时画
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return 是一个对象{idx:xxx,level:xxx,node:obj,left:lll}left 是计算最左边的坐标
            *@example toDO
            */
            "drawNormal": function(fArr, idx, cx, cy, lastNode, left, isSon) {
                //获取基本的函数片段信息
                var fragment = fArr[idx];
                //创建矩阵节点
                var hasTodo = false;
                if (fragment.code == "" || fragment.code == null) {
                    hasTodo = true;
                } else {
                    for (var i = 0; fragment.subCommand && i < fragment.subCommand.length; i++) {
                        if (fragment.subCommand[i].type == "++") {
                            hasTodo = true;
                        }
                    }
                }
                var node = (!hasTodo) ? this.API.private('drawNode', this.API.private("pText", fragment.command), cx, cy, this.MY.NORMAL) : this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.NORMAL, this.MY.YELLOW);
                node.fragment = fragment;
                if (lastNode == null) {
                    node.parentFragment = null;
                } else if (isSon) {
                    node.parentFragment = lastNode.fragment;
                } else {
                    node.parentFragment = lastNode.parentFragment;
                }
                //if(有lastNode){画连线
                if (lastNode) {
                    this.API.private("drawLine", lastNode, node);
                }
                //}
                //调用drawAny，画出下一个图
                var nextLeft = (cx - this.MY.stepW);
                nextLeft = (nextLeft < left) ? nextLeft: left;
                return this.API.private("drawAny", fArr, idx + 1, cx, cy + this.MY.oneStep, node, nextLeft);
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawBranch
            *@description [功能]设定分支节点的画图函数
            *[思路]函数原节点增加一个isClose的记录，标识这个节点是否关闭，关闭和不关闭有不同的画法
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "drawBranch": function(fArr, idx, cx, cy, lastNode, left, isSon) {
                //获取基本的函数片段信息
                var fragment = fArr[idx];
                //创建矩阵节点
                var node = null;
                //创建矩阵节点
                var firstNode = null;
                if (!fragment.isClose) {
                    firstNode = node = this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.BRANCH);
                } else {
                    firstNode = node = this.API.private("drawNode", "", cx, cy, this.MY.BRANCH, this.MY.RED);
                }

                if (lastNode == null) {
                    node.parentFragment = null;
                } else if (isSon) {
                    node.parentFragment = lastNode.fragment;
                } else {
                    node.parentFragment = lastNode.parentFragment;
                }
                node.fragment = fragment;

                if (lastNode) {
                    this.API.private("drawLine", lastNode, node);
                }

                var branchList = fragment.subList || [];

                var totalWidth = this.API.private("getFunFragmentWidth", fragment);
                var startCx = cx - totalWidth / 2 + (this.MY.oneBaseW + this.MY.stepW) / 2;

                var oneWidth = this.MY.oneBaseW + this.MY.stepW * 2;
                var totalLen = oneWidth * branchList.length;
                var firstX = cx - totalLen / 2 + (oneWidth / 2);
                var lastLevel = cy;
                var resultObjArr = [];
                var cLeft = startCx;

                for (var i = 0; ! fragment.isClose && i < branchList.length; i++) {
                    var oneWidth = this.API.private("getFunFragmentWidth", branchList[i]);
                    var dx = startCx + oneWidth / 2 - (this.MY.oneBaseW + this.MY.stepW) / 2;
                    startCx += oneWidth;

                    var hasTodo = false;
                    if (branchList[i].code == "" || branchList[i].code == null) {
                        hasTodo = true;
                    } else {
                        for (var ii = 0; branchList[i].subCommand && ii < branchList[i].subCommand.length; ii++) {
                            if (branchList[ii].subCommand[ii].type == "++") {
                                hasTodo = true;
                            }
                        }
                    }

                    var tmpNode = !hasTodo ? this.API.private("drawNode", branchList[i].command.replace(/\{/, "\n"), dx, cy + this.MY.oneStep, this.MY.BRANCHLIST) : this.API.private("drawNode", branchList[i].command.replace(/\{/, "\n"), dx, cy + this.MY.oneStep, this.MY.BRANCHLIST, this.MY.YELLOW);

                    var line = this.API.private("drawLine", node, tmpNode, "", [{
                        x: cx + this.MY.oneBaseW / 2,
                        y: cy + this.MY.oneStep / 2
                    },
                    {
                        x: dx + this.MY.oneBaseW / 2,
                        y: cy + this.MY.oneStep / 2
                    }]);

                    tmpNode.fragment = branchList[i];
                    tmpNode.parentFragment = fragment;
                    //递归画儿子
                    var r = this.API.private("drawAny", branchList[i].subList, 0, dx, cy + this.MY.oneStep * 2, tmpNode, cLeft, true);
                    //将记录所有分支里面，层次最深的那个
                    if (r != null) {
                        if (r.level > lastLevel) {
                            lastLevel = r.level;
                        }
                        if (cLeft > r.left - 4) {
                            cLeft = r.left - 4;
                        }
                        resultObjArr.push(r.node);
                    }
                }

                if (!fragment.isClose) {
                    node = this.API.private("drawNode", fragment.command, cx, lastLevel + this.MY.oneStep, this.MY.BRANCH, this.MY.BLACK);
                    node.fragment = firstNode.fragment;
                    node.parentFragment = firstNode.parentFragment;
                    for (var i = 0; i < resultObjArr.length; i++) {
                        var p1 = resultObjArr[i].point.down;
                        var p2 = node.point.up;
                        this.API.private("drawLine", resultObjArr[i], node, null, [{
                            x: p1.x,
                            y: p2.y - 10
                        },
                        {
                            x: p2.x,
                            y: p2.y - 10
                        }]);
                    }
                    var line = this.API.private("drawLine", firstNode, node, null, [{
                        x: cLeft,
                        y: cy + this.MY.oneBaseH / 4
                    },
                    {
                        x: cLeft,
                        y: lastLevel + this.MY.oneStep + this.MY.oneBaseH / 4
                    }]);
                    line.addEvent({
                        onclick: function() {
                            firstNode.fragment.isClose = true;
                            _this.showFun(funObj, editCallback);
                        }
                    });
                }
                //递归画下一个
                if (!fragment.isClose) {
                    return this.API.private("drawAny", fArr, idx + 1, cx, lastLevel + this.MY.oneStep * 2, node, cLeft);
                } else {
                    return this.API.private("drawAny", fArr, idx + 1, cx, cy + this.MY.oneStep, node, cLeft);
                }
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawCycle
            *@description [功能]设定block节点的画图函数
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "drawCycle": function(fArr, idx, cx, cy, lastNode, left, isSon) {
                //获取基本的函数片段信息
                var fragment = fArr[idx];
                //确定是否代完成状态
                var hasTodo = false;
                if (fragment.code == "" || fragment.code == null) {
                    hasTodo = true;
                } else {
                    for (var i = 0; fragment.subCommand && i < fragment.subCommand.length; i++) {
                        if (fragment.subCommand[i].type == "++") {
                            hasTodo = true;
                        }
                    }
                }
                //创建矩阵节点
                var firstNode = node = null;
                if (!fragment.isClose) {
                    firstNode = node = (!hasTodo) ? this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.CYCLE) : this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.CYCLE, this.MY.YELLOW);
                } else {
                    firstNode = node = this.API.private("drawNode", "", cx, cy, this.MY.CYCLE, this.MY.RED);
                }

                if (lastNode == null) {
                    node.parentFragment = null;
                } else if (isSon) {
                    node.parentFragment = lastNode.fragment;
                } else {
                    node.parentFragment = lastNode.parentFragment;
                }
                node.fragment = fragment;

                if (lastNode) {
                    this.API.private("drawLine", lastNode, node);
                }

                var branchList = fArr[idx].subList || [];
                var lastLevel = 0;
                var lastNode = null;

                var dx = cx;

                var nextLeft = (cx - this.MY.stepW);
                //递归画儿子
                var _this = this;
                if (!fragment.isClose) {
                    var r = this.API.private("drawAny", branchList, 0, dx, cy + this.MY.oneStep, node, null, true);
                    //将记录所有分支里面，层次最深的那个
                    lastLevel = r.level;
                    lastNode = r.node;

                    if (r.left - 4 < nextLeft) {
                        nextLeft = r.left - 4;
                    }

                    node = this.API.private("drawNode", "", cx, lastLevel + this.MY.oneStep, this.MY.CYCLEEND, this.MY.BLACK);
                    node.fragment = firstNode.fragment;
                    node.parentFragment - firstNode.parentFragment;
                    this.API.private("drawLine", lastNode, node);

                    var line = this.API.private("drawLine", firstNode, node, null, [{
                        x: nextLeft,
                        y: cy + this.MY.oneBaseH / 2
                    },
                    {
                        x: nextLeft,
                        y: lastLevel + this.MY.oneStep + this.MY.oneBaseH / 2
                    }]);
                    line.addEvent({
                        onclick: function() {
                            firstNode.fragment.isClose = true;
                            _this.showFun(_this.MY.funObj, _this.MY.editCallback);
                        }
                    });
                }

                nextLeft = (nextLeft < left) ? nextLeft: left;
                if (!fragment.isClose) {
                    return this.API.private("drawAny", fArr, idx + 1, cx, lastLevel + this.MY.oneStep * 2, node, nextLeft);
                } else {
                    return this.API.private("drawAny", fArr, idx + 1, cx, cy + this.MY.oneStep, node, nextLeft);
                }
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawBlock
            *@description [功能]设定block节点的画图函数
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "drawBlock": function(fArr, idx, cx, cy, lastNode, left, isSon) {
                //获取基本的函数片段信息
                var fragment = fArr[idx];
                var _this = this;

                var hasTodo = false;

                for (var i = 0; fragment.subCommand && i < fragment.subCommand.length; i++) {
                    if (fragment.subCommand[i].type == "++") {
                        hasTodo = true;
                    }
                }
                //创建矩阵节点
                var node = null;
                var firstNode = node = null;
                if (fragment.isClose) {
                    firstNode = node = this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.BLOCK, this.MY.RED);
                } else {
                    firstNode = node = (hasTodo) ? this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.BLOCK, this.MY.YELLOW) : this.API.private("drawNode", this.API.private("pText", fragment.command), cx, cy, this.MY.BLOCK);
                }

                if (lastNode == null) {
                    node.parentFragment = null;
                } else if (isSon) {
                    node.parentFragment = lastNode.fragment;
                } else {
                    node.parentFragment = lastNode.parentFragment;
                }
                node.fragment = fragment;

                if (lastNode) {
                    drawLine(lastNode, node);
                }

                var branchList = fArr[idx].subList || [];
                var lastLevel = 0;
                var lastNode = null;

                var dx = cx;
                //我自己的最左坐标
                var myLeft = (cx - this.MY.stepW);
                //递归画儿子
                if (!fragment.isClose) {
                    //画自己，应该用自己的最左坐标
                    var r = this.API.private("drawAny", branchList, 0, dx, cy + this.MY.oneStep, node, null, true);
                    //将记录所有分支里面，层次最深的那个
                    lastLevel = r.level;
                    lastNode = r.node;

                    if (r.left - 4 < myLeft) {
                        myLeft = r.left - 4;
                    }

                    node = this.API.private("drawNode", "", cx, lastLevel + this.MY.oneStep, this.MY.BLOCK, this.MY.BLACK);
                    node.fragment = firstNode.fragment;
                    node.parentFragment = firstNode.parentFragment;
                    this.API.private("drawLine", lastNode, node);

                    var line = this.API.private("drawLine", firstNode, node, null, [{
                        x: myLeft,
                        y: cy + this.MY.oneBaseH / 2
                    },
                    {
                        x: myLeft,
                        y: lastLevel + this.MY.oneStep + this.MY.oneBaseH / 2
                    }]);
                    line.addEvent({
                        onclick: function() {
                            firstNode.fragment.isClose = true;
                            _this.showFun(_this.MY.funObj, _this.MY.editCallback);
                        }
                    });
                }
                //递归画下一个
                //总体上的最左坐标
                var nextLeft = (myLeft < left) ? myLeft: left;
                if (!fragment.isClose) {
                    return this.API.private("drawAny", fArr, idx + 1, cx, lastLevel + this.MY.oneStep * 2, node, nextLeft);
                } else {
                    return this.API.private("drawAny", fArr, idx + 1, cx, cy + this.MY.oneStep, node, nextLeft);
                }
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$drawAny
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param p1 toDo
            *@return toDo
            *@example toDO
            */
            "drawAny": function(fArr, idx, cx, cy, lastNode, left, isSon) {
                if (left == null) {
                    left = 100000;
                }
                if (fArr == null || idx >= fArr.length) {
                    return {
                        idx: idx - 1,
                        level: cy - this.MY.oneStep,
                        node: lastNode,
                        left: left
                    }
                }
                var fragment = fArr[idx];
                var result = null;
                if (fragment.type == "normal") {
                    result = this.API.private("drawNormal", fArr, idx, cx, cy, lastNode, left, isSon);
                    result.left = result.left > left ? left: result.left;
                } else if (fragment.type == "branchList") {
                    result = this.API.private("drawBranch", fArr, idx, cx, cy, lastNode, left, isSon);
                    result.left = result.left > left ? left: result.left;
                } else if (fragment.type == "cycle") {
                    result = this.API.private("drawCycle", fArr, idx, cx, cy, lastNode, left, isSon);
                    result.left = result.left > left ? left: result.left;
                } else if (fragment.type == "block") {
                    result = this.API.private("drawBlock", fArr, idx, cx, cy, lastNode, left, isSon);
                    result.left = result.left > left ? left: result.left;
                }
                return result;
            },
            /**
            *@function
            *@memberOf graphyFunMod
            *@name private$pText
            *@description [功能]自动截取长度的函数
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param t toDo
            *@return toDo
            *@example toDO
            */
            "pText": function(t) {
                if (t == null) {
                    return "";
                }
                var oneLine = this.MY.oneBaseW / 14;
                var tt = t.replace(/(\)\s*)\{/,
                function(a, b) {
                    return b + "``";
                });
                var array = tt.split("``");
                var dvStr = function(tt) {
                    var result = [];
                    var s = 0;
                    while (true) {
                        result.push(tt.substr(s, oneLine));
                        if (s + oneLine >= tt.length) {
                            break;
                        }
                        s += oneLine;
                    }
                    return result;
                }
                var result = "";
                for (var i = 0; i < array.length; i++) {
                    if (i > 0) {
                        result += "\n";
                    }
                    result = result + (dvStr(array[i]).join('\n'));
                }
                return result;
            }
        },
        view: {
            'mainView': require("./resource/graphyFunMod/mainView.tpl")
        }

    },
    module);
    return FW;
});