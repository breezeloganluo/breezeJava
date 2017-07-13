<div class="container-fluid">
  <div class="row">
    <div class="col-md-4">
        <div>
        
        <button type="button" class="btn btn-warning btn-xs" onclick="FireEvent.newAttr();">新属性</button>

        <button type="button" class="btn btn-danger btn-xs" onclick="FireEvent.newJavaFun();">新方法</button>

        <button type="button" class="btn btn-info btn-xs" onclick="FireEvent.showAllTemplate();">导入模板</button>
        
        </div>
        <div id="javaTree">
        菜单树
        </div>
    </div>
    <div class="col-md-8" id="mainContent">中间内容</div>
  </div>
</div>