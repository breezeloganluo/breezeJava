<!--$for (var i=0;i<data.length;i++){console.log(data[i]);-->
<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
       <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
       
       <!--$if (!data[i].isOnpen){-->
       <a href="#" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.open('${i}');" title="打开">第${i+1}组</a>
       <!--$}else{-->
       <a href="#" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.close('${i}');" title="关闭">第${i+1}组</a>
       <!--$}-->
       
       
       <a href="#" class="pull-right glyphicon glyphicon glyphicon-minus" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.delOne('${i}');" title="删除"></a>
       <a href="#" class="pull-right glyphicon glyphicon glyphicon-plus" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.addOne('${i}');" title="添加"></a>
</div>
  <!--$if (data[i].isOnpen){-->
    <div class="alert alert-warning" onclick="" role="alert" style="cursor: pointer;padding-top: 1px;padding-bottom: 1px;margin-top: 1px;margin-bottom: 0px;">
    input
    <a href="#" class="pull-right glyphicon glyphicon-ok" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.saveInput('${i}');" title="保存"></a>
    </div>
    <textarea id="inputtext" style="width:100%;height:280px">${p:("parserContent",data[i].input)}</textarea>

    <div class="alert alert-warning" onclick="" role="alert" style="cursor: pointer;padding-top: 1px;padding-bottom: 1px;margin-top: 1px;margin-bottom: 0px;">
    output
    <a href="#" class="pull-right glyphicon glyphicon-ok" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.saveOutput('${i}');" title="保存"></a>
    </div>
    <textarea  id="outputtext"  style="width:100%;height:280px">${p:("parserContent",data[i].output)}</textarea>
  <!--$}-->
<!--$}-->