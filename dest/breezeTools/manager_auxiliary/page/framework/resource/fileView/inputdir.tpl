<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true">目录跳转</span>

       
       <a class="pull-right glyphicon glyphicon-remove" style="margin-left: 3px;margin-right: 3px;" title="关闭" onclick="FW.unblockUI();"></a>
</div>

<form>
  <div class="form-group">
    <label for="exampleInputPassword1"></label>
    <input type="text" class="form-control" id="maskgoRndDir" >
  </div>
</form>
<div style="text-align:center">
<button type="button" class="btn btn-success" onclick="FireEvent.goRndDir();">确定</button>
</div>

输入要跳转的路径注意，支持http://的网页写法哦