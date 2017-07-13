<table class="table table-bordered">
             <thead>
                 <tr>
                     <td>字段</td>
                     <td>字段名</td>
                     <td>字段类型</td>
                 </tr>
             </thead>
             <tbody style="font-size: xx-small;">
             
             <!--$for (var i=0;i<data.dataDesc.length;i++){-->
                 <tr class="warning">
                     <!--$var one = data.dataDesc[i];-->
                     <td>${one.fieldname}</td>
                     <td>${one.title}</td>
                     <td>${one.fieldType}</td>
                 </tr>
             <!--$}-->
             
             </tbody>
</table>