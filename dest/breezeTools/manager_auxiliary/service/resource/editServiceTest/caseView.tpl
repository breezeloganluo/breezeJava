<div class="tabbable">
											<ul class="nav nav-tabs" id="myTab">
												<li class="active">
													<a data-toggle="tab" href="#case_home" >
														<i class="green icon-home bigger-110"></i>
														基本信息
													</a>
												</li>
                                                
                                                <li class="">
													<a data-toggle="tab" href="#case_param"  onclick="FireEvent.showCaseParam('${data.idx}')">
														测试参数
													</a>
												</li>

												<li class="">
													<a data-toggle="tab" href="#case_session"  onclick="FireEvent.showCaseMemSession('${data.idx}')">
														内存与session
													</a>
												</li>

												<li class="dropdown">
													<a data-toggle="dropdown" class="dropdown-toggle" href="#">
														数据库设置 &nbsp;
														<i class="icon-caret-down bigger-110 width-auto"></i>
													</a>

													<ul class="dropdown-menu dropdown-info">
														<!--$for(var i=0;i<data.tables.length;i++){-->
														<li class="">
															<a data-toggle="tab" href="#tables" onclick="FireEvent.showCaseDB('${data.idx}','${data.tables[i]}',null,null,'initData')">${data.tables[i]}</a>
														</li>
                                                        <!--$}-->
													</ul>
												</li>
                                                <li class="dropdown">
													<a data-toggle="dropdown" class="dropdown-toggle" href="#">
														期待结果 &nbsp;
														<i class="icon-caret-down bigger-110 width-auto"></i>
													</a>

													<ul class="dropdown-menu dropdown-info">
                                                        <li class="">
															<a data-toggle="tab" href="#expresult" onclick="FireEvent.showExpMsgResult('${data.idx}')">service结果</a>
														</li>
                                                        <!--$for(var i=0;i<data.tables.length;i++){-->
														<li class="">
															<a data-toggle="tab" href="#tables" onclick="FireEvent.showCaseDB('${data.idx}','${data.tables[i]}',null,null,'expDBResult')">${data.tables[i]}</a>
														</li>
                                                        <!--$}-->
													</ul>
												</li>
											</ul>

											<div class="tab-content" >
												<div id="case_home" class="tab-pane active">
                                                    <div id="case_home_show">
													用例名称:<input type="text" value="${data.caseName}" id="casenametext"/>
                                                    </div>
                                                    <button class="btn btn-success" onclick="FireEvent.saveCaseName('${data.idx}')">保存</button>
                                                    <button class="btn btn-success" onclick="FireEvent.deleteCase('${data.idx}')">删除用例</button>
                                                    <button class="btn btn-success" onclick="FireEvent.goTest('${data.idx}')">去测试</button>
												</div>
                                                
                                                <div id="case_param" class="tab-pane">
                                                     <div id="case_param_show">
                                                     </div>
                                                </div>

												<div id="case_session" class="tab-pane">
                                                    <div id="case_session_show" class="container-fluid">
                                                       
                                                    </div>
													
												</div>
                                                
                                                <div id="tables" class="tab-pane">
                                                    <div id="tablescontent">
													    <p>数据表显示错误</p>
                                                    </div>
												</div>
                                                
                                                <div id="expresult" class="tab-pane">
                                                <div id="expresultcontent">
													<p>数据表显示错误</p>
                                                </div>
												</div>

												
											</div>
										</div>