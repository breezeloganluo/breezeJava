<div style="heitht:60px;" >
    <div class="alert alert-danger" role="alert" style="padding-top: 5px;padding-bottom: 5px;margin-bottom: 5px;">
        <span class="glyphicon glyphicon-th" aria-hidden="true"></span>
        请输入要上传的服务器地址，记得带上http和应用名，例如http://www.joinlinking.com/div/
    </div>
    <input type="text" style="width:100%;margin-top:20px;margin-bottom:20px;" id="mask_upload2other"/>

    <div style="text-align:center">

    <button type="button" class="btn btn-info" onclick="FireEvent.comfirmUpload2Other('${data.baseDir}','${data.fileName}')">确定</button>

    <button type="button" class="btn btn-warning" onclick="FW.unblockUI();">返回</button>
    </div>
</div>