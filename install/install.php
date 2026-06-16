<?php
error_reporting(0);
@header('Content-Type: text/html; charset=UTF-8');
$do=isset($_GET['do'])?$_GET['do']:'0';
if(file_exists('install.lock')){
	$installed=true;
	$do='0';
}

function checkfunc($f,$m = false) {
	if (function_exists($f)) {
		return '<font color="green">可用</font>';
	} else {
		if ($m == false) {
			return '<font color="black">不支持</font>';
		} else {
			return '<font color="red">不支持</font>';
		}
	}
}

function checkclass($f,$m = false) {
	if (class_exists($f)) {
		return '<font color="green">可用</font>';
	} else {
		if ($m == false) {
			return '<font color="black">不支持</font>';
		} else {
			return '<font color="red">不支持</font>';
		}
	}
}

function random($length, $numeric = 0) {
	$seed = base_convert(md5(microtime().$_SERVER['DOCUMENT_ROOT']), 16, $numeric ? 10 : 35);
	$seed = $numeric ? (str_replace('0', '', $seed).'012340567890') : ($seed.'zZ'.strtoupper($seed));
	$hash = '';
	$max = strlen($seed) - 1;
	for($i = 0; $i < $length; $i++) {
		$hash .= $seed{mt_rand(0, $max)};
	}
	return $hash;
}

?>

<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport">
    <meta content="yes" name="apple-mobile-web-app-capable">
    <meta content="black" name="apple-mobile-web-app-status-bar-style">
    <title><?php echo base64_decode('5p6B566A6aqM6K+B57O757ufLeWuieijhQ==')?></title>
    <link href="https://bootswatch.com/3/paper/bootstrap.min.css" rel="stylesheet">
</head>
<body background="../assets/img/bg.jpg">
<div class="container"><br>
    <div class="row">
        <div class="col-xs-12 col-sm-10 col-md-8 col-lg-6 center-block text-center" style="float: none;">
            <div class="alert alert-success" role="alert"><center>我们不断进步-只为用户更好的体验</center></div>
        </div>
        <div class="col-xs-12 col-sm-10 col-md-8 col-lg-6 center-block" style="float: none;">
<div class="panel panel-primary">

