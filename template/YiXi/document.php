<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="<?php echo $conf['sitename']?>,开发文档">
  <title>开发文档 - <?php echo $conf['sitename']?></title>
  <meta name="keywords" content="<?php echo $conf['sitename']?>开发文档,开发者后台验证系统，简单，快捷，安全，高效。">
  <link rel="shortcut icon" href="assets/images/favicon.ico" />
  <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700">
  <link rel="stylesheet" href="assets/css/nucleo.css" type="text/css">
  <link rel="stylesheet" href="assets/css/fortawesome.css" type="text/css">
  <link rel="stylesheet" href="assets/css/Yxwl.min.css" type="text/css">
 <script src="/assets/other/js/jquery.min.js"></script>
<script src="/assets/layui/layui.all.js"></script> <style type="text/css">.row {margin:0;}</style>
</head>
<body>
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
          </li>
            </a>
          </li>
        </ul>
      </div>
    </div>
  </nav>
  <!-- Main content -->
  <div class="main-content">
    <!-- Header -->
    <div class="header bg-gradient-success pt-5 pb-7">
      <div class="container">
        <div class="header-body text-center">
          <div class="row justify-content-center">
            <h1 class="text-white">开发文档</h1>
            <p class="text-lead text-white">使用此接口可以实现应用与云端的对接。适合用户进行对接开发程序，本页面对各项参数进行介绍及解释，便于用户的开发使用。温馨提示：check为二次效验功能，计算规则为md5(服务器返回的时间戳+APPKEY)</p>
          </div>
        </div>
      </div>
    </div><br>
            <div class="mt-2 mb-2">
            <div class="container">
                <div class="row">
					<div class="col-xl-12">
						<div class="card">
							<div class="card-body">
								<div>
									<h2>协议规则</h2>
									<p>传输方式：HTTP</p>
									<p>提交方式：POST</p>
									<p>返回格式：JSON</p>
									<p>数据加密：RSA、RC4</p>
									<p>签名算法：MD5</p>
									<p>字符编码：UTF-8</p>
									<hr/>
								</div>
								 <?php 
		                        $rs=$DB->query("SELECT * FROM yixi_program WHERE visible='y'");
                                foreach($rs as $res):?>
                                <a href="../doc.php?act=<?php echo $res['api_path'];?>" "target="_blank" class="btn btn-link btn-sm">
						        [API]<?php echo $res['name'];?></a>
                                <?php endforeach;
                                ?>
								<a href="#sign"class="btn btn-link btn-sm">[示例]sign计算</a>
								<a href="#sdk"class="btn btn-link btn-sm">[示例]SDK及工具</a>
								</div>
							</div> <!-- end card-body-->
						</div> <!-- end card-->
					</div> <!-- end col -->
				</div><!-- end row-->
            </div><!-- end container -->
        </div>
	<!-- end container -->
</div><!-- end page -->
        <div class="mt-1 mb-2">
	<div class="container">
		<div class="row">
			<div class="col-xl-12">
				<div class="card">
					<div class="card-body">
						<h2 id="sign">[示例]Sign签名计算方式</h2>
						
						<p>已注册普通注册为例：</p>
					<p class="linep">代码示例：</p>
				<pre class="layui-code">Sign = 取MD5值("user=" + 编辑框_user.内容 + "&password=" + 编辑框_mima.内容 + "&inv=" + 编辑框_inv.内容 + "&markcode=" + 机器码 + "&t=" + 取现行时间戳(2) + "&" + APPKEY)</pre>
						<div class="alert alert-warning alert-dismissible fade show mt-2" role="alert">
							<button type="button" class="close" data-dismiss="alert" aria-label="Close">
								<span aria-hidden="true">&times;</span>
							</button>
							<strong>算法提示 - </strong> 不管是什么接口，只需要将所有post的数据进行拼接，然后 加上：&APPKEY 取MD5值即是Sign签名参数
						</div>
					</div>
				</div> <!-- end /.text-center-->
			</div> <!-- end col -->
		</div>
		<!-- end row -->
	</div>
	<!-- end container -->
</div><!-- end page -->
		<div class="mt-1 mb-2">
	<div class="container">
		<div class="row">
			<div class="col-xl-12">
				<div class="card">
					<div class="card-body">
						<h2 id="sdk">[示例]SDK及工具下载</h2>
						<a href="./SDK/#" "target="_blank" class="btn btn-link btn-sm">[示例]lua</a>
						<a href="./SDK/#" "target="_blank" class="btn btn-link btn-sm">[示例]andlua</a>
						<a href="./SDK/#" "target="_blank" class="btn btn-link btn-sm">[示例]iapp</a>
						<a href="./SDK/#" "target="_blank" class="btn btn-link btn-sm">[示例]结绳类库</a>
						<a href="./SDK/#" "target="_blank" class="btn btn-link btn-sm">[软件]lua一键注入</a>
					</div>
				</div> <!-- end /.text-center-->
			</div> <!-- end col -->
		</div>
		<!-- end row -->
	</div>
		<div class="mt-5">
          </div>
        </div>
      </div>
    </div>
  </div>
  <footer class="py-5" id="footer-main">
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
              <a href="http://beian.miit.gov.cn/" class="nav-link" target="_blank">备案号：琼ICP备2020004104号</a>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </footer>
  <script src="assets/js/bootstrap.bundle.min.js"></script>
</body>
</html> 