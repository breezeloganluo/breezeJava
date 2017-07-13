/**
* @namespace
* @name lex 
* @version 0.01 罗光瑜 词法分析类
0.02 罗光瑜 修改了解析注释的bug，支持单个除法符号，以及解析字符串的bug
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
                var result = {
                    type: 'e',
                    content: ""
                };
                var idx = this.MY.idx;
                var text = this.MY.text;
                var c = "";
                var n = "";
                //while(当前字符的位置小于字符串长度){
                while (idx < text.length) {
                    //读取当前位置的一个字符
                    c = text[idx];
                    //if(是字符数字或者_){记录结果中
                    if (/[0-9a-zA-Z_]/.test(c)) {
                        //把这个字符追加到结果变量中
                        result.type = 'l';
                        result.content += c;
                        //当前位置下移
                        ++idx;
                        this.MY.idx = idx;
                    }
                    //}
                    //else if(是"或'字符){调用字符串处理
                    else if (c == '"' || c == "'") {
                        //调用私有的parserString解析字符串
                        result = this.API.private('parserString', c);
                        //如果为空则返回空，否则返回这个字符串
                        if (result == null) {
                            return null;
                        } else {
                            return {
                                type: c == '"' ? 'S': 's',
                                content: "\"" + result + "\""
                            };
                        }
                    }
                    //}
                    //else if(是/){调用parserNote处理注释
                    else if (c == '/') {
                        //前面已经有内容，先返回内容，索引不变
                        if (result.content) {
                            return result;
                        }
                        //调用parserNote处理注释
                        result = this.API.private('parserNote');
                        //if (返回结果为空){这是普通除号
                        if (result == null) {
                            this.MY.idx++;

                            return {
                                type: "b",
                                content: "/"
                            };
                        }
                        //}
                        //else{正常注释情况
                        else {
                            n = text[idx + 1];
                            return {
                                type: n == "/" ? 'n': 'N',
                                content: result
                            };
                        }
                        //}
                    }
                    //}
                    //else if(是@符号){调用parserAt函数进行@解释
                    else if (c == '@') {
                        //调用parserAt并直接返回
                        var v = this.API.private('parserAt');
                        return {
                            type: '@',
                            content: v
                        };
                    }
                    //}
                    //else{空格或者回车的情况
                    else if (c == ' ' || c == '\r' || c == '\n' || c == '\t') {
                        //if(结果字符串还是空的){continue继续循环
                        if (!result.content) {
                            //位置下移动一位并continue
                            ++idx;
                            this.MY.idx = idx;
                        }
                        //}
                        //else{位置下移动并返回结果
                        else {
                            //指针下移动，并返回结果，表示读到了一个word的结束，然后结束循环
                            ++idx;
                            this.MY.idx = idx;
                            return result;
                        }
                        //}
                    }
                    //}
                    //else if(是一个符号){返回这个符号
                    else {
                        if (!result.content) {
                            //当前位置后移一位
                            //返回这个符号
                            ++idx;
                            this.MY.idx = idx;
                            return {
                                type: 'b',
                                content: c
                            };
                        } else {
                            return result;
                        }
                    }
                    //}
                }
                //}
                //返回结束情况
                return result;
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
                //返回的字符串的定义
                var str = "";
                //对于需要的类型
                var typeStr = type;
                var text = this.MY.text;
                var idx = this.MY.idx;
                //进入死循环，要求长度要小于整体长度  
                while (idx < text.length) {
                    //得到当前位置的下一个位置的字符
                    var str1 = text.charAt(idx + 1);
                    //先进行判断有没有转义字符，有的话，先进行逻辑判断
                    if (str1 == "\\") {
                        //如果有的话，取到这个字符的下一个，然后直接进入Continue
                        var str2 = text.charAt(idx + 2);
                        str = str + str1 + str2;
                        idx = idx + 2;
                        continue;
                    }
                    //进行判断是不是结束
                    if (str1 == type) {
                        //结束了的话，需要将位置执行当前位置
                        this.MY.idx = idx + 2;
                        return str;
                    }
                    //如果上面都没有匹配的话，就直接进入这里面，因为如果是带引号的直接上面就放回了
                    str = str + str1;
                    idx = idx + 1;
                }
                //如果进行到这一步，代表上面的都没有匹配的，也就是没有结束的逻辑，那么就需要返回NULL
                this.MY.idx = idx;
                return null;
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
                var text = this.MY.text;
                var idx = this.MY.idx;
                //用于接收需要返回的字符串
                var result = "";
                //因为当前调用的时候已经是/后面的一个字符
                ++idx;
                var zhenzhe = /\n/;
                //直接判断是属于哪一个
                while (idx < text.length) {
                    //如果是这种情况，我们需要一直判断到换行符
                    var str1 = text.charAt(idx);
                    if (zhenzhe.test(str1)) {
                        //如果进来了就代表是换行了，可以结束了
                        this.MY.idx = idx;
                        return result;
                    }
                    //如果不是这种情况，那么就需要保存这个字符
                    result = result + str1;
                    //并且下标加1
                    idx = idx + 1;
                }
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
                var text = this.MY.text;
                var idx = this.MY.idx;
                //用于接收需要返回的字符串
                var result = "";
                //因为当前调用的时候已经是/后面的一个字符
                var str = text.charAt(idx + 1);
                idx = idx + 2;
                var zhenzhe = /\n/;
                //直接判断是属于哪一个
                //如果是属于*号的情况下
                if (str == "*") {
                    while (idx + 1 < text.length) {
                        //如果是这一个，那么需要连续判断到下一个结束为止，
                        var str1 = text.charAt(idx);
                        var str2 = text.charAt(idx + 1);
                        if (str1 == "*" && str2 == "/") {
                            //是这种情况我们就代表结束了
                            this.MY.idx = idx + 2;
                            return result;
                        }
                        //如果不是这种情况，那么就需要保存这个字符
                        result = result + str1;
                        //并且下标加1
                        idx = idx + 1;
                    }
                }
                //如果是属于/的情况下
                if (str == "/") {
                    while (idx < text.length) {
                        //如果是这种情况，我们需要一直判断到换行符
                        var str1 = text.charAt(idx);
                        if (zhenzhe.test(str1)) {
                            //如果进来了就代表是换行了，可以结束了
                            this.MY.idx = idx;
                            return result;
                        }
                        //如果不是这种情况，那么就需要保存这个字符
                        result = result + str1;
                        //并且下标加1
                        idx = idx + 1;
                    }

                } else {
                    //进入这里面，代表上面2中情况都不是，那么久应该放回null了
                    return null;
                }
            }
        }
    },
    module);
    return FW;
});