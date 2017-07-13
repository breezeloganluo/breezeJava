      <ul class="nav nav-tabs" id="m_tag">
              <!--$if (data == "base" ){-->
              
              <li role="presentation" class="active"><a href="#">功能特征</a></li>
              <li role="presentation" ><a href="#" onclick="FireEvent.showBaseInfo();return false;">方案信息</a></li>
              <li role="presentation" ><a href="#" onclick="FireEvent.showActor();return false;">角色定义</a></li>
              
              <!--$}else if(data == "actor"){-->
              
              <li role="presentation" ><a href="#"  onclick="FireEvent.showOne();return false;">功能特征</a></li>
              <li role="presentation" ><a href="#" onclick="FireEvent.showBaseInfo();return false;">方案信息</a></li>
              <li role="presentation" class="active"><a href="#">角色定义</a></li>
              
              <!--$}else if(data == "info"){-->
              
              <li role="presentation" ><a href="#"  onclick="FireEvent.showOne();return false;">功能特征</a></li>
              <li role="presentation" class="active"><a href="#">方案信息</a></li>
              <li role="presentation" ><a href="#" onclick="FireEvent.showActor();return false;">角色定义</a></li>
              
              <!--$}-->
      </ul>