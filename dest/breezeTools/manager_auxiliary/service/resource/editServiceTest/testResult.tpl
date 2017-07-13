	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#testresultgroup" href="#result${data.casename}"
                   <!--$if (data.code != 0){-->
						style="color:red"
				   <!--$}-->
                >
					<i class="bigger-110 icon-angle-right" data-icon-hide="icon-angle-down" data-icon-show="icon-angle-right">${data.casename}</i>
					&nbsp;
					<!--$if (data.code == 0){-->
						测试通过
					<!--$}else{-->
						${data.error.obj}测试不通过
					<!--$}-->
				</a>
			</h4>
		</div>

		<div class="panel-collapse collapse" id="result${data.casename}" style="height: 0px;">
			<div class="panel-body">
				<!--$if (data.code == 0){-->
						测试通过
					<!--$}else{-->
						期望数据:<br/>
                        <textarea style="width:100%;height:100px">
                           ${data.error.expect}  
                        </textarea>
                        实际返回数据:<br/>
                        <textarea style="width:100%;height:100px">
                           ${data.error.result}  
                        </textarea>
					<!--$}-->
			</div>
		</div>
	</div>