<?php
if(!defined('IN_CRONLITE'))exit();
$url = "http://api.fcypay.com/";
$m = md5(rand(1000000,9999999).date('YmdHis').uniqid());
$code_url = $url.'get_openid_qrcode?mark='.$m;
$cron_url = $url.'get_openid_status?mark='.$m;
if ($_POST) {
	if (!isset($_POST['type']) || !isset($_POST['money']) || !isset($_POST['account'])) exit(json_encode(['code'=>0,'msg'=>'请填写完整']));
	if (!($_POST['money']) || !($_POST['account'])) exit(json_encode(['code'=>0,'msg'=>'请填写完整']));
	if ($conf['transfer_check'] == 'FORCE_CHECK' && !isset($_POST['name'])) exit(json_encode(['code'=>0,'msg'=>'请填写完整']));
	if ($conf['transfer_check'] == 'FORCE_CHECK' && !($_POST['name'])) exit(json_encode(['code'=>0,'msg'=>'请填写完整']));
	if (!is_numeric($_POST['money'])) exit(json_encode(['code'=>0,'msg'=>'金额格式错误']));
	if ($_POST['money'].''<'1') exit(json_encode(['code'=>0,'msg'=>'最低提交一元']));
	$type = (int)$_POST['type'];
	if ($type == 0) {
	    $type = 'qqpay';
	    $payee_type = 'wxpay';
	}elseif ($type == 1) {
	    $type = 'qqpay';
	    $payee_type = 'alipay';
	}elseif ($type == 2) {
	    $type = 'wxpay';
	    $payee_type = 'qqpay';
	}elseif ($type == 3) {
	    $type = 'wxpay';
	    $payee_type = 'alipay';
	}elseif ($type == 4) {
	    $type = 'alipay';
	    $payee_type = 'qqpay';
	}elseif ($type == 5) {
	    $type = 'alipay';
	    $payee_type = 'wxpay';
	}else{
	    exit(json_encode(['code'=>0,'msg'=>'mgj']));
	}
	$sxf = isset($conf['transfer_sxf'])?$conf['transfer_sxf']:3;
	$_POST['money'] = round($_POST['money'],2);
	$realmoney = round($_POST['money']+($_POST['money']*$sxf/100),2);
	$trade_no=date("YmdHis").rand(111,999);
	$c = json_encode(array('money'=>$_POST['money'],'account'=>trim(daddslashes($_POST['account'])),'type'=>$payee_type,'name'=>isset($_POST['name'])?$_POST['name']:'',));
    $sql="insert into `yixi_pay` (`trade_no`,`type`,`uid`,`input`,`name`,`money`,`ip`,`addtime`,`status`) values ('".$trade_no."','0','1','".$c."','换钱','".$realmoney."','".$clientip."','".$date."','0')";
	if($DB->query($sql)){
		exit(json_encode(['code'=>1,'msg'=>'OK','url'=>'./other/submit.php?type='.$type.'&orderid='.$trade_no]));
	}else{
		exit(json_encode(['code'=>0,'msg'=>'提交失败']));
	}
}
?>
<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no"/>
    <title><?php echo $conf['sitename']?> - 在线换钱</title>
    <link rel="stylesheet" href="./assets/simple/css/plugins.css">
    <link rel="stylesheet" href="./assets/simple/css/main.css">
    <link rel="stylesheet" href="./assets/css/bootstrap.min.css">
	<script src="https://lib.baomitu.com/jquery/2.2.4/jquery.min.js"></script>
    <link href="https://lib.baomitu.com/layer/3.1.1/theme/default/layer.css" rel="stylesheet">
	<script src="https://lib.baomitu.com/layer/3.1.1/layer.js"></script>
	<script type="text/javascript" src="//www.fcypay.com/assets/qrcode/qrcode.min.js"></script>
