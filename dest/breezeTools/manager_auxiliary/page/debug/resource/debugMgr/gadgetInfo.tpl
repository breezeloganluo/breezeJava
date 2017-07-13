<H1><a href="#" onclick="FireEvent.clickGadgetInfo();">gadgt的版本信息</a></H1>
<div id="clickGadgetInfo">
<!--$for (var n in data){-->
      <h3>${n}</h3>
      <!--$for (var nn in data[n]){-->
      <p>
         ${nn}:${data[n][nn]}
      </p>
      <!--$}-->
<!--$}-->
</div