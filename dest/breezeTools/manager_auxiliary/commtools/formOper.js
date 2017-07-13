/**
* @namespace
* @name formOper 
* @version 0.01 罗光瑜 初稿
0.02 罗光瑜 列表删除一行时，保存原来的值
* @description  这是个表单的生成器，支持按照表单的的结构，生成输入框的表单模式，还有列表页的列表模式：
*格式类似如下：
*{
*        "package": {
*            name: "包名",
*        },
*        name: {
*            name: "类名",
*            type: "Text"
*        },
*        comments: {
*            name: "类说明",
*            type: "TextArea"
*        },
*        include: {
*            name: "import",
*            type: "List",
*            valueRange: ["Text"]
*        },
*        "extends": {
*            name: "extends",
*            type: "List",
*            valueRange: ["Text"]
*        },
*        "implements": {
*            name: "implements",
*            type: "List",
*            valueRange: ["Text"]
*        },
*        version: {
*            name: "版本信息",
*            type: "List",
*            valueRange: [{
*                author: {
*                    name: "作者",
*                    type: "Text",
*                },
*                description: {
*                    name: "描述",
*                    type: "Text"
*                },
*                version: {
*                    name: "版本",
*                    type: "Text"
*                }
*            }]
*        }
*不同的呈现类型，采用不同视图实现      
*扩展模式一下，不同的类型描述可以有扩展函数，通用的就是用fun成员，这个在名字部分，响应onClick方法
*其他的就要在字段内部和不同类型约定好了，比如selector的onchage方法。
*这里的事件，都建议用原生态的onxxx方法表示事件    
*/
define(function(require, exports, module) {
    var FW = require("breeze/framework/js/BreezeFW");
    FW.register({
        "name": "formOper",
        /**
        *@function
        *@memberOf formOper
        *@name onCreate$onCreate
        *@description
        */
        "onCreate": function() {
            //toDO
        },
        "public": {
            /**
            *@function
            *@memberOf formOper
            *@name public$showForm
            *@description [功能]将表单内容显示到页面上
            *[思路]按照不同的类型逐个显示视图，注意视图采用反调用模式，几个参数要加入缓存，便于后面重新show的时候使用。
            *注意，数据为空的时候要传入{}如果不填，则认为使用缓存数据
            *@param targetId 表单目标target
            *@param metadata 表单元数据：
            {
            field:{
            name:"名称",
            type:"Text/select...",
            valueRange:"[{a:b}]"
            }
            }
            *@param data 表单数据，可选
            */
            "showForm": function(targetId, metadata, data) {
                //处理参数缓存
                if (targetId) {
                    this.MY.targetId = targetId;
                } else {
                    targetId = this.MY.targetId;
                }

                if (metadata) {
                    this.MY.metadata = metadata;
                } else {
                    metadata = this.MY.metadata;
                }

                if (data) {
                    this.MY.data = data;
                } else {
                    data = this.MY.data;
                }
                //直接显示主视图
                var showData = data || {};
                this.API.show("mainForm", {
                    m: metadata,
                    d: showData
                },
                targetId);
            },
            /**
            *@function
            *@memberOf formOper
            *@name public$getFormData
            *@description [功能]获取form的数据，根据每个表单的名称，将其转换成对应的json数据，其中name就是json的间隔符号
            *[思路]调用基本的lang函数实现转换功能，用this.MY中targetid去获取
            *@param fieldName 可以为空，则获取所有数据，
            可以为字符串，就获取该字符下的所有表单内容
            可以是一个字符串列表，表示值能获取这个字段的值
            */
            "getFormData": function(fieldName) {
                //获取结果数据
                var result = FW.use().getFormValue(this.MY.targetId);
                //根据参数返回不同的结果
                if (fieldName == null) {
                    return result;
                }
                var tmpResult = {};
                if (/string/i.test(typeof(fieldName))) {
                    tmpResult[fieldName] = result[fieldName];
                    return tmpResult;
                }

                for (var i = 0; i < fieldName.length; i++) {
                    tmpResult[fieldName[i]] = result[fieldName[i]];

                }
                return tmpResult;
            }
        },
        "private": {
            /**
            *@function
            *@memberOf formOper
            *@name private$createFormType
            *@description [功能]用对应类型创建表单
            *[思路]根据不同表单类型使用不同的视图显示
            *@param field 字段名
            *@param metadata 元数据
            *@param data 实际数据
            *@return 表单的内容html片段
            */
            "createFormType": function(field, metadata, data) {
                //处理由反向列表valueRange单值情况的值
                if (/string/i.test(typeof(metadata))) {
                    var tmp = metadata;
                    metadata = {
                        name: field,
                        type: tmp
                    }
                }
                //拼接类型
                var type = "form_" + metadata.type;
                return this.API.show(type, {
                    f: field,
                    m: metadata,
                    d: data || ""
                },
                "-");
            },
            /**
            *@function
            *@memberOf formOper
            *@name private$parserOneData
            *@description [功能]解析一个数据，当是空的时候，返回空字符串，否则返回实际字符
            *@param p 输入的参数
            */
            "parserOneData": function(p, t) {
                //判断类型
                if (t == "Text" && /string/i.test(typeof(p))) {
                    p = p.replace(/"/ig, "&quot;");
                }
                //写值
                if (/String/i.test(typeof(p))) {
                    return p;
                }
                return "";
            },
            /**
            *@function
            *@memberOf formOper
            *@name private$hidden
            *@description [功能]隐藏或显示对应的内容区
            *@param field 字段名
            */
            "hidden": function(field) {
                //获取对应的dom
                var dom = $("#" + this.MY.targetId).find("#content_" + field);
                //控制是否显示
                if ("none" == dom.css("display")) {
                    dom.show();
                } else {
                    dom.hide();
                }
            }
        },
        "FireEvent": {
            /**
            *@function
            *@memberOf formOper
            *@name FireEvent$addOneLine
            *@description [功能]添加新的一行
            *[思路]MVC的思想，添加新的一行，然后重新show一遍
            *@param field 新字段的名称
            */
            "addOneLine": function(field) {
                //获取原来的数据
                var orgData = this.MY.data;
                this.MY.data = this.getFormData();

                if (this.MY.data[field] == null) {
                    this.MY.data[field] = [];
                }
                this.MY.data[field].push({});

                this.showForm();
            },
            /**
            *@function
            *@memberOf formOper
            *@name FireEvent$delOneLine
            *@description [功能]删除掉一行
            *[思路]将一行内容删除掉
            *@param tableField 代表这个表单的field字段名
            */
            "delOneLine": function(tableField) {
                //获取所有被选中的索引
                var allIdx = [];
                $("#tableFeld_" + tableField).find("input:checkbox:checked").each(function() {
                    allIdx.push(Number($(this).attr("idx")));
                });
                //删除数据
                //--因为删除要改版数组，所以要从后往前删除
                this.MY.data = this.getFormData();
                for (var i = allIdx.length - 1; i >= 0; i--) {
                    this.MY.data[tableField].splice(allIdx[i], 1);
                }
                //重新显示
                this.showForm();
            },
            /**
            *@function
            *@memberOf formOper
            *@name FireEvent$fun
            *@description [功能]扩展点击功能，点击后调用本类的公有方法，私有方法，最后是类调用者的公有方法
            *[思路]这里描述实现的基本思路
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param funName 函数名
            *@param fieldName 被调用的字段名
            */
            "fun": function(funName, fieldName) {
                //尝试公有方法
                if (this[funName]) {
                    this[funName](fieldName);
                    return;
                }
                //尝试私有方法
                if (this.private[funName]) {
                    this.API.private(funName, fieldName);
                }
                //尝试调用拥有者的函数
                if (this.owner && this.owner[funName]) {
                    this.owner[funName](fieldName);
                }
            }
        },
        view: {
            'mainForm': require("./resource/formOper/mainForm.tpl"),
            'form_Text': require("./resource/formOper/form_Text.tpl"),
            'form_TextArea': require("./resource/formOper/form_TextArea.tpl"),
            'form_List': require("./resource/formOper/form_List.tpl"),
            'form_Select': require("./resource/formOper/form_Select.tpl"),
            'form_ReadOnly': require("./resource/formOper/form_ReadOnly.tpl")
        }

    },
    module);
    return FW;
});