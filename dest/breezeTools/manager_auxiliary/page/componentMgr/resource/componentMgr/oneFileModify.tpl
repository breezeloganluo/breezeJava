 <!--$if (data.type == "m"){-->
<div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
  <span class="glyphicon glyphicon-th" aria-hidden="true">组件信息</span>
  <a href="#none" class="pull-right glyphicon glyphicon glyphicon-ok" style="margin-left: 3px;margin-right: 3px;" title="提交" onclick="FireEvent.modifyOne();"></a>  
</div>
 <!--$}else{-->
 <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
  <span class="glyphicon glyphicon-th" aria-hidden="true">组件信息</span>
  <a href="#none" class="pull-right glyphicon glyphicon glyphicon-ok" style="margin-left: 3px;margin-right: 3px;" title="提交" onclick="FireEvent.addOne();"> </a>  
</div>
 <!--$}-->

<form class="container-fluid" id="oneDetail">

<div class="row">
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        标题
      </span>
      <input name="title" type="text" class="form-control" aria-label="标题">
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        类型
      </span>
      <select name="dtype" class="form-control">
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
  </div><!-- /.col-lg-6 -->
</div><!-- /.row -->


<div class="row">
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        简述
      </span>
      <input name="sDesc" type="text" class="form-control" aria-label="简述">
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        关键词
      </span>
      <input name="keyword" type="text" class="form-control" aria-label="关键词">
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
</div><!-- /.row -->


<div class="row">
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        版本号
      </span>
      <input name="version" type="text" class="form-control" aria-label="版本号">
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        版本描述
      </span>
      <input name="vdesc" type="text" class="form-control" aria-label="版本描述">
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
</div><!-- /.row -->

<div class="row">
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        作者
      </span>
      <input name="author" type="text" class="form-control" aria-label="作者">
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
  <div class="col-lg-6">
    <div class="input-group">
      <span class="input-group-addon">
        是否breeze
      </span>
      <select name="isBreeze" class="form-control">
         <option value="breeze">breeze</option>
         <option value="other">其他</option>
      </select>
    </div><!-- /input-group -->
  </div><!-- /.col-lg-6 -->
</div><!-- /.row -->


<div class="form-group">
    <label for="exampleInputFile">描述</label>
    <textarea name="resDesc" class="form-control" rows="3"></textarea>
</div>
  
  

</form>
