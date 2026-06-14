<!doctype html>
<html lang="zh">

<head>
	<meta charset="UTF-8">
	<meta name="viewport"
		content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
	<meta http-equiv="X-UA-Compatible" content="ie=edge">
	<title><?php echo $conf['sitename']?> - <?php echo $conf['title']?></title>
	<meta name="description" content="<?php echo $web['description'];?>">
	<meta name="keywords" content="<?php echo $web['keywords'];?>">
	<meta name="author" content="<?php echo $conf['sitename']?>">
	<meta name="founder" content="<?php echo $conf['sitename']?>">
	<link href="/assets/other/css/site.min.css" rel="stylesheet">
	<link href="/assets/other/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" href="/assets/other/css/layui.css">
	<link href="/assets/other/css/oneui.css" rel="stylesheet">
	<link rel="shortcut icon" type="image/x-icon" href="/favicon.ico">
	<script src="/assets/other/js/jquery.min.js"></script>
	<script src="/assets/layui/layui.all.js"></script>
</head>

<body>
	<header class="site-header">
		<nav class="nav_jsxs">
			<span style="float: left;"><a class="logo_jsxs" href=""></a></span>
			<a href="../">首页</a>
			<a href="./user/login.php">登录</a>
			<a href="./?mod=document">文档</a>
		</nav>
		<div class="box-text">
			<h1><?php echo $conf['sitename']?></h1>
			<p>稳定、快速、高效的 数据接口服务<br>
				<span class="package-amount">当前接口状态 · <?php if($row['active']=="y"){echo'正常';}else{echo'<font color="#ff3d00">接口维护中</font>';}?>
				</span>
			</p>
		</div>

	</header>
<style>
	.url {
		word-break: break-all;
		cursor: pointer;
		margin-left: 5px;
		color: #777;
		border: none;
		border-radius: 0;
		border-bottom: 2px solid #5FB878;
	}

	.simpleTable {
		line-height: 20px;
		padding-bottom: 16px;
	}

	.linep {
		font-size: 14px;
		font-weight: 700;
		color: #555;
		padding-left: 14px;
		height: 16px;
		line-height: 16px;
		margin-bottom: 18px;
		position: relative;
		margin-top: 15px;
	}

	.linep:before {
		content: '';
		width: 4px;
		height: 16px;
		background: #00aeff;
		border-radius: 2px;
		position: absolute;
		left: 0;
		top: 0;
	}

	::-webkit-scrollbar {
		width: 9px;
		height: 9px
	}

	::-webkit-scrollbar-track-piece {
		background-color: #ebebeb;
		/* -webkit-border-radius: 4px */
	}

	::-webkit-scrollbar-thumb:vertical {
		height: 32px;
		background-color: #ccc;
		/* -webkit-border-radius: 4px */
	}

	::-webkit-scrollbar-thumb:horizontal {
		width: 32px;
		background-color: #ccc;
		/* -webkit-border-radius: 4px */
	}

	.layui-container {
		min-height: 273px;
	}
</style>
<div class="layui-container">
	<div class="layui-row">
		<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
			<legend><?php echo $data['info']['l_title'];?></legend>
		</fieldset>
		<blockquote class="layui-elem-quote"><?php echo $row['desc'];?></blockquote>
	</div>
	<div class="layui-tab layui-tab-brief" lay-filter="docDemoTabBrief">
		<ul class="layui-tab-title" style="text-align: center!important;">
			<li class="layui-this">API文档</li>
			<li>错误码参照</li>
			<li>示例代码</li>
		</ul>
		<div class="layui-tab-content">
			<div class="layui-tab-item layui-show">
				<p class="simpleTable">
					<span class="layui-badge layui-bg-black">接口地址：</span>
					<span class="url" data-clipboard-text="<?php echo $_SERVER['REQUEST_SCHEME'].'s://'.$_SERVER['HTTP_HOST'].'/api.php?api='.$row['api_path'];?>">
						<?php echo $_SERVER['REQUEST_SCHEME'].'s://'.$_SERVER['HTTP_HOST'].'/api.php?api='.$row['api_path'];?>
					</span>
				</p>
				<p class="simpleTable">
					<span class="layui-badge layui-bg-green">返回格式：</span>
					<span class="url" data-clipboard-text="<?php echo $row['api_return'];?>">
						<?php echo $row['api_return'];?>
					</span>
				</p>
				<p class="simpleTable">
					<span class="layui-badge">请求方式：</span>
					<span class="url" data-clipboard-text="<?php echo $row['api_request'];?>">
						<?php echo $row['api_request'];?>
					</span>
				</p>
				<p class="simpleTable">
					<span class="layui-badge layui-bg-blue">请求示例：</span>
					<span class="url" data-clipboard-text="<?php echo $row['api_url'];?>">
						<?php echo $row['api_url'];?>
					</span>
				</p>
				<p class="linep">请求参数说明：</p>
				<table class="layui-table" lay-size="sm">
					<thead>
						<tr>
							<th>名称</th>
							<th>变量</th>
							<th>必填</th>
							<th>类型</th>
							<th>说明</th>
						</tr>
					</thead>
					<tbody>	
			   		<?php include_once 'doc/'.$row['api_path'].'/request.html';?>
					</tbody>
				</table>
				<p class="linep">返回参数说明：</p>
				<table class="layui-table" lay-size="sm">
					<thead>
						<tr>
							<th>名称</th>
							<th>类型</th>
							<th>说明</th>
						</tr>
					</thead>
					<tbody>
					<?php include_once 'doc/'.$row['api_path'].'/return.html';?>
					</tbody>
				</table>
				<p class="linep">返回示例：</p>
				<pre class="layui-code"><?php include_once 'doc/'.$row['api_path'].'/demo.html';?></pre>
			</div>
			<div class="layui-tab-item">
				<p class="linep">错误码格式说明：</p>
				<table class="layui-table" lay-size="sm">
					<thead>
						<tr>
							<th>名称</th>
							<th>类型</th>
							<th>说明</th>
						</tr>
					</thead>
					<tbody>
					<?php 
					include_once 'DefaultDoc/error.html';
					include_once 'doc/'.$row['api_path'].'/error.html';
					?>
					</tbody>
				</table>
			</div>
			<div class="layui-tab-item">
				<p class="linep">代码示例：</p>
				<pre class="layui-code"><?php include_once 'doc/'.$row['api_path'].'/example.html';?></pre>
			</div>
		</div>
	</div>
</div>
<script src="/assets/other/js/clipboard.min.js"></script>
<script>
	layui.use('code', function () { //加载code模块
		layui.code(); //引用code方法
	});
	var clipboard = new ClipboardJS('.url');
	clipboard.on('success', function (e) {
		layer.msg('复制成功!');
	});
	clipboard.on('error', function (e) {
		layer.msg('复制成功!');
	});
</script>
<footer id="footer" class="footer hidden-print1">
      <div class="copy-right">
        <?php echo $conf['icp'];?>
    </div>
   </div>
</footer>
</body>

</html>