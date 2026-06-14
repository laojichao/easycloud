<?php
include_once '../includes/common.php';
if($islogin==1){}else sysmsg("您还未登录，请先登录",2,'./login.php',true);
if(isset($_GET['id'])) {
$id=isset($_GET['id'])?intval($_GET['id']):sysmsg("参数错误",2,'./',true);
$row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
if(!$row)sysmsg("系统不存在该应用",2,'./applist.php',true);
$title='编辑应用';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                编辑应用
            </div>
            <div class="card-body">
                <form class="form-horizontal layui-form">
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">应用名称：</label>
                        <input type="text" name="name" value="<?php echo $row['name']?>" class="form-control" placeholder="输入该应用名称" lay-verType="tips" lay-verify="required">
                    </div>
                    <div class="form-group mb-3">
                        <input type="file" id="file" onchange="fileUpload()" style="display:none;"/>
                        <label for="example-input-normal" style="font-weight: 500">应用图标：<span class="badge badge-success-lighten" onclick="fileView()">查看应用图标</span> <a href="javascript:fileSelect()" class="badge badge-danger-lighten">上传应用图标</a></label>
                        <input type="text" name="img" value="<?php echo $row['img']?>" class="form-control" placeholder="上传该应用图标" lay-verType="tips" lay-verify="required">
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">应用公告：</label>
                        <textarea name="appgg" class="form-control" style="height:100px;" placeholder="此处为应用公告，没有可先不填" lay-verType="tips"><?php echo $row['app_gg']?></textarea>
                    </div>
					<div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">应用版本号：</label>
                        <input type="text" name="version" value="<?php echo $row['version']?>" class="form-control">
                        <small><font color="red">说明：版本号类似于1.0 1.5 2.0 3.0 （显示用）</font></small>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">应用版本信息：</label>
                        <textarea name="version_info" class="form-control" placeholder="此处为应用版本信息，没有可先不填" style="height:100px;"><?php echo $row['version_info']?></textarea>
                    </div>
					<div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">是否强制更新：</label>
                        <select name="app_update_must" class="form-control" lay-search lay-filter="app_update_must"><option <?php echo $row['app_update_must'] == y ? 'selected ' : '' ?>value="y">开启</option><option <?php echo $row['app_update_must'] == n ? 'selected ' : '' ?>value="n">关闭</option></select>
						<small><font color="red">说明：开启强制更新后，用户必须更新，否则无法使用</font></small>
                    </div>
                    <div class="form-row">
							<div class="form-group col-md-2">
								<label for="example-input-normal" style="font-weight: 500">更新方式</label>
								<select name="type" class="form-control" lay-search lay-filter="type"><option <?php echo $row['app_update_url_type'] == lanzou ? 'selected ' : '' ?> value="lanzou">蓝奏云</option><option <?php echo $row['app_update_url_type'] == other ? 'selected ' : '' ?> value="other">其他外链</option></select>
							</div>
							<div class="form-group col-md-10">
								<label for="example-input-normal" style="font-weight: 500" id="amount_name">更新地址</label>
								<div class="input-group">
									<input type="text" id="app_update_url" name="app_update_url" class="form-control" value="<?php echo $row['app_update_url']?>" placeholder="请在此处填写下载直链">
									<div class="input-group-prepend" id="frame_set1" style="display:inherit">
									<input type="text" id="lanzou_pass" name="lanzou_pass" class="form-control" value="<?php echo $row['lanzou_pass']?>" placeholder="外链密码,没有则不填写">
									</div>
								</div>
							</div>
						</div>
					<div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">更新内容：</label>
                        <textarea name="app_update_show" class="form-control" style="height:100px;" placeholder="此处为更新内容，没有可先不填" lay-verType="tips"><?php echo $row['app_update_show']?></textarea>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">应用是否运行：</label>
                        <select name="active" class="form-control" lay-search lay-filter="active"><option <?php echo $row['active'] == y ? 'selected ' : '' ?>value="y">开启</option><option <?php echo $row['active'] == n ? 'selected ' : '' ?>value="n">关闭</option></select>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_appedit">保存内容</button>
                </form>
            </div>
        </div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<script type="text/javascript">
layui.use(['form'], function () {
    var form = layui.form;
    form.on('submit(submit_appedit)', function (data) {
        layer.alert('是否要执行当前操作？', {
            icon: 3, btn: ['确定', '取消'], btn1: function (layero, index) {
                appedit();
            }
        });
        return false;
    });
	form.on('select(type)', function(data){ 
        if(data.value == 'lanzou'){
				  $("#frame_set1").css("display","inherit");
				  document.getElementById('file_url').setAttribute("placeholder","蓝奏云外链链接");
    }
	    if(data.value == 'other'){
				  $("#frame_set1").css("display","none");
				  document.getElementById('file_url').setAttribute("placeholder","其他外链链接");
		}
    });
});
function fileSelect(){
    $("#file").trigger("click");
}
function fileView(){
    var img = $("input[name='img']").val();
    if(img=='') {
        layer.msg("请先上传图片，才能预览", {icon: 5});
        return;
    }
    if(img.indexOf('http') == -1)img = '../'+img;
    layer.open({
        type: 1,
        area: ['360px', '400px'],
        title: '应用图标查看',
        shade: 0.3,
        anim: 1,
        shadeClose: true,
        content: '<center><img width="300px" src="'+img+'"></center>'
    });
}
function fileUpload(){
    var fileObj = $("#file")[0].files[0];
    if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
        return;
    }
    var formData = new FormData();
    formData.append("do","upload");
    formData.append("type","program");
    formData.append("file",fileObj);
    var ii = layer.msg('正在上传中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        url: "ajax.php?act=uploadappimg",
        data: formData,
        type: "POST",
        dataType: "json",
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {
            layer.close(ii);
            if(data.code == 0){
                layer.msg('上传应用图标成功', {icon: 6});
                $("input[name='img']").val(data.url);
            }else{
                layer.alert(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
    })
}
function appedit() {
    var name = $("input[name='name']").val();
    var img = $("input[name='img']").val();
    var appgg = $("textarea[name='appgg']").val();
    var version = $("input[name='version']").val();
    var version_info = $("textarea[name='version_info']").val();
    var active = $("select[name='active']").val();
	var app_update_must = $("select[name='app_update_must']").val();
    var app_update_url = $("input[name='app_update_url']").val();
    var app_update_show = $("textarea[name='app_update_show']").val();
    var type = $("select[name='type']").val();
	var app_update_url = $("input[name='app_update_url']").val();
	var lanzou_pass = $("input[name='lanzou_pass']").val();
    var ii = layer.msg('正在修改中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type: "POST",
        url: "ajax.php?act=appedit&id=<?php echo $id;?>",
        data : {name:name,img:img,appgg:appgg,version:version,version_info:version_info,active:active,app_update_must:app_update_must,type:type,app_update_url:app_update_url,lanzou_pass:lanzou_pass,app_update_show:app_update_show},
        dataType: "json",
        success: function(data) {
            layer.close(ii);
            if (data.code == 0) {
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        },
        error:function(data){
            layer.msg('服务器错误', {icon: 5});
            return false;
        }
    });
    return false;
};
</script>
<?php
}else{
    sysmsg("参数错误",2,'./',true);
}
?>