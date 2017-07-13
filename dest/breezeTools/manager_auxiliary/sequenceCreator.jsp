<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="./server/module/auth.jsp"%>

			<html>

			<head>
				<meta charset="utf-8" />
				<link rel="shortcut icon" href="./img/icon/editgadget.jpg">
				<!--basic styles-->
				<link href="./assets/css/bootstrap.min.css" rel="stylesheet" />
				<link href="./assets/css/bootstrap-responsive.min.css" rel="stylesheet" />
				<link rel="stylesheet" href="./assets/css/font-awesome.min.css" />

				<!--[if IE 7]>
	  <link rel="stylesheet" href="./assets/css/font-awesome-ie7.min.css" />
	<![endif]-->

				<!--page specific plugin styles-->
				<link rel="stylesheet" href="./assets/css/jquery-ui-1.10.3.custom.min.css" />
				<link rel="stylesheet" href="./assets/css/chosen.css" />

				<!--ace styles-->
				<link rel="stylesheet" href="./assets/css/ace.min.css" />
				<link rel="stylesheet" href="./assets/css/ace-responsive.min.css" />
				<link rel="stylesheet" href="./assets/css/ace-skins.min.css" />

				<!--[if lt IE 9]>
	  <link rel="stylesheet" href="./assets/css/ace-ie.min.css" />
	<![endif]-->
            <!--@config@{"manager_auxiliary":"/manager_auxiliary","../":"/"}-->
			</head>

			<body>
				<div>
					

					<div id="breadcrumbs" class="breadcrumbs textctrclose">
						<ul class="breadcrumb">
							<li> <i class="icon-home home-icon"></i>
								<a href="index.jsp">返回菜单</a>
								<span class="divider"> <i class="icon-angle-right arrow-icon"></i>
					</span>
							</li>
							<li class="active">

							</li>
						</ul>
						
						<div id="appMainWithSearch" class="FWApp nav-search" style="display: block;">
							<div id="baseSearchView">
								<form id="detailSearch" class="form-search" onsubmit="var args=['order_cumstomer_search'];var app = FW.getAPP('appMainWithSearch');app.FireEvent.search.apply(app,args);return false;" style="display: none;">
									<span class="input-icon">
							<input type="text" autocomplete="off" id="order_cumstomer_search" class="input-small nav-search-input" placeholder="Search ...">
							<i class="icon-search nav-search-icon"></i>
						</span>
								</form>
							</div>
						</div>
						
					</div>
				
					
					
					<div class="page-content clearfix">						
						<div class="page-header position-relative textctrclose">
							<h1 id="pageH1">
					<div class="pull-right" id="btnAction">
						<!--按钮部分，注释掉->
						<div class="btn-toolbar">
							<div class="btn-group">
								<button class="btn btn-success dropdown-toggle" data-toggle="dropdown" >
									<i class="icon-cogs"></i>
									文件
									<span class="caret"></span>
								</button>
								<ul class="dropdown-menu dropdown-primary pull-right">
									<li>
										<a href="javascript:void(0)" onclick="FW.getAPP('editGadget').showFileSelecte();"><i class="icon-edit"></i> 打开</a>
									</li>
									<li>
										<a href="javascript:void(0)" onclick="FW.getAPP('editGadget').showFileSave();"><i class="icon-plus"></i> 保存</a>
									</li>
								</ul>
							</div>
							<div class="btn-group">
								<button class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
									<i class="icon-cogs"></i>
									新建
									<span class="caret"></span>
								</button>
								<ul class="dropdown-menu dropdown-primary pull-right">
									<li >
										<a href="javascript:void(0)" onclick="FW.getAPP('editGadget').newGadget();"><i class="icon-edit"></i> Gadget</a>
									</li>
									<li >
										<a href="javascript:void(0)" onclick="FW.getAPP('editGadget').newAttribute();"><i class="icon-plus"></i> 属性</a>
									</li>
									<li >
										<a href="javascript:void(0)" onclick="FW.getAPP('editGadget').newFun();"><i class="icon-plus"></i> 方法</a>
									</li>
								</ul>
							</div>
							<div class="btn-group">
								<button class="btn btn-warning dropdown-toggle" data-toggle="dropdown">
									<i class="icon-cogs"></i>
									工具
									<span class="caret"></span>
								</button>
								<ul class="dropdown-menu dropdown-primary pull-right">
									<li >
										<a href="javascript:void(0)" onclick="FireEvent('editGadget.go2Test')"><i class="icon-edit"></i> 去测试</a>
									</li>
									<li >
										<a href="javascript:void(0)" onclick="FireEvent('editGadget.goMgrPage')"><i class="icon-edit"></i> 模型设定</a>
									</li>
									
								</ul>
							</div>	
							
						</div>
						
						<--按钮部分结束 -->
					</div>
					<span id="aliasTitle">文件编辑</span>
					<!--small>
						<i class="icon-double-angle-right"></i>
						<span id="actionName"></span>
					</small-->
				</h1>
						</div>
                        
                        
                        
                        
                        
                        <!--正式开始-->
						<div class="sidebar-shortcuts-large FWApp" id="htmledit" style="width:100%;">
							<!--@editSequence@
								{
								
								}
							-->
						</div>

					</div>
					<!-- ====================== -->
					<!--PAGE CONTENT ENDS HERE-->

				</div>
				<!--/.main-container-->




				<!--resource begin-->
				<div style="display:none">
					<div class="FWRES" APPID="htmledit"  RESID="graphview" >
                        <button type="button" class="btn btn-mini btn-warning btn-add-con" onclick="FireEvent.save();">
                          <i class="icon-plus bigger-120"> 保存</i>
                        </button>
                        
                        <button type="button" class="btn btn-mini btn-success btn-add-con" onclick="FireEvent.showInfo();">
                          <i class="icon-plus bigger-120"> 基本信息</i>
                        </button>
                        
                        <button type="button" class="btn btn-mini btn-success btn-add-con" onclick="FireEvent.addNew();">
                          <i class="icon-plus bigger-120"> 增加新节点</i>
                        </button>
						<div id="graphcontainer" style="width:100%;"></div>
                        
					</div>
                    
                    <div class="FWRES" APPID="htmledit"  RESID="info">
                    	<form id="infoform">
                          <div class="form-horizontal clearfix">
                            <div>表达场景</div>
                            <div>
                            <textarea id="scene" style="width:100%" class="inp_text _name_inp" type="text" name="scene" >${_}{data.scene}</textarea>
                            </div>
                          </div>
                          <br/>
                          
                          <div class="form-horizontal clearfix">
                            <div>设计思路</div>
                            <div>
                            <textarea id="desc" style="width:100%" class="inp_text _name_inp" type="text" name="desc" >${_}{data.desc}</textarea>
                            </div>
                          </div>
                          <br/>

                          <table style="margin-bottom:10px; width:" class="table table-list table_parameters table-striped table-bordered table-hover">
                              <thead>
                                  <tr>
                                      <th style="width:60px;" class="center">选择</th>
                                      <th class="th_name th_TextArea" style="cursor:pointer">作者</th>
                                      <th class="th_name th_TextArea" style="cursor:pointer">时间</th>
                                      <th class="th_desc th_TextArea" style="cursor:pointer">描述</th>
                                  </tr>
                              </thead>
                              <tbody>
                                  <!--$for (var i=0;i<data.history.length;i++){-->
                                  <tr>
                                      <td style="width:60px;" class="center">
                                         <label><input name="rowCheckbox" type="checkbox" idx="${_}{i}"><span class="lbl"></span></label>
                                      </td>
                                      <td class="td_parameters_name td_TextArea">
                                         <div class="row-fluid"><input type="text" name="history[${_}{i}].auth"  class="span12 _parameters_name_tex" value="${_}{data.history[i].auth}" /></div>
                                      </td>
                                      <td class="td_parameters_name td_TextArea">
                                         <div class="row-fluid"><input type="text" name="history[${_}{i}].date" class="span12 _parameters_name_tex" value="${_}{data.history[i].date}"/></div>
                                      </td>
                                      <td class="td_parameters_desc td_TextArea">
                                         <div class="row-fluid"><input type="text" name="history[${_}{i}].desc" class="span12 _parameters_desc_tex" value="${_}{data.history[i].desc}"/></div>
                                      </td>
                                  </tr>
                                  <!--$}-->

                               </tbody>
                          </table>
                        </form>
                        <div>
                        	<button type="button" class="btn btn-mini btn-success btn-add-con" onclick="FireEvent.addHistroyRecord();"><i class="icon-chevron-left bigger-120"> 添加记录</i></button>
                            <button type="button" class="btn btn-mini btn-success btn-add-con" onclick="FireEvent.delHistroyRecord();"><i class="icon-chevron-right bigger-120">删除记录</i></button>
                        </div>
                        <br/>
                        <div>
                            <a href="javascript:void(0)" onclick="FireEvent.updateInfo();" class="btn btn-info">
                                    <i class="icon-ok bigger-110"></i>
                                    确认
                            </a>
                            <a href="#none" class="btn btn-warning" onclick="FireEvent.goBack();">
                                    <i class="icon-ok bigger-110"></i>
                                    返回
                            </a>
                        </div>
                       
                    </div>
                    
                    <div class="FWRES" APPID="htmledit"  RESID="editForm">
                    	
                    	<div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">名称</label>
                                  <div class="controls">
                                    <input id="name" class="inp_text _name_inp" type="text" name="name" value="${_}{data.name}">
                                    <span class="help-inline">文件名</span>
                              	  </div>
                               </div>
                          </div>
                         </div>
                         <div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">路径</label>
                                  <div class="controls">
                                    <input id="dir" class="inp_text _name_inp" type="text" name="dir" value="${_}{data.dir}">
                                    <span class="help-inline">路径名</span>
                              	  </div>
                               </div>
                          </div>
                          <input type="hidden" id="oldName" value="${_}{data.name}"/>
                        </div>
                        <div>
                        	<button type="button" class="btn btn-mini btn-success btn-add-con" onclick="FireEvent.movenode('${_}{data.name}','moveup');"><i class="icon-chevron-left bigger-120"> 前移</i></button>
                            <button type="button" class="btn btn-mini btn-success btn-add-con" onclick="FireEvent.movenode('${_}{data.name}','movedown');"><i class="icon-chevron-right bigger-120"> 后移</i></button>
                        </div>
                        <br/>
                        <div>
                            <a href="javascript:void(0)" onclick="FireEvent.updateNode();" class="btn btn-info">
                                    <i class="icon-ok bigger-110"></i>
                                    确认
                            </a>
                            <a href="${_}{data.href}" class="btn btn-success" target="_blank">
                                    <i class="icon-ok bigger-110"></i>
                                    编辑
                            </a>

							<a href="#none" class="btn btn-danger" onclick="FireEvent.deleteNode('${_}{data.name}')">
                                    <i class="icon-ok bigger-110"></i>
                                    删除
                            </a>
                            <a href="#none" class="btn btn-warning" onclick="FireEvent.goBack();">
                                    <i class="icon-ok bigger-110"></i>
                                    返回
                            </a>
                        </div>
                         <br/><br/>
                        <h4>说明：</h4>
                        <h5>当前支持页面，servicegadget，service编辑器的编辑</h5>
                        <h5>页面文件扩展名是.jsp,service的文件扩展名是.js这两类型的路径都是从根开始写，不用以/开头</h5>
                        <h5>service的文件填写为扩展名是.brz，路径就是包名</h5>
                       
                    </div>
                    
                    <div class="FWRES" APPID="htmledit"  RESID="lineForm">
                    	<input type="hidden" id="idx" value="${_}{data.idx}"/>
                    	<div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">起始节点</label>
                                  <div class="controls">
                                    <select id="start" class="form-control" type="text" name="start" value="${_}{data.start}">
                                    <!--$for(var i=0;i<data.node.length;i++){-->
                                        <!--$if (data.start == data.node[i].name){-->
                                        <option value="${_}{data.node[i].name}" selected="true">${_}{data.node[i].name}</option>
                                        <!--$}else{-->
                                        <option value="${_}{data.node[i].name}">${_}{data.node[i].name}</option>
                                        <!--$}-->
                                    <!--$}-->
                                    </select>
                                    <span class="help-inline">起始节点</span>
                              	  </div>
                               </div>
                          </div>
                         </div>
                         <div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">终止节点</label>
                                  <div class="controls">
                                    <select id="end" class="form-control" type="text" name="end" value="${_}{data.end}">
                                    <!--$for(var i=0;i<data.node.length;i++){-->
                                        <!--$if (data.end == data.node[i].name){-->
                                        <option value="${_}{data.node[i].name}" selected="true">${_}{data.node[i].name}</option>
                                        <!--$}else{-->
                                        <option value="${_}{data.node[i].name}">${_}{data.node[i].name}</option>
                                        <!--$}-->
                                    <!--$}-->
                                    </select>
                                    <span class="help-inline">终止节点</span>
                              	  </div>
                               </div>
                          </div>
                        </div>
                        <div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">类型</label>
                                  <div class="controls">
                                    <select id="dtype" class="form-control" type="text" name="dtype" value="${_}{data.dtype}">
                                    <option value="${_}{data.dtype}" selected="true">${_}{data.dtype}</option>
                                    <option value="页面操作" >页面操作</option>
                                    <option value="函数调用" >函数调用</option>
                                    <option value="ServiceCall" >ServiceCall</option>
                                    <option value="DB调用" >DB调用</option>
                                    <option value="自返判断">自返判断</option>
                                    </select>
                                    <span class="help-inline">线类型</span>
                              	  </div>
                               </div>
                          </div>
                        </div>
                        <div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">简述</label>
                                  <div class="controls">
                                    <input id="desc" class="inp_text _name_inp" type="text" name="end" value="${_}{data.desc}">
                                    <span class="help-inline">如果是函数写函数名即可，尽量简单，因为写到外部线上</span>
                              	  </div>
                               </div>
                          </div>
                        </div>
                        <div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">详情</label>
                                  <div class="controls">
                                    <textarea id="detail" class="inp_text _name_inp" type="text" name="detail">${_}{data.detail||""}</textarea>
                                    <span class="help-inline">做什么，输入参数，输出参数</span>
                              	  </div>
                               </div>
                          </div>
                        </div>
                        <div id="infoForm" class="form-horizontal clearfix">
                          <div class="form-wrap pull-left clearfix">
                              <div class="control-group c_Text">
                                  <label for="data.name" class="control-label">场景跳转</label>
                                  <div class="controls">
                                    <input id="jumb" class="inp_text _name_inp" type="text" name="jumb" value="${_}{p:('parserJumb',data.jumb)}">
                                    <a href="javascript:void(0)" onclick="FireEvent.jumb2scene();">
                                            跳转
                                    </a>

                              	  </div>
                               </div>
                          </div>
                        </div>
                        <br/>
                        <div>
                            <a href="javascript:void(0)" onclick="FireEvent.updateLine('${_}{data.idx}');" class="btn btn-info">
                                    <i class="icon-ok bigger-110"></i>
                                    确认
                            </a>

							<a href="#" class="btn btn-danger" onclick="FireEvent.deleteLine('${_}{data.idx}')">
                                    <i class="icon-ok bigger-110"></i>
                                    删除
                            </a>
                            <a href="#none" class="btn btn-warning" onclick="FireEvent.goBack();">
                                    <i class="icon-ok bigger-110"></i>
                                    返回
                            </a>
                        </div>
                    </div>
				</div>
				<!-- footer -->



				<!-- //脚本 -->
				<script src="../breeze/lib/js/jquery.js"></script>
				<script src="../breeze/lib/js/sea.js"></script>

				<script>
					seajs.config({
						base: "${B}"
					});
					seajs.use(['manager_auxiliary/service/editSequence'], function(a) {
						a.go("${S}");
						window.FW = a;
					});
				</script>

			</body>

			</html>