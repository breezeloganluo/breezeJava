<h1>
当前的日志标识:
<!--$if (data == null || data.s == null){-->
已停止
<!--$}else{-->
${data.s}
<!--$}-->
</h1>

<button type="button" class="btn btn-primary" onclick="FireEvent.newOne();">设置新的</button>


<button type="button" class="btn btn-success" onclick="FireEvent.stopOne();">停止调试</button>
<div id="gadgetInfo">
</div>
<div id="logInfo">
</div>