</head>
<body>
<style>
body{
background:#ecedf0 url("//cdn.qqzzz.net/assets/img/background/<?php echo rand(1,19); ?>.jpg") fixed;
<?php echo $repeat?>}
</style>
<br/>
<div class="col-xs-12 col-sm-10 col-md-8 col-lg-5 center-block" style="float: none;">
    <div class="block">
        <div class="block-title">
            <h2><i class="fa fa-share-alt"></i>&nbsp;&nbsp;<b>在线换钱</b></h2>
        </div>
        <form action="" method="post">
			<div class="form-group">
				<div class="alert alert-info alert-dismissable">
					<span id="loginmsg">注意：换钱到微信必须使用自动获取账号，不然不能正常换成功；<br>填错不负责 请真实填写信息<br>未到账等其他问题联系客服<br>系统会自动增加手续费提交支付</span>
				</div>
			<div class="form-group">
				<div class="input-group">
					<div class="input-group-addon">换钱方式</div>
						<select name="type" required="required" class="form-control">
				            <option value="0">ＱＱ&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;—— 换 ——&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;微信</option>
				            <option value="1">ＱＱ&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;—— 换 ——&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支付宝</option>
				            <option value="2">微信&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;—— 换 ——&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ＱＱ</option>
				            <option value="3">微信&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;—— 换 ——&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;支付宝</option>
				            <option value="4">支付宝&nbsp;&nbsp;&nbsp;—— 换 ——&nbsp;&nbsp;&nbsp;ＱＱ</option>
				            <option value="5">支付宝&nbsp;&nbsp;&nbsp;—— 换 ——&nbsp;&nbsp;&nbsp;微信</option>
				    </select>
				</div>
			</div>
		<div class="form-group">
			<div class="input-group">
				<div class="input-group-addon">换钱金额</div>
					<input type="text" name="money" id="input_money" required placeholder="1元以上" autocomplete="off" class="form-control">
				</div>
			</div>
			<div class="form-group">
				<div class="input-group">
					<div class="input-group-addon" id="s">微信Openid</div>
						<input type="text" name="account" id="input_account" required placeholder="要换到的支付宝/QQ/微信账号请自动获取" autocomplete="off" class="form-control">
						<div class="input-group-addon auto" style="padding:0;">
				        <a href="javascript:;" class="btn btn-info btn-sm btn-auto" style="border-radius: 0;height: 100%" >自动获取</a>
				    </div>
				</div>
			</div>
			<?php if($conf['transfer_check'] == 'FORCE_CHECK'): ?>
			<div class="form-group">
				<div class="input-group">
					<div class="input-group-addon">真实姓名</div>
					<input type="text" name="name" id="input_name" required placeholder="要换到的支付宝/QQ/微信真实姓名" autocomplete="off" class="form-control">
				</div>
			</div>
			<?php endif; ?>
              <input type="button" value="提交" onclick="vb();" class="btn btn-primary btn-block"/>
			</div>
			<hr>
			<div class="form-group">
		</div>
		</form>
        </div>
      </div>
    </div>
  </div>
<div id="qrcode" style="display: none"></div>
</div>
<script language=javascript>
function show_date_time(){
window.setTimeout("show_date_time()", 1000);
BirthDay=new Date("11-19-2019 12:12:24");//建站日期
today=new Date();
timeold=(today.getTime()-BirthDay.getTime());
sectimeold=timeold/1000
secondsold=Math.floor(sectimeold);
msPerDay=24*60*60*1000
e_daysold=timeold/msPerDay
daysold=Math.floor(e_daysold);
e_hrsold=(daysold-e_daysold)*-24;
hrsold=Math.floor(e_hrsold);
e_minsold=(hrsold-e_hrsold)*-60;
minsold=Math.floor((hrsold-e_hrsold)*-60);
seconds=Math.floor((minsold-e_minsold)*-60);
momk.innerHTML=daysold+"天"+hrsold+"小时"+minsold+"分"+seconds+"秒" ;
}
show_date_time();
</script>
<style>
#momk{animation:change 10s infinite;font-weight:800; }
@keyframes change{0%{color:#5cb85c;}25%{color:#556bd8;}50%{color:#e40707;}75%{color:#66e616;}100% {color:#67bd31;}}
</style>
<!--安全运行代码结束-->
<script type="text/javascript">
	$(document).on("change", "select[name=type]", function () {
        var type = $(this).val();
        if (type == '0' || type == '5') {
            $(".auto").show();
            $("#s").text('微信Openid');
        }else{
            $(".auto").hide();
            $("#s").text('收款账号');
        }
    });
    var qrcode = new QRCode("qrcode", {
	  text: '<?php echo $code_url;?>',
	  width: 300,
	  height: 300,
	});
    $(document).on("click", ".btn-auto", function () {
        var open = layer.open({
	        type:1,
	        title:'',
	        content:'<div class="layui-card-body"><h3 style="text-align:center">请使用微信扫一扫</h3><div><br>'+$("#qrcode").html()+'</div></div>',
	        cancel: function(index, layero){ 
	            layer.close(open);
	            window.clearInterval(cron); 
	        }    
	    });
	    var cron = setInterval(function(){
	        $.ajax({
	            type: "GET",
	            url: '<?php echo $cron_url;?>'+'&r='+Math.random(),
	            dataType: "json",
	            success: function(data){
	                if (data.code) {
	                    $("input[name=account]").val(data.data);
	                    layer.close(open);
	                    window.clearInterval(cron); 
	                }
	            }
	        });
	    },3000);
    });
	var vb = function(){
		if (!$("#input_money").val()) { layer.msg('请填写完整');return false; }
		if (!$("#input_account").val()) { layer.msg('请填写完整');return false; }
		var load  = layer.load();
		$.ajax({
			type: "POST",
			url : "./?mod=transfer",
			data: $("form").serialize(),
			dataType: "json",
			success: function(res){
				layer.close(load);
				if (res.code) {
					$("body").html("正在跳转支付....");
					window.location.href=res.url;
				} else {
					layer.msg(res.msg);
				}
			}
		});
	}
</script>
</body>
</html>