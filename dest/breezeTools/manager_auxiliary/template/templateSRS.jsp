<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:include page="/page/allhead.jsp"/>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title></title>

		<meta name="description" content="Draggabble Widget Boxes &amp; Containers" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />

		<!--basic styles-->

		<link href="${B}manager_auxiliary/assets/css/bootstrap.min.css" rel="stylesheet" />
		<link href="${B}manager_auxiliary/assets/css/bootstrap-responsive.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="${B}manager_auxiliary/assets/css/font-awesome.min.css" />

		<!--[if IE 7]>
		  <link rel="stylesheet" href="${B}manager_auxiliary/assets/css/font-awesome-ie7.min.css" />
		<![endif]-->

		<!--page specific plugin styles-->

		<!--ace styles-->

		<link rel="stylesheet" href="${B}manager_auxiliary/assets/css/ace.min.css" />
		<link rel="stylesheet" href="${B}manager_auxiliary/assets/css/ace-responsive.min.css" />
		<link rel="stylesheet" href="${B}manager_auxiliary/assets/css/ace-skins.min.css" />

		<!--[if lte IE 8]>
		  <link rel="stylesheet" href="${B}manager_auxiliary/assets/css/ace-ie.min.css" />
		<![endif]-->

		<!--inline styles related to this page-->
		
		
		<script src="${B}breeze/lib/js/jquery.js"></script>
		<script type="text/javascript">
			$(function(){
				var t = $($(".span12")[0]);
			
				var parserSRSObject = function(str){
					var execArray = /<!--SRS\(([^\)]+)\)([\s\S]+?)--\>/ig.exec(str);
					if (execArray == null){
						return "没有需求点";
					}
					var srsObj = eval("("+execArray[2]+")");
					var result = execArray[1]+":<br/>";
					result += "<table width='100%' border='1'>";
					for (var i=0;i<srsObj.length;i++){
						result +="<tr>";
						result +="<td width='30%'>";
						result += srsObj[i].name;
						result +="</td>";
						result +="<td width='70%'>";
						result +=srsObj[i].desc;
						result +="</td>";
						result +="</tr>";
					}
					result += "</table>";
					return result;
				}
	
				t.find("div[class^='span']>*").not("*[class='row-fluid']").mouseover(function(){
					(function(spanObj){
						var processor = function(){
							var value = parserSRSObject(spanObj.innerHTML);
							var displayContent = '<div id="pupbox" class="pupbox" style="background:#f9f9f9;border:1px solid #ccc;width:1000px;height:600px;overflow-y:auto;position:absolute;z-index:99;left:10px;top:10px;padding:10px;border-radius:3px;"><p>---c---</p></div>';
							displayContent = displayContent.replace(/---c---/i,value);
							//添加弹出内容层
							$("body").append(displayContent);
							//添加蒙版层
							$("body").append('<div id="pupbg" style="background:#000;opacity:0.7;width:100%;height:100%;position:absolute;z-index:2;left:0px;top:0px;"></div>')
							
							//设置在页面的位置为居中
							$(".pupbox").css("left",($(document).width()-$(".pupbox").width())/2);
							$(".pupbox").css("top",($(document).height()-$(".pupbox").height())/2);
	
	
							//点击弹出内容层或蒙版层隐藏内容
							$("#pupbox,#pupbg").click(function(){
								$(".pupbox,#pupbg").hide()
							})
	
												
						}
	
						var aDom = $(spanObj).find(".button_openSrs_asdfeett")[0];
						$(".button_openSrs_asdfeett").each(function(){
							if (this == aDom){
								return;
							}
							$(this).remove();
						});
						if (aDom == null){
							if (/<!--SRS\(([^\)]+)\)([\s\S]+?)--\>/i.test(spanObj.innerHTML)){
								$(spanObj).append("<a href='javascript:void(0)' class='button_openSrs_asdfeett'>查看需求</a>");
								($(spanObj).find(".button_openSrs_asdfeett"))[0].onclick=processor;
							}else{
								$(spanObj).append("<a href='javascript:void(0)' class='button_openSrs_asdfeett'>-----</a>");
							}
							($(spanObj).find("a"))[0].openStatus = true;					
						}
						
					})(this);
				})
			});

			
		</script>
	</head>

	<body>

		<div class="main-container container-fluid">
			
			

				<div class="page-content">

					<div class="row-fluid">
						<div class="span12">
				<div class="row-fluid" id="row[0]">
					<div class="span12" id="col[0][0]">
									<div id="navbar" class="navbar navbar-default" style="background:#2c6aa0;height:50px;width:100%;">
      

      <div id="navbar-container" class="navbar-container">
        <div class="navbar-header pull-left" style="color:#fff;font-size:14px; padding-left:10px; line-height:50px;">
          <a class="navbar-brand" href="#">
            <small style="color:#fff;font-size:14px;">
              <i class="icon-leaf" style="color:#fff;font-size:14px;"></i>
              众联无限后台管理系统
            </small>
          </a>
        </div>
		
		<div role="navigation" class="navbar-header pull-right">
          <ul class="nav ace-nav">
            

            <li class="light-blue">
              <a class="dropdown-toggle" href="#" data-toggle="dropdown">
               
				
                <span class="user-info">
                  <small>欢迎光临,</small>
                  超级管理员
                </span>

                <i class="icon-caret-down"></i>
              </a>

              <ul class="user-menu pull-right dropdown-menu dropdown-yellow dropdown-caret dropdown-close">
                <li>
                  <a href="#">
                    <i class="icon-cog"></i>
                    设置
                  </a>
                </li>

                <li>
                  <a href="#">
                    <i class="icon-user"></i>
                    个人资料
                  </a>
                </li>

                <li class="divider"></li>

                <li>
                  <a href="#">
                    <i class="icon-off"></i>
                    退出
                  </a>
                </li>
              </ul>
            </li>
          </ul>
        </div>

        
      </div>
    </div>
					</div>
				</div><!--/row-->
				<div class="row-fluid" id="row[1]">
					<div class="span2" id="col[1][0]">
									<div id="sidebar" class="sidebar" style="width: 189px;height: 100%; position: static;">
					

					<ul class="nav nav-list">
						


						<li class="open">
							<a class="dropdown-toggle" href="#">
								<i class="icon-dashboard"></i>
								<span class="menu-text"> 会员管理 </span>

								<b class="arrow icon-angle-down"></b>
							</a>

							<ul class="submenu" style="display: block;">
								<li>
									<a href="#">
										<i class="icon-double-angle-right"></i>
										会员管理
									</a>
								</li>

								<li>
									<a href="#">
										<i class="icon-double-angle-right"></i>
										在线充值
									</a>
								</li>

							
							</ul>
						</li>

						<li>
							<a class="dropdown-toggle" href="#">
								<i class="icon-dashboard"></i>
								<span class="menu-text"> 权限管理 </span>

								<b class="arrow icon-angle-down"></b>
							</a>

							
						</li>

					</ul>