<?php if ($do == '0') { ?>
<div class="panel panel-black">
	<div class="panel panel-primary">
	<div class="panel-heading" style="background: #15A638;">
		<h3 class="panel-title" align="center"><?php echo base64_decode('5p6B566A6aqM6K+B57O757ufLeWuieijhQ==')?></h3>
	</div>
	<div class="panel-body">
	<img src="../assets/img/timthumb.jpg" alt="img"; width=99% height=99%><p><p><p><center><font color="#00FFFF">不</font><font color="#05FAFF">妥</font><font color="#0AF5FF">协</font><font color="#0FF0FF">，</font><font color="#14EBFF">不</font><font color="#19E6FF">逐</font><font color="#1EE1FF">流</font><font color="#23DCFF">；</font><font color="#28D7FF">随</font><font color="#2DD2FF">性</font><font color="#32CDFF">而</font><font color="#37C8FF">不</font><font color="#3CC3FF">失</font><font color="#41BEFF">个</font><font color="#46B9FF">性</font><font color="#4BB4FF">，</font><font color="#50AFFF">有</font><font color="#55AAFF">设</font><font color="#5AA5FF">计</font><font color="#5FA0FF">而</font><font color="#649BFF">不</font><font color="#6996FF">漏</font><font color="#6E91FF">痕</font><font color="#738CFF">迹</font><br><font color="#7887FF">极</font><font color="#7D82FF">简</font><font color="#827DFF">验</font><font color="#8778FF">证</font><font color="#8C73FF">系</font><font color="#916EFF">统</font><font color="#9669FF">，</font><font color="#9B64FF">繁</font><font color="#A060FF">华</font><font color="#A560FF">阅</font><font color="#AA60FF">尽</font><font color="#AF60FF">处</font><font color="#B460FF">，</font><font color="#B960FF">简</font><font color="#BE60FF">约</font><font color="#C360FF">不</font><font color="#C860FF">简</font><font color="#CD60FF">单</font><font color="#D260FF">！</font></center>
<?php if ($installed) { ?>
		<div class="alert alert-warning">您已经安装过极简验证系统，如需重新安装请删除<font color=red> “install/install.lock” </font>文件后再安装！</div>
		<?php }else{?>
		<p align="center"><a class="btn btn-primary" href="?do=1">开始安装</a></p>
		<?php }?>
	</div>
</div>

<?php }elseif($do=='1'){?>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" align="center">环境检查</h3>
	</div>
<div class="progress progress-striped">
  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 10%">
	<span class="sr-only">10%</span>
  </div>
</div>
<table class="table table-striped">
	<thead>
		<tr>
			<th style="width:20%">函数检测</th>
			<th style="width:15%">需求</th>
			<th style="width:15%">当前</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>PHP 5.2+</td>
			<td><font color="red">安装后倘若运行出错，请切换PHP版本至正常运行为止！</font></td>
			<td><?php echo phpversion(); ?></td>
		</tr>
		<tr>
			<td>curl_exec()</td>
			<td>必须</td>
			<td><?php echo checkfunc('curl_exec',true); ?></td>
		</tr>
		<tr>
			<td>file_get_contents()</td>
			<td>必须</td>
			<td><?php echo checkfunc('file_get_contents',true); ?></td>
		</tr>
	</tbody>
</table>
<p><span><a class="btn btn-primary" href="install.php?do=0">上一步</a></span>
<span style="float:right"><a class="btn btn-primary" href="install.php?do=2" align="right">下一步</a></span></p>
</div>

<?php }elseif($do=='2'){?>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" align="center">数据库配置</h3>
	</div>
<div class="progress progress-striped">
  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 30%">
	<span class="sr-only">30%</span>
  </div>
</div>
	<div class="panel-body">
	<?php
if(defined("SAE_ACCESSKEY"))
echo <<<HTML
检测到您使用的是SAE空间，支持一键安装，请点击 <a href="?do=3">下一步</a>
HTML;
else
echo <<<HTML
		<form action="?do=3" class="form-sign" method="post">
		<label for="name">数据库地址:</label>
		<input type="text" class="form-control" name="db_host" value="localhost">
		<label for="name">数据库端口:</label>
		<input type="text" class="form-control" name="db_port" value="3306">
		<label for="name">数据库用户名:</label>
		<input type="text" class="form-control" name="db_user">
		<label for="name">数据库密码:</label>
		<input type="text" class="form-control" name="db_pwd">
		<label for="name">数据库名:</label>
		<input type="text" class="form-control" name="db_name">
		<br><input type="submit" class="btn btn-primary btn-block" name="submit" value="保存配置">
		</form><br/>
  <center>如果已事先填写好config.php文件中相关数据库配置，请 <a href="?do=3&jump=1">点击此处</a> 跳过这一步。<br><font color="red">安装后倘若运行出错，请切换PHP版本至正常运行为止！</font><br>极简验证系统-极致体验</center>
HTML;
?>
	</div>
</div>
<?php }elseif($do=='3'){
?>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" align="center">保存数据库</h3>
	</div>
<div class="progress progress-striped">
  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 50%">
	<span class="sr-only">50%</span>
  </div>
</div>
	<div class="panel-body">
<?php
require './db.class.php';
if(defined("SAE_ACCESSKEY") || $_GET['jump']==1){
	include_once '../config.php';
	if(!$user||!$pwd||!$dbname) {
		echo '<div class="alert alert-danger">请先填写好数据库并保存后再安装！<hr/><a href="javascript:history.back(-1)"><< 返回上一页</a></div>';
	} else {
		if(!$con=DB::connect($host,$user,$pwd,$dbname,$port)){
			if(DB::connect_errno()==2002)
				echo '<div class="alert alert-warning">连接数据库失败，数据库地址填写错误！</div>';
			elseif(DB::connect_errno()==1045)
				echo '<div class="alert alert-warning">连接数据库失败，数据库用户名或密码填写错误！</div>';
			elseif(DB::connect_errno()==1049)
				echo '<div class="alert alert-warning">连接数据库失败，数据库名不存在！</div>';
			else
				echo '<div class="alert alert-warning">连接数据库失败，['.DB::connect_errno().']'.DB::connect_error().'</div>';
		}else{
			echo '<div class="alert alert-success">数据库配置文件保存成功！</div>';
 if (DB::query("select * from yixi_config where 1") == FALSE) echo '<p align="right"><a class="btn btn-primary btn-block" href="?do=4">创建数据表>></a></p>';
 else echo '<div class="list-group-item list-group-item-info">系统检测到你已安装过极简验证系统</div>
				<div class="list-group-item">
					<a href="?do=6" class="btn btn-block btn-info">跳过安装</a>
				</div>
				<div class="list-group-item">
					<a href="?do=4" onclick="if(!confirm(\'全新安装将会清空所有数据，是否继续？\')){return false;}" class="btn btn-block btn-warning">强制全新安装</a>';
		}
	}
}else{
	$db_host=isset($_POST['db_host'])?$_POST['db_host']:NULL;
	$db_port=isset($_POST['db_port'])?$_POST['db_port']:NULL;
	$db_user=isset($_POST['db_user'])?$_POST['db_user']:NULL;
	$db_pwd=isset($_POST['db_pwd'])?$_POST['db_pwd']:NULL;
	$db_name=isset($_POST['db_name'])?$_POST['db_name']:NULL;
	$db_qz=isset($_POST['db_qz'])?$_POST['db_qz']:'keke';
	if($db_host==null || $db_port==null || $db_user==null || $db_pwd==null || $db_name==null){
		echo '<div class="alert alert-danger">保存错误,请确保每项都不为空<hr/><a href="javascript:history.back(-1)"><< 返回上一页</a></div>';
	} else {
		$config="<?php
/*数据库配置*/
\$dbconfig=array(
	'host' => '{$db_host}', //数据库服务器
	'port' => {$db_port}, //数据库端口
	'user' => '{$db_user}', //数据库用户名
	'pwd' => '{$db_pwd}', //数据库密码
	'dbname' => '{$db_name}', //数据库名
	'dbqz' => '{$db_qz}' //数据表前缀
);
?>";
		if(!$con=DB::connect($db_host,$db_user,$db_pwd,$db_name,$db_port)){
			if(DB::connect_errno()==2002)
				echo '<div class="alert alert-warning">连接数据库失败，数据库地址填写错误！</div>';
			elseif(DB::connect_errno()==1045)
				echo '<div class="alert alert-warning">连接数据库失败，数据库用户名或密码填写错误！</div>';
			elseif(DB::connect_errno()==1049)
				echo '<div class="alert alert-warning">连接数据库失败，数据库名不存在！</div>';
			else
				echo '<div class="alert alert-warning">连接数据库失败，['.DB::connect_errno().']'.DB::connect_error().'</div>';
		}elseif(file_put_contents('../config.php',$config)){
			echo '<div class="alert alert-success">数据库配置文件保存成功！</div>';
			if(DB::query("select * from auth_config where 1")==FALSE)
				echo '<p align="right"><a class="btn btn-primary btn-block" href="?do=4">创建数据表>></a></p>';
			else
				echo '<div class="list-group-item list-group-item-info">系统检测到你已安装过极简验证系统</div>
				<div class="list-group-item">
					<a href="?do=6" class="btn btn-block btn-info">跳过安装</a>
				</div>
				<div class="list-group-item">
					<a href="?do=4" onclick="if(!confirm(\'全新安装将会清空所有数据，是否继续？\')){return false;}" class="btn btn-block btn-warning">强制全新安装</a>
				</div>';
		}else
			echo '<div class="alert alert-danger">保存失败，请确保网站根目录有写入权限<hr/><a href="javascript:history.back(-1)"><< 返回上一页</a></div>';
	}
}
?>
	</div>
</div>
<?php }elseif($do=='4'){?>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" align="center">创建数据表</h3>
	</div>
<div class="progress progress-striped">
  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 70%">
	<span class="sr-only">70%</span>
  </div>
</div>
	<div class="panel-body">
<?php
include_once '../config.php';
if(!$dbconfig['user']||!$dbconfig['pwd']||!$dbconfig['dbname']) {
	echo '<div class="alert alert-danger">请先填写好数据库并保存后再安装！<hr/><a href="javascript:history.back(-1)"><< 返回上一页</a></div>';
} else {
	require './db.class.php';
	$sql=file_get_contents("simple.sql");
	$sql=explode(';',$sql);
	$cn = DB::connect($dbconfig['host'],$dbconfig['user'],$dbconfig['pwd'],$dbconfig['dbname'],$dbconfig['port']);
	if (!$cn) die('err:'.DB::connect_error());
	DB::query("set sql_mode = ''");
	DB::query("set names utf8");
	$t=0; $e=0; $error='';
	for($i=0;$i<count($sql);$i++) {
		if ($sql[$i]=='')continue;
		if(DB::query($sql[$i])) {
			++$t;
		} else {
			++$e;
			$error.=DB::error().'<br/>';
		}
	}
	date_default_timezone_set("PRC");
	$aa=123;
	DB::query("INSERT INTO `yixi_config` (`k`, `v`) VALUES ('syskey', '".random(32)."')");
}
if($e==0) {
	echo '<div class="alert alert-success">安装成功！<br/>SQL成功'.$t.'句/失败'.$e.'句</div><p align="right"><a class="btn btn-block btn-primary" href="install.php?do=5">下一步>></a></p>';
} else {
	echo '<div class="alert alert-danger">安装失败<br/>SQL成功'.$t.'句/失败'.$e.'句<br/>错误信息：'.$error.'</div><p align="right"><a class="btn btn-block btn-primary" href="install.php?do=4">点此进行重试</a></p>';
}
?>
	</div>
</div>

<?php }elseif($do=='5'){?>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" align="center">安装完成</h3>
	</div>
<div class="progress progress-striped">
  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
	<span class="sr-only">100%</span>
  </div>
</div>
	<div class="panel-body">
<?php
	@file_put_contents("install.lock",'欢迎使用极简验证系统，这是安装锁文件！没有安全何来保障？2022，我们依旧在努力！');
	echo '<div class="alert alert-info" style="background-color:#2196f3"><font color="#FF0000">恭喜，极简验证系统已安装完成！（安装后倘若运行出错，请切换PHP版本至正常运行为止！）请使用安装时设置的管理员账号密码登录</font><br/><br/><a href="../">>>网站首页</a>｜<a href="../admin/">>>后台管理</a><hr/>更多设置选项请登录后台管理进行修改。<br/><br/><font color="black">如果你的空间不支持本地文件读写，请自行在install/ 目录建立 install.lock 文件！</font></div>';
?>
</div>
</div>

<?php }elseif($do=='6'){?>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title" align="center">安装完成</h3>
	</div>
<div class="progress progress-striped">
  <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
	<span class="sr-only">100%</span>
  </div>
</div>
	<div class="panel-body">
<?php
	@file_put_contents("install.lock",'欢迎使用极简验证系统，这是安装锁文件！没有安全何来保障？2022，我们依旧在努力！');
	echo '<div class="alert alert-info" style="background-color:#2196f3"><font color="#FF0000">恭喜，极简验证系统已安装完成！（安装后倘若运行出错，请切换PHP版本至正常运行为止！）请使用安装时设置的管理员账号密码登录</font><br/><br/><a href="../">>>网站首页</a>｜<a href="../admin/">>>后台管理</a><hr/>更多设置选项请登录后台管理进行修改。<br/><br/><font color="black">如果你的空间不支持本地文件读写，请自行在install/ 目录建立 install.lock 文件！</font></div>';
?>
	</div>
</div>
<?php }?>
</div>
</body>
</html>