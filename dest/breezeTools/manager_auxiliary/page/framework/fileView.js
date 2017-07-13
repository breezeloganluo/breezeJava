/**
* @namespace
* @name fileView 
* @version 0.01 罗光瑜 初始版本
0.02 罗光瑜 fileView对外开放了若干个接口，比如initDir,拷贝粘贴
0.03 罗光瑜 修改findTreeObjByPath函数，解决children中某将一个儿子不存在的情况，也可能是缓存所致，所以要重新刷新
0.04 罗光瑜 扩展名信息结构增加是否可编辑和是否可新增权限
0.05 罗光瑜 支持直接输入文件名直接改变到文件路径上
0.06 罗光瑜 恢复历史记录功能，将拷贝源和目标弄反了
* @description  这是一个文件类，用于显示文件编辑器的内容，这个类显示的不是目录树展开模式，而是类似文件浏览器，只有右边部分。
*这个类参考原来的fileControl实现，这个类的文件目录结构如下：
*[{
*    name: "显示名称",
*    realName"真正的文件名",
*    type: "item/folder",
*    dir:"当前节点路径",
*    children: [儿子的内容，循环上面父亲的结构]
*}]       
*调用this.param.main.setTitle(this.param.dataArr, this.param.idx, "你妹", this.param.tagIdx, this.param.tagPath);可以刷新主框架上分页栏的标题名字                      
*如何创建新窗口，参见newFile函数  
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../../commtools/fileselect");
    FW.register({
        "name": "fileView",
        /**
        *@function
        *@memberOf fileView
        *@name onCreate$onCreate
        *@description 初始化文件操作对象以及顶节点数据，注意这里不用直接显示的，显示过程要上级对象主动调用showContent去实现
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
            //读入当前路径
            this.MY.curContentList = FW.use().load("fileView_" + this.id, true);
            //初始化顶节点
            this.MY.allData = {
                name: "/",
                realName: "/",
                type: "folder",
                dir: "/"
            }

            if (this.MY.curContentList != null) {
                this.MY.curContentList = this.findTreeObjByPath(this.MY.curContentList.dir + "/" + this.MY.curContentList.name);
            } else {
                this.MY.curContentList = this.MY.allData;
            }

            this.param.initDir = "/";
        },
        "public": {
            /**
            *@function
            *@memberOf fileView
            *@name public$showContent
            *@description [功能]这个类是被上级调框架显示基本信息时调用的接口函数，即被main.js调用
            */
            "showContent": function() {
                //显示当前路径信息
                this.showList();
            },
            /**
            *@function
            *@memberOf fileView
            *@name public$findTreeObjByPath
            *@description [功能]根据路径查找对应的目录树对象
            *[思路]以/为间隔符切分路径，然后再this.MY.allData中按照每层结构查找，找不到就返回空
            *[接口.this.MY.allData]
            *@param path 目录路径
            *@return 符合要求的目录树节点对象，对象结构参考基本信息中的说明
            */
            "findTreeObjByPath": function(path) {
                // 用/\切分成数组
                if (path == null) {
                    return null;
                }
                var pathArr = path.split(/[\\\/]/);
                //获取顶级节点作为当前节点
                //--this.MY.allData顶级节点一定是/
                var result = this.MY.allData;
                //while(所有数组分解出来的内容){按照遍历的每一个去寻找节点对象
                for (var i = 0; i < pathArr.length; i++) {
                    //去掉空格
                    if (pathArr[i] == "") {
                        continue;
                    }
                    //用上级节点查找当前输入的节点
                    if (result.type != "folder") {
                        console.log("查找路径失败，当前节点应该是目录，但确实文件");
                        return null;
                    }
                    if (result.children == null) {
                        result.children = this.API.private('getFileList', result);
                        if (result.children == null) {
                            return;
                        }
                    }
                    var found = false;
                    for (var j = 0; j < result.children.length; j++) {
                        if (result.children[j].realName == pathArr[i]) {
                            found = true;
                            result = result.children[j];
                            break;
                        }
                    }
                    //找不到就返回为空
                    //没有找到，就清除缓存再找一次
                    if (!found) {
                        result.children = this.API.private('getFileList', result);
                        for (var j = 0; j < result.children.length; j++) {
                            if (result.children[j].realName == pathArr[i]) {
                                found = true;
                                result = result.children[j];
                                break;
                            }
                        }
                        if (!found) {
                            return null;
                        }
                    }
                }
                //}
                //返回这个对象
                return result;
            },
            /**
            *@function
            *@memberOf fileView
            *@name public$showList
            *@description [功能]用于显示这个节点下的列表信息，如果节点信息不在内存中，会到后台读文件补齐
            *[思路]参考fileControl同名函数实现，根据当前节点获取下一个节点列表，然后显示对应的视图
            *[接口.this.MY.curContentList]当前传入的参数要保存起来
            *[接口.this.MY.curDir]记录当前的路径
            *@param nodedata 这是一个文件类，用于显示文件编辑器的内容，这个类显示的不是目录树展开模式，而是类似文件浏览器，只有右边部分。
            这个类参考原来的fileControl实现，这个类的文件目录结构如下：
            {
            name: "显示名称",
            realName"真正的文件名",
            type: "item/folder",
            dir:"当前节点路径",
            children: [儿子的内容，循环上面父亲的结构]
            }
            
            如果这个参数为null，则说明参数的值要this.MY.curContentList中获取
            */
            "showList": function(nodedata) {
                //保存参数的值，便于不传参数时用上次的值
                var hasparam = true;
                if (nodedata) {
                    this.MY.curContentList = nodedata;
                } else {
                    nodedata = this.MY.curContentList;
                    hasparam = false;
                }
                //保存数据到本地磁盘上
                FW.use().save("fileView_" + this.id, nodedata, true);
                //刷新上级的目录名
                this.param.main && this.param.main.setTitle && this.param.main.setTitle(this.param.dataArr, this.param.idx, nodedata.name, this.param.tagIdx, this.param.tagPath);
                //整理数据到本点击节点信息
                var data = nodedata;
                //if (如果是文件类型){显示文件的视图
                if (data.type == "item") {
                    //显示显示单文件视图
                    this.API.show("这是文件了", data);
                }
                //}
                //else{显示目录类型
                else {
                    //将当前目录记录this.my.curDir中
                    this.MY.curDir = nodedata.dir + "/" + nodedata.name;
                    this.MY.curDir = this.MY.curDir.replace(/[\\\/]+/g, "/");
                    //判断是否真的有儿子
                    //--没有就去获取数据
                    if (data.children == null) {
                        data.children = this.API.private('getFileList', data);
                        if (data.children == null) {
                            return;
                        }
                    }
                    //整理目录列表
                    var list = data.children;
                    //if(有参数传入){将所有子节点的选中状态设置成false
                    if (hasparam) {
                        //将所有选中状态置否
                        for (var n = 0; n < list.length; n++) {
                            list[n] && list[n].name && (list[n].selected = false);
                        }
                        list.selectedItem = null;
                    }
                    //}
                    list.copycut = this.MY.copycut;
                    list.name = data.realName;
                    list.dir = data.dir.replace(/[\\\/]+/g, "/").replace(/\/$/i, "");
                    list.displayDir = list.dir + "/" + list.name.replace(/^[\\\/]*/, "");
                    var initDir = this.param.initDir.replace(/[\\\/]*$/i, "");

                    if (list.displayDir.indexOf(initDir) == 0) {
                        list.displayDir = list.displayDir.substr(initDir.length);
                        list.displayDir = this.API.private('changeDisplayName', list.displayDir);
                    }
                    if (list.displayDir == "") {
                        list.displayDir = "/";
                    }
                    if (list.displayDir.length > 35) {
                        list.displayDir = "..." + list.displayDir.substr(list.displayDir.length - 30, 30);
                    }
                    //显示目录视图
                    this.API.show("folderShow", list);
                }
                //}
            },
            /**
            *@function
            *@memberOf fileView
            *@name public$setInitDir
            *@description [功能]设置初始化的公有方法
            *[思路]修改一下全局变量，然后重新show一下
            *@param initDir 初始路径
            */
            "setInitDir": function(initDir) {
                //显示初始化路径
                this.param.initDir = initDir.replace(/[\\\/]+/g, "/").replace(/\/$/i, "") + "/";
                this.MY.curContentList = this.findTreeObjByPath(this.param.initDir);
            },
            /**
            *@function
            *@memberOf fileView
            *@name public$setCopyCut
            *@description [功能]因为拷贝粘贴有点像黏贴板，所以应该对外开放的
            *@param fileName 文件名
            *@param dir 文件路径
            *@param type 文件类型:type/cut,缺省是copy
            */
            "setCopyCut": function(fileName, dir, type) {
                //设置内存结构信息
                this.MY.copycut = {
                    dir: dir,
                    file: fileName,
                    type: type || "copy"
                }
            },
            /**
            *@function
            *@memberOf fileView
            *@name public$parse
            *@description [功能]粘贴文件也可能是公有方法，从FireEvent中设置出来是合理的
            *@param destDir 要拷贝的目标文件，可以不传递
            */
            "parse": function(destDir) {
                //获取拷贝目标信息
                var tmp = this.MY.curContentList;
                var destDir = destDir || tmp.dir + "/" + tmp.name;
                destDir = destDir.replace(/[\\\/]+/g, "/");
                //获取拷贝源信息
                var srcInfo = this.MY.copycut;
                var destFile = srcInfo.file;
                if (destDir == srcInfo.dir) {
                    destFile = "coopy_" + destFile;
                }
                var result = 0;
                //if(拷贝情况){进行拷贝
                if (srcInfo.type == "copy") {
                    //拷贝
                    this.MY.fileOper.setPath(srcInfo.dir);
                    result = this.MY.fileOper.copyFile(srcInfo.file, destDir, destFile);
                }
                //}
                //else{剪切情况
                else {
                    //剪切
                    this.MY.fileOper.setPath(srcInfo.dir);
                    result = this.MY.fileOper.moveFile(srcInfo.file, destDir, destFile);
                }
                //}
                //if(失败){提示并退出
                if (result && result.code != 0) {
                    //提示并退出
                    FW.alert("操作失败！");
                    return;
                }
                //}
                //提示操作成功
                alert("操作成功");
                //清除拷贝粘贴标识
                this.MY.copycut = null;
                //重新初始化
                this.MY.curContentList.children = null;
                this.showList();
            }
        },
        "private": {
            /**
            *@function
            *@memberOf fileView
            *@name private$getFileList
            *@description [功能]获取文件列表
            *[思路]调用文件列表直接获取数据就行
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param dirObj 要查询的目录结构
            {
            name: "显示名称",
            realName"真正的文件名",
            type: "item/folder",
            dir:"当前节点路径"
            children: [儿子的内容，循环上面父亲的结构]
            }
            当节点为空的时候，就是查询顶节点
            *@return [{
            name: "显示名称",
            realName"真正的文件名",
            type: "item/folder",
            dir:"当前节点路径",
            children: [儿子的内容，循环上面父亲的结构]
            }]
            */
            "getFileList": function(dirObj) {
                //合成访问路径
                var dir = "/";
                if (dirObj != null) {
                    dir = dirObj.dir + "/" + dirObj.realName;
                }
                //查询文件
                this.MY.fileOper.setPath(dir);
                var resultList = this.MY.fileOper.queryDir();
                //转换数据并返回
                var result = this.API.private('changeData', resultList, dir);
                return result;
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$changeData
            *@description [功能]从fileOper中获取到的数据转换将自定义的数据转换成目录树所能理解的结构
            *另外，将文件的类型做一定的变换，变换的要求：
            *1.默认目录都是灰色，但是servicegadget和page是红色和粉红色
            *2.jsp，html是图片用叶子图标，图片文件用图片图标，js文件用图片图标，其他用默认图标
            *最后，要进行一次排序，将目录和文件分开顺序，目录排列在前面，文件排列在后面
            *@param filesData 从fileOper获取到的文件名:
            {
            fileName:{
            type:"F/D"
            }
            }
            *@param initDir 原始的目录
            *@return treeView能够理解的目录树结构
            [
            {
            name:"显示名称",
            type:"item/folder",
            dir:"当前文件的目录"
            ]
            }
            ]
            */
            "changeData": function(filesData, initDir) {
                //声明结果变量
                if (filesData == null) {
                    return [];
                }
                var resultList;
                var dirList = [],
                fileList = [];
                //while(所有filesData数据){进行转换
                for (var n in filesData) {
                    //如果WEB-INF就祛除掉
                    if (n == "web.xml" || n == "breeze" || n == "backup") {
                        continue;
                    }
                    //获取其中一个数据
                    var one = {
                        name: n,
                        realName: n,
                        type: ("F" == filesData[n].type) ? "item": "folder",
                        dir: initDir
                    }
                    //if (是目录){处理目录的特殊要求
                    if (one.type == "folder") {
                        //直接加入到列表中
                        //--后续如果有特殊属性可在这里加入，比如不同的图标
                        dirList.push(one);
                    }
                    //}
                    //else{
                    else {
                        //直接加入数组中
                        //--后续如果要有什么特殊的处理，在这个分支中完成，比如不同的扩展名给出不同的文件图标
                        fileList.push(one);
                    }
                    //}
                }
                //}
                //将文件列表和目录列表分别排序
                fileList.sort(function(a, b) {
                    return (a.realName > b.realName) ? 1 : -1;
                });
                dirList.sort(function(a, b) {
                    return (a.realName > b.realName) ? 1 : -1;
                });
                //返回结果
                resultList = dirList.concat(fileList);
                return resultList;
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$changeDisplayDir
            *@description [功能]显示的当前菜单名要合并initDIr，合并完再进行编码
            *[接口.this.param.initDir]
            *@param curDir 当前路径
            */
            "changeDisplayDir": function(curDir) {
                if (curDir == "/") {
                    return "";
                }
                //合并路径
                var result = curDir;
                result = result.replace(/[\\\/]+/ig, "/");
                result = result.replace(/[\\\/]+$/i, "");
                result = this.API.private('changeDisplayName', result);
                return result;
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$changeDisplayName
            *@description [功能]在列表显示时，转换中文
            *@param name 输入的名字信息
            */
            "changeDisplayName": function(name) {
                if (name == "/") {
                    return "";
                }
                //解码并返回
                return decodeURI(name.replace(/@/ig, "%"));
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$changeInputName
            *@description [功能]将输入的文件名进行编码
            */
            "changeInputName": function(name) {
                //直接返回编码内容
                return encodeURI(name).replace(/%/ig, "@");
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$parserFileStyle
            *@description [功能]根据是否选中处理页面上样式信息
            *[思路]如果样式是选中状态，就是一种样式，否则是另外一种状态
            *@param isselected true/false
            *@return 选中则返回alert alert-success否则显示：alert alert-warning
            */
            "parserFileStyle": function(isselected) {
                //根据输入返回不同
                if (isselected) {
                    return "alert alert-success";
                } else {
                    return "alert alert-warning";
                }
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$formattime
            *@description [功能]格式化时间格式将20150901改成2015年09月01日
            *@param d 输入的时间字符串
            *@return 格式化好后的时间文本
            */
            "formattime": function(d) {
                var execResult = /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/i.exec(d);
                if (execResult == null) {
                    return d;
                }
                var result = execResult[1] + "年" + execResult[2] + "月" + execResult[3] + "日&nbsp" + execResult[4] + ":" + execResult[5] + ":" + execResult[6];
                execResult = /_(\d+)/i.exec(d);
                if (execResult != null) {
                    result += "(usercid:" + execResult[1] + ")";
                }
                return result;
            },
            /**
            *@function
            *@memberOf fileView
            *@name private$showFileOperIcon
            *@description [功能]返回每个文件可操作图标的html片段
            *[接口.fileGlobleSetting]fileGlobleSetting = [
            *							{
            *								  name:"gadget编辑器",
            *								  exp:"|js|多个|",
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
            *								exp:"jsp",
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
            *                                                                  auth:{
            *                                                                         edit:true,
            *                                                                         add:false
            *                                                                  }
            *							}
            *                                                        ]
            *其中auth表示是否有独立权限，这个字段可选，如果没有说明忽略这个字段名。
            *@param fileName 输入的文件名
            *@return html片段
            */
            "showFileOperIcon": function(fileName) {
                //初始化可用节点数组
                var data = [];
                var fileArr = fileName.split('.');
                var ext = "|" + fileArr[fileArr.length - 1] + "|";
                //while(全局的文件列表对象){判断匹配的就加入
                for (var i = 0; i < fileGlobleSetting.length; i++) {
                    //获取其中一个
                    var one = fileGlobleSetting[i];
                    //判断类型
                    if (one.type == null || (one.type != "file" && one.type != "selfedit")) {
                        continue;
                    }
                    //判断权限
                    if (one.auth != null && !one.auth.edit) {
                        continue;
                    }
                    //判断扩展名
                    if (one.exp.indexOf(ext) < 0) {
                        continue;
                    }
                    //加入匹配数组
                    data.push(one);
                    one.idx = i;
                }
                //}
                //返回视图内容
                data.name = fileName;
                var result = this.API.show("fileIcon", data, "-");
                return result;
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$go2dir
            *@description [功能]改变目录的函数
            *[思路]拿到目录后，要寻找跟目录，然后再进行跳转处理
            *@param dir 跳转的目录
            */
            "go2dir": function(dir) {
                //阻止window事件
                window.event ? window.event.cancelBubble = true: evt.stopPropagation();
                //判断输入参数
                if (dir == null || dir == ".") {
                    this.MY.curContentList.children = null;
                    this.showList();
                }
                //查找对象
                var nodedata = this.findTreeObjByPath(dir);
                //显示
                this.showList(nodedata);
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$openNewDir
            *@description [功能]显示添加目录的功能列表
            *[接口.this.MY.curContentList]
            */
            "openNewDir": function() {
                //获取当前路径
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                //将添加的蒙板层显示出来
                this.API.mask("newDir", srcDir, "400", "220");
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$newDir
            *@description [功能]创建新目录
            *[思路]获取从页面输入的路径和名称，创建目录,需要重新初始化并展开
            *@param dir 页面上获取的路径
            */
            "newDir": function(dir) {
                //处理传入参数
                var newDirName = this.API.private('changeInputName', $('#newDirName').val());
                var newDir = dir + "/" + newDirName;
                this.MY.fileOper.setPath(newDir);
                var result = this.MY.fileOper.queryDir();
                if (result && result.code == 0) {
                    FW.alert("操作成功");
                }
                //取消蒙板层
                FW.unblockUI();
                //重新初始化并重新展开目录树
                this.MY.curContentList.children = null;
                this.showList();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$selectFile
            *@description [功能]选中功能，点击后选中某个文件
            *[思路]每个节点都有自己的选中标识，通过控制这个表示实现不同的选中状态
            *@param idx 文件对象的索引号
            */
            "selectFile": function(idx) {
                //清除所有原先的值
                var data = this.MY.curContentList;
                if (data.children == null) {
                    return;
                }
                var list = data.children;
                for (var n = 0; n < list.length; n++) {
                    if (list[n] && list[n].name) {
                        list[n].selected = false;
                    }
                }
                //根据传入名称，给对应的对象添加选中标记
                list[idx].selected = true;
                list.selectedItem = idx;
                //通知上层,如果有的话
                this.param.main && this.param.main.fileViewSelectFile && this.param.main.fileViewSelectFile((list[idx].dir + "/" + list[idx].name).replace(/[\\\/]+/ig, "/"), this);
                //重新显示
                this.showList();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$copy
            *@description [功能]记录拷贝的文件内容
            *[思路]从内存中获取文件列表
            *[接口.this.MY.curContentList]当前被保存起来的列表对象
            *[接口.this.MY.copycut]{
            *  srcfile:"要拷贝的文件路径",
            *  type:"copy/cut"
            *}
            *@param idx 要拷贝的索引
            */
            "copy": function(idx) {
                //获取文件路径
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                var srcFile = data[idx].realName;
                //设置到内存中
                this.setCopyCut(srcFile, srcDir, "copy");
                this.showList();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$cut
            *@description [功能]记录拷贝的文件内容
            *[思路]从内存中获取文件列表
            *[接口.this.MY.curContentList]当前被保存起来的列表对象
            *[接口.this.MY.copycut]{
            *  srcfile:"要拷贝的文件路径",
            *  type:"copy/cut"
            *}
            *@param idx 要拷贝的文件索引
            */
            "cut": function(idx) {
                //获取文件路径
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                var srcFile = data[idx].realName;
                //设置到内存中
                this.setCopyCut(srcFile, srcDir, "cut");
                this.showList();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$parse
            *@description [功能]完成粘贴操作
            *[思路]先进行文件操作，然后左边树要重新初始化，再展开到当前
            *[接口.this.MY.curContentList]当前列表信息可获取目录信息
            *[接口.this.MY.copycut]记录拷贝粘贴的原信息
            */
            "parse": function() {
                this.parse();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$deleteFile
            *@description [功能]删除文件
            *[思路]获取到文件内容后，将文件删除，注意要让用户二次确认，删除后，为了要让之生效，还要重新初始化目录树以及展开到当前目录
            *@param idx 传入的名称，注意，不是文件名，而是传入的对象的对象名data0，data1之类的
            */
            "deleteFile": function(idx) {
                //获取要删除的文件
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                var srcFile = data[idx].realName;
                //给出提示二次确认
                var _this = this;
                FW.confirm("您确认要进行删除",
                function(result) {
                    if (result) {
                        //实施删除
                        _this.MY.fileOper.setPath(srcDir);
                        _this.MY.fileOper.setFileName(srcFile);
                        _this.MY.fileOper.deleteFile();
                        //重新初始化并重新展开目录树
                        _this.MY.curContentList.children = null;
                        _this.showList();
                    }
                });
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$showFileHistory
            *@description [功能]显示文件的所有历史文件
            *[接口.this.MY.curContentList]当前的列表页
            *@param idx 被选中的文件索引
            */
            "showFileHistory": function(idx) {
                //合成要读取的路径信息
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                var srcFile = data[idx].realName;
                var destDir = "/backup/" + srcDir + "/" + srcFile;
                //读取目录信息
                this.MY.fileOper.setPath(destDir);
                var resultData = this.MY.fileOper.queryDir();
                //转成数组并排序
                var showData = [];
                for (var n in resultData) {
                    if (resultData[n].type && resultData[n].type == "F") {
                        showData.push(n);
                    }
                }
                showData.sort(function(a, b) {
                    return parseInt(b.replace(/\D/g, "")) - parseInt(a.replace(/\D/g, ""));
                });
                showData.dir = (srcDir + "/" + srcFile).replace(/[\\\/]+/g, "/");
                //显示列表
                this.API.show("fileHistory", showData);
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$recovery
            *@description [功能]恢复某个版本的文件，并重新返回原文件
            *[思路]传入的是当前文件名包括路径，以及被恢复的文件名。要根据这两项进行文件目录的处理
            *@param srcFile 源文件路径名
            *@param recoveryFile 要恢复的文件名
            */
            "recovery": function(srcFile, recoveryFile) {
                //获取源的文件和路径
                var exec = /([\s\S]+?)[\\\/]+([^\\\/]+$)/i.exec(srcFile);
                if (!exec) {
                    alert("历史文件错误无法恢复");
                    return;
                }
                var orgDir = exec[1];
                var orgFile = exec[2];
                //获取恢复的文件和路径
                var reDir = "/backup/" + srcFile;
                var reFile = recoveryFile;
                //进行拷贝操作
                this.MY.fileOper.setPath(reDir);
                var result = this.MY.fileOper.copyFile(reFile, orgDir, orgFile);
                //恢复显示
                if (!result || result.code != 0) {
                    FW.alert("版本恢复失败");
                    return;
                }

                this.showList();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$goEdit
            *@description [功能]去编辑
            *[思路]这里实现编辑功能
            *@param name 文件名
            *@param idx 全局中的索引
            */
            "goEdit": function(fileName, pidx) {
                //合成被打开的文件路径字符串
                var data = this.MY.curContentList.children;
                var dir = data.dir + "/" + data.name + "/" + fileName;
                var dir = dir.replace(/[\\\/]+/ig, "/");
                //if(是打开文件模式){打开文件
                if (fileGlobleSetting[pidx].type == "file") {
                    //进行编码
                    var enUrl = encodeURIComponent(dir);
                    //合成url
                    var url = "../../" + fileGlobleSetting[pidx].clickSetting["编辑"].replace("[fileUrl]", enUrl);
                    //跳转
                    window.open(url);
                }
                //}
                //else{在本窗口中打卡
                else {
                    //创建对应gadget实例
                    var gadgetName = fileGlobleSetting[pidx].gadget;
                    var app = FW.createApp(name, gadgetName, {
                        dom: "m_content"
                    });
                    app.param.fileUrl = dir;
                    //设置应用名称
                    var nameExecResult = /\w+/.exec(fileName);
                    var name = nameExecResult && nameExecResult[0] || fileName;
                    //显示到页面
                    this.param.main && this.param.main.addTopApp && this.param.main.addTopApp(app, name, [gadgetName, gadgetName, {
                        dom: "m_content"
                    }]);
                }
                //}
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$openNewFile
            *@description [功能]打开一个创建新页面的蒙板层
            */
            "openNewFile": function() {
                //获取当前路径
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                data = [];
                data.srcDir = srcDir;
                //for(所有文件扩展配置)获取当前可管理列表
                for (var i = 0; i < fileGlobleSetting.length; i++) {
                    //获取其中一个
                    var one = fileGlobleSetting[i];
                    //判断类型
                    if (one.type == null || (one.type != "file" && one.type != "selfedit")) {
                        continue;
                    }
                    //权限判断
                    if (one.auth != null && !one.auth.add) {
                        continue;
                    }
                    //加入匹配数组
                    data.push(one);
                    one.idx = i;
                }
                //}
                //将添加的蒙板层显示出来
                this.API.mask("newFile", data, "400", "220");
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$openGoDir
            *@description [功能]简单的打开一个蒙版的对话框，支持输入文本路径
            *[思路]文本输入框
            */
            "openGoDir": function() {
                //打开蒙版对话框
                this.API.mask("inputdir", null, 500);
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$goRndDir
            *@description [功能]响应确定事件，然后跳转到指定的目录上
            *[思路]用原来的函数跳转
            */
            "goRndDir": function() {
                var dir = $('#maskgoRndDir').val();
                //去掉http头部
                var url = dir.replace(/^http[s]?:\/\/[^\/]+/i, "");
                //去掉尾部?部分
                url = url.replace(/[#\?][\s\S]*$/i, "");
                //去掉应用名部分
                if (url.indexOf(Cfg.baseUrl) == 0) {
                    url = url.substr(Cfg.baseUrl.length);
                }
                //block(块){实现跳转
                //判断和保留最后的文件
                var execResult = /([\s\S]+)\/([^\.]+\.\w+$)/.exec(url);
                var dir = url;
                var file = null;
                if (execResult != null) {
                    dir = execResult[1];
                    file = execResult[2];
                }
                //查找路径
                var nodeData = this.findTreeObjByPath(dir);
                if (nodeData == null) {
                    FW.alert("没找到路径:" + dir);
                    return;
                }
                this.showList(nodeData);
                //如果有文件，把文件变成选中状态
                if (file != null) {
                    var currData = this.MY.curContentList;
                    console.log(currData);
                    for (var i = 0; i < currData.children.length; i++) {
                        if (currData.children[i].realName == file) {
                            currData.children[i].selected = true;
                        } else {
                            currData.children[i].selected = false;
                        }
                    }
                    this.showList();
                }
                //}
                //返回
                FW.unblockUI();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$upload2other
            *@description [功能]上传到其他工程
            *[思路]获取文件信息然后调用服务上传
            *@param idx 文件索引
            */
            "upload2other": function(idx) {
                //获取文件路径
                var data = this.MY.curContentList.children;
                var srcDir = data.dir + "/" + data.name;
                var srcFile = data[idx].realName;

                this.API.mask("mask_upload2other", {
                    baseDir: srcDir,
                    fileName: srcFile
                },
                600);
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$comfirmUpload2Other
            *@description [功能]真正实施上传
            *[思路]
            *@param baseDir 路径
            *@param fileName 文件名
            */
            "comfirmUpload2Other": function(baseDir, fileName) {
                //获取上传信息
                var destHost = $('#mask_upload2other').val();
                if (!/^https?:\/\//i.test(destHost)) {
                    FW.alert("必须以http(s):[host]/[app]格式");
                    return;
                }
                //调用服务进行上传
                var result = this.API.doServer("upload2other", "file", {
                    baseDir: baseDir,
                    fileName: fileName,
                    host: destHost
                });
                if (!result || result.code != 0) {
                    FW.alert("上传文件失败，请确认对端服务是否已经开启");
                    return;
                }
                FW.alert("文件上传成功");
                FW.unblockUI();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$newFile
            *@description [功能]添加新文件
            *[思路]合成具体的文件名，通过url传递到被编辑的逻辑页面中完成
            *[接口.合成hash的json格式]{
            *  gadget:"gadget名称",
            *  file:"url地址"
            *}
            *[接口.this.MY.controlTitle]{
            *  curSig:"当前的标识"
            *  sigList:{
            *     sig:{
            *        editObj:编辑对象，如果为空，就是自己
            *        name:显示到页面的字符串
            *     }
            *  }
            *}
            *如何创建新的窗口应用，参见newFile事件
            *@param dir 传入的文件路径
            *@param cidx 当前编辑索引号
            */
            "newFile": function(dir, cidx) {
                //合成被打开的文件路径字符串
                var fName = this.API.private('changeInputName', $('#newFileName').val());
                var expArr = fileGlobleSetting[cidx].exp.split("|");
                var execResult = /\.(\w+)$/.exec(fName);
                if (execResult == null || fileGlobleSetting[cidx].exp.indexOf("|" + execResult[1] + "|") < 0) {
                    for (var i = 0; i < expArr.length; i++) {
                        if (expArr[i] != "") {
                            fName += ("." + expArr[i]);
                            break;
                        }
                    }
                }
                var file = this.param.initDir + dir + "/" + fName;
                var file = file.replace(/[\\\/]+/ig, "/");
                //if(类型是file类型){
                if (fileGlobleSetting[cidx].type == "file") {
                    //进行编码
                    var enUrl = encodeURIComponent(file);
                    //合成url
                    var url = fileGlobleSetting[cidx].clickSetting["newone"].replace("[fileUrl]", enUrl);
                    //跳转
                    window.open("../../" + url);
                }
                //}
                //else{
                else {
                    //创建对应gadget实例
                    var gadgetName = fileGlobleSetting[cidx].gadget;
                    var app = FW.createApp(gadgetName, gadgetName, {
                        dom: "m_content"
                    });
                    app.param.fileUrl = file;
                    //显示到页面
                    this.param.main && this.param.main.addTopApp && this.param.main.addTopApp(app, fName, [gadgetName, gadgetName, {
                        dom: "m_content"
                    }]);
                }
                //}
                //去除蒙板层
                FW.unblockUI();
            },
            /**
            *@function
            *@memberOf fileView
            *@name FireEvent$compareOne
            *@description [功能]这里描述基本功能
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param srcFile
            *@param destFile
            */
            "compareOne": function(srcFile, recoveryFile) {
                //获取比较数据
                var exec = /([\s\S]+?)[\\\/]+([^\\\/]+$)/i.exec(srcFile);
                if (!exec) {
                    alert("历史文件错误无法恢复");
                    return;
                }
                var orgDir = exec[1];
                var orgFile = exec[2];
                //获取恢复的文件和路径
                var reDir = "/backup/" + srcFile;
                var reFile = recoveryFile;

                fileObj = {
                    lfDir: reDir + '/' + reFile,
                    rfDir: orgDir + '/' + orgFile
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
            'main': require("./resource/fileView/main.tpl"),
            'folderShow': require("./resource/fileView/folderShow.tpl"),
            'newDir': require("./resource/fileView/newDir.tpl"),
            'fileHistory': require("./resource/fileView/fileHistory.tpl"),
            'fileIcon': require("./resource/fileView/fileIcon.tpl"),
            'newFile': require("./resource/fileView/newFile.tpl"),
            'inputdir': require("./resource/fileView/inputdir.tpl"),
            'mask_upload2other': require("./resource/fileView/mask_upload2other.tpl")
        }

    },
    module, '0.06');
    return FW;
});