    <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        ${data.dir}的历史文件信息
        
       
       <a class="pull-right glyphicon glyphicon-arrow-left" style="margin-right:15px" href="#" onclick="FireEvent.go2dir();" title="返回"></a>
       
          
    </div>
    
    
    <div style="height:85%;overflow:auto">
	
        <!--$for(var i=0;i<data.length;i++){-->
        <div class="alert alert-warning" role="alert" style="padding-top: 1px;padding-bottom: 1px;margin-top: 1px;margin-bottom: 0px;">
                  ${p:("formattime",data[i])}
                  <a class="pull-right glyphicon glyphicon-share-alt" href="#none" onclick="FireEvent.recovery('${data.dir}','${data[i]}')"></a>
                  <a class="pull-right glyphicon glyphicon-eye-open" href="#none" onclick="FireEvent.compareOne('${data.dir}','${data[i]}')"></a>
        </div>
		<!--$}-->
		

    </div>