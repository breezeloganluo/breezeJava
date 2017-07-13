/**
* @namespace
* @name seqmgr 
* @version 0.01 罗光瑜 初始版本
0.02 罗光瑜 顺序图列表，显示的最新的场景描述字段，而不是思路字段
* @description  这个类根据某个输入的文件，然后将其所有相关的seq图全部显示出来进行统一的管理       
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "seqmgr",
        /**
        *@function
        *@memberOf seqmgr
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf seqmgr
            *@name public$showContent
            *@description [功能]显示主页面
            *[思路]
            */
            "showContent": function() {
                var searchFile = this.param.fileUrl;
                if (searchFile == null) {
                    searchFile = FW.use().load("seqmgrurl", true);
                } else {
                    FW.use().save("seqmgrurl", searchFile, true);
                }
                var allData = this.API.private('getAllSeqInfo');
                var showData = this.API.private('parserFile', searchFile, allData);

                this.API.show("main", {
                    orgFile: searchFile,
                    seq: showData
                });
            }
        },
        "private": {
            /**
            *@function
            *@memberOf seqmgr
            *@name private$getAllSeqInfo
            *@description [功能]获取所有的顺序图，
            *[思路]
            *1.如果有缓存从缓存中取，用session级别的缓存
            *2.如果没缓存，从服务器端取到大文件，自己过滤和整理，用同步方法
            *[接口.getAllSeqInfo.seqmgr.param]{}
            *[接口.getAllSeqInfo.seqmgr.param.return]{
            *    code:结果码,
            *    data:[
            *          {
            *               "fileName":文件内容
            *          }
            *    ]
            *}
            *@return {
            "fileName":seq的内部结构
            }
            */
            "getAllSeqInfo": function() {
                //如果缓存存在，直接读取并返回
                var result = FW.use().load("seqmgr");
                if (result != null) {
                    return result;
                }
                //发消息到服务器上获取文件内容
                var data = this.API.doServer("getAllSeqInfo", "seqmgr");
                if (data.code != 0) {
                    FW.alert(data.data || "获取数据失败");
                    return null;
                }
                //遍历整理内容
                result = {};
                for (var n in data.data) {
                    var nn = ("/" + n).replace(/[\\\/]+/ig, "/");
                    try {
                        var content = eval("(" + data.data[n] + ")");
                        result[nn] = content;
                    } catch(e) {}
                }
                //放入缓存中
                FW.use().save("seqmgr", result);
                //返回
                return result;
            },
            /**
            *@function
            *@memberOf seqmgr
            *@name private$parserFile
            *@description [功能]将所有文件过滤出来，形成一个可以show的文档
            *[思路]根据输入的文件名，还有传入的结构，进行文件匹配
            *@param destFile 目标文件
            *@param allSeq {
            文件名路径:seq中的文件结构
            }
            *@return [
            {
            fileName:"sel的文件名，转义后的",
            fileDesc:"相关的场景描述"
            }
            ]
            */
            "parserFile": function(destFile, allSeq) {
                //初始化变量
                var result = [];
                var lfile = ("/" + destFile).replace(/[\/\\]+/ig, "/");
                //while(每一个顺序图){找节点
                for (var n in allSeq) {
                    //获取描述
                    var desc = allSeq[n].note && allSeq[n].note.scene || "";
                    //while(每一个note){目录匹配
                    for (var i = 0; i < allSeq[n].node.length; i++) {
                        //获取一个
                        var one = allSeq[n].node[i];
                        var rfile = ("/" + one.dir + "/" + one.name).replace(/[\/\\]+/ig, "/");
                        //匹配成功就加入结果
                        if (lfile == rfile) {
                            result.push({
                                fileName: n,
                                fileDesc: desc
                            });
                        }
                    }
                    //}
                }
                //}
                //返回结果
                return result;
            },
            /**
            *@function
            *@memberOf seqmgr
            *@name private$changeDisplayName
            *@description [功能]将字符串转换成中文
            *[思路]只是用@替代％的url编码而已
            *@param name 输入的文件信息
            *@return 转换后的用于显示的文字
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
            *@memberOf seqmgr
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
                    if (one.name == "顺序图管理") {
                        continue;
                    }
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
            *@memberOf seqmgr
            *@name FireEvent$openSeq
            *@description [功能]打开一个顺序图文件
            *[思路]使用url重定向即可
            *@param fileUrl 顺序图所在文件路径
            */
            "openSeq": function(fileUrl) {
                //合成url
                var enUrl = encodeURIComponent(("/design/" + fileUrl).replace(/[\\\/]+/ig, "/"));
                //合成url
                var url = "sequenceCreator.jsp?fileUrl=" + enUrl;
                //跳转
                window.open("../../" + url);
            },
            /**
            *@function
            *@memberOf seqmgr
            *@name FireEvent$goEdit
            *@description [功能]去编辑
            *[思路]这里实现编辑功能
            *@param fileName 文件名
            */
            "goEdit": function(fileName, pidx) {
                //合成被打开的文件路径字符串
                var dir = fileName;
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
            }
        },
        view: {
            'main': require("./resource/seqmgr/main.tpl"),
            'fileIcon': require("./resource/seqmgr/fileIcon.tpl")
        }

    },
    module);
    return FW;
});