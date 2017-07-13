<form>
     <a href="#" onclick="FireEvent.goParserJavaClassFunction()">临时测试</a>|<a href="#" onclick="FireEvent.goParserJavaClassFunctionComment()">临时测试注释</a><br/>
     默认内容的代码：<br/>
     //abd<br/>
     String a = 20;<br/>
     ff;<br/>
     //if(adf){<br/>
     if(sdf){<br/>
     sdfff<br/>

      }<br/>
      //}<br/>
     <textarea id="insrc">
     [{
        type: "comments",
        content: [{
            type: "n",
            content: "abd"
        }]
    },

    {
        type: "code",
        content: [{
            type: "l",
            content: "String"
        },
        {
            type: "l",
            content: "a"
        },
        {
            type: "b",
            content: "="
        },
        {
            type: "l",
            content: "20"
        },
        {
            type: "b",
            content: ";"
        },
        {
            type: "l",
            content: "ff"
        },
        {
            type: "b",
            content: ";"
        }]
    },
    {
        type: "comments",
        content: [{
            type: "n",
            content: "block(adf){"
        }]
    },

    {
        type: "code",
        content: [{
            type: "l",
            content: "if"
        },
        {
            type: "b",
            content: "("
        },
        {
            type: "l",
            content: "sdf"
        },
        {
            type: "b",
            content: ")"
        },
        {
            type: "b",
            content: "{"
        },
        {
            type: "l",
            content: "sdfff"
        },
        {
            type: "b",
            content: ";"
        },
        {
            type: "b",
            content: "}"
        }]
    },
    {
        type: "comments",
        content: [{
            type: "n",
            content: "}"
        }]
    }]
     </textarea><br/>
     dest:<br/>
     <textarea id="result">
     </textarea><br/>
     <a href="#"  onclick="FireEvent.parserJavaClassFunctionBody('insrc','result')">提交</a>
</form>