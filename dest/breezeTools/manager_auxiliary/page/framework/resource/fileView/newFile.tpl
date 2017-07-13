

  <div> 
   <div > 
    <h4 style="text-align:left">
      添加文件
      <a class="pull-right glyphicon glyphicon-remove" href="#" onclick="FW.unblockUI();"> </a> 
    </h4> 

     

   </div> 
   <div> 
     <div style="text-align:left"> 
      <label class="blue">当前目录是：${data.srcDir}，请输入文件名</label>
      <input type="text" class="form-control" id="newFileName" placeholder="目录名"></input>
     </div> 
     <hr /> 
     
     <!--$for(var i=0;i<data.length;i++){-->
     	<a class="btn  btn-success" href="#" onclick="FireEvent.newFile('${data.srcDir}',${data[i].idx});return false;" title="${data[i].name}">
     	
           <img src="../../${data[i].icon}" widht="18" height="18"></img>

        </a>
     <!--$}-->
    </div> 

  </div>
