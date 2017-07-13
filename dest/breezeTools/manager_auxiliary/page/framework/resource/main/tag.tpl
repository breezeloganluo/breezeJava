
      <!--$for (var i=0;i<data.tags.length;i++){-->
         <!--$if (data.tags[i].selected == true){-->
      		<li role="presentation" class="active"><a href="#" >${data.tags[i].name}</a></li>
         <!--$}else{-->
            <li role="presentation" ><a href="#" onclick="FireEvent.changeTag('${data.path}',${i});return false;">${data.tags[i].name}</a></li>
         <!--$}-->
      <!--$}-->
