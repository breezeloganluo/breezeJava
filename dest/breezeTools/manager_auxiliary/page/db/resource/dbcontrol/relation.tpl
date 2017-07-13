
      <!--$for (var i=0;i<data.length;i++){-->
      <div class="alert alert-success" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
          <a href="#" onclick="FireEvent.clickRelation('${data[i].table}','${data[i].alias}');return false;">${data[i].type}:${data[i].alias}</a>
      </div>
      <div  id="relation${data[i].table}${data[i].alias}" style="display:none">
      </div>
      <!--$}-->
      
      <!--$if (data.length == 0){-->
          没有任何外部关系
      <!--$}-->
             
