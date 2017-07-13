/**
* @namespace
* @name codeEditor 
* @version 0.01 罗光瑜 初始版本
* @description  这个类是一个代码的编辑类，主要是封装了相关的代码编辑器              
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "codeEditor",
        /**
        *@function
        *@memberOf codeEditor
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf codeEditor
            *@name public$getToken
            *@description [功能]获取要匹配和替换的一个词
            *[思路]如果是.那么token的字符要改变
            *@param editor 编辑器对象
            *@param cur 当前对象值
            *@return 返回token对象
            */
            "getToken": function(editor, cur) {
                //返回token值
                var token = editor.getTokenAt(cur);
                if (token.string == ".") {
                    token.start += 1;
                }
                return token;
            },
            /**
            *@function
            *@memberOf codeEditor
            *@name public$getTipsArray
            *@description [功能]获取提示信息数组
            *[思路]这个类是通过函数调用，获取能给做提示信息的字符串数组
            *这个函数实际是有多个算法的，这里最基本的算法，就是依据两个对象：
            *baseStruct对象:{
            *   a:{
            *        b:"true"
            *   }
            *}
            *这个基本对象就是进行冒泡提示的基本数据，他按照层次（.做分割）的结构，其每个对象的成员名就是其冒泡提示的结构名。
            *一层一层下来，显示每一层对应可返回的提示。
            *另外还有一个结构体，就是varExp这个保存了变量到类型的转换，因为变量本身是没有层次结构的，只有固定了的类型才会有，所以这里有这样一个对象进行转换，varExp中的成员名就是变量名，而其值，就是在baseStruct中登记的类型结构名。
            *另外，类型结构也可以以非完整方式补充，比如成员名对应的值是false，表示这个类型的层级结构没有加载完毕。
            *透出接口，获取的这些信息，都从函数this.MY.xxx中获取，而这个些个函数，都是showtext中，由上层业务代码给出
            *@param line 输入一行的信息
            *@param cur 当前光标信息
            {
            line:行号
            ch:所在字符位置
            }
            */
            "getTipsArray": function(line, cur) {
                //计算有效判断字符串
                //--比如要过滤出a.b.c
                var dealWord = line;
                if (line.length != 0 && (line.length == cur.ch || /[^\w]/.test(line.charAt(cur.ch)))) {
                    var leftLine = line.substring(0, cur.ch);
                    var execResult = /[^\w\.\(]*([\w\.(]+)\t?$/.exec(leftLine);
                    if (execResult == null) {
                        return [];
                    }
                    var dealWord = execResult[1];
                }
                //准备好提示词字典对象
                //--两个，一个是匹配词，对象为结构,成员名就是关键词，而成员值，true表示已经完成，false，表示还未完成
                //--另一个是替换词，用于变量，变量的内部成员结构，需要根据其变量类型来判断，成员名就是变量，其值代表类型
                var baseWordStruct = this.MY.getBaseTipsStruct && this.MY.getBaseTipsStruct() || {};

                var varExp = this.MY.getVar && this.MY.getVar() || {};
                //block(块){匹配有效提示词
                //把被匹配词按.变成分词
                //--objPath是记录对象路径分词的结构，记录结构是走curStruct线，而不是用实际的words记录
                var words = dealWord.split(/[\.\(]/);
                var resultList = [];
                var curStruct = baseWordStruct;
                var objPath = "";
                //for (每一个分词){按照情况匹配
                for (var i = 0; i < words.length; i++) {
                    //获取其中一个成员
                    var one = words[i];
                    //if(分词只有一个的情况){匹配完变量，再匹配词结构
                    if (words.length == 1) {
                        for (var n in varExp) {
                            if (n.indexOf(one) == 0) {
                                resultList.push(n);
                            }
                        }
                        for (var n in baseWordStruct) {
                            if (n.indexOf(one) == 0) {
                                resultList.push(n);
                            }
                        }
                    }
                    //}
                    //else{多个分词，多次匹配
                    else {
                        //如果是第一个，进行变量替换
                        //--即将匹配是否是变量，如果是变量将分词转换成类型
                        if (i == 0) {
                            if (varExp[one]) {
                                one = varExp[one];
                            }
                        }
                        //如果curStruct对象是字符串了，进行换词
                        //--如果是第二个，支持this.xxx然后换词，换词后，从头开始
                        //--注意，这里有点绕的，curStruct是指向one的母亲级别的，所以这个分支切换后，是不用continue的
                        if (/string/i.test(typeof(curStruct))) {
                            objPath = curStruct + ".";
                            curStruct = baseWordStruct[curStruct];

                        }
                        //如果curStruct是false表示数据还未获取
                        //--调用业务代码，获取远程数据
                        //--curStruct就是返回的对象，这里给curStruct赋值即可，而总对象赋值的事情，外部函数内部处理
                        if (curStruct === false) {
                            objPath = objPath.replace(/\.$/, "");
                            curStruct = this.MY.getOneTipsStruct(objPath);
                        }
                        //如果curStruct为空则返回了
                        if (curStruct == null) {
                            break;

                        }
                        //if(匹配上){进入下一个
                        if (curStruct[one] != null) {
                            //继续
                            curStruct = curStruct[one];
                            objPath += (one + ".");
                            continue;
                        }
                        //}
                        //else if(匹配不上，且不是最后一个){尝试的去发请求获取，因为有可能是静态方法或调用
                        if (curStruct[one] == null && i < words.length - 1) {
                            if (curStruct[one] === null) {
                                break;
                            }
                            if (i == 0) {
                                curStruct[one] != false;
                                objPath += (one + ".");
                                curStruct = false;
                                continue;
                            }
                            //返回空数组
                            break;;
                        }
                        //}
                        //else if(匹配不上，且是最后一个){按照子串获取数组
                        if (curStruct[one] == null && i == words.length - 1) {
                            //循环这层结构，并且子串匹配并返回
                            for (var n in curStruct) {
                                if (n.indexOf(one) == 0 || one == "") {
                                    resultList.push(n);
                                }
                            }
                            break;
                        }
                        //}
                    }
                    //}
                }
                //}
                //}
                //返回结果
                return resultList;
            },
            /**
            *@function
            *@memberOf codeEditor
            *@name public$getText
            *@description [功能]获取文本内容
            *[思路]调用组件返回信息
            */
            "getText": function() {
                //返回对应的值
                return this.MY.editor.getValue();
            },
            /**
            *@function
            *@memberOf codeEditor
            *@name public$showText
            *@description [功能]显示对应文本的代码编辑器
            *[思路]调用codemirror的函数实现
            *@param code 代码部分
            *@param type 要显示代码的文本类型
            *@param funs 透给组件的调用的上层业务函数，比如获取提示数组等
            *@param spesLine 特殊行，可以将该行进行高亮显示
            */
            "showText": function(code, type, funs, spesLine) {
                //注册外部函数
                if (funs) {
                    for (var n in funs) {
                        this.MY[n] = funs[n];
                    }
                }
                //显示文本
                var domid = "myCodeMirror";
                this.API.show("main", {
                    i: domid,
                    d: code
                });
                //转换成编辑器
                var modName = type || "javascript";
                this.MY.editor = CodeMirror.fromTextArea(document.getElementById(domid), {
                    lineNumbers: true,
                    extraKeys: {
                        "Ctrl-Space": "autocomplete"
                    },
                    mode: {
                        name: modName,
                        globalVars: true
                    }
                });
                this.MY.editor.setSize("100%", "100%");
                //准备各种函数和变量
                var Pos = CodeMirror.Pos;
                var _this = this;
                var _editor = null;
                var _changes = null;
                //注册提示信息
                CodeMirror.registerHelper("hint", "javascript", javascriptHint);
                //注册提示判断函数
                function javascriptHint(editor, options) {
                    return _this.API.private('getJavaHint', editor, options);

                };
                //注册提示事件
                this.MY.editor.on('change',
                function(editor, changes) {
                    _editor = editor;
                    _changes = changes;
                    if (changes.origin == "complete") {
                        return;
                    }
                    if (changes.text[0] == '\t' || changes.text[0] == '.' || changes.text[0] == '(') {
                        if (changes.text[0] == '\t') {
                            var cur = editor.getCursor();
                            editor.replaceRange("", changes.from, CodeMirror.Pos(changes.to.line, changes.to.ch + 1), changes.to);

                        }
                        _this.MY.editor.showHint();

                    }

                });
                //ctrl+s时的操作按钮
                this.MY.editor.on("keydown",
                function(Editor, Eevent) {
                    if (83 == Eevent.keyCode) {
                        if (Eevent.ctrlKey || Eevent.altKey) {
                            _this.MY.save && _this.MY.save();
                        }
                    }
                });
            },
            /**
            *@function
            *@memberOf codeEditor
            *@name public$spesLine
            *@description [功能]高亮显示某一行
            *[思路]调用codeMirror的方法实现
            *@param line 高亮显示的行号
            *@param color 颜色，默认为红色
            *@param clearOther 是否清除以前的mark
            */
            "spesLine": function(line, color, clearOther) {
                //处理清除行为
                if (clearOther) {
                    var markArr = this.MY.editor.getAllMarks();
                    for (i = 0; i < markArr.length; i++) {
                        markArr[i].clear();
                    }
                }
                //获取行信息
                var col = color || "red";
                var realLine = Number(line) - 1;
                var lineStr = this.MY.editor.getLine(realLine);
                var lineLen = lineStr.length;
                var css = "background-color:" + col;
                //高亮显示
                this.MY.editor.markText({
                    line: realLine,
                    ch: 0
                },
                {
                    line: realLine,
                    ch: lineLen
                },
                {
                    css: css
                });
            }
        },
        "private": {
            /**
            *@function
            *@memberOf codeEditor
            *@name private$getJavaHint
            *@description [功能]获取java的提示信息数组
            *[思路]参考原来的scriptHint函数实现
            *原来的代码在add-one中的hint的javascript-hint.js
            *@param editor 编辑器对象
            *@param option 选项信息，
            *@return 返回对应的处理数组
            */
            "getJavaHint": function(editor, option) {
                //设定基础变量
                var Pos = CodeMirror.Pos;
                var cur = editor.getCursor();
                //获取当前词
                var token = this.getToken(editor, cur);
                //获取行
                var line = editor.getLine(cur.line);
                //获取返回数组
                var typArr = this.getTipsArray(line, cur);
                //返回结果
                var result = {
                    list: typArr,
                    from: Pos(cur.line, token.start),
                    to: Pos(cur.line, token.end)
                };
                return result;
            }
        },
        view: {
            'main': require("./resource/codeEditor/main.tpl")
        }

    },
    module);
    return FW;
});