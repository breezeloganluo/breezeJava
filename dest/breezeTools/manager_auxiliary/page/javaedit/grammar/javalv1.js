/**
* @namespace
* @name javalv1 
* @version 0.01 罗光瑜 初始版本设计
0.02 罗光瑜 每个私有的解析函数都增加日志，好知道程序解析代码去哪一步了
0.03 罗光瑜 parserClass函数中，{也要放入到每个被解析函数中
* @description  这是一个java文件的level1的分析类，这个类主要通过parser方法分析并返回一个半解析好的结构，其结构如下：
*[
*   {
*        type:"note/package/import/class/@",其中note表示注释
*        content:[],这里是这个类型的完整的详细内容，其内容是由词法分析返回的结果的数组，比如import com.go;词法分析将得到import com  .   go   ;这5个元素
*        children:[//只有type为class才会有，就是类里面的详细内容了
*            {
*             type:"var/function/note/@"
*             content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容，的词法数组
*             children:[
*                     {
*                             type:"code/comments",
*                             content:[]//对应类型的词法数组
*                     }
*             ]//如果是函数或者是变量，那么就是{}之间的内容
*             }
*        ]
*   }
*]
*语法分析，主要是调用lex的词法分析的每一个词进行一个个的分析                      
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    require("./lex");
    FW.register({
        "name": "javalv1",
        /**
        *@function
        *@memberOf javalv1
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //创建词法分析类
            this.MY.lex = FW.createApp("lex", "lex", this);
        },
        "public": {
            /**
            *@function
            *@memberOf javalv1
            *@name public$parser
            *@description [功能]这是主分析类，将输入的文本分析出一个结构体
            *[思路]利用词法分析以及基本的语法分析法则，递归调用完成语法分析
            *[接口.this.MY.lex]词法分析实例
            *@param text 要分析的文本
            *@return [
            {
            type:"note/package/import/class/@",其中note表示注释
            content:[],这里是这个类型的完整的详细内容，其内容是由词法分析返回的结果的数组，比如import com.go;词法分析将得到import com  .   go   ;这5个元素
            children:[//只有type为class才会有，就是类里面的详细内容了
            {
            type:"var/function/note/@"
            content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容
            children:[]//如果是函数或者是变量，那么就是{}之间的内容
            }
            ]
            }
            ]
            */
            "parser": function(text) {
                //初始化词法分析类
                this.MY.lex = FW.createApp("lex", "lex", this);
                this.MY.lex.setText(text);
                //声明并初始化结果变量
                var result = [];
                var item = {};
                var content = null;
                //while(true){无限循环
                while (true) {
                    //用词法分析读一个词
                    item = this.MY.lex.parserOne();
                    //如果这个是结束词，则返回结果
                    if (item == null || item.type == 'e') {
                        return result;
                    }
                    //if(读到的词是package，类型是l){分析package情况
                    if (item.type == 'l' && item.content == 'package') {
                        //调用parserPackage，并将返回值放入结果中
                        content = this.API.private('parserPackage');
                        //--注意，为空要返回空，表示语法错误
                        if (content == null) {
                            console.error("package is null");
                            return null;
                        }
                        result.push(content);
                    }
                    //}
                    //else if(读到的词是import，类型是l){调用私有方法进行import处理
                    else if (item.type == 'l' && item.content == 'import') {
                        //调用parserImport，并将返回值放入结果中
                        content = this.API.private('parserImport');
                        //--注意，为空要返回空，表示语法错误
                        if (content == null) {
                            console.error("import is null");
                            return null;
                        }
                        result.push(content);
                    }
                    //}
                    //else if(读到的词是public,类型是l){调用私有函数进行class处理
                    else if (item.type == 'l' && item.content == 'public') {
                        //调用parserClass，并把返回值放入到结果中
                        content = this.API.private('parserClass');
                        //--注意，为空要返回空，表示语法错误
                        if (content == null) {
                            console.error("parserClass is null");
                            return null;
                        }
                        result.push(content);
                    }
                    //}
                    //else if(读到的词类型是n或者N或者@){处理注释情况
                    else if (item.type == 'n' || item.type == 'N' || item.type == '@') {
                        //直接构造结构对象并放入结果中
                        result.push({
                            type: 'note',
                            content: [item]
                        });
                    }
                    //}
                    //else{出错了，直接返回
                    else {
                        //直接return返回null
                        console.error("else not type found");
                        return null;
                    }
                    //}
                }
                //}
                //将结果返回
                return result;
            }
        },
        "private": {
            /**
            *@function
            *@memberOf javalv1
            *@name private$parserPackage
            *@description [功能]解析package情况，就是类似package com.d.a;
            *[思路]调用词法分析，获取一个个的分词，直到;结束
            *[接口.this.MY.lex]词法分析类
            *@return {
            type:"package",
            content:[词法分析的关于package部分的词法结果数组]
            }
            */
            "parserPackage": function() {
                console.log("\r\nparserPackage:");
                var content = [];
                var item = {};
                //从lex中读取字符片段，直到遇到分号或出错
                while (true) {
                    item = this.MY.lex.parserOne();
                    //出错了
                    if (item == null) {
                        return null;
                    }
                    //遇到分号，本次package结束
                    if (item.content == ';') {
                        content.push(item);
                        break;
                    } else {
                        content.push(item);
                    }
                }
                //return
                var result = {
                    type: 'package',
                    content: content
                };

                console.log(result);
                return result;
            },
            /**
            *@function
            *@memberOf javalv1
            *@name private$parserImport
            *@description [功能]解析package情况，就是类似import com.d.a;
            *[思路]调用词法分析，获取一个个的分词，直到;结束
            *[接口.this.MY.lex]词法分析类
            *@return {
            type:"import",
            content:[词法分析的关于package部分的词法结果数组]
            }
            */
            "parserImport": function() {
                console.log("\r\nparser import");
                var content = [];
                var item = {};
                //从lex中读取字符片段，直到遇到分号或出错
                while (true) {
                    item = this.MY.lex.parserOne();
                    //出错了
                    if (item == null) {
                        return null;
                    }
                    //遇到分号，本次import结束
                    if (item.content == ';') {
                        content.push(item);
                        break;
                    } else {
                        content.push(item);
                    }
                }
                //return
                var result = {
                    type: 'import',
                    content: content
                };

                console.log(result);
                return result;
            },
            /**
            *@function
            *@memberOf javalv1
            *@name private$parserClass
            *@description [功能]完成解析class部分
            *[思路]class是一个复杂结构，所以分开两部分处理，第一部分分析class的声明部分，第二部分分析{后面的内容}
            *[接口.this.MY.lex]用于分析的词法解析类
            *@return {
            type:"class",
            content:[{以前的class的声明部分的词法解析结果数组],
            children:[
            {
            每个lv1元素，每一个代表一个函数，或者成员变量
            }
            ]
            }
            */
            "parserClass": function() {
                console.log("\r\n 准备嵌套parser class");
                //声明并初始化结果变量
                var result = {
                    type: 'class',
                    content: [],
                    children: []
                };
                var item = {};
                //--之前的public也要先写入结果中
                result.content.push('public');
                //block(块){分析{以前的类声明部分
                //while(true){一直循环
                while (true) {
                    //读入一个词
                    item = this.MY.lex.parserOne();
                    //if(如果是{类型是b){break出循环
                    if (item.content == '{') {
                        //class头部解析完毕
                        break;
                    }
                    //}
                    //else if(词结束){出错了返回错误
                    else if (item.type == 'e') {
                        //toDo
                        console.error("解析头部 词结束");
                        return null;
                    }
                    //}
                    //else if(类型是l的内容){接受并放入到结果中
                    else if (item.type == 'l') {
                        //toDo
                        result.content.push(item);
                    }
                    //}
                    //else{出错了返回null
                    else {
                        //toDo
                        console.error("解析头部 其他出错了");
                        return null;
                    }
                    //}
                }
                //}
                //}
                //block(块){类内部分析
                //声明并初始化一个临时存放class里面的children对象，类型为var
                //--itemInner在children里面
                var children = [];
                var itemInner = {
                    type: 'var',
                    content: [],
                    children: []
                };
                var isVar = false;
                //while(true){无限循环
                while (true) {
                    //读取一个词
                    item = this.MY.lex.parserOne();
                    //初始化处理
                    if (item == null) {
                        return null;
                    }
                    if (item.content == '=') {
                        isVar = true;
                    }
                    //if(读到(类型为b){确定为函数类型, 括号的前面出现过等号就认定为是变量的赋值
                    if (isVar == false && item.content == '(' && item.type == 'b') {
                        //改类型为函数，并记录到临时变量的content中
                        itemInner.type = 'function';
                        itemInner.content.push(item);
                    }
                    //}
                    //else if(读到@){记录@
                    else if (item.type == '@') {
                        //如果临时变量不为空，那么将临时变量结束，记录到结果中
                        if (itemInner && itemInner.content && itemInner.content.length > 0) {
                            children.push(itemInner);
                            itemInner = {};
                        }
                        //再将@内容记录到结果中
                        itemInner.type = '@';
                        itemInner.content = [];
                        itemInner.content.push(item);
                        children.push(itemInner);
                        //清空初始化临时变量
                        itemInner = {
                            type: "var",
                            content: [],
                            children: []
                        };
                        isVar = false;
                    }
                    //}
                    //else if(读到n/N){这是注释情况
                    else if (item.type == 'n' || item.type == 'N') {
                        //如果临时变量不为空，那么僵临时变量结束，记录到结果中
                        if (itemInner && itemInner.content && itemInner.content.length > 0) {
                            children.push(itemInner);
                            itemInner = {};
                        }
                        //将注释内容记录到结果中
                        itemInner.type = 'note';
                        itemInner.content = [];
                        itemInner.content.push(item);
                        children.push(itemInner);
                        //清空临时记录
                        itemInner = {
                            type: "var",
                            content: [],
                            children: []
                        };
                        isVar = false;
                    }
                    //}
                    //else if(读到;类型是b){ 估计是成员变量结束了
                    else if (item.content == ';') {
                        //将;计入临时变量
                        itemInner.content.push(item);
                        //将临时变量加入结果中
                        children.push(itemInner);
                        //清空并初始化临时记录
                        itemInner = {
                            type: "var",
                            content: [],
                            children: []
                        };
                        isVar = false;
                    }
                    //}
                    //else if(读到{){调用parserKuohao处理括号内容
                    else if (item.content == '{') {
                        //调用parserKuohao的结果放入临时变量的children中
                        //--反括号本身也要放入进去，不然后续java解析会出错
                        itemInner.content.push(item);
                        itemInner.children = this.API.private('parserKuohao');
                        //将临时变量放入结果中
                        children.push(itemInner);
                        //清空初始化临时变量
                        itemInner = {
                            type: "var",
                            content: [],
                            children: []
                        };
                        isVar = false;
                    }
                    //}
                    //else if(读到}){表示类结束
                    else if (item.content == '}') {
                        //返回结果
                        if (itemInner.content && itemInner.content.length > 0) {
                            children.push(itemInner);
                        }
                        break;
                    }
                    //}
                    //else if(读到结束符){退出表示出错
                    else if (item.type == 'e') {
                        //return null
                        console.error("解析body 词结束了，应该是最后一个函数}没有闭合，请检查日志");
                        return null;
                    }
                    //}
                    //else{存起来
                    else {
                        itemInner.content.push(item);
                    }
                    //}
                }
                //}
                //}
                result.children = children;

                console.log("finished class");
                return result;
            },
            /**
            *@function
            *@memberOf javalv1
            *@name private$parserKuohao
            *@description [功能]解析括号内容，将返回一个函数体或者对象体的切分对象，切分内容是以注释为间隔，将函数体切分成多段，注意，返回结果不再有{}字符了
            *[思路]不断的词法解析，用一个计数器记录{和}的匹配情况，当读到词法中多出的}就返回
            *[接口.this.MY.lex]词法解析器
            *@return [
            {
            type:"code/comments",//代码类型或者是注释类型，其实只要不是注释类型就是代码类型，这里是用注释作为间隔将整个函数体切分成一段段的结果
            content:[]//对应实体的词法数组
            }
            ]
            */
            "parserKuohao": function() {
                console.log("\r\nparser kuohao");
                //结果
                var result = [];
                //记录括号个数，{+1, }-1，直到个数变为0表示遇到最外层匹配的}
                var kuohaoCount = 1;
                //切分对象
                var item = {
                    type: null,
                    content: []
                };
                var rd = {};
                //循环读取lex，直到遇到结束括号或出错
                while (true) {
                    rd = this.MY.lex.parserOne();
                    //遇到错误
                    if (rd == null) {
                        return null;
                    }
                    //遇到注释了，保存上一片code
                    //--注意，pushitem后一定要重新创建一个对象，不能重用，否则数组中的对象都是旧的
                    if (rd.type == 'n' || rd.type == 'N') {
                        if (item.type) {
                            result.push(item);
                            item = {
                                type: null,
                                content: []
                            };
                        }
                        //保存这一次的注释
                        item.type = 'comment';
                        item.content = [rd];
                        result.push(item);
                        //清空item，开始记录下一段code
                        item = {
                            type: null,
                            content: []
                        };
                    } else {
                        if (item.type == null) {
                            item.type = 'code';
                        }
                        if (rd.content == '}') {
                            kuohaoCount = kuohaoCount - 1;
                            //遇到匹配括号了，结束读取
                            if (kuohaoCount == 0) {
                                result.push(item);
                                break;
                            }
                            item.content.push(rd);
                        } else if (rd.content == '{') {
                            kuohaoCount = kuohaoCount + 1;
                            item.content.push(rd);
                        } else {
                            item.content.push(rd);
                        }
                    }
                }
                //return
                console.log(result);
                return result;
            }
        }
    },
    module);
    return FW;
});