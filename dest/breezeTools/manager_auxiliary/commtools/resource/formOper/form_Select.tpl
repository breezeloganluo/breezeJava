
<!--$var evn = "";-->
<!--$for(var n in data.m){-->
<!--$if(/^on/i.test(n)){-->
<!--$evn = n + "=\"FireEvent.fun('"+data.m[n]+"','"+data.f+"');\"";-->
<!--$}-->
<!--$}-->
<select class="form-control" name="${data.f}" ${evn}>
  <!--$for (var i=0;i<data.m.valueRange.length;i++){-->
  <!--$for (var n in data.m.valueRange[i]){-->
     <!--$if (data.m.valueRange[i][n] == data.d){-->
     	<option value="${data.m.valueRange[i][n]}" selected="true">${n}</option>
     <!--$}else{-->
        <option value="${data.m.valueRange[i][n]}">${n}</option>
     <!--$}-->
  <!--$}-->
  <!--$}-->
  
</select>