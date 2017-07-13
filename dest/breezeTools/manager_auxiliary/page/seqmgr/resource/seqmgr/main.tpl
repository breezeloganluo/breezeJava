
<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        ${data.orgFile}
        ${p:("showFileOperIcon",data.orgFile)}
</div>

<!--$for (var i=0;i<data.seq.length;i++){-->

<div class="alert alert-warning"  role="alert" style="cursor: pointer;padding-top: 1px;padding-bottom: 1px;margin-top: 1px;margin-bottom: 0px;">
  <span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
  <a href="#none" onclick="FireEvent.openSeq('${data.seq[i].fileName}')">${p:("changeDisplayName",data.seq[i].fileName)}</a>
  <a href="#" class="pull-right" title="打开顺序图" >
      <img src="../.././img/icon/sequence.png" width="20" height="20">
  </a>  
  <textarea style="width:100%;height:50">${data.seq[i].fileDesc}</textarea>
</div>


<!--$}-->



<!--$if (data.seq == null || data.seq.length==0){-->
   没有关联的顺序图
<!--$}-->
