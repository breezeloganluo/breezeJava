<div style="max-width:100%;">
    <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        <a href="#none" onclick="FireEvent.openGoDir();return false;">${data.displayDir}</a>
        
       <!--$if (data.selectedItem && data[data.selectedItem].type == "item"){-->
       <a class="pull-right  glyphicon glyphicon-share-alt" style="margin-left: 3px;margin-right: 3px;" href="#none"  onclick="FireEvent.showFileHistory('${data.selectedItem}');"  title="历史文件"></a>
       <!--$}else{-->
       <span class="pull-right  glyphicon glyphicon-share-alt" style="margin-left: 3px;margin-right: 3px;" title="历史文件"></span>
       <!--$}-->
       
       <!--$if (data.selectedItem){-->
       <a class="pull-right glyphicon glyphicon-trash " style="margin-left: 3px;margin-right: 3px;" href="#none"  onclick="FireEvent.deleteFile('${data.selectedItem}')"  title="删除"></a>
       <!--$}else{-->
       <span class="pull-right glyphicon glyphicon-trash" style="margin-left: 3px;margin-right: 3px;" title="删除"></span>
       <!--$}-->
        
        
       
       <!--$if (data.copycut != null){-->
       <a class="pull-right glyphicon glyphicon-paperclip" style="margin-left: 3px;margin-right: 3px;" href="#none"  onclick="FireEvent.parse();"   title="粘贴"></a>
       <!--$}else{-->
       <span class="pull-right glyphicon glyphicon-paperclip" style="margin-left: 3px;margin-right: 3px;"  title="粘贴"></span>
       <!--$}-->
       
       
        <!--$if (data.selectedItem && data[data.selectedItem].type == "item"){-->
        <a class="pull-right glyphicon glyphicon-scissors"  style="margin-left: 3px;margin-right: 3px;" href="#none"  onclick="FireEvent.cut('${data.selectedItem}');"  title="剪切"></a>
        <a class="pull-right glyphicon glyphicon-duplicate" style="margin-left: 3px;margin-right: 3px;" href="#none"  onclick="FireEvent.copy('${data.selectedItem}')"  title="复制"></a>
        <a class="pull-right glyphicon glyphicon-cloud-upload" style="margin-left: 3px;margin-right: 3px;" href="#none"  onclick="FireEvent.upload2other('${data.selectedItem}')"  title="上传到其他工程"></a>
        <!--$}else{-->
        <span class="pull-right glyphicon glyphicon-scissors " style="margin-left: 3px;margin-right: 3px;" title="剪切"></span>
        <span class="pull-right glyphicon glyphicon-duplicate" style="margin-left: 3px;margin-right: 3px;"  title="复制"></span>
        <span class="pull-right glyphicon glyphicon-cloud-upload" style="margin-left: 3px;margin-right: 3px;"   title="上传到其他工程"></span>
        <!--$}-->
        

        <a class="pull-right glyphicon glyphicon-level-up" style="margin-left: 3px;margin-right: 3px;" href="#none" onclick="FireEvent.go2dir('${data.dir}/')" title="返回上层"></a>
 
        
        
        <a class="pull-right glyphicon glyphicon-folder-open" style="margin-left: 3px;margin-right: 3px;" href="#none" onclick="FireEvent.openNewDir()"   title="创建新目录"></a>
        
  
        <a href="#" class="pull-right glyphicon glyphicon-file" style="margin-left: 3px;margin-right: 3px;" onclick="FireEvent.openNewFile()" title="新文件"></a>
          

    </div>
    <div style="height:85%;overflow:auto">
    
    	<!--$for(var n=0;n< data.length;n++){-->
        <!--$if (data[n] && data[n].name != null){-->
    		<div class="${p:('parserFileStyle',data[n].selected)}" onclick="FireEvent.selectFile('${n}');return false;" role="alert" style="cursor: pointer;padding-top: 1px;padding-bottom: 1px;margin-top: 1px;margin-bottom: 0px;">
            <!--$if (data[n].type == "folder"){-->
                  <a  class="glyphicon glyphicon-folder-open" style="margin-right: 5px;"  ></a>
                  <a href="#none" onclick="FireEvent.go2dir('${data.dir}/${data.name}/${data[n].name}');return false;">${p:('changeDisplayName',data[n].name)}</a>
            <!--$}else{-->
                  <span style="cursor: pointer;" onclick="window.open('${Cfg.baseUrl}/${data.dir}/${data.name}/${data[n].realName}');return false;">${p:('changeDisplayName',data[n].name)}</span>
                  ${p:("showFileOperIcon",data[n].name)}
            <!--$}-->
			</div>
        <!--$}-->
        <!--$}-->
    </div>
</div>