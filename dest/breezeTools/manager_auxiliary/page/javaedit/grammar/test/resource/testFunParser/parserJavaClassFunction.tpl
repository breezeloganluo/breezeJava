<form>
     <a href="#" onclick="FireEvent.gotmpMain()">测试parserJavaClassFunctionBody</a><br/>
     默认内容的代码：<br/>
     public static void main(String[] args)throws Exception{
     <br/>
     <textarea id="insrc">

[
	{
		type:"function",
		content:[
			{
				type:"l",
				content:"public"
			},
			{
				type:"l",
				content:"static"
			},
			{
				type:"l",
				content:"void"
			},
			{
				type:"l",
				content:"main"
			},
			{
				type:"b",
				content:"("
			},
			{
				type:"l",
				content:"String"
			},
			{
				type:"b",
				content:"["
			},
			{
				type:"b",
				content:"]"
			},
			{
				type:"l",
				content:"args"
			},
			{
				type:"b",
				content:")"
			},
			{
				type:"l",
				content:"throws"
			},
			{
				type:"l",
				content:"Exception"
			},
			{
				type:"b",
				content:"{"
			}
		],
        children:[{
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
	}
]
     </textarea><br/>
     dest:<br/>
     <textarea id="result">
     </textarea><br/>
      <a href="#"  onclick="FireEvent.parserJavaClassFunction('insrc','result')">提交</a>
</form>