<ul class="nav nav-tabs">
  <li role="presentation" class="active"><a href="#">基本属性</a></li>
  <li role="presentation"><a href="#"  onclick="FireEvent.showFunGraphy('${data.i}');">流程图</a></li>
  <li role="presentation"><a href="#" onclick="FireEvent.showFunCode('${data.i}')">代码</a></li>
</ul>
<form class="form-horizontal">
   <div id="funBase"></div>
   <div>

      <button type="button" class="btn btn-primary" onclick="FireEvent.setFunBase('${data.i}')">确定</button>


      <button type="button" class="btn btn-success">复制到</button>


      <button type="button" class="btn btn-info">复制到本地</button>


      <button type="button" class="btn btn-danger" onclick="FireEvent.delFun('${data.i}')">删除</button>

  </div>
</form>

