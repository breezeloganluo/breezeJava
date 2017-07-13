<form>
     <a href="#" onclick="FireEvent.gotmpMain()">测试parserJavaClassFunctionBody</a><br/>
     默认内容的代码：<br/>
     public static void main(String[] args)throws Exception{
     <br/>
     <textarea id="insrc">
/**
            *@function
            *@memberOf javalv2
            *@name private$parserJavaClass
            *@description [功能]这里要将java的类体的结构，根据lv1的结构全部解析出来
            *[思路]这里分两个阶段，主函数只解析主内容，下面子类容，再调用对应的子函数进行解析
            *[接口.this.MY.xxx]这里描述内部全局变量定义
            *[接口.service.pkg.name.param]{这里描述服务pkg.name要传入参数内容}
            *[接口.service.pkg.name.return]{这里描述doserver返回值的json的结构}
            *@param lv1Struct 对于java的结构
            {
            type:"class",其中note表示注释
            content:[],这里是这个类型的完整的详细内容，其内容是由词法分析返回的结果的数组，比如import com.go;词法分析将得到import com  .   go   ;这5个元素
            children:[//只有type为class才会有，就是类里面的详细内容了
            {
            type:"var/function/note/@"
            content:[];//和父亲部分的content结构是一样的，如果是函数，或者是变量，那么就是{以前的内容
            children:[]//如果是函数或者是变量，那么就是{}之间的内容
            }
            ]
            }
            *@return {
            name: "MyClass",
            extends:[
            "MyGadget"
            ],
            implements:["abc"],
            attributeFragment:{
            参数部分
            },
            functionFragment:{
            函数部分
            }
            }
            }
            */
     </textarea><br/>
     dest:<br/>
     <textarea id="result">
     </textarea><br/>
      <a href="#"  onclick="FireEvent.parserJavaClassFunctionComment('insrc','result')">提交</a>
</form>