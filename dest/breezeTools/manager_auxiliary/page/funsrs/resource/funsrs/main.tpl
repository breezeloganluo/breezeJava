
<div class="container-fluid">
  <div class="row">
    <div class="col-md-4">
        <div>
        
        <button type="button" class="btn btn-warning btn-xs" onclick="FireEvent.newTop();">顶级功能</button>

        <button type="button" class="btn btn-danger btn-xs" onclick="FireEvent.newSub();">子功能</button>

        <button type="button" class="btn btn-info btn-xs" onclick="FireEvent.delFun();">删除功能</button>
        
        <button type="button" class="btn btn-primary btn-xs" onclick="FireEvent.showDoc();">查看文档</button>
        </div>
        <div id="javaTree">
        菜单树
        </div>
    </div>
    <div class="col-md-8" >
      <div id = "tag">

      </div>
      
      <div id="mainContent">
           内容
      </div>

    </div>
  </div>
</div>