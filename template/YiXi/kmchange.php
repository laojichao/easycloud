<?php
if(!defined('IN_CRONLITE'))exit();
?>
<!DOCTYPE html>
<html lang="zh">
<head>
	<meta charset="utf-8">
	<title><?php echo $conf['sitename']?> - <?php echo $conf['title']?></title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
	<link rel="stylesheet" href="../assets/layui/css/layui.css" media="all">
	<link rel="stylesheet" href="../assets/layui/style/admin.css" media="all">
	<link rel="stylesheet" href="../assets/layui/style/login.css" media="all">
	<meta name="keywords" content="<?php echo $conf['keywords'];?>">
	<meta name="description" content="<?php echo $conf['description'];?>">
	<!--[if lt IE 9]>
	<script src="https://cdn.staticfile.org/html5shiv/r29/html5.min.js"></script>
	<script src="https://cdn.staticfile.org/respond.js/1.4.2/respond.min.js"></script>
	<![endif]-->
</head>
<style type="text/css">
	body {
		font-family: '微软雅黑';
		font-weight: 400
	}
	.images {
		width: 110%;
		height: 110%;
		background-size: 100% 100%;
		z-index: -100;
		position: fixed;
		left: -5%;
		top: -5%;
		background-color: #C9BDBB;
		filter: blur(0px);
	}
	.layui-input {
		border: none;
		box-shadow: 2px 2px 12px 0px whitesmoke;
		border-bottom: solid 1px whitesmoke;
		text-align: center;
		border-radius: 0.2rem;
		margin-top: 1rem;
	}
	.auth_color {
		background: linear-gradient(to left, #FF8A80, #FF5252);
		border-radius: 0.3rem;
		box-shadow: 1px 1px 9px 1px #eee;
	}
	.auth_color2 {
		background: linear-gradient(to left, #0091EA, #4FC3F7);
		border-radius: 0.3rem;
		box-shadow: 1px 1px 9px 1px #eee;
	}
	.auth_color3 {
		background: linear-gradient(to left, #5E35B1, #651FFF);
		border-radius: 0.3rem;
		box-shadow: 1px 1px 9px 1px #eee;
	}
	.misett {
		transition: all 0.4s;
	}
	.layui-layer-btn {
		border-top: solid 1px #eee;
	}
	.layui-layer-title {
		background: linear-gradient(to left, #F0F2F0, #000C40,#ff7e5f,#ef32d9,#89fffd,#c2e59c,#64b3f4,#7c4dff,#536dfe,#9575cd);
		background-size: 1915%;
		animation: gradientBackground 8s alternate ease-out;
		animation-iteration-count: infinite;
		color: white !important;
	}
	.layui-layer-content {
		text-align: center;
	}
	.layui-layer-btn0, .layui-layer-btn1, .layui-layer-btn2, .layui-layer-btn3 {
		margin-left: 0%;
		border-radius: 0.3rem;
		background-color: #FFFFFF !important;
		color: darkslategray !important;
		border: none !important;
		transition: all 0.5s;
	}
	.layui-layer-btn0:hover, .layui-layer-btn1:hover, .layui-layer-btn2:hover, .layui-layer-btn3:hover {
		background-color: #F5F5F5 !important;
	}
	@keyframes gradientBackground {
		0% {
			background-position: 0 0
		}
		50% {
			background-position: 50% 100%
		}
		100% {
			background-position: 100% 0
		}
	}
	.image_body {
		height: 60px;
		overflow: hidden;
		padding: 0;
		padding-bottom: 1rem;
		text-align: center;
		margin-bottom: 1rem;
	}
</style>
<body class="layui-layout-body">
<div class="images"></div>
<div class="layui-fluid layadmin-homepage-fluid">
	<div class="layui-row layui-col-space8">
		<div class="layui-col-xs12 layui-col-sm8 layui-col-sm-offset2 layui-col-md6 layui-col-md-offset3 misett">
			<div class="layui-card">
				<div class="layui-card-header" style="font-size: 1.5rem;text-align: center;height: 3rem;line-height: 3rem;">卡密注册</div>
				<div class="layui-card-body">
					<?php if ($conf['kmchange_open'] == 1) {?>
					<div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
						<ul class="layui-tab-title" style="text-align: center;margin-bottom: 1rem;">
							<li class="layui-this">兑换授权</li>
							<li>兑换认证</li>
							<li>兑换权限</li>
						</ul>
						<div class="layui-tab-content">
							<div class="layui-tab-item layui-show">
									<form class="layui-form">
										<div class="layui-form-item">
											<input type="text" name="authname" placeholder="请填写授权的站点名称" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="authqq" placeholder="请填写授权的QQ" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="authurl" placeholder="请填写授权的域名" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="authkm" placeholder="请填写您的域名授权兑换卡卡密" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<div class="layui-row layui-col-space12">
												<div class="layui-col-xs6"><button type="submit" class="layui-btn layui-btn-fluid auth_color" lay-submit lay-filter="submit_authchange">立即兑换</button></div>
												<div class="layui-col-xs6"><span class="layui-btn layui-btn-fluid auth_color3" onclick="authdesc();">兑换介绍</span></div>
											</div>
										</div>
									</from>
								</div>
							<div class="layui-tab-item">
									<from class="layui-form">
										<div class="layui-form-item">
											<input type="text" name="payname" placeholder="请填写认证的站点名称" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="payqq" placeholder="请填写认证的QQ" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="payurl" placeholder="请填写认证的域名" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="paykm" placeholder="请填写您的易支付域名认证兑换卡卡密" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<div class="layui-row layui-col-space12">
												<div class="layui-col-xs6"><button type="submit" class="layui-btn layui-btn-fluid auth_color" lay-submit lay-filter="submit_paychange">立即兑换</button></div>
												<div class="layui-col-xs6"><span class="layui-btn layui-btn-fluid auth_color3" onclick="paydesc();">兑换介绍</span></div>
											</div>
										</div>
									</from>
								</div>
							<div class="layui-tab-item">
									<from class="layui-form">
										<div class="layui-form-item">
											<input type="text" name="user" placeholder="请填写登录账号" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="pwd" placeholder="请填写6位以上密码" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="qq" placeholder="请填写联系QQ，方便联系" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="email" placeholder="请填写邮箱账号" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<input type="text" name="userkm" placeholder="请填写您的权限兑换卡卡密" lay-verType="tips" class="layui-input" lay-verify="required" style="width: 100%;"/>
										</div>
										<div class="layui-form-item">
											<div class="layui-row layui-col-space12">
												<div class="layui-col-xs6"><button type="submit" class="layui-btn layui-btn-fluid auth_color" lay-submit lay-filter="submit_userchange">立即注册</button></div>
												<div class="layui-col-xs6"><span class="layui-btn layui-btn-fluid auth_color3" onclick="userdesc();">注册介绍</span></div>
											</div>
										</div>
									</from>
								</div>
							</div>
						</div>
					<?php } else {?>
					<center><font color="red">本站未开启卡密注册功能</font></center>
					<?php }?>
					<div class="layui-form">
						<div class="layui-form-item">
							<div class="layui-row layui-col-space12">
								<div class="layui-col-xs6"><a href="?mod=buy" class="layui-btn layui-btn-fluid auth_color">在线购买</a></div>
								<div class="layui-col-xs6"><a href="http://wpa.qq.com/msgrd?v=3&uin=<?php echo $conf['kfqq'];?>&site=qq&menu=yes" class="layui-btn layui-btn-fluid auth_color3">联系客服</a></div>
							</div>
							<div class="layui-row layui-col-space12">
								<div class="layui-col-xs6"><a href="?mod=change" class="layui-btn layui-btn-fluid auth_color">在线更换</a></div>
								<div class="layui-col-xs6"><a href="?mod=getprogram" class="layui-btn layui-btn-fluid auth_color3">源码下载</a></div>
							</div>
						</div>
					</div>
					<?php if($conf['qunlj']){?><center><a href="<?php echo $conf['qunlj'];?>" target="_blank" style="color:seagreen;">点我加入官方群<font color="red"></font></a></center><?php }?>
				</div>
			</div>
		</div>
		<?php if ($conf['Market_open'] == 1 && $conf['recommend_show_open'] == 1) {?>
		<div class="layui-col-xs12 layui-col-sm8 layui-col-sm-offset2 layui-col-md6 layui-col-md-offset3 misett" style="margin-top: 0.5rem;">
			<div class="layui-card">
				<div class="layui-card-header" style="font-size: 1.5rem;text-align: center;height: 3rem;line-height: 3rem;transition: all 0.5s;" id="text_renqi">每日推荐展示</div>
				<div class="layui-card-body">
					<div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
						<ul class="layui-tab-title" style="text-align: center;margin-bottom: 1rem;">
							<li class="layui-this">人气最高</li>
							<li>模板新品</li>
							<li>插件新品</li>
							<li>其他新品</li>
							<li onclick="window.open('user')">登陆后台</li>
							<li onclick="window.open('user/reg.php')">在线注册</li>
						</ul>
						<div class="layui-tab-content">
							<div class="layui-tab-item layui-show">
								<div class="layui-row layui-col-space20">
									<?php
									$rs=$DB->query("SELECT * FROM yixi_shop WHERE 1 order by count desc limit 8");
									while($res = $DB->fetch($rs))
									{
									$shopimg = explode(',', $res['image']);
									if ($res['uid'] == 1) {
										if($conf['admin_qq']){
											$qq=$conf['admin_qq'];
										}else{
											$qq=$conf['kfqq'];
										}
									} else {
										$user = $DB->get_row("select * from yixi_user where uid='" . $res['uid'] . "' limit 1");
										$qq = $user['qq'];
									}
									echo '<div class=" layui-col-xs6 layui-col-sm4 layui-col-md3 layui-anim layui-anim-upbit" style="box-shadow: 3px 3px 8px 1px whitesmoke;border-radius: 0.5rem;">
											<div class="layui-card" style="box-shadow: none;">
												<div class="layui-card-header layui-elip" align="center" title="'.$res['name'].'于'.$res['date'].'时投稿" onclick="layer.msg(\''.$res['name'].'于'.$res['date'].'时投稿\')">'.$res['name'].'</div>
												<div class="layui-card-body image_body"><img src="'.$shopimg[0].'" onclick="image_msg('.$res['id'].')" width="70%"/></div>
												<div class="price layui-text layui-elip" align="center">
													<img style="width: 1.2rem;height: 1.2rem;margin-bottom: 0.3rem;border-radius: 0.3rem;" src="http://q4.qlogo.cn/headimg_dl?dst_uin='.$qq.'&spec=100" class="layui-anim layui-anim-up"/>
													<i class="layui-icon layui-icon-username"></i>用户:'.qqname($qq).'投稿
												</div>
											</div>
										</div>';
									}
									?>
								</div>
							</div>
							<div class="layui-tab-item">
								<div class="layui-row layui-col-space20">
									<?php
									$rs=$DB->query("SELECT * FROM yixi_shop WHERE type=1 order by id desc limit 8");
									while($res = $DB->fetch($rs))
									{
									$shopimg = explode(',', $res['image']);
									if ($res['uid'] == 1) {
										if($conf['admin_qq']){
											$qq=$conf['admin_qq'];
										}else{
											$qq=$conf['kfqq'];
										}
									} else {
										$user = $DB->get_row("select * from yixi_user where uid='" . $res['uid'] . "' limit 1");
										$qq = $user['qq'];
									}
									echo '<div class=" layui-col-xs6 layui-col-sm4 layui-col-md3 layui-anim layui-anim-upbit" style="box-shadow: 3px 3px 8px 1px whitesmoke;border-radius: 0.5rem;">
											<div class="layui-card" style="box-shadow: none;">
												<div class="layui-card-header layui-elip" align="center" title="'.$res['name'].'于'.$res['date'].'时投稿" onclick="layer.msg(\''.$res['name'].'于'.$res['date'].'时投稿\')">'.$res['name'].'</div>
												<div class="layui-card-body image_body"><img src="'.$shopimg[0].'" onclick="image_msg('.$res['id'].')" width="70%"/></div>
												<div class="price layui-text layui-elip" align="center">
													<img style="width: 1.2rem;height: 1.2rem;margin-bottom: 0.3rem;border-radius: 0.3rem;" src="http://q4.qlogo.cn/headimg_dl?dst_uin='.$qq.'&spec=100" class="layui-anim layui-anim-up"/>
													<i class="layui-icon layui-icon-username"></i>用户:'.qqname($qq).'投稿
												</div>
											</div>
										</div>';
									}
									?>
								</div>
							</div>
							<div class="layui-tab-item">
								<div class="layui-row layui-col-space20">
									<?php
									$rs=$DB->query("SELECT * FROM yixi_shop WHERE type=2 order by id desc limit 8");
									while($res = $DB->fetch($rs))
									{
									$shopimg = explode(',', $res['image']);
									if ($res['uid'] == 1) {
										if($conf['admin_qq']){
											$qq=$conf['admin_qq'];
										}else{
											$qq=$conf['kfqq'];
										}
									} else {
										$user = $DB->get_row("select * from yixi_user where uid='" . $res['uid'] . "' limit 1");
										$qq = $user['qq'];
									}
									echo '<div class=" layui-col-xs6 layui-col-sm4 layui-col-md3 layui-anim layui-anim-upbit" style="box-shadow: 3px 3px 8px 1px whitesmoke;border-radius: 0.5rem;">
											<div class="layui-card" style="box-shadow: none;">
												<div class="layui-card-header layui-elip" align="center" title="'.$res['name'].'于'.$res['date'].'时投稿" onclick="layer.msg(\''.$res['name'].'于'.$res['date'].'时投稿\')">'.$res['name'].'</div>
												<div class="layui-card-body image_body"><img src="'.$shopimg[0].'" onclick="image_msg('.$res['id'].')" width="70%"/></div>
												<div class="price layui-text layui-elip" align="center">
													<img style="width: 1.2rem;height: 1.2rem;margin-bottom: 0.3rem;border-radius: 0.3rem;" src="http://q4.qlogo.cn/headimg_dl?dst_uin='.$qq.'&spec=100" class="layui-anim layui-anim-up"/>
													<i class="layui-icon layui-icon-username"></i>用户:'.qqname($qq).'投稿
												</div>
											</div>
										</div>';
									}
									?>
								</div>
							</div>
							<div class="layui-tab-item">
								<div class="layui-row layui-col-space20">
									<?php
									$rs=$DB->query("SELECT * FROM yixi_shop WHERE type=3 order by id desc limit 8");
									while($res = $DB->fetch($rs))
									{
									$shopimg = explode(',', $res['image']);
									if ($res['uid'] == 1) {
										if($conf['admin_qq']){
											$qq=$conf['admin_qq'];
										}else{
											$qq=$conf['kfqq'];
										}
									} else {
										$user = $DB->get_row("select * from yixi_user where uid='" . $res['uid'] . "' limit 1");
										$qq = $user['qq'];
									}
									echo '<div class=" layui-col-xs6 layui-col-sm4 layui-col-md3 layui-anim layui-anim-upbit" style="box-shadow: 3px 3px 8px 1px whitesmoke;border-radius: 0.5rem;">
											<div class="layui-card" style="box-shadow: none;">
												<div class="layui-card-header layui-elip" align="center" title="'.$res['name'].'于'.$res['date'].'时投稿" onclick="layer.msg(\''.$res['name'].'于'.$res['date'].'时投稿\')">'.$res['name'].'</div>
												<div class="layui-card-body image_body"><img src="'.$shopimg[0].'" onclick="image_msg('.$res['id'].')" width="70%"/></div>
												<div class="price layui-text layui-elip" align="center">
													<img style="width: 1.2rem;height: 1.2rem;margin-bottom: 0.3rem;border-radius: 0.3rem;" src="http://q4.qlogo.cn/headimg_dl?dst_uin='.$qq.'&spec=100" class="layui-anim layui-anim-up"/>
													<i class="layui-icon layui-icon-username"></i>用户:'.qqname($qq).'投稿
												</div>
											</div>
										</div>';
									}
									?>
								</div>
							</div>
							<div class="layui-text" align="center" style="margin-top: 2.5rem;color: darkgrey;">因排版原因只展示如上内容! <a href="user">体验</a></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<?php }?>
	</div>
</div>
<div style="color: white;width: 100%;text-align: center;margin-bottom: 2rem;">
	<p><?php echo $conf['footer'];?><br>
	<p>
		<span><a href="user" style="color: white;padding: 0 0.5rem 0 0.5rem;" target="_blank">登录后台</a></span>
		<span><a href="user/reg.php" style="color: white;" target="_blank">在线注册</a></span>
	</p>
</div>
<script src="//lib.baomitu.com//jquery/3.4.1/jquery.min.js"></script>
<script src="../assets/layui/layui.all.js"></script>
<script type="text/javascript">
layui.use(['form', 'element'], function () {
	var form = layui.form;
	var element = layui.element;
	form.on('submit(submit_authchange)', function (data) {
		authchange();
		return false;
	});
	form.on('submit(submit_paychange)', function (data) {
		paychange();
		return false;
	});
	form.on('submit(submit_userchange)', function (data) {
		userchange();
		return false;
	});
});
<?php if ($conf['kmchange_notice']) {?>
function aa() {
	layer.open({
		type: 1,
		title: false,
		closeBtn: 0,
		area: '300px',
		shade: 0.8,
		id: 'LAY_layuipro',
		btn: ['好的了解', '登陆后台'],
		btnAlign: 'c',
		moveOut: true,
		moveType: 0,
		btn2: function (layero, index) {window.open("/user", "_blank");},
		content: '<div style="background-color:#393D49;color:#eeeeee;padding:0.5em"><h3 style="text-align:center;padding-top:0.5em">平台公告</h3><hr><?php echo $conf['kmchange_notice'];?><hr/><center>客服QQ：<?php echo $conf['kfqq'];?></center></div>'
	});
};
aa();
<?php }?>
layui.use('util', function () {
	var util = layui.util;
	util.fixbar({
		bar2: '&#xe667;', 
		bar1: true, 
		click: function (type) {
			if (type === 'bar1') {
				layer.alert('是否需要联系站长,若有任何不懂的地方,可以随时联系他哦~<br>客服QQ:<?php echo $conf['kfqq'];?>', {
					title: '联系客服',
					icon: '3',
					btn: ['这就联系Ta', '不用了'],
					btn1: function (layero, index) {
						layer.msg('正在跳转聊天窗口!');
						window.open("http://wpa.qq.com/msgrd?v=3&uin=<?php echo $conf['kfqq'];?>&site=qq&menu=yes", "_blank");
					}
				});
			}
			if (type === 'bar2') {
				aa()
			}
		}
	});
});
function image_msg(id) {
	$.getJSON('ajax.php?act=image_shop&id=' + id, function (json) {
		layer.photos({photos: json, anim: 5});
	});
}
function authdesc() {
	layer.open({
		title: "兑换授权介绍",
		content: '1.兑换授权，自动辨别该卡密所属程序</br>2.系统自动辨别兑换卡类型',
		skin: 'layui-layer-admin',
		btnAlign: 'c',
		btn: '好的',
		icon: 1,
		type: 1
	})
}
function paydesc() {
	layer.open({
		title: "兑换认证介绍",
		content: '1.兑换认证，自动辨别该卡密所属程序</br>2.系统自动辨别兑换卡类型',
		skin: 'layui-layer-admin',
		btnAlign: 'c',
		btn: '好的',
		icon: 1,
		type: 1
	})
}
function userdesc() {
	layer.open({
		title: "兑换权限介绍",
		content: '1.兑换权限，自动辨别该卡密所属程序(全能管理员权限卡不辨别程序)</br>2.系统自动辨别兑换卡类型3.授权商：可以授权你所选择的授权</br>超级管理员：可以授权你所选择的授权和授权商</br>全能管理员：0成本添加任何授权，授权商，超管</br>',
		skin: 'layui-layer-admin',
		btnAlign: 'c',
		btn: '好的',
		icon: 1,
		type: 1
	})
}
function authchange() {
	var name = $("input[name='authname']").val();
	var qq = $("input[name='authqq']").val();
	var url = $("input[name='authurl']").val();
	var km = $("input[name='authkm']").val();
	var index = layer.msg('正在操作中...', {icon: 16, time: 10 * 1000});
	$.ajax({
		type: "post",
		url: "ajax.php?act=authchange",
		data : {name:name,qq:qq,url:url,km:km},
		dataType: 'json',
		success: function (data) {
			if (data.code == 0) {
				layer.open({
					title: "操作成功通知",
					content: data.msg,
					skin: 'layui-layer-admin',
					btnAlign: 'c',
					btn: '好的',
					icon: 1,
					type: 1
				})
			} else {
				layer.open({
					title: "操作失败通知",
					content: data.msg,
					skin: 'layui-layer-admin',
					btnAlign: 'c',
					btn: '好的',
					icon: 2,
					type: 1
				})
			}
			layer.close(index);
		}, error: function () {
			layer.msg('服务器错误');
			layer.close(index);
		}
	});
}
function paychange() {
	var name = $("input[name='payname']").val();
	var qq = $("input[name='payqq']").val();
	var url = $("input[name='payurl']").val();
	var km = $("input[name='paykm']").val();
	var index = layer.msg('正在操作中...', {icon: 16, time: 10 * 1000});
	$.ajax({
		type: "post",
		url: "ajax.php?act=paychange",
		data : {name:name,qq:qq,url:url,km:km},
		dataType: 'json',
		success: function (data) {
			if (data.code == 0) {
				layer.open({
					title: "操作成功通知",
					content: data.msg,
					skin: 'layui-layer-admin',
					btnAlign: 'c',
					btn: '好的',
					icon: 1,
					type: 1
				})
			} else {
				layer.open({
					title: "操作失败通知",
					content: data.msg,
					skin: 'layui-layer-admin',
					btnAlign: 'c',
					btn: '好的',
					icon: 2,
					type: 1
				})
			}
			layer.close(index);
		}, error: function () {
			layer.msg('服务器错误');
			layer.close(index);
		}
	});
}
function userchange() {
	var user = $("input[name='user']").val();
	var pwd = $("input[name='pwd']").val();
	var qq = $("input[name='qq']").val();
	var power = $("select[name='power']").val();
	var email = $("input[name='email']").val();
	var km = $("input[name='userkm']").val();
	var index = layer.msg('正在操作中...', {icon: 16, time: 10 * 1000});
	$.ajax({
		type: "post",
		url: "ajax.php?act=userchange",
		data : {user:user,pwd:pwd,qq:qq,power:power,email:email,km:km},
		dataType: 'json',
		success: function (data) {
			if (data.code == 0) {
				layer.open({
					title: "操作成功通知",
					content: data.msg,
					skin: 'layui-layer-admin',
					btnAlign: 'c',
					btn: '好的',
					icon: 1,
					type: 1
				})
			} else {
				layer.open({
					title: "操作失败通知",
					content: data.msg,
					skin: 'layui-layer-admin',
					btnAlign: 'c',
					btn: '好的',
					icon: 2,
					type: 1
				})
			}
			layer.close(index);
		}, error: function () {
			layer.msg('服务器错误');
			layer.close(index);
		}
	});
}
function changeColor() {
	var color = "#EF5350|#FF8A80|#FF4081|#EC407A|#F44336|#E91E63|#00E676|#FF8F00|#F4511E|#FF3D00";
	color = color.split("|");
	document.getElementById("text_renqi").style.color = color[parseInt(Math.random() * color.length)];
}
function ajax_image() {
	$.ajax({
		type: "post",
		url: "ajax.php?act=image",
		async: true,
		typeData: 'json',
		success: function (data) {
			$(".images").fadeOut(0);
			$(".images").attr('style', 'background-image: url(' + data['image'] + ');')
			$(".images").fadeIn(6000);
			setTimeout("ajax_image();", 1000 * 30);
		}
	});
};
setTimeout("ajax_image();", 1000 * 30);
setTimeout("changeColor();", 1000);
ajax_image();
</script>
</body>
</html>