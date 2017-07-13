<div class="container-fluid">
  <div class="row" style="height:50%">
      <div class="col-md-5" id="prjFileMgr"></div>
      <div class="col-md-2">
          <!--这里开始工具部分-->
          <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
             <span class="glyphicon glyphicon-th" aria-hidden="true"></span>

             <a href="#none" class="pull-right glyphicon glyphicon-share-alt" style="margin-left: 3px;margin-right: 3px;" title="返回" onclick="FireEvent.showList();"></a>
             <a href="#none" class="pull-right glyphicon glyphicon-eye-open" style="margin-left: 3px;margin-right: 3px;" title="查看文件" onclick="FireEvent.viewOneFile()"></a>
			 <a href="#none" class="pull-right glyphicon glyphicon-align-justify" style="margin-left: 3px;margin-right: 3px;" title="查看信息" onclick="FireEvent.showOneModify()"></a>
             <a href="#none" class="pull-right glyphicon glyphicon glyphicon-plus" style="margin-left: 3px;margin-right: 3px;" title="创建新组建" onclick="FireEvent.newOne()"></a>
             <a href="#none" class="pull-right glyphicon glyphicon-open" style="margin-left: 3px;margin-right: 3px;" title="重新上传" onclick="FireEvent.zipupload()"></a>
             
          </div>
          <!--工具部分结束-->
          
          <div id="file2copy">
          </div>
          <p>
          <button type="button" class="btn btn-default btn-lg" aria-label="Left Align" style="width: 100%;"  onclick="FireEvent.copy2Left()">
            <span class="glyphicon glyphicon-backward" aria-hidden="true" style="width: 100%;"></span>
		  </button>
          </p>
          
          
          <p>
          <button type="button" class="btn btn-default btn-lg" aria-label="Left Align" style="width: 100%;" onclick="FireEvent.copy2Right()">
            <span class="glyphicon glyphicon-forward" aria-hidden="true" style="width: 100%;"></span>
		  </button>
          </p>
          
          
          
          
      </div>
      <div class="col-md-5" id="componentFileMgr">.col-md-5</div>
  </div>
  <div class="row"  style="height:50%">
      <div class="col-md-6"  id="onefileLeftButtom">查看文本信息</div>
      <div class="col-md-6" id="decompressed">浏览已经下载的组件</div>
  </div>
</div>