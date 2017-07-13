/**
* @namespace
* @name componentMgr 
* @version v0.01 罗光瑜 初始版本
0.02 罗光瑜 增加文件浏览功能
* @description  组件管理功能，向weima发起服务请求，获取组件信息。该框架要被主框架main.js调用，所以要实现公有方法
*showContent，调用this.param.main.setTitle(this.param.dataArr, this.param.idx, "你妹", this.param.tagIdx, this.param.tagPath);可以刷新主框架上分页栏的标题名字                                      
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("../framework/fileView");
    FW.register({
        "name": "componentMgr",
        "param": {
            /**
            *@memberOf componentMgr
            *@name host
            *@description 默认情况下的下周域名
            */
            "host": "http://www.joinlinking.com/",
            /**
            *@memberOf componentMgr
            *@name compressdir
            *@description 解压缩的路径
            */
            "compressdir": "/manager_auxiliary/data/componentMgr/"
        },
        /**
        *@function
        *@memberOf componentMgr
        *@name onCreate$onCreate
        *@description 初始化相应的参数操作
        *[接口.this.MY.oper]当前的正在进行的操作，给showContent使用
        *[接口.this.MY.operParam]当前正在进行的操作的参数，给showContent使用
        */
        "onCreate": function() {
            //初始化参数
            this.MY.oper = "showAllComponent";
            //初始化文件对象
            var pageParam = {
                id: 'fileselect',
                dom: this.dom,
                param: {
                    viewid: "--"
                },
                view: {}

            }
            this.MY.fileOper = FW.createApp("fileselect", "fileselect", pageParam);
            //创建文件管理视图
            var param = {
                id: "prjFileMgr",
                dom: "prjFileMgr"
            }
            this.MY.prjFileMgr = FW.createApp("prjFileMgr", "fileView", param);
            this.MY.prjFileMgr.param.main = this;

            param = {
                id: "componentFileMgr",
                dom: "componentFileMgr"
            }
            this.MY.componentFileMgr = FW.createApp("componentFileMgr", "fileView", param);
            this.MY.componentFileMgr.param.main = this;
            this.MY.file2copy = {};
        },
        "public": {
            /**
            *@function
            *@memberOf componentMgr
            *@name public$showContent
            *@description [功能]被主窗口调用的入口方法，用于显示当前状态下的内容
            */
            "showContent": function() {
                //根据状态显示不同的视图
                this[this.MY.oper](this.MY.operParam);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$showOne
            *@description [功能]显示其中一个组件详情
            *[思路]
            *1.通过cid和后台交互，获取某一个组件的基本信息。参见showAllComponent调用doServer请求proxy.getRemote而其中的参数param = {
            *        name: "one",
            *        package: "res",
            *        param: {
            *            cid:参数中的cid
            *        }
            *    }
            *   返回值的对象类型为：
            *   {
            *     cid:''
            *     title:标题
            *     dtype:类型
            *     resDesc：资源描述,
            *     resLink:资源的链接，后续下载文件操作中要使用
            *   }
            *2.调用this.MY.fileOper获取组件列表，判断组件状态，是否已经被解加压缩过，判断标准就是对应目录下是否存在被解压的目录名。
            *解压的目录放置在：/manager_auxiliary/data/componentMgr/下面
            *解压的目录名为：c_[cid]_[title名称]
            *this.MY.fileOper调用方法：先调用fileOper，设置要查询的目录名
            *再调用：queryDir方法，查询目录下的所有子目录和文件名，返回值是一个对象{"文件名":"类型"}类型说明了文件还是目录，其值为：dir/file
            *遍历目录后要判断，当前组件是否处于已下载状态
            *3.最后显示的视图参见：manager_auxiliary/page/componentMgr/page/detail.jsp，这可以考取片段用作视图部分
            *   原型参见：design/SRS/@E7@BB@84@E4@BB@B6/detail.jsp
            *   显示的视图名为oneComponent
            *   注意，“管理组件”按钮一定是当前组件已经除以解压状态后才显示
            *@param cid 组件对应的cid
            */
            "showOne": function(cid) {
                //基本赋值
                _this = this;
                this.MY.oper = "showOne";
                this.MY.operParam = cid;

                var param = {
                    name: "one",
                    package: "res",
                    param: {
                        cid: cid
                    }
                }
                //根据CID查询详细信息
                var result = this.API.doServer("getRemote", "proxy", param);
                if (result == null || result.code != 0) {
                    FW.alert("调用服务端出错");
                }
                var data = result.data[0];
                this.MY.oneData = data;
                //block(代码块){遍历服务器，看是否已经被压缩
                //设置要查找的文件夹路径
                _this.MY.fileOper.setPath(this.param.compressdir);
                //查询该路径下的所有文件夹和文件
                var dirFile = _this.MY.fileOper.queryDir();
                //拼接要查询的文件夹名字
                var tempName = "c_" + data.cid + "_" + data.title;
                //遍历所有文件夹目录
                var flag = false;
                //for (返回的文件目录){检查目录是否已经解压缩过
                for (var fileName in dirFile) {
                    //获取基本变量
                    var object = dirFile[fileName];
                    //如果名字相符就设置标识并退出循环
                    if (object.type == "D" && fileName == tempName) {
                        flag = true;
                    }
                }
                //}
                //设置标识
                data.isUnZip = flag;
                if (data.isUnZip) {
                    data.zipDir = this.param.compressdir + tempName;
                }
                //}
                //显示详情
                this.API.show("oneComponent", data, null);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$downUnzip
            *@description [功能]下载并解压当前组件，并重新显示单个组件
            *[思路]调用两个服务请求，分别完成组件下载，和zip解压缩
            *[service.file.download]文件下载。参数：
            *{
            *    url:远程地址，在参数url中获取,
            *    fileName:下载后的文件名，有拼接部分，参见showOne,地址为/manager_auxiliary/data/componentMgr/c_[cid]_[title].zip
            *}
            *返回值就表示成功失败
            *[service.file.unzip]对某个zip文件解压参数为：
            *{
            *    zip:zip文件目录，第一次doServer发起file.download下载下来的文件
            *    dir:解压到和文件名同名的目录中
            *}
            *返回结果如下：
            *返回的是该目录解压后的目录文件列表:
            *{
            *     fileName:false,
            *     dir:{
            *          fileName2
            *     }
            *}
            *这里如果发现返回的结果中，第一级对象仍然有.zip文件，则继续解压，解压规则同上，将目录命名为何zip文件同名的目录名
            *这里全部用同步调用即可。
            *同时在所有成功后，重新调用showOne函数，再重新显示页面
            *@param url 要下载的文件地址
            *@param cid 组件对应的cid
            *@param title 标题
            *@param zipChartset zip的字符集
            *@return toDo
            *@example toDO
            */
            "downUnzip": function(url, cid, title, zipChartset) {
                //基本赋值
                _this = this;
                var path = "/manager_auxiliary/data/componentMgr/";
                var rootPath = path + "c_" + cid + "_" + title + "/";
                var zipPath = path + "c_" + cid + "_" + title + ".zip";
                var param = {
                    url: url,
                    fileName: zipPath
                }
                //先创建目录
                //--先创建目录，这样即使下载失败，也能进入管理页面进行管理
                this.API.doServer("addComponerDir", "component", {
                    dir: rootPath
                });
                //发起远程查询下载文件
                var result = this.API.doServer("download", "file", param);

                if (result == null || result.code != 0) {

                    FW.alert("下载文件失败");
                    _this.showOne(cid);
                    return;
                }
                //block(代码块){解压
                //合成解压参数
                var zipParam = {
                    zip: zipPath,
                    dir: rootPath,
                    chartset: zipChartset
                }
                //发起远程查询解压下载得到的文件
                var zipResult = this.API.doServer("unzip", "file", zipParam);
                console.info(zipResult);
                //_this.unzipFile(zipResult, rootPath);
                var tmpResult = this.API.private('unzipFile', zipResult, rootPath, zipChartset);
                if (tmpResult == 0) {
                    FW.alert("解压成功");
                }
                //}
                //重新显示
                _this.showOne(cid);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$showOneMgr
            *@description [功能]显示其中的一个管理页面
            *[思路]显示其中一个管理界面
            *@param cid 这个cid
            */
            "showOneMgr": function(cid) {
                //处理获取的数据
                //--如果缓存中没有数据，则重新请求
                var data = this.MY.oneData;
                if (data == null || data.cid != cid) {
                    var param = {
                        name: "one",
                        package: "res",
                        param: {
                            cid: cid
                        }
                    }

                    var result = this.API.doServer("getRemote", "proxy", param);
                    if (result == null || result.code != 0) {
                        FW.alert("调用服务端出错");
                    }
                    data = result.data[0];
                    data.zipDir = "/manager_auxiliary/data/componentMgr/c_" + data.cid + "_" + data.title;
                    this.MY.oneData = data;

                }
                //显示
                this.API.show("oneMgr");
                this.MY.prjFileMgr.showContent();
                this.MY.componentFileMgr.setInitDir(data.zipDir);
                this.MY.componentFileMgr.showContent();
                this.API.private('showFile2Copy');
                //显示表单信息
                this.showOneModify();
                //显示已解压列表
                this.showDecompressList();
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$fileViewSelectFile
            *@description [功能]接受内部调用，选中一个文件的处理函数
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.file2copy.src]设定右边的被拷贝的源头
            *[接口.this.MY.file2copy.dest]设定左边要拷贝的源头
            *@param filedir 文件的全路径
            *@param app 发起请求的app
            */
            "fileViewSelectFile": function(filedir, app) {
                //处理数据
                if (/componentFileMgr/.test(app.id)) {
                    this.MY.file2copy.src = filedir;
                }

                if (/prjFileMgr/.test(app.id)) {
                    this.MY.file2copy.dest = filedir;
                }

                this.MY.selectedFile = filedir;
                //显示
                this.API.private('showFile2Copy');
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$viewOneFile
            *@description [功能]查看某个文件，并显示出来
            *[思路]调用file空间进行文件查询，调用文本组件，进行文本显示，文本显示因为不用编辑，直接用textArea即可
            *@param filePath 文件路径
            */
            "viewOneFile": function(filePath) {
                //分离目录和文件
                var execResult = /(.*)[\\\/]([^\\\/]+$)/.exec(filePath);
                if (execResult == null) {
                    FW.alert("无法解析文件");
                    return;
                }

                var dir = execResult[1] || "/";
                var fileName = execResult[2];
                //发送请求获取文件文本
                this.MY.fileOper.setPath(dir);
                this.MY.fileOper.setFileName(fileName);
                var text = this.MY.fileOper.queryFileContent();
                //将其显示出来
                this.API.show("fileContentPathView", null, "onefileLeftButtom");
                $("#fileContentView").val(text);
                $("#fileContentPathView").html(filePath);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$showDecompressList
            *@description [功能]显示解压缩的文件列表
            *[思路]获取指定目录下的列表信息，然后显示出来，show到第四个窗体中
            */
            "showDecompressList": function() {
                //查询文件列表
                this.MY.fileOper.setPath(this.param.compressdir);
                var result = this.MY.fileOper.queryDir();
                //处理结果数据
                //--把路径名和cid解析出来
                //--结构是 "c_" + data.cid + "_" + data.title;
                var showData = [];
                for (var n in result) {
                    if (result[n].type != "D") {
                        continue;
                    }

                    var execResult = /^c_(\d+)_(.+)/.exec(n);
                    if (execResult == null) {
                        continue;
                    }

                    showData.push({
                        cid: execResult[1],
                        name: execResult[2]
                    });
                }
                //显示
                this.API.show("decompresslist", showData, "decompressed");
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$showOneModify
            *@description [功能]单独显示一个组件，但是给的是输入框允许被修改
            *[思路]根据存入的组件内存信息，进行显示
            */
            "showOneModify": function() {
                //直接显示
                this.API.show("oneFileModify", {
                    type: "m",
                    data: this.MY.oneData
                },
                "onefileLeftButtom");
                FW.use().setFormValue("oneDetail", this.MY.oneData);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name public$showAllComponent
            *@description [功能]显示所有组件的页面
            *[思路]参数是查询条件，如果为null则是查询所有的组件
            *@param condition 查询条件
            *@param start 开始索引
            */
            "showAllComponent": function(condition, start) {
                //处理参数
                limit = 10;
                start = start || 0;
                //记录当前操作
                this.MY.oper = "showAllComponent";
                this.MY.operParam = condition;
                //发起远程查询
                var param = {
                    name: "query",
                    package: "res",
                    param: {
                        key: "",
                        dtype: "--",
                        isBreeze: "--",
                        start: start * limit,
                        limit: limit
                    }
                }

                if (condition != null) {
                    param.param = condition;
                    param.param.start = start * limit;
                    param.param.limit = limit;
                }

                var result = this.API.doServer("getRemote", "proxy", param);
                param.name = "queryCount"
                var count = this.API.doServer("getRemote", "proxy", param);
                result.data.count = parseInt((parseInt(count.data[0].total) - 1) / limit) + 1;
                result.data.start = start;
                //显示内容
                this.API.show("list", result.data);
                //显示表单
                if (condition != null) {
                    condition.key = condition.key.replace(/(^%+)|(%+$)/ig, "");
                    FW.use().setFormValue("searchForm", condition);

                }
            }
        },
        "private": {
            /**
            *@function
            *@memberOf componentMgr
            *@name private$unzipFile
            *@description [功能]遍历返回结果进行处理
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param result 解压获取的返回结果
            *@param rootPath 解压的目录的根路径
            *@param zipChartset zip时文件名的字符集
            *@return toDo
            *@example toDO
            */
            "unzipFile": function(result, rootPath, zipChartset) {
                //参数赋值和校验
                _this = this;

                if (result == null) {
                    FW.alert("解压文件失败,无返回结果");
                    return 999;
                }
                if (result.code != 0) {
                    FW.alert("解压文件失败");
                    return result.code;
                }
                //block(代码块){ //处理解压后的返回结果
                var datas = result.data;
                //for(循环处理解压后的结果){
                for (var fileName in datas) {
                    var object = datas[fileName];
                    //if (是文件类型){处理文件
                    if (object == "true" || object == true) {
                        var tempName = new RegExp('.zip');
                        if (tempName.test(fileName)) {

                            var nowPath = fileName.replace(/\..*$/i, "");
                            var zipPath = rootPath + fileName;
                            var zipRootPath = rootPath + nowPath + "/";

                            var param = {
                                zip: zipPath,
                                dir: zipRootPath,
                                chartset: zipChartset
                            }
                            var nowresult = _this.API.doServer("unzip", "file", param);
                            this.API.private('unzipFile', nowresult, zipRootPath, zipChartset);

                        }
                    }
                    //}
                    //else{递归调用,继续解析目录内容
                    else {
                        var tmpResult = this.API.private('unzipFile', {
                            code: 0,
                            data: object
                        },
                        rootPath + fileName + "/", zipChartset);
                        if (tmpResult != 0) {
                            return tmpResult;
                        }
                    }
                    //}
                }
                //}
                //返回成功
                return 0;
                //}
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name private$showFile2Copy
            *@description [功能]显示要拷贝的文件内容
            */
            "showFile2Copy": function() {
                //获取数据
                var src = this.MY.file2copy && this.MY.file2copy.src || "";
                var dest = this.MY.file2copy && this.MY.file2copy.dest || "";
                if (src.length > 20) {
                    src = "..." + src.substr(src.length - 17);
                }
                if (dest.length > 20) {
                    dest = "..." + dest.substr(dest.length - 17);
                }
                //显示
                this.API.show("file2copy", {
                    src: src,
                    dest: dest
                },
                "file2copy");
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$showOne
            *@description [功能]用户点击“详情”按钮后显示单个组件的详情内容
            *[思路]调用同名公有方法实现，点击事件在视图list中设置。
            *这个用户操作的完整流程参见：manager_auxiliary/sequenceCreator.jsp?fileUrl=%2Fdesign%2Fdesign%2F%40E7%40BB%4084%40E4%40BB%40B6%2Fclick2Detail.js
            *@param cid cid
            */
            "showOne": function(cid) {
                _this = this;
                //toDo
                if (cid == '') {
                    FW.alert("获取组件CID失败");
                    return;
                }
                _this.showOne(cid);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$downUnzip
            *@description [功能]下载并解压当前组件，并重新显示单个组件
            *[思路]调用两个服务请求，分别完成组件下载，和zip解压缩
            *[service.file.download]文件下载。参数：
            *{
            *    url:远程地址，在参数url中获取,
            *    fileName:下载后的文件名，有拼接部分，参见showOne,地址为/manager_auxiliary/data/componentMgr/c_[cid]_[title].zip
            *}
            *返回值就表示成功失败
            *[service.file.unzip]对某个zip文件解压参数为：
            *{
            *    zip:zip文件目录，第一次doServer发起file.download下载下来的文件
            *    dir:解压到和文件名同名的目录中
            *}
            *返回结果如下：
            *返回的是该目录解压后的目录文件列表:
            *{
            *     fileName:false,
            *     dir:{
            *          fileName2
            *     }
            *}
            *这里如果发现返回的结果中，第一级对象仍然有.zip文件，则继续解压，解压规则同上，将目录命名为何zip文件同名的目录名
            *这里全部用同步调用即可。
            *同时在所有成功后，重新调用showOne函数，再重新显示页面
            *@param url 要下载的文件地址
            *@param cid 本组件的cid
            *@param title 文件名标题
            */
            "downUnzip": function(url, cid, title) {
                _this = this;
                var chartset = $("#unzipchartset").val();
                if (!/^http/i.test(url)) {
                    if (/^[\\\/]?res/i.test(url)) {
                        url = "http://breezecomponent.oss-cn-shenzhen.aliyuncs.com/" + url;
                    } else {
                        url = this.param.host + url;
                    }
                }
                url = url.replace(/[\\\/]+/ig, "/");
                url = url.replace(/^http:\/+/i, "http://");
                _this.downUnzip(url, cid, title, chartset);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$showList
            *@description [功能]直接显示列表
            *[思路]调用对应的公有方法实现
            */
            "showList": function() {
                //显示
                this.showAllComponent();
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$showOneMgr
            *@description [功能]显示单个内容
            *[思路]调用同名公有方法
            *@param cid 这个cid
            */
            "showOneMgr": function(cid) {
                //调用公有方法
                this.showOneMgr(cid);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$copy2Left
            *@description [功能]从右边拷贝到左边
            *[思路]根据内存中设定的参数，还有fireView控件完成右边到左边的拷贝工作
            *[接口.this.MY.file2copy.src]设定被拷贝的文件
            *[接口.this.MY.file2copy.dest]设定要被拷贝的文件
            */
            "copy2Left": function() {
                //获取文件
                var src = this.MY.file2copy.src;
                var dest = this.MY.file2copy.dest;
                if (src == null || dest == null || /^\s*$/.test(src) || /^\s*$/.test(dest)) {
                    FW.alert("原信息或目标信息没有设定，请先选中");
                    return;
                }
                var srcFileObj = this.MY.componentFileMgr;
                var destFileObj = this.MY.prjFileMgr;
                //设定被拷贝区域的粘贴内容
                var execResult = /(.+?)[\\\/]([^\\\/]*$)/.exec(src);
                var srcFile = "",
                srcDir = "/";
                if (execResult != null) {
                    srcFile = execResult[2];
                    srcDir = execResult[1];
                }
                //进行拷贝
                destFileObj.setCopyCut(srcFile, srcDir);
                destFileObj.parse(dest);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$viewOneFile
            *@description [功能]点击后，查看某个具体的文件
            *[思路]调用同名的公有方法实现，本方法重点是获取对应的文本路径
            */
            "viewOneFile": function() {
                //获取参数
                if (this.MY.selectedFile == null) {
                    FW.alert("请选中要查看的文件");
                    return;
                }
                //检查是否是文件
                if (!/[\\\/]\w+\.\w+$/.test(this.MY.selectedFile)) {
                    FW.alert("不能查看目录");
                    return;
                }
                this.viewOneFile(this.MY.selectedFile);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$modifyOne
            *@description [功能]修改详细信息内容
            *[思路]使用表单的通用操作获取表单数据，然后通过代理设置修改信息
            */
            "modifyOne": function() {
                //获取数据
                var data = FW.use().getFormValue("oneDetail");
                for (var n in this.MY.oneData) {
                    if (data[n] == null) {
                        data[n] = this.MY.oneData[n];
                    }
                }
                var modifyVersion = (data.version != this.MY.oneData.version);
                data.resLink = "/res/res_" + data.cid + "/v" + data.version + ".zip";
                //整理提交参数
                var param = {
                    name: modifyVersion ? "upgrade": "modify",
                    package: "res",
                    param: data
                }

                console.log(param);
                //提交修改
                var result = this.API.doServer("getRemote", "proxy", param);
                if (result == null || result.code != 0) {
                    FW.alert("修改失败");
                    return;
                }
                this.MY.oneData = null;
                if (modifyVersion) {
                    FW.alert("修改成功\n您已经修改了版本号，请重新上传组件。");
                } else {
                    FW.alert("修改成功\n您未修改版本号，无需上传组件，重新上传组件则会将原来覆盖。");
                }
                this.showOneMgr(data.cid);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$showOneModify
            *@description [功能]显示一个字段信息内容
            *[思路]调用对应的公有函数，进行显示
            */
            "showOneModify": function() {
                //toDo
                this.showOneModify();
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$zipupload
            *@description [功能]将对应的文件目录里面的内容打包然后上传
            *[思路]这里传入的是cid,title,version到服务端，服务端根据cid就可以找到文件目录，然后根据文件目录进行打包，然后发到无服务器上传阿里云。
            *原始的这些参数从this.MY.oneData中获取
            */
            "zipupload": function() {
                //整理参数
                var param = {
                    cid: this.MY.oneData.cid,
                    title: this.MY.oneData.title,
                    version: this.MY.oneData.version
                }
                //提交服务器 
                var isFinished = false;
                this.API.doServer("zipupload", "component", param,
                function(code, data) {
                    isFinished = true;

                    var result = this.API.doServer("zipuploadprogress", "component");
                    $("#uploadwaitprogress").html(result.data);

                    if (code != 0) {
                        if (code != 40) {
                            $("#uploadwaitprogress_complete").html("操作失败<br/>" + result.data + "<br/><input class='btn btn-default' type='button' value='确定' onclick='FW.unblockUI();'>");
                        }
                        if (code == 40) {
                            $("#uploadwaitprogress_complete").html("上一次上传还未结束，上次流程结果如下，不能启动新的事务，点击继续查看上次事务结果如上<br/><input class='btn btn-default' type='button' value='确定' onclick='FW.unblockUI();window.open(\"../../server/component/zipuploadprogress.jsp\")'>");
                        }
                        return;
                    }

                    $("#uploadwaitprogress_complete").html("操作成功<br/><input class='btn btn-default' type='button' value='确定' onclick='FW.unblockUI();'>");

                });
                this.API.mask("uploadWait", null, 600, 200);

                var _this = this;
                var progress = function() {
                    if (isFinished) {
                        return;
                    }

                    var result = _this.API.doServer("zipuploadprogress", "component");
                    $("#uploadwaitprogress").html(result.data);

                    setTimeout(progress, 2000);

                }

                progress();
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$showNewComponent
            *@description [功能]添加新组建
            */
            "showNewComponent": function() {
                //直接显示表单
                this.API.show("oneFileModify", {
                    type: "new"
                });
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$addOne
            *@description [功能]添加一个新的组件，插入数据库，在添加文件目录
            */
            "addOne": function() {
                //获取数据
                var data = FW.use().getFormValue("oneDetail");
                //数据校验
                if (data.title == null || data.title == "") {
                    FW.alert("请必须填写标题");
                    return;
                }
                if (data.version == null || data.version == "") {
                    FW.alert("请填写版本信息");
                    return;
                }

                if (data.keyword == null || data.keyword == "") {
                    FW.alert("请填写关键字信息");
                    return;
                }

                if (data.vdesc == null || data.vdesc == "") {
                    FW.alert("请填写版本信息");
                    return;
                }

                if (data.sDesc == null || data.sDesc == "") {
                    FW.alert("请填写简述信息");
                    return;
                }
                //添加数据
                var param = {
                    name: "add",
                    package: "res",
                    param: data
                }

                var result = this.API.doServer("getRemote", "proxy", param);
                if (result == null || result.code != 0) {
                    FW.alert("修改失败");
                    return;
                }
                //添加文件目录
                var cid = result.data[0][1];
                var dir = "/manager_auxiliary/data/componentMgr/c_" + cid + "_" + data.title;
                var res = this.API.doServer("addComponerDir", "component", {
                    dir: dir
                });

                if (res.code != 0) {
                    alert("失败");
                }
                //显示单个管理页面
                this.showOneMgr(cid);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$copy2Right
            *@description [功能]将文件拷贝到右端组件区域
            *[思路]主要是调用fileView控件实现
            */
            "copy2Right": function() {
                //获取文件
                var src = this.MY.file2copy.dest;
                var dest = this.MY.file2copy.src;
                if (src == null || dest == null || /^\s*$/.test(src) || /^\s*$/.test(dest)) {
                    FW.alert("原信息或目标信息没有设定，请先选中");
                    return;
                }
                var srcFileObj = this.MY.prjFileMgr;
                var destFileObj = this.MY.componentFileMgr;
                //设定被拷贝区域的粘贴内容
                var execResult = /(.+?)[\\\/]([^\\\/]*$)/.exec(src);
                var srcFile = "",
                srcDir = "/";
                if (execResult != null) {
                    srcFile = execResult[2];
                    srcDir = execResult[1];
                }
                //进行拷贝
                destFileObj.setCopyCut(srcFile, srcDir);
                destFileObj.parse(dest);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$search
            *@description [功能]进行条件查询
            *@param page 访问的页码
            */
            "search": function(page) {
                //获取查询条件
                var searchData = FW.use().getFormValue("searchForm");
                searchData.key = "%" + searchData.key + "%";
                //记录查询条件
                this.MY.queryCondiction = searchData;
                //查询并显示
                this.showAllComponent(searchData, 0);
            },
            /**
            *@function
            *@memberOf componentMgr
            *@name FireEvent$goPage
            *@description [功能]页面的翻页功能
            *@param idx 第几页
            */
            "goPage": function(idx) {
                //获取参数
                this.MY.queryCondiction;
                if (this.MY.queryCondiction != null) {
                    this.MY.queryCondiction.key = this.MY.queryCondiction.replace(/(^%*)|(%*$)/ig, "%");
                }
                //调用公有函数显示
                this.showAllComponent(this.MY.queryCondiction, idx);
            }
        },
        view: {
            'list': require("./resource/componentMgr/list.tpl"),
            'oneComponent': require("./resource/componentMgr/oneComponent.tpl"),
            'oneMgr': require("./resource/componentMgr/oneMgr.tpl"),
            'file2copy': require("./resource/componentMgr/file2copy.tpl"),
            'decompresslist': require("./resource/componentMgr/decompresslist.tpl"),
            'fileContentPathView': require("./resource/componentMgr/fileContentPathView.tpl"),
            'oneFileModify': require("./resource/componentMgr/oneFileModify.tpl"),
            'uploadWait': require("./resource/componentMgr/uploadWait.tpl")
        }

    },
    module);
    return FW;
});