<textarea style="display:none">  
  //这是为了支持独立模块进行菜单操作使用的脚本，该脚本能够获取菜单url并自动控制哪个菜单项显示那个菜单项不显示
  //1.小菜单的a标签要有样式menuctr
  //2.默认小菜单是不显示的
  //3.大菜单a标签也有启动后第一个页面的url
  //4.将本标签的textarea标签改成script跟随放到独立文件中，并且放到div标签的外面
  //5.原来被编辑的页面要用模块共享属性modulesrc="共享模板文件的地址"，那么外部的这个标签就会被统一成这个共享模块的内容
  var url = location.href.toString();
  var execResult = /([\w\.]+)#?$/.exec(url);
  if (execResult == null){
    alert('解析url出错，无法显示左边菜单');
    
  }
  var file = execResult &amp;&amp; execResult[1];
  if (file){
    $(".menuctr").each(function(){
      var href = this.href;
      var execResult = /([\w\.]+)$/.exec(href);
      
      if (execResult == null){
        return;
      }
      
      if (execResult[1] == file){
        this.innerHTML = "["+this.innerHTML+"]";
        $(this).parents(".submenu").show();
      }
      
    });
  }
</textarea>
					
</div>
									<textarea style="display:none">  //这个是支持了模块提取功能后，针对菜单项如果单独提取的话做的脚本，他可以根据当前 页面的url进行判断哪个菜单是被选中的，然后显示哪一个
  //要求的接口：在a标签中必须有样式menuctr，另外标签默认是隐藏的，还有就是一级菜单的a标签也要有连接
  var url = location.href.toString();
  var execResult = /([\w\.]+)#?$/.exec(url);
  if (execResult == null){
    alert('解析url出错，无法显示左边菜单');
    
  }
  var file = execResult &amp;&amp; execResult[1];
  if (file){
    $(".menuctr").each(function(){
      var href = this.href;
      var execResult = /([\w\.]+)$/.exec(href);
      
      if (execResult == null){
        return;
      }
      
      if (execResult[1] == file){
        this.innerHTML = "["+this.innerHTML+"]";
        $(this).parents(".submenu").show();
      }
      
    });
  }
</textarea>
					</div>
					<div class="span10" id="col[1][1]">
									<div id="breadcrumbs" class="breadcrumbs">
						

						<ul class="breadcrumb">
							<li>
								<i class="icon-home home-icon"></i>
								<a href="#">首页  &gt; </a>
							</li>

							<li>
								<a href="#">二级  &gt; </a>
							</li>
							<li class="active">当前</li>
						</ul>

						<button class="btn btn-primary pull-right">添加</button>
