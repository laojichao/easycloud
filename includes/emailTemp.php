<?php
function email1($title,$code,$conf,$date)
{
	return '<!DOCTYPE html>
<html>
<head>
	<title>'.$conf['sitename'].'-邮件通知</title>
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1">
</head>
<body>
	<div id="qm_con_body">
		<div id="mailContentContainer" class="qmbox qm_con_body_content qqmail_webmail_only" style="">
			<div style="margin:0px;padding:0px;border:0px;outline:0px;font-family:Helvetica, Tahoma, Arial;font-size:14px;white-space:normal;">
				<div style="margin:0px;padding:0px;border:0px;outline:0px;">
					<div style="margin:0px;padding:0px;border:0px;outline:0px;position:relative;">
						<div class="mobile" style="margin:50px auto;padding:0px;border:0px;outline:0px;background:#FFFFFF;box-shadow:0px 2px 8px 0px;border-radius:4px;max-width:696px;overflow:hidden;">
							<h1 style="text-align:center;margin:0px;padding:20px 50px;border:0px;outline:0px;background:#5A86E1;"><strong style="" color:=""><span style="color:#FFFFFF;font-size:32px;">'.$title.'</strong></h1>
							<div class="content_wrap" style="margin:0px;padding:0px 50px;border:0px;outline:0px;">
								<p style="text-align:center;font-size:18px;margin-top:34px;"><strong style="font-family:Helvetica, Tahoma, Arial;text-align:center;white-space:normal;background-color:#FFFFFF;font-size:32px;"><span style="color:#337FE5;">您好</span><span style="color:#337FE5;"><span style="display:none;" id="__kindeditor_bookmark_start_15__"></span> '.$conf['site_name'].'<span style="display:none;" id="__kindeditor_bookmark_end_16__"></span>用户</span></strong></p>
								<p style="text-align:center;margin-top:8px;font-family:Helvetica, Tahoma, Arial;white-space:normal;background-color:#FFFFFF;font-size:16px;"><span style="font-size:24px;color:#337FE5;"><strong>感谢您使用'.$conf['sitename'].'</strong></span><span style="font-size:24px;color:#337FE5;"><strong>，您的满意便是我们的动力</strong></span></p>
								<div style="text-align:center;margin:24px 0px 0px;padding:0px;border-width:1px 0px 0px;border-top-style:solid;border-right-style:initial;border-bottom-style:initial;border-left-style:initial;border-top-color:#F1F1F1;border-right-color:initial;border-bottom-color:initial;border-left-color:initial;border-image:initial;outline:0px;">
							</div>
								<p class="p2" style="text-align:center;font-family:Helvetica, Tahoma, Arial;font-size:14px;white-space:normal;background-color:#FFFFFF;"><blockquote style="padding: 10px 20px; margin: 0 0 20px; font-size: 17.5px; border-left: 5px solid #eee;"><p>'.$code.'</p></blockquote></p>
								<table border="0" cellpadding="0" cellspacing="0" class="ke-zeroborder" style=" text-align:center; " box-sizing:border-box=""><tbody><tr></tr></tbody></table>
								<p><br></p>
								<hr style="text-align:center;">
								<p class="p2" style="text-align:center;font-family:Helvetica, Tahoma, Arial;font-size:14px;white-space:normal;background-color:#FFFFFF;"><span style="text-align:right;font-size:16px;color:#999999;">来自 </span><span style="text-align:right;font-size:16px;color:#999999;">'.$conf['sitename'].'官方：<a href="'.$authurl.'" rel="noopener" target="_blank">'.$_SERVER['HTTP_HOST'].'</a></span></p>
							</div>
						</div>
					</div>
				</div>
			</div>
			<style type="text/css">
			.qmbox style,
			.qmbox script,
			.qmbox head,
			.qmbox link,
			.qmbox meta {
				display: none !important;
			}
			</style>
		</div>
	</div>
</body>

</html>';
}
 
