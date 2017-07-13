<form class="form-horizontal">
   <div id="funFragmentBase"></div>
   <div>

      <button type="button" class="btn btn-primary" onclick="FireEvent.setFunFragmentBase();" >确定</button>


      <button type="button" class="btn btn-info" onclick="FireEvent.inserFragmentBefore()">添加注释到前面</button>


      <button type="button" class="btn btn-info" onclick="FireEvent.appendFragmentAfter()">添加注释到后面</button>

      <button type="button" class="btn btn-success" onclick="FireEvent.insertFragmentInside()">添加注释到里面</button>

      <button type="button" class="btn btn-danger" onclick="FireEvent.deleteFragment()">删除</button>
      
      <button type="button" class="btn btn-warning" onclick="FireEvent.showFunGraphy('${data.i}')">返回图形</button>

  </div>
</form>
下面是与片段相对应的代码片段，按ctrl-s直接保存<br/>
${data.funHead}
<div   id="codeEditor">
   代码显示
</div>