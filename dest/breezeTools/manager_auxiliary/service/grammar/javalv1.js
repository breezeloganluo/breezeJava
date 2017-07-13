/**
* @namespace
* @name javalv1 
* @version 0.01 罗光瑜 初始版本设计
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
                //声明并初始化结果变量
                //while(true){无限循环
                //用词法分析读一个词
                //如果这个是结束词，则返回结果
                //if(读到的词是package，类型是l){分析package情况
                //调用parserPackage，并将返回值放入结果中
                //--注意，为空要返回空，表示语法错误
                //}
                //else if(读到的词是import，类型是l){调用私有方法进行import处理
                //调用parserImport，并将返回值放入结果中
                //--注意，为空要返回空，表示语法错误
                //}
                //else if(读到的词是public,类型是l){调用私有函数进行class处理
                //调用parserClass，并把返回值放入到结果中
                //--注意，为空要返回空，表示语法错误
                //}
                //else if(读到的词类型是n或者N或者@){处理注释情况
                //直接构造结构对象并放入结果中
                //}
                //else{出错了，直接返回
                //直接return返回null
                //}
                //}
                //将结果返回
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
                //toDo
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
                //toDo
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
                //声明并初始化结果变量
                //--之前的public也要先写入结果中
                //block(块){分析{以前的类声明部分
                //while(true){一直循环
                //读入一个词
                //if(如果是{类型是b){break出循环
                //toDo
                //}
                //else if(词结束){出错了返回错误
                //toDo
                //}
                //else if(类型是l的内容){接受并放入到结果中
                //toDo
                //}
                //else{出错了返回null
                //toDo
                //}
                //}
                //}
                //block(块){类内部分析
                //声明并初始化一个临时存放class里面的children对象，类型为var
                //while(true){无限循环
                //读取一个词
                //if(读到(类型为b){确定为函数类型
                //改类型为函数，并记录到临时变量的content中
                //}
                //else if(读到@){记录@
                //如果临时变量不为空，那么僵临时变量结束，记录到结果中
                //再将@内容记录到结果中
                //清空初始化临时变量
                //}
                //else if(读到n/N){这是注释情况
                //如果临时变量不为空，那么僵临时变量结束，记录到结果中
                //将注释内容记录到结果中
                //清空临时记录
                //}
                //else if(读到;类型是b){估计是成员变量结束了
                //将;计入临时变量
                //将临时变量加入结果中
                //清空并初始化临时记录
                //}
                //else if(读到{){调用parserKuohao处理括号内容
                //调用parserKuohao的结果放入临时变量的children中
                //将临时变量放入结果中
                //清空初始化临时变量
                //}
                //else if(读到}){表示类结束
                //返回结果
                //}
                //else if(读到结束符){退出表示出错
                //return null
                //}
                //}
                //}
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
                //toDo
            }
        }
    },
    module);
    return FW;
});