function email2($title,$code,$conf,$date)
{
 
 return'<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<title>'.$conf['sitename'].'-邮件通知</title>
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1">
</head>

<body>
	<div style="width:90%; margin:0 auto; background:#fafafa;margin:50px auto;padding:0px;border:0px;outline:0px;border-radius:6px;max-width:696px;overflow:hidden;">
		<table cellpadding="0" cellspacing="0" border="0" align="center" width="100%" style="">
			<tbody>
				<tr bgcolor="#35bdbc" height="80">
					<td width="580" style="line-height:10px;" align="center">
						<span style="font-size:24px; color:#ffffff;"></span><br>
						<span style="color:#ffffff; font-size:36px;"><span>'.$title.'</span></span></td>
				</tr>
			</tbody>
		</table>
		&nbsp;&nbsp;
		<table cellpadding="0" cellspacing="0" border="0" align="center" width="86%" style="">
			<tbody>
				<tr height="20"></tr>
				<tr>
					<td style="">
						<img style="margin-right:5px; " src="http://www.jiankongbao.com/img/report/mark.png"><b>操作通知</b>
					</td>
				</tr>
				<tr height="20"></tr>
			</tbody>
		</table>
		<table cellpadding="0" cellspacing="0" border="0" align="center" width="86%" style="background-color: white;">
			<tbody>
				<tr height="20"></tr>
				<tr>
					<td style="" align="center">
						<span style="font-size:24px; color:#000;">' . $code . '</span>
					</td>
				</tr>
				<tr height="20"></tr>
			</tbody>
		</table>
		<table width="100%" cellspacing="0" border="0" cellpadding="0" style="background-color: #fafafa;">
			<tbody>
				<tr>
					<td width="20"></td>
					<td>
						<table width="100%" cellspacing="0" border="0" cellpadding="0">
							<tbody>
								<tr>
									<td height="20"></td>
								</tr>
								<tr>
								</tr>
								<tr>
									<td height="20"></td>
								</tr>
							</tbody>
						</table>
					</td>
					<td width="20"></td>
				</tr>
			</tbody>
		</table>
		<table width="100%" cellspacing="0" border="0" cellpadding="0" style="background-color: #35bdbc;">
			<tbody>
				<tr>
					<td width="20"></td>
					<td>
						<table width="100%" cellspacing="0" border="0" cellpadding="0">
							<tbody>
								<tr>
									<td height="20"></td>
								</tr>
								<tr>
									<td style="font-size:13px;text-align:center;">Copyright &nbsp;© 2020 <strong>'.$conf['sitename'].'</strong>版权所有</td>
								</tr>
								<tr>
									<td height="20"></td>
								</tr>
							</tbody>
						</table>
					</td>
					<td width="20"></td>
				</tr>
			</tbody>
		</table>
	</div>
</body>
</html>';
}

function email3($title,$code,$conf,$date)
{
	return '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en">

<head>
	<meta charset="UTF-8">
	<title>邮件提醒</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
</head>

<body style="margin: 0; padding: 0;">
	<table align="center" border="0" cellpadding="0" cellspacing="0" width="90%" style="border-collapse: collapse;">
		　
		<tr>
			<td>
				<div style="margin: 20px;text-align: center;margin-top: 50px">
					<h3>'.$title.'</h3>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div style="border: #36649d 1px dashed;margin: 30px;padding: 20px">
					<label style="font-size: 22px;color: #36649d;font-weight: bold">'.$code.'</label>
					<p style="font-size: 16px">亲爱的&nbsp;<label style="font-weight: bold"> '.$conf['sitename'].'用户</label>
					</p>
					<p style="font-size: 16px">您的满意便是我们的动力，希望更好的为您服务！</p>
				</div>
			</td>
		</tr>
		　
		<tr>
			<td>
				<div style="margin: 40px">
					<p style="color:red;font-size: 14px ">（这是一封自动发送的邮件，请勿回复。）</p>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div align="right" style="margin: 40px;border-top: solid 1px gray" id="bottomTime">
					<p style="margin-right: 20px"> '.$conf['sitename'].' 团队</p>
					<label style="margin-right: 20px">'.$date.'</label>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>';
}
function email4($title,$code,$conf,$date)
{
	return '<div style="width:800px;padding:10px;color:#333;background-color:#fff;border-radius:10px;box-shadow:4px 4px 12px #999;font-family:Verdana, sans-serif;margin:auto;">
	<header style="height:15px;background:url(https://weixin.qq.com/zh_CN/htmledition/images/weixin/letter/mmsgletter_2_bg_topline.png) repeat-x 0 0;"></header>
	<main style="text-align:left;padding:20px;font-size:14px;line-height:1.5;">
		<article>'.$code.'</article>
		<aside style="padding-top:30px;">
			<img class="ASpayGravatar"src="https://q4.qlogo.cn/g?b=qq&nk='.$conf['kfqq'].'&s=40"style="float:left;">
			<footer style="margin-left:54px;">
				<p class="ASpayName"style="margin:0 0 10px;">'.$conf['sitename'].'官方 - '.$_SERVER['HTTP_HOST'].'</p>
				<span class="ASpayInfo"style="font-size:12px;line-height:1.2;color:#407700;">'.$conf['sitename'].'官方客服QQ：<a href="http://wpa.qq.com/msgrd?v=3&uin='.$conf['kfqq'].'&site=qq&menu=yes"target="_blank">'.$conf['kfqq'].'</a><br>
					<span style="color:#407700;">'.$conf['sitename'].'官方交流群：<a href="'.$conf['qunlj'].'"target="_blank">'.$conf['qunhao'].'</a></span>
				</span>
			</footer>
		</aside>
	</main>
</div>
';
}
function email5($title,$code,$conf,$date)
{
	return '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html lang="en">

<head>
	<meta charset="UTF-8">
	<title>邮件提醒</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
</head>
<body>
	<p>'.$title.'</p>
	<p>'.$code.'</p>
	<p>'.$conf['sitename'].'：<a href="'.$authurl.'">'.$_SERVER['HTTP_HOST'].'</a></p>
</body>
</html> ';
}
?>