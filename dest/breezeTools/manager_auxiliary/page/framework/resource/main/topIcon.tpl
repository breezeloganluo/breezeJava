<!--$for (var i=0;i<data.length;i++){-->

  <!--$if (data[i].type == null){-->
    <a href="../../${data[i].toolsUrl}" title="${data[i].name}" target="_blank">
        <img src="../../${data[i].icon}" width="56" height="50">
    </a>
  <!--$}else{-->
  	<a href="#" title="${data[i].name}"  onclick="FireEvent.openApp('${data[i].name}','${data[i].idx}')">
        <img src="../../${data[i].icon}" width="56" height="50">
    </a>
  <!--$}-->

<!--$}-->
