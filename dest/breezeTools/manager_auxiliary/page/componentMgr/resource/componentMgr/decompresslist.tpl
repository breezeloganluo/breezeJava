<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true">所有解压过的组件</span>
</div>
    

<div style="height:85%;overflow:auto">
    
    
        <!--$for(var i=0;i<data.length;i++){-->
    		<div class="alert alert-warning" role="alert" style="cursor: pointer;padding-top: 1px;padding-bottom: 1px;margin-top: 1px;margin-bottom: 0px;">
                  <span class="glyphicon glyphicon-hand-right" style="margin-right: 5px;"></span>
                  <a href="#none" onclick="FireEvent.showOneMgr('${data[i].cid}');return false;">${data[i].name}</a>
			</div>
        <!--$}-->
        
</div>