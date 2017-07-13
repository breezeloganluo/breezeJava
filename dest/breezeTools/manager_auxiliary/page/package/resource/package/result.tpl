<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        <a href="#" onclick="$('#okList').slideToggle();return false;">成功列表</a>         
</div>
<div id="okList">
<!--$for (var i=0;data.okList && !/string/i.test(typeof(data.okList))  &&i<data.okList.length;i++){-->
<div class="alert alert-warning" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        ${data.okList[i]}
</div>
<!--$}-->
</div>



<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        失败列表         
</div>
<!--$for (var i=0;data.noList && !/string/i.test(typeof(data.noList))  &&i<data.noList.length;i++){-->
<div class="alert alert-warning" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        ${data.noList[i]}
</div>
<!--$}-->




<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        找不到文件         
</div>
<!--$for (var i=0;data.contentNullList && !/string/i.test(typeof(data.contentNullList))  &&i<data.contentNullList.length;i++){-->
<div class="alert alert-warning" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        ${data.contentNullList[i]}
</div>
<!--$}-->