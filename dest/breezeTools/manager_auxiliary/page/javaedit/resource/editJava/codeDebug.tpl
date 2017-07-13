<a href="#" onclick="FireEvent.compileJava();">编译</a>&nbsp;&nbsp;&nbsp;
<a href="#" onclick="FireEvent.loadJava();">装载</a>&nbsp;&nbsp;&nbsp;
<a href="#" onclick="FireEvent.debugJava();">调试</a>
<div>
    日志标识<input type="Text" id="threadSignal" value="${data.threadSignal}"/> 
    <a href="#" onclick="FireEvent.setThreadSignal();">设置日志标识</a>&nbsp;&nbsp;|&nbsp;&nbsp;
    <a class="glyphicon-class" href="#" onclick="FireEvent.nextDebug();return false;">下一个</a>
</div>
<div style="width:100%;height:100%;overflow-y :auto;background-color: #FFF;">
<!--$for (var i=data.msgArr.length-1;i>=0;i--){-->
      <!--$if (data.msgArr[i][1] == data.cs){-->
           ${i+1}:<span style="color:blue">${data.msgArr[i][1]}(${data.msgArr[i][2]})</span><span style="color:red">${data.msgArr[i][0]}</span>
      <!--$}else{-->
           ${i+1}:<span style="color:blue">${data.msgArr[i][1]}(${data.msgArr[i][2]})</span>${data.msgArr[i][0]}
      <!--$}-->
      <br/>
<!--$}-->
</div>