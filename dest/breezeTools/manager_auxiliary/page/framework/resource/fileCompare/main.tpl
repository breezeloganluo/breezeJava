<div>
    <div class="row">
        <div class="col-lg-12" id="buttonHead">
        
                                <input name="ctype" type="button" value="开始比较"  onclick="FireEvent.loadFiles();"></input>

        </div>
    </div>
</div>
<div class="container-fluid">
    <div class="row">
    
        <div class="col-lg-6" id="leftsite">
             
             <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
                <span class="glyphicon glyphicon-th" aria-hidden="true">文件属性选择</span>
                <span class="pull-right  glyphicon glyphicon-share-alt" style="margin-left: 3px;margin-right: 3px;" title="历史文件"></span>
                <a href="#" class="pull-right glyphicon glyphicon-file" style="margin-left: 3px;margin-right: 3px;" onclick="var args=[];var app = FW.getAPP('undefined_[0].content[0][0]');app.FireEvent.openNewFile.apply(app,args)" title="新文件"></a>
            </div>
            <div >
                <form class="form-horizontal" id="singleFormDataLeft">
                    <div class="form-group">
                        <label class="control-label col-lg-2">文件类型</label>
                        <div class="col-lg-10" id="field_ra" data-value="ra" data-type="Radio">
                            <label class="radio-inline">
                                <input name="ctype" checked="checked" type="radio" value="当前文件" onclick="FireEvent.selectType('Left');">当前文件
                            </label>
                            <label class="radio-inline">
                                <input name="ctype" type="radio" value="历史文件"  onclick="FireEvent.selectType('Left');">历史文件
                            </label>
                        </div>
                    </div>
                    <div id="moreSetLeft">
                    </div>
                </form>
            </div>
            
        </div>
        
        
        
        <div class="col-lg-6" id="rightsite">
        
            <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
                <span class="glyphicon glyphicon-th" aria-hidden="true">文件属性选择</span>
                <span class="pull-right  glyphicon glyphicon-share-alt" style="margin-left: 3px;margin-right: 3px;" title="历史文件"></span>
                <a href="#" class="pull-right glyphicon glyphicon-file" style="margin-left: 3px;margin-right: 3px;" onclick="var args=[];var app = FW.getAPP('undefined_[0].content[0][0]');app.FireEvent.openNewFile.apply(app,args)" title="新文件"></a>
            </div>
            <div >
                <form class="form-horizontal" id="singleFormDataRight">
                    <div class="form-group">
                        <label class="control-label col-lg-2">文件类型</label>
                        <div class="col-lg-10" id="field_ra" data-value="ra" data-type="Radio">
                            <label class="radio-inline">
                                <input name="ctype" checked="checked" type="radio" value="当前文件" onclick="FireEvent.selectType('Right');">当前文件
                            </label>
                            <label class="radio-inline">
                                <input name="ctype" type="radio" value="历史文件"  onclick="FireEvent.selectType('Right');">历史文件
                            </label>
                        </div>
                    </div>
                    <div id="moreSetRight">
                    </div>
                </form>
            </div>
        
        </div>
        
    </div>
</div>