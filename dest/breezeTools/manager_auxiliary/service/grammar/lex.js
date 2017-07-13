/**
* @namespace
* @name lex 
* @version 0.01 罗光瑜 词法分析类
* @description  这是一个专用于词法分析的类，不做别的只做词法分析。
*所谓词法分析就是扫描文本中的每一个字符，将单词，间隔符分别一个一个的返回回去
*单词简单，关键是字符串和注释，要求能够完整个体的返回   
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "lex",
        /**
        *@function
        *@memberOf lex
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf lex
            *@name public$setText
            *@description [功能]设置要被解释的文本
            *[思路]将文本记录到内存文件中，同时初始化字符所在文本的位置
            *[接口.this.MY.text]被分析的文本
            *[接口.this.MY.idx]正在被分析的当前位置
            *@param text 输入的text文件
            */
            "setText": function(text) {
                //设置文本内容
                this.MY.text = text;
                this.MY.idx = 0;
            },
            /**
            *@function
            *@memberOf lex
            *@name public$parserOne
            *@description [功能]从当前字母索引开始往后，解析其中一个词或者一个间隔符，有如下几种情况：
            *1.如果是一个词，字母或者下划线，或者数字，就直接返回
            *2.如果是一个符号，任何符号除"/，都直接返回
            *3.如果是注释，包括//注释和/ *就把整个注释内容返回
            *4.如果是字符串，包括"还是'就把整个字符串内容返回
            *[思路]逐个字符遍历，内部做一个字符变量，如果碰到是连续的字符就累加到这个变量中，直到下一个不是字符或者数字或者_就把这个累计的内容返回出去，如果当前是符号，判断一下不是注释和字符串就直接返回。如果是字符串或者注释，就调用私有的对应的字符串和注释解析函数返回对应的内容。
            *[接口.this.MY.text]被分析的文本
            *[接口.this.MY.idx]正在被分析的字符串（this.MY.text）当前位置，也就是当前每次调用就是从当前这个位置开始往后分析字符串
            *@return {
            type:'l/s/S/n/N/b/@/e',其中l代表字符，s表示'的字符串，S表示"的字符串,n表示//的注释N表示/*的注释,b代表符号,e代表分析结束,@代表java的注解，这是content是空的
            content:"实际的内容"
            
            }
            */
            "parserOne": function() {
                //声明结果变量
                //while(当前字符和位置小于字符串长度){
                //读取当前位置的一个字符
                //if(是字符数字或者_){记录结果中
                //把这个字符追加到结果变量中
                //当前位置下移
                //}
                //else if(是"或'字符){调用字符串处理
                //调用私有的parserString解析字符串
                //如果为空则返回空，否则返回这个字符串
                //}
                //else if(是/){调用parserNote处理注释
                //调用parserNote处理注释
                //如果为空则返回空，否则返回函数返回的注释内容
                //}
                //else if(是@符号){调用parserAt函数进行@解释
                //调用parserAt并直接返回
                //}
                //else if(是一个符号){返回这个符号
                //当前位置后移一位
                //返回这个符号
                //}
                //else{空格或者回车的情况
                //if(结果字符串还是空的){continue继续循环
                //位置下移动一位并continue
                //}
                //else{位置下移动并返回结果
                //指针下移动，并返回结果
                //}
                //}
                //}
                //返回结束情况
            }
        },
        "private": {
            /**
            *@function
            *@memberOf lex
            *@name private$parserString
            *@description [功能]返回整个字符串内容，注意入口是当前字符位置是引号后的，返回内容后，当前位置也将指向闭合引号后的位置，另外当前字符串可能是"也可能是'这个是由输入参数决定的
            *[思路]逐个字符扫描，然后找到另外一个"或者'(由输入的type参数决定),注意引号的转义符号\"这个不是闭合字符，另外也要注意空字符串，这个时候就返回空字串。返回的内容是不带引号的。如果解析发现没有闭合（返现回车换行前没有闭合），那么就返回null
            *[接口.this.MY.text]被分析的文本
            *[接口.this.MY.idx]正在被分析的字符串（this.MY.text）当前位置，也就是当前每次调用就是从当前这个位置开始往后分析字符串
            *@param type 字符串类型"/'
            *@return 一个不带引号的字符串内容，如果语法错误，没有闭合，那么就直接返回null
            */
            "parserString": function(type) {
                //toDo
            },
            /**
            *@function
            *@memberOf lex
            *@name private$parserNote
            *@description [功能]解析注释内容
            *[思路]调用时，应该是/后面一个字符的位置，根据这个字符是*还是/判断当前注释是//注释还是/*注释（如果不是这两者，就直接返回错误），并且解析剩余内容，把剩余整个注释返回。如果是//注释，返回的是一行的内容，就是一直解析到回车换行为止，如果是/*注释则要找到闭合的注释字符。这里也必须一个一个字符的找，当结束后，字符串当前位置是指向注释后的一个字符
            *[接口.this.MY.text]被分析的文本
            *[接口.this.MY.idx]正在被分析的字符串（this.MY.text）当前位置，也就是当前每次调用就是从当前这个位置开始往后分析字符串
            */
            "parserNote": function() {
                //toDo
            },
            /**
            *@function
            *@memberOf lex
            *@name private$parserAt
            *@description [功能]解析java里头比较特殊的@符号，就是解析一行内容
            *[思路]调用时指向@后的一个字符，调用后，指向下一个字符，本解析以回车换行为结束
            *[接口.this.MY.text]被分析的文本
            *[接口.this.MY.idx]正在被分析的字符串（this.MY.text）当前位置，也就是当前每次调用就是从当前这个位置开始往后分析字符串
            *@return @的内容
            */
            "parserAt": function() {
                //toDo
            }
        }
    },
    module);
    return FW;
});