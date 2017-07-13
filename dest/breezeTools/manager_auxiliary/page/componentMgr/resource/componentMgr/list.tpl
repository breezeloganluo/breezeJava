<div  class="container-fluid">
<div class="row">
    <div class="col-md-10" id="searchForm">
        <div class="row">
          <div class="col-lg-3">
            <div class="input-group">
              <span class="input-group-addon">
                类型
              </span>
              <select name="dtype" class="form-control">
                 <option value="--">全部</option>
                 <option value="BreezeJava">BreezeJava</option>
                 <option value="BreezeJavaFlowUnit">BreezeJavaFlowUnit</option>
                 <option value="BreezeJavaChecker">BreezeJavaChecker</option>
                 <option value="BreezeJavaBTLFun">BreezeJavaBTLFun</option>
                 <option value="Breeze前端">Breeze前端</option>
                 <option value="BreezeCMSControl">BreezeCMSControl</option>
                 <option value="BreezeCMSDecorator">BreezeCMSDecorator</option>
                 <option value="BreezeCMSOther">BreezeCMSOther</option>
                 <option value="BreezeTools">BreezeTools</option>
                 <option value="BreezeTools">BreezeTools</option>
                 <option value="Breeze综合">Breeze综合</option>
              </select>
            </div><!-- /input-group -->
          </div><!-- /.col-lg-3 -->
          <div class="col-lg-3">
            <div class="input-group">
              <span class="input-group-addon">
                是否是breeze
              </span>
              <select name="isBreeze" class="form-control">
                 <option value="--">不区分</option>
                 <option value="breeze">是</option>
                 <option value="other">不是</option>
              </select>
            </div><!-- /input-group -->
          </div><!-- /.col-lg-3 -->
          <div class="col-lg-3">
            <div class="input-group">
              <span class="input-group-addon">
                关键词
              </span>
              <input name="key" type="text" class="form-control" aria-label="关键词">
            </div><!-- /input-group -->
          </div><!-- /.col-lg-3 -->
          <div class="col-lg-3">
            <button type="button" class="btn btn-success" onclick="FireEvent.search();">ok</button>
          </div><!-- /.col-lg-3 -->
        </div>
    </div>
    <div class="col-md-2">
        <button type="button" class="pull-right btn btn-danger" onclick="FireEvent.showNewComponent();">添加新组建</button>
    </div>
</div>
</div>

<div class="table-responsive">
  <table id="view_formObj" class="table table-responsive table-striped table-bordered table-hover dataTable no-footer">
			<thead>
				<tr>
                            <th>组件名称</th>
                            <th>组件类型</th>
                            <th>组件简述</th>
                            <th>操作</th>
				</tr>
			</thead>
			<tbody>
						<!--$for (var i=0;i<data.length;i++){-->

                        <tr>
                        <td>${data[i].title}</td>
                        <td>${data[i].dtype}</td>
                        <td>${data[i].sDesc}</td>
                        <td><button type="button" class="btn btn-primary"    onclick="FireEvent.showOne(${data[i].cid})"  >详情</button></td>
                        </tr>
                        <!--$}-->
			</tbody>
		</table>
</div>
<nav>
  <ul class="pagination">
    <li>
      <a href="#" aria-label="Previous">
        <span aria-hidden="true">&laquo;</span>
      </a>
    </li>
    
    <!--$for (var i=0;i<data.count;i++){-->
       <!--$if (i == data.start){-->
           <li  class="active"><a href="#none">${i+1}</a></li>
       <!--$}else{-->
           <li><a href="#" onclick="FireEvent.goPage('${i}');return false;">${i+1}</a></li>
       <!--$}-->
    <!--$}-->

    <li>
      <a href="#" aria-label="Next">
        <span aria-hidden="true">&raquo;</span>
      </a>
    </li>
  </ul>
</nav>