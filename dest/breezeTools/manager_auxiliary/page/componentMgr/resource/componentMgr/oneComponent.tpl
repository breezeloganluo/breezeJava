        <form class="form-horizontal" onsubmit="return false;">
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">标题</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="inputEmail3" placeholder="Email" disabled value=${data.title}>
            </div>
          </div>
            
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">版本</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="inputEmail3" placeholder="Email" disabled value=${data.version}>
            </div>
          </div>
          
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">版本描述</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="inputEmail3" placeholder="Email" disabled value=${data.vdesc}>
            </div>
          </div>
          
          
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">资源类型</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" id="inputEmail3" placeholder="Email" disabled value=${data.dtype} >
            </div>
          </div>
          
           
          
            
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">资源状态</label>
            <div class="col-sm-10">
            <!--$ var jieya="";  -->    
             <!--$ if(data.isUnZip ){ -->
               <!--$ 	jieya="已解压";  -->
               <!--$ }else{ -->
               <!--$ 	jieya ="未解压";  -->

               <!--$ } -->
              
               
              <input type="text" class="form-control" id="inputEmail3" placeholder="Email" disabled value=${jieya}>
            </div>
          </div>
          
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">资源摘要</label>
            <div class="col-sm-10">
            ${data.sDesc}
            </div>
          </div>
          
          <div class="form-group">
            <label for="inputEmail3" class="col-sm-2 control-label">资源描述</label>
            <div class="col-sm-10">
            ${data.resDesc}
            </div>
          </div>
          
          <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
              解压字符集<input type="Text" id="unzipchartset" value="UTF-8">
              <button type="submit" class="btn btn-default" onclick="FireEvent.downUnzip('${data.resLink}','${data.cid}','${data.title}')" >解压</button>
              
               <!--$ if(data.isUnZip){ -->
                  <button class="btn btn-default" onclick="FireEvent.showOneMgr(${data.cid});return false;">管理</button> 
               <!--$ }  -->
               <botton class = "btn btn-warning" onclick="FireEvent.showList()">返回</button>
                  
               
              
             
            </div>
            在压缩zip文件时，如果文件名为中文，就涉及压缩时的字符集，默认使用window手动压缩时使用的是GB2312，当然如果使用本工具统一压缩就都是UTF-8，所以为避免字符错误，文件名尽量不要用中文
          </div>
        </form>