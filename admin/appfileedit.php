<?php
include_once '../includes/common.php';
if($islogin==1){}else sysmsg("您还未登录，请先登录",2,'./login.php',true);
if(isset($_GET['id'])) {
$id=isset($_GET['id'])?intval($_GET['id']):sysmsg("参数错误",2,'./',true);
$row=$DB->get_row("SELECT * FROM yixi_appfile WHERE id='{$id}' limit 1");
if(!$row)sysmsg("系统不存在该文件",2,'./applist.php',true);
$title='文件编辑';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                文件编辑
            </div>
            <div class="card-body">
                <form class="form-horizontal layui-form">
				         <div class="form-group mb-3">
                            <label for="example-input-normal" style="font-weight: 500">归属应用：</label>
                            <select name="proid" class="form-control" lay-search lay-filter="proid">
                            <?php
                            $rs=$DB->query("SELECT * FROM yixi_apps WHERE 1 order by id desc");
                            while($res = $DB->fetch($rs))
                            {
                            echo '<option value="'.$res['id'].'"'.($row['appid']==$res['id']?' selected="selected" ':'').'>'.$res['name'].'</option>';
                            }
                            ?>
                            </select>
                         </div>
					<div class="form-row">
							<div class="form-group col-md-2">
								<label for="example-input-normal" style="font-weight: 500">云端选择</label>
								<select name="type" class="form-control" lay-search lay-filter="type"><option value="lanzou" <?php if($row['type']=='lanzou')echo'selected="selected"';?>>蓝奏云</option><option value="other" <?php if($row['type']=='other')echo'selected="selected"';?>>其他外链</option></select>
							</div>
							<div class="form-group col-md-10">
								<label for="example-input-normal" style="font-weight: 500" id="amount_name">外链地址</label>
								<div class="input-group">
									<input type="text" id="file_url" name="file_url" class="form-control" value='<?php echo $row['file_url'];?>' placeholder="请在此处填写下载直链">
									<div class="input-group-prepend" id="frame_set1" style="display:<?php echo $row['type'] == 'lanzou' ? 'inherit ' : 'none' ?>">
									<input type="text" id="lanzou_pass" name="lanzou_pass" class="form-control" value='<?php echo $row['lanzou_pass'];?>' placeholder="外链密码,没有则不填写">
									</div>
								</div>
							</div>
						</div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_editappfile">保存</button>
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
    form.on('submit(submit_editappfile)', function (data) {
        layer.alert('是否要执行当前操作？', {
            icon: 3, btn: ['确定', '取消'], btn1: function (layero, index) {
                editappfile();
            }
        });
        return false;
    });
    form.on('select(type)', function(data){ 
        if(data.value == 'lanzou'){
				  $("#frame_set1").css("display","inherit");
				  document.getElementById('file_url').setAttribute("placeholder","蓝奏云外链链接--蓝奏云暂不可用");
    }
	    if(data.value == 'other'){
				  $("#frame_set1").css("display","none");
				  document.getElementById('file_url').setAttribute("placeholder","其他外链链接");
		}
    });
});
function editappfile() {
    var type = $("select[name='type']").val();
	var file_url = $("input[name='file_url']").val();
    var proid = $("select[name='proid']").val();
	var lanzou_pass = $("input[name='lanzou_pass']").val();
    var ii = layer.msg('正在添加中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type: "POST",
        url: "ajax.php?act=editappfile",
        data : {id:<?php echo $id;?>,type:type,file_url:file_url,proid:proid,lanzou_pass:lanzou_pass},
        dataType: "json",
        success: function(data) {
            layer.close(ii);
            if (data.code == 0) {
                layer.msg(data.msg, {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.href = 'appfilelist.php';
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