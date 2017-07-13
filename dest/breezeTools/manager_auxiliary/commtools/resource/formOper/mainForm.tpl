
  <!--$for (var n in data.m){-->
  <div class="form-group">
    <label for="inputEmail3" class="col-sm-2 control-label">
        <!--$if (data.m[n].fun == null){-->
        ${data.m[n].name}
        <!--$}else{-->
        <a href="#" onclick="FireEvent.fun('${data.m[n].fun.fun}','${n}');" title="${data.m[n].fun.tips}">${data.m[n].name}</a>
        <!--$}-->
    </label>
    <div class="col-sm-10" id="content_${n}">
      <!--$if (data.m[n].desc){-->
          ${data.m[n].desc}
      <!--$}-->
      ${p:("createFormType",n,data.m[n],data.d[n])}
    </div>
  </div>
  <!--$}-->
  
