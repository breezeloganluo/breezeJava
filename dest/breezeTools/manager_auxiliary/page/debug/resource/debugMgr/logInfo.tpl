<H1><a href="#" onclick="FireEvent.clickLog();">日志信息</H1><a href="#" onclick="FireEvent.clearLog();">清除</a>
<div id="innerLogInfo">
<!--$for (var h=0;data && h<data.length;h++){-->
    <table width="100%">
    <!--$for (var i=0;data && i<data[h].length;i++){-->
       <tr>
          <td>${data[h][i].log}</td>
          <td>${data[h][i].time}</td>
       </tr>
    <!--$}-->
    </table>
    ----------------------------------------------------
<!--$}-->
</div>