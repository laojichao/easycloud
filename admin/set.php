<?php
/**
 * 系统设置
**/
include_once '../includes/common.php';
if($islogin==1){}else sysmsg("您还未登录，请先登录",2,'./login.php',true);
$mod=isset($_GET['mod'])?$_GET['mod']:sysmsg("参数错误",2,'./',true);
if($mod=='account_n' && $_POST['do']=='submit'){
    $user=$_POST['user'];
    $oldpwd=$_POST['oldpwd'];
    $newpwd=$_POST['newpwd'];
    $newpwd2=$_POST['newpwd2'];
    if($user==null)sysmsg('用户名不能为空！',2,'./set.php?mod=account',true);
    saveSetting('admin_user',$user);
    if(!empty($newpwd) && !empty($newpwd2)){
        if($oldpwd!=$conf['admin_pwd'])sysmsg('旧密码不正确！',2,'./set.php?mod=account',true);
        if($newpwd!=$newpwd2)sysmsg('两次输入的密码不一致！',2,'./set.php?mod=account',true);
        saveSetting('admin_pwd',$newpwd);
    }
    $ad=$CACHE->clear();
    if($ad)sysmsg('修改成功！请重新登录',1,'./set.php?mod=account',true);
    else sysmsg('修改失败！<br/>'.$DB->error(),2,'./set.php?mod=account',true);
}elseif($mod=='captcha_n' && $_POST['do']=='submit'){
    $captcha_open=$_POST['captcha_open'];
    $captcha_id=$_POST['captcha_id'];
    $captcha_key=$_POST['captcha_key'];
    $captcha_open_buy=$_POST['captcha_open_buy'];
    $captcha_open_adminlogin=$_POST['captcha_open_adminlogin'];
    $captcha_open_reg=$_POST['captcha_open_reg'];
    $captcha_open_login=$_POST['captcha_open_login'];
    saveSetting('captcha_open',$captcha_open);
    saveSetting('captcha_id',$captcha_id);
    saveSetting('captcha_key',$captcha_key);
    saveSetting('captcha_open_buy',$captcha_open_buy);
    saveSetting('captcha_open_adminlogin',$captcha_open_adminlogin);
    saveSetting('captcha_open_reg',$captcha_open_reg);
    saveSetting('captcha_open_login',$captcha_open_login);
    $ad=$CACHE->clear();
    if($ad)sysmsg('修改成功！',1,'./set.php?mod=captcha',true);
    else sysmsg('修改失败！<br/>'.$DB->error(),2,'./set.php?mod=captcha',true);
}
?>
<?php
if($mod=='site'){
$title='网站信息配置';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                网站信息配置
            </div>
            <div class="card-body">
                <form class="form-horizontal layui-form">
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">网站名称：</label>
                        <input type="text" name="sitename" value="<?php echo $conf['sitename']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">标题栏后辍：</label>
                        <input type="text" name="title" value="<?php echo $conf['title']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">关键词：</label>
                        <input type="text" name="keywords" value="<?php echo $conf['keywords']; ?>" class="form-control"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">网站描述：</label>
                        <input type="text" name="description" value="<?php echo $conf['description']; ?>" class="form-control"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">网站关于：</label>
                        <textarea class="form-control" name="orgname" value='关于'><?php echo $conf['orgname'];?></textarea>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">备案号：</label>
                        <textarea class="form-control" name="icp" value='备案号'><?php echo $conf['icp'];?></textarea>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">客服Q Q：</label>
                        <input type="text" name="kfqq" value="<?php echo $conf['kfqq']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_set">保存内容</button>
                </form>
            </div>
        </div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<?php
}elseif($mod=='setuser'){
$title='个人信息配置';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                个人信息配置
            </div>
            <div class="card-body">
                <form onsubmit="return saveSetting(this)" method="post" class="form-horizontal layui-form" role="form">
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">我的Q Q：</label>
                        <input type="text" name="admin_qq" value="<?php echo $conf['admin_qq']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">我的邮箱：</label>
                        <input type="text" name="email" value="<?php echo $conf['email']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">我的手机号：</label>
                        <input type="text" name="phone" value="<?php echo $conf['phone']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">我的个性签名：</label>
                        <textarea class="form-control" name="gxqm" rows="5" placeholder="填写个性签名"><?php echo $conf['gxqm']?></textarea>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_set">保存内容</button>
                </form>
            </div>
        </div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<?php
}elseif($mod=='defend'){
$title='防CC模板配置';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                防CC模板配置
            </div>
            <div class="card-body">
                <form onsubmit="return defend()" method="post" class="form-horizontal layui-form" role="form">
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">CC防护等级：</label>
                        <select class="form-control" name="defendid" default="<?php echo CC_Defender;?>"><option value="0">关闭</option><option value="1">低(推荐)</option><option value="2">中</option><option value="3">高</option></select>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_set">保存内容</button>
                </form>
            </div>
            <div class="card-footer">
                <span class="layui-icon layui-icon-tips"></span> CC防护说明<br/>
                高：全局使用防CC，会影响网站APP和对接软件的正常使用<br/>
                中：会影响搜索引擎的收录，建议仅在正在受到CC攻击且防御不佳时开启<br/>
                低：用户首次访问进行验证（推荐）<br/>
            </div>
        </div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<?php
}elseif($mod=='account'){
$title='管理员账号配置';
include_once './header.php';
?>
<style>
.pwd{width:100px;height:20px;line-height:14px;padding-top:2px;}  
.pwd_f{color:#BBBBBB;}  
.pwd_c{background-color:#F3F3F3;border-top:1px solid #D0D0D0;border-bottom:1px solid #D0D0D0;border-left:1px solid #D0D0D0;}
.pwd_Weak_c{background-color:#FF4545;border-top:1px solid #BB2B2B;border-bottom:1px solid #BB2B2B;border-left:1px solid #BB2B2B;}  
.pwd_Medium_c{background-color:#FFD35E;border-top:1px solid #E9AE10;border-bottom:1px solid #E9AE10;border-left:1px solid #E9AE10;}  
.pwd_Strong_c{background-color:#3ABB1C;border-top:1px solid #267A12;border-bottom:1px solid #267A12;border-left:1px solid #267A12;}  
.pwd_c_r{border-right:1px solid #D0D0D0;}  .pwd_Weak_c_r{border-right:1px solid #BB2B2B;}  .pwd_Medium_c_r{border-right:1px solid #E9AE10;}  
.pwd_Strong_c_r{border-right:1px solid #267A12;} 
</style>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                管理员账号配置
            </div>
            <div class="card-body">
                <form action="./set.php?mod=account_n" method="post" class="form-horizontal layui-form" role="form"><input type="hidden" name="do" value="submit"/>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">用户名：</label>
                        <input type="text" name="user" value="<?php echo $conf['admin_user']; ?>" class="form-control" lay-verType="tips" lay-verify="required"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">旧密码：</label>
                        <input type="password" name="oldpwd" class="form-control" placeholder="请输入当前的管理员密码"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">新密码：</label>
                        <input type="password" name="newpwd" id="newpass" onKeyUp="CheckIntensity(this.value)" value="" class="form-control" placeholder="不修改请留空"/>
                    </div>
                    <div class="text-center">
                        <table border="0" cellpadding="0" cellspacing="0">
                            <tr align="center">
                                <td id="pwd_Weak" class="pwd pwd_c"> </td>
                                <td id="pwd_Medium" class="pwd pwd_c pwd_f">无</td>
                                <td id="pwd_Strong" class="pwd pwd_c pwd_c_r"> </td>
                            </tr>
                        </table>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">重输密码：</label>
                        <input type="password" name="newpwd2" class="form-control" placeholder="不修改请留空"/>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_account">保存内容</button>
                </form>
            </div>
        </div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<script type="text/javascript">
function CheckIntensity(pwd) {
    var Mcolor, Wcolor, Scolor, Color_Html;       
    var m = 0;      
    //匹配数字
    if (/\d+/.test(pwd)) {
        debugger;
        m++;
    };
    //匹配字母
    if (/[A-Za-z]+/.test(pwd)) {     
        m++;
    };
    //匹配除数字字母外的特殊符号
    if (/[^0-9a-zA-Z]+/.test(pwd)) {        
        m++;
    };
    if (pwd.length <= 6) { m = 1; }
    if (pwd.length <= 0) { m = 0; }       
    switch (m) {
        case 1:
        Wcolor = "pwd pwd_Weak_c";
        Mcolor = "pwd pwd_c";
        Scolor = "pwd pwd_c pwd_c_r";
        Color_Html = "弱";
        break;
        case 2:
        Wcolor = "pwd pwd_Medium_c";
        Mcolor = "pwd pwd_Medium_c";
        Scolor = "pwd pwd_c pwd_c_r";
        Color_Html = "中";
        break;
        case 3:
        Wcolor = "pwd pwd_Strong_c";
        Mcolor = "pwd pwd_Strong_c";
        Scolor = "pwd pwd_Strong_c pwd_Strong_c_r";
        Color_Html = "强";
        break;
        default:
        Wcolor = "pwd pwd_c";
        Mcolor = "pwd pwd_c pwd_f";
        Scolor = "pwd pwd_c pwd_c_r";
        Color_Html = "无";
        break;
    }
    document.getElementById('pwd_Weak').className = Wcolor;
    document.getElementById('pwd_Medium').className = Mcolor;
    document.getElementById('pwd_Strong').className = Scolor;
    document.getElementById('pwd_Medium').innerHTML = Color_Html;
}
</script>
<?php
}elseif($mod=='template'){
$title='网站模板配置';
include_once './header.php';
$mblist=Template::getList();
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                首页模板配置
            </div>
            <div class="card-body">
    <?php if ($conf['template']) {?>
    <h4>当前使用模板：</h4>
    <div class="row text-center">
        <div class="col-xs-6 col-sm-4">
        <img class="img-responsive img-thumbnail img-rounded" src="/template/<?php echo $conf['template']?>/<?php echo $conf['template']?>.png" onerror="this.src='/assets/img/NoImg.png'">
        </div>
        <div class="col-xs-6 col-sm-4">
        <p>模板名称：<font color="red"><?php echo $conf['template']?></font></p>
        <p>适应版本：<font color="orange">1.0＋</font></p>
        <p>模板作者：<font color="blue">可可</font></p>
        </div>
    </div>
    <hr/>
    <?php }?>
    <h4>更换模板：</h4>
    <div class="layui-row">
    <?php foreach($mblist as $template){?>
    <?php if ($conf['template'] == $template) {?>
    <div class="layui-col-xs6 layui-col-sm4 layui-col-md3 layui-anim layui-anim-upbit" style="box-shadow: 3px 3px 8px 1px whitesmoke;border-radius: 0.5rem;">
        <div class="layui-card" style="box-shadow: none;">
            <div class="layui-card-header layui-elip" align="center" title="<?php echo $template?>模板" onclick="layer.msg('<?php echo $template?>模板')"><?php echo $template?>模板</div>
            <div class="layui-card-body image_body"><img class="img-responsive img-thumbnail img-rounded" src="/template/<?php echo $template?>/<?php echo $template?>.png" onerror="this.src='/assets/img/NoImg.png'" title="点击更换到该模板"/></div>
        </div>
    </div>
    <?php } else {?>
    <a href="javascript:changeTemplate('<?php echo $template?>')">
    <div class="layui-col-xs6 layui-col-sm4 layui-col-md3 layui-anim layui-anim-upbit" style="box-shadow: 3px 3px 8px 1px whitesmoke;border-radius: 0.5rem;">
        <div class="layui-card" style="box-shadow: none;">
            <div class="layui-card-header layui-elip" align="center" title="<?php echo $template?>模板" onclick="layer.msg('<?php echo $template?>模板')"><?php echo $template?>模板</div>
            <div class="layui-card-body image_body"><img class="img-responsive img-thumbnail img-rounded" src="/template/<?php echo $template?>/<?php echo $template?>.png" onerror="this.src='/assets/img/NoImg.png'" title="点击更换到该模板"/></div>
        </div>
    </div>
    </a>
    <?php }?>
    <?php }?>
</div>
    </div>
</div>
</div>
</div>
<?php
include_once './bottom.php';
?>
<?php
}elseif($mod=='captcha'){
$title='验证与IP配置';
include_once './header.php';
?>
<div class="row">
    <div class="col-lg-12">
        <div class="card">
            <div class="card-header">
                滑块验证码设置
            </div>
            <div class="card-body">
                <form action="./set.php?mod=captcha_n" method="post" class="form-horizontal layui-form" role="form"><input type="hidden" name="do" value="submit"/>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">验证码选择：</label>
                        <select class="form-control" name="captcha_open" default="<?php echo $conf['captcha_open'];?>"><option value="0">关闭</option><option value="1">极限滑动验证码</option><option value="2">顶象滑动验证码</option><option value="3">VAPTCHA手势验证码</option></select>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">接口ID：</label>
                        <input type="text" name="captcha_id" value="<?php echo $conf['captcha_id'];?>" class="form-control"/>
                    </div>
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">接口KEY：</label>
                        <input type="text" name="captcha_key" value="<?php echo $conf['captcha_key'];?>" class="form-control"/>
                    </div>
                    <label for="example-input-normal" style="font-weight: 500">开启验证背景：</label>
                    <div class="mb-3">
                        <div class="custom-control custom-checkbox">
                            <input name="captcha_open_buy" type="checkbox" value="1"<?php echo $conf['captcha_open_buy'] == 1 ? ' checked' : '';?> class="custom-control-input" id="captcha_open_buy">
                            <label class="custom-control-label" for="captcha_open_buy">免费购买</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                            <input name="captcha_open_adminlogin" type="checkbox" value="1"<?php echo $conf['captcha_open_adminlogin'] == 1 ? ' checked' : '';?> class="custom-control-input" id="captcha_open_adminlogin">
                            <label class="custom-control-label" for="captcha_open_adminlogin">管理员登录</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                            <input name="captcha_open_reg" type="checkbox" value="1"<?php echo $conf['captcha_open_reg'] == 1 ? ' checked' : '';?> class="custom-control-input" id="captcha_open_reg">
                            <label class="custom-control-label" for="captcha_open_reg">用户注册</label>
                        </div>
                        <div class="custom-control custom-checkbox">
                            <input name="captcha_open_login" type="checkbox" value="1"<?php echo $conf['captcha_open_login'] == 1 ? ' checked' : '';?> class="custom-control-input" id="captcha_open_login">
                            <label class="custom-control-label" for="captcha_open_login">用户登录</label>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_set">保存内容</button>
                </form>
            </div>
            <div class="card-footer">
                <span class="layui-icon layui-icon-tips"></span>
                极限验证码：<a href="https://www.geetest.com/Register" rel="noreferrer" target="_blank">点击进入</a>（免费版每小时限流，需人工审核）<br/>
                顶象验证码：<a href="https://www.dingxiang-inc.com/business/captcha" rel="noreferrer" target="_blank">点击进入</a>（收费的，可免费试用）<br/>
                VAPTCHA手势验证码：<a href="https://www.vaptcha.com/" rel="noreferrer" target="_blank">点击进入</a> （目前完全免费）选择极限验证码，然后ID和KEY留空保存，即可直接免费使用公共接口(测试中)
            </div>
        </div>
        <div class="card">
            <div class="card-header">
                用户IP地址获取设置
            </div>
            <div class="card-body">
                <form onsubmit="return saveSetting(this)" method="post" class="form-horizontal layui-form" role="form">
                    <div class="form-group mb-3">
                        <label for="example-input-normal" style="font-weight: 500">用户IP地址获取方式：</label>
                        <select class="form-control" name="ip_type" default="<?php echo $conf['ip_type']?>"><option value="0">0_X_FORWARDED_FOR</option><option value="1">1_X_REAL_IP</option><option value="2">2_REMOTE_ADDR</option></select>
                    </div>
                    <button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_set">保存内容</button>
                </form>
            </div>
            <div class="card-footer">
                <span class="layui-icon layui-icon-tips"></span>
                此功能设置用于防止用户伪造IP请求。<br/>
                X_FORWARDED_FOR：之前的获取真实IP方式，极易被伪造IP<br/>
                X_REAL_IP：在网站使用CDN的情况下选择此项，在不使用CDN的情况下也会被伪造<br/>
                REMOTE_ADDR：直接获取真实请求IP，无法被伪造，但可能获取到的是CDN节点IP<br/>
                <b>你可以从中选择一个能显示你真实地址的IP，优先选下方的选项。</b>
            </div>
        </div>
    </div>
</div>
<?php
include_once './bottom.php';
?>
<script type="text/javascript">
$(document).ready(function(){
    $.ajax({
        type : "GET",
        url : "ajax.php?act=iptype",
        dataType : 'json',
        async: true,
        success : function(data) {
            $("select[name='ip_type']").empty();
            var defaultv = $("select[name='ip_type']").attr('default');
            $.each(data, function(k, item){
                $("select[name='ip_type']").append('<option value="'+k+'" '+(defaultv==k?'selected':'')+'>'+ item.name +' - '+ item.ip +' '+ item.city +'</option>');
            })
        }
    });
})
</script>
<?php
}
?>
<script>
layui.use(['form'], function () {
    var form = layui.form;
    form.on('submit(submit_set)', function (data) {
        var ii = layer.msg('正在修改中,请稍后...', {icon: 16, time: 10 * 1000});
        $.post('ajax.php?act=set', data.field, function (data) {
            layer.close(ii);
            if(data.code == 0){
                layer.msg('设置保存成功！', {
                    icon: 6,
                    end: function (layero, index) {
                        window.location.reload();
                    }
                });
            } else {
                layer.msg(data.msg, {icon: 5});
            }
        });
        return false;
    });
});
var items = $("select[default]");
for (i = 0; i < items.length; i++) {
    $(items[i]).val($(items[i]).attr("default")||0);
}
function saveSetting(obj){
    var ii = layer.msg('正在修改中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type : 'POST',
        url : 'ajax.php?act=set',
        data : $(obj).serialize(),
        dataType : 'json',
        success : function(data) {
            layer.close(ii);
            if(data.code == 0){
                layer.msg('设置保存成功！', {
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
}
function changeTemplate(template){
    var ii = layer.msg('正在更换中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type : 'POST',
        url : 'ajax.php?act=set',
        data : {template:template},
        dataType : 'json',
        success : function(data) {
            layer.close(ii);
            if(data.code == 0){
                layer.msg('更换模板成功！', {
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
}
</script>