</div>
									<div>
    	<style>
	        table.dataintable {
	        margin-top:10px;
	        border-collapse:collapse;
	        border:1px solid #eee;
	        width:100%;
	        }
	
	      table.dataintable th {
	        vertical-align:baseline;
	        padding:5px 15px 5px 6px;
	        background-color:#f1f1f1;
	        border:1px solid #eee;
	        text-align:left;
	        }
	
	      table.dataintable td {
	        vertical-align:text-top;
	        padding:6px 15px 6px 6px;
	        background-color:#f9f9f9;
	        border:1px solid #eee;
	        }
	    </style>
	
	  <table class="dataintable">
	      <tbody><tr>
	        <th>帐号</th>
	        <th>昵称</th>
            <th>操作</th>
	      </tr>
	
	        <tr>
	        <td><a href="/cssref/pr_tab_border-collapse.asp" title="">131341</a></td>
	        <td>wang</td>
            <td>
              <a href="javascript:void(0);" title="编辑" class="FormOperBtn btn btn-mini btn-info _formlist_editbtn_inp"> <i class="icon-edit bigger-120"></i></a>
              <a href="javascript:void(0);" title="删除" class="FormOperBtn btn btn-mini btn-danger _formlist_delbtn_inp"> <i class="icon-trash bigger-120"></i></a> 
             </td>
	        </tr>
	
	        <tr>
	        <td><a href="/cssref/pr_tab_border-spacing.asp" title="">24241</a></td>
	        <td>wangwi</td>
            <td>
              <a href="javascript:void(0);" title="编辑" class="FormOperBtn btn btn-mini btn-info _formlist_editbtn_inp"> <i class="icon-edit bigger-120"></i></a>
              <a href="javascript:void(0);" title="删除" class="FormOperBtn btn btn-mini btn-danger _formlist_delbtn_inp"> <i class="icon-trash bigger-120"></i></a> 
             </td>
	        </tr>
	
	        <tr>
	        <td><a href="/cssref/pr_tab_caption-side.asp" title="">142111</a></td>
	        <td>ikwj</td>
            <td>
              <a href="javascript:void(0);" title="编辑" class="FormOperBtn btn btn-mini btn-info _formlist_editbtn_inp"> <i class="icon-edit bigger-120"></i></a>
              <a href="javascript:void(0);" title="删除" class="FormOperBtn btn btn-mini btn-danger _formlist_delbtn_inp"> <i class="icon-trash bigger-120"></i></a> 
             </td>
	        </tr>
	
	      </tbody>
	   </table> 
    </div>
									<div class="pagination">
              <ul>
                <li><a href="#">«</a></li>
                <li><a href="#">1</a></li>
                <li><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li><a href="#">4</a></li>
                <li><a href="#">5</a></li>
                <li><a href="#">»</a></li>
              </ul>
    </div>
					</div>
				</div><!--/row-->
			</div><!--/.span-->
					</div><!--/.row-fluid-->
				</div><!--/.page-content-->

			
			
		</div><!--/.main-container-->

		






		

		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
		<script src="${B}manager_auxiliary/assets/js/bootstrap.min.js"></script>

		<!--page specific plugin scripts-->

		<script src="${B}manager_auxiliary/assets/js/jquery-ui-1.10.3.custom.min.js"></script>
		<script src="${B}manager_auxiliary/assets/js/jquery.ui.touch-punch.min.js"></script>
		<script src="${B}manager_auxiliary/assets/js/jquery.slimscroll.min.js"></script>

		<!--ace scripts-->

		<script src="${B}manager_auxiliary/assets/js/ace-elements.min.js"></script>
		<script src="${B}manager_auxiliary/assets/js/ace.min.js"></script>

		<!--inline scripts related to this page-->

		<script type="text/javascript">
			$(function() {
				// Portlets (boxes)
			    $('.widget-container-span').sortable({
			        connectWith: '.widget-container-span',
					items:'> .widget-box',
					opacity:0.8,
					revert:true,
					forceHelperSize:true,
					placeholder: 'widget-placeholder',
					forcePlaceholderSize:true,
					tolerance:'pointer'
			    });
				$('.widget-container-span').sortable({
			        connectWith: '.widget-container-span',
					items:'> .widget-box',
					opacity:0.8,
					revert:true,
					forceHelperSize:true,
					placeholder: 'widget-placeholder',
					forcePlaceholderSize:true,
					tolerance:'pointer'
			    });
				
				$(".widget-box").mouseover(function(){
					$(this).find(".widget-header").show();
				})
				$(".widget-box").mouseout(function(){
					$(this).find(".widget-header").hide();
				})
			});
		</script>
		<!--共享魔板的模块-->
		<script>
           var setModule = function(jqDom){
              if (jqDom == null){
                 jqDom = $("[moduleSrc]");//所有含有moduleSrc的都是目标
              }
              jqDom.each(function(){
              	var dom = $(this);
                var src = dom.attr("moduleSrc");
                if (src == null){
                  return;
                }
                //下面用ajax获取文本内容
                $.ajax({ url: src, context: this, success: function(data){
                  $(this).html(data);
                }});
              });
           }
           
           $(function(){
             setModule();
           });
           
           window.afterSaveModule = function(dom){
               setModule(dom);
           }
        </script>
		
	</body>
</html>
