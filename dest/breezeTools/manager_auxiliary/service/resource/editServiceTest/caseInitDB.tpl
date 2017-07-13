<div class="table-header">
											${data.name}
                                            <label class="pull-right inline">
                                                    <a href="#" class="icon-plus-sign-alt bigger-160 alert-info" onclick="FireEvent.newInitDBData('${data.idx}','${data.name}')">
                                                    </a>
                                                    <a href="#" class="icon-minus-sign-alt bigger-160 alert-info" onclick="FireEvent.deleteInitDB('${data.idx}','${data.name}')">
                                                    </a>
                                            </label>
</div>
<table id="sample-table-1" class="table table-striped table-bordered table-hover">
												<thead>
													<tr>
                                                        <!--$for(var i=0;i<data.struct.length;i++){-->
														<th>${data.struct[i] }</th>
                                                        <!--$}-->
													</tr>
												</thead>

												<tbody>
                                                    <!--$for(var i=0;i<data.length;i++){-->
													<tr>
                                                        <!--$for(var ii=0;ii<data.struct.length;ii++){-->
														
                                                                <!--$if (data.row && data.colName && data.row == i && data.colName == data.struct[ii]){-->
                                                                    <td class="center">
																    <label>
                                                                    <textarea id="dbinput" idx="${data.row}" name="${data.colName}">${data[i][data.struct[ii]]||""}</textarea>
                                                                    </label>
														            </td>
                                                                <!--$}else{-->
                                                                <td class="center" onclick="FireEvent.showCaseDB('${data.idx}','${data.name}','${i}','${data.struct[ii]}')">
																    <label>
																        ${data[i][data.struct[ii]] || "" }
                                                                    </label>
														         </td>
                                                                <!--$}-->
															
                                                        <!--$}-->
													</tr>
                                                    <!--$}-->
												</tbody>
</table>