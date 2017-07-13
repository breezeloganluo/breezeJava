<div class="container-fluid">
  <div class="row">
  	
      <div class="col-xs-8">
          <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">表信息</div>
      </div>
      <div class="col-xs-4 visible-lg-block">
          <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">外部关系</div>
      </div>
    
  </div>
  
  <!--$for (var n in data){-->
      <div class="col-xs-8">
          <div class="alert alert-success" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
              ${data[n].tableName}(${n}|${data[n].displayName})
              <!--$if (data[n].dataMemo == null || /^\s*$/i.test(data[n].dataMemo)){-->
                  <span class="pull-right" >没有备注</span>
              <!--$}else{-->
                  <a class="pull-right" href="#none" onclick="FireEvent.showTabMemo('${n}');return false;">查看备注</a>
              <!--$}-->
          </div>
          <table class="table table-bordered">
             <thead>
                 <tr>
                     <td>字段</td>
                     <td>字段名</td>
                     <td>字段类型</td>
                     <td>外链描述</td>
                     <td>备注说明</td>
                     <td width="80">长度</td>
                 </tr>
             </thead>
             <tbody style="font-size: xx-small;">
             
             <!--$for (var i=0;i<data[n].dataDesc.length;i++){-->
                 <tr class="warning">
                     <!--$var one = data[n].dataDesc[i];-->
                     <td>${one.fieldname}</td>
                     <td>${one.title}</td>
                     <td>${one.fieldType}</td>
                     <td>${one.ourterLink}</td>
                     <td>${p:("memo",one.fieldMemo,n,i)}</td>
                     <td>${one.fieldLen}</td>
                 </tr>
             <!--$}-->
             
             </tbody>
          </table>
      </div>
      <div class="col-xs-4 visible-lg-block">
          ${p:("showRelation",data[n])}
      </div>
  <!--$}-->
</div>