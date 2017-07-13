<!--$for (var i=0;i<data.length;i++){-->
  <a href="#" class="pull-right" title="${data[i].name}" onclick="FireEvent.goEdit('${data.name}','${data[i].idx}');">
      <img src="../../${data[i].icon}" width="20" height="20"/>
  </a>
<!--$}-->
