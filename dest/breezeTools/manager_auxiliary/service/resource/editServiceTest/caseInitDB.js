<table id="sample-table-1" class="table table-striped table-bordered table-hover">
												<thead>
													<tr>
                                                        <!--$for(var i=0;i<data.struct.length;i++){
														<th>${data.struct[i]}</th>
                                                        <!--$}-->
													</tr>
												</thead>

												<tbody>
                                                    <!--$for(var i=0;i<data.length;;i++){-->
													<tr>
                                                        <!--$for(var ii=0;ii<data.struct.length;ii++){
														<td class="center">
															<label>
																${data[i][data.struct[ii]]}
															</label>
														</td>
                                                        <!--$}-->
													</tr>
                                                    <!-$-}-->
												</tbody>
</table>