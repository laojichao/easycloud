<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="<?php echo $conf['sitename']?>,开发文档">
  <title>卡密查询 - <?php echo $conf['sitename']?></title>
  <meta name="keywords" content="<?php echo $conf['sitename']?>卡密查询,开发者后台验证系统，简单，快捷，安全，高效。">
  <link rel="shortcut icon" href="assets/img/favicon.ico" />
  <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700">
  <link rel="stylesheet" href="assets/css/nucleo.css" type="text/css">
  <link rel="stylesheet" href="assets/css/fortawesome.css" type="text/css">
  <link rel="stylesheet" href="assets/css/Yxwl.min.css" type="text/css">
 <script src="/assets/other/js/jquery.min.js"></script>
<script src="/assets/layui/layui.all.js"></script> <style type="text/css">.row {margin:0;}</style>
</head>
<body class="bg-default">
  <!-- Navbar -->
  <nav id="navbar-main" class="navbar navbar-horizontal navbar-transparent navbar-main navbar-expand-lg navbar-light">
    <div class="container">
      <a class="navbar-brand" href="/"><?php echo $conf['sitename']?></a>
      <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbar-collapse" aria-controls="navbar-collapse" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="navbar-collapse navbar-custom-collapse collapse" id="navbar-collapse">
        <div class="navbar-collapse-header">
          <div class="row">
            <div class="col-6 collapse-brand">
              <a href="/"><?php echo $conf['sitename']?></a>
            </div>
            <div class="col-6 collapse-close">
              <button type="button" class="navbar-toggler" data-toggle="collapse" data-target="#navbar-collapse" aria-controls="navbar-collapse" aria-expanded="false" aria-label="Toggle navigation">
                <span></span>
                <span></span>
              </button>
            </div>
          </div>
        </div>
           <ul class="navbar-nav mr-auto">
          <li class="nav-item">
            <a href="/index.php" class="nav-link">
              <span class="nav-link-inner--text">官网首页</span>
            </a>
          </li>
              <li class="nav-item">
            <a href="/user/agreement.php" class="nav-link">
              <span class="nav-link-inner--text">服务条款</span>
            </a>
          </li>
             <li class="nav-item">
            <a href="/user/login.php" class="nav-link">
              <span class="nav-link-inner--text">用户登录</span>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <div class="main-content">
    <!-- Header -->
    <div class="header bg-gradient-success py-7 py-lg-8 pt-lg-9">
      <div class="container">
        <div class="header-body text-center mb-7">
          <div class="row justify-content-center">
            <div class="col-xl-5 col-lg-6 col-md-8 px-5">
              <h1 class="text-white">卡密查询</h1>
            </div>
          </div>
        </div>
      </div>
      <div class="separator separator-bottom separator-skew zindex-100">
        <svg x="0" y="0" viewBox="0 0 2560 100" preserveAspectRatio="none" version="1.1" xmlns="http://www.w3.org/2000/svg">
          <polygon class="fill-default" points="2560 0 2560 100 0 100"></polygon>
        </svg>
      </div>
    </div>
    <!-- Page content -->
    <div class="container mt--8 pb-5">
      <div class="row justify-content-center">
        <div class="col-lg-5 col-md-7">
          <div class="card bg-secondary border-0 mb-0">
            <div class="card-body px-lg-5 py-lg-5">
              <div class="text-center text-muted mb-4">
                <small><?php echo $conf['sitename'].'欢迎您的使用！';?></small>
              </div>
              <form method="post" role="form" onSubmit="return checkURL();"> 
                <div class="form-group mb-2">
                  <div class="input-group input-group-merge input-group-alternative">
                    <div class="input-group-prepend">
                      <span class="input-group-text"><i class="ni ni-world"></i></span>
                    </div>
                    <input class="form-control" placeholder="请输入需要查询的卡密" value="<?=@$_POST['kami']?>" name="kami" type="text" required="">
                  </div>
                </div>         
               <br/>
               <button type="submit"class="btn btn-success form-control">确认查询</button>       
               <?php
 if($kami=addslashes($_POST['kami'])) {
	 $res = $DB->get_row("select * from yixi_appkm where binary kami='" . $kami . "' limit 1");
	 if ($res['state'] == 'n') {
     $status = '<font color="red">已封禁</font>';
     } else if ($res['km_use'] !== 'n') {
     $status = '<font color="red">已使用</font>';
     } else {
     $status = '<font color="green">未使用</font>';
     }
	 if ($res['state'] == 'n') {
     $usetime = '卡密已封禁';
     } else if ($res['km_use'] !== 'n') {
     $usetime = date('Y/m/d H:i:s',$res['use_time']);
     } else {
     $usetime = '卡密未使用';
     }
     if ($res['type'] == 'code') {
     if($res['km_time']=='hour'){
	 $km_code=$res['amount'].'小时';
     }else if($res['km_time']=='day'){
	 $km_code=$res['amount'].'天';
     }else if($res['km_time']=='week'){
	 $km_code=$res['amount'].'周';
     }else if($res['km_time']=='month'){
  	 $km_code=$res['amount'].'个月';
     }else if($res['km_time']=='season'){
	 $km_code=$res['amount'].'个季';
     }else if($res['km_time']=='year'){
   	 $km_code=$res['amount'].'年';
     }else if($res['km_time']=='longuse'){
	 $km_code='永久卡';
     }else if($res['km_time']=='vipcard'){
	 $km_code='贵宾卡';
     }   $type_name = '单码卡密';
     $xq = '可用于<font color="red">'.$program['name'].'</font>['.$km_code.']单码卡密登录';
     } else if ($res['type'] == 'vip') {
		 $type_name = '会员卡密';
     $xq = '可兑换<font color="red">'.$program['name'].'</font>['.$res['amount'].'天]会员';
     } else if ($res['type'] == 'fen') {
		 $type_name = '积分卡密';
     $xq = '可兑换<font color="red">'.$program['name'].'</font>['.$res['amount'].']积分';
     } else if ($res['type'] == 'single') {
		 $type_name = '次数卡密';
     $xq = '可用于<font color="red">'.$program['name'].'</font>['.$res['amount'].']次单码卡密登录';
     } else if ($res['type'] == 'svipcard') {
		 $type_name = '至尊卡';
     $xq = '可用于<font color="red">所有应用</font>单码卡密登录';
     } else {
     $xq = '未知的卡密类型';
     }
 	if($kami==$res['kami']){
 	echo '<hr><div class="alert alert-default">卡密状态：'.$status.'<br>卡密类型：'.$type_name.'<br>卡密详情：'.$xq.'</div>';
	}else{
	echo '<hr><div class="alert alert-default">查询结果：此卡密不存在！</div>';
}
}
$DB->close();
?>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- Footer -->
  <footer class="py-3" id="footer-main">
 <div class="container">
      <div class="row align-items-center justify-content-xl-between">
        <div class="col-xl-6">
          <div class="copyright text-center text-xl-left text-muted">
            &copy; <?=date('Y')?> <a href="/" class="font-weight-bold ml-1" target="_blank"><?php echo $conf['sitename']?></a>
          </div>
        </div>
        <div class="col-xl-6">
          <ul class="nav nav-footer justify-content-center justify-content-xl-end">
            <li class="nav-item">
              <a href="http://beian.miit.gov.cn/" class="nav-link" target="_blank">备案号：<?php echo $conf['sitename']?>-极致体验</a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </footer>
  <div style="display:none" align=center>
  <span style="display:none"> 
  </span>
  </div>
  <script src="assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>