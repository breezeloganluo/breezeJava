<!--表头处理-->
<table class="table table-bordered" id="tableFeld_${data.f}">
      <thead>
        <tr class="active">
          <th width="45px"><input type="checkbox"></th>
          <!--$if (/String/i.test(typeof(data.m.valueRange[0]))){-->
                <th>${data.m.name}</th>
          <!--$}else{-->
            <!--$for (var n in data.m.valueRange[0]){-->
                <th>${data.m.valueRange[0][n].name}</th>
            <!--$}-->
          <!--$}-->
        </tr>
      </thead>
<!--数据处理-->
     <tbody>
		<!--$if (data.d != null){-->
            <!--$for (var i=0;i<data.d.length;i++){-->
            <tr >
                <td width="45px"><input type="checkbox" idx="${i}"></td>
                <!--$if (/String/i.test(typeof(data.m.valueRange[0]))){-->
                         <td>
                             ${p:("createFormType",data.f+"["+i+"]",data.m.valueRange[0],data.d[i])}
                         </td>
                <!--$}else{-->
                    <!--$for (var n in data.m.valueRange[0]){-->
                		 <td>
                             ${p:("createFormType",data.f+"["+i+"]."+n,data.m.valueRange[0][n],data.d[i][n])}
                         </td>
                    <!--$}-->
                <!--$}-->
				
            </tr>    
            <!--$}-->
        
        <!--$}-->

<!--无数据的处理-->
        <!--$if (data.d == ""){-->
        <tr class="list-none">
            <td colspan="100" style="padding:40px; font-size:16px; color:orange; text-align:center;">暂无数据</td>
        </tr>
        <!--$}-->
     </tbody>
</table>
<!--表格控制数据的处理-->

<button type="button" class="btn btn-primary  btn-xs"  onclick="FireEvent.addOneLine('${data.f}')">添加</button>

<button type="button" class="btn btn-danger  btn-xs" onclick="FireEvent.delOneLine('${data.f}')">删除</button>
