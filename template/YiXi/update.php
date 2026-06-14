<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="<?php echo $confs['css_title']?> by YiXi">
  <meta name="author" content="YiXi">
  <title>更新记录 | <?php echo $conf['sitename']?></title>
  <meta name="keywords" content="<?php echo $conf['keywords'];?>">
  <meta name="description" content="<?php echo $conf['description'];?>">
  <link rel="shortcut icon" href="./assets/img/favicon.ico" />
  <link rel="stylesheet" href="./assets/css/nucleo.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="./assets/css/sweetalert.css"/>
  <script src="./assets/js/sweetalert.min.js"></script>
  <link rel="stylesheet" href="./assets/css/fortawesome.css" type="text/css">
  <link rel="stylesheet" href="./assets/css/Yxwl.min.css" type="text/css">
  <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700">
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
            <a href="./index.php" class="nav-link">
              <span class="nav-link-inner--text">官网首页</span>
            </a>
          </li>
           <li class="nav-item">
            <a href="./user/index.php" class="nav-link">
              <span class="nav-link-inner--text">用户登录</span>
            </a>
          </li>
             <li class="nav-item">
            <a href="#" class="nav-link">
              <span class="nav-link-inner--text">卡密查询</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="#" class="nav-link">
              <span class="nav-link-inner--text">订单查询</span>
            </a>
          </li>
          <li class="nav-item">
            <a href="/?mod=document" class="nav-link">
              <span class="nav-link-inner--text">开发文档</span>
            </a>
          </li>
          </li>
        </ul>  
      </div>
    </div>
  </nav>    <!-- Header -->
       <div class="header bg-gradient-success pt-5 pb-7">
      <div class="container">
        <div class="header-body text-center mb-7">
          <div class="row justify-content-center">
            <div class="col-xl-5 col-lg-6 col-md-8 px-5">
              <h1 class="text-white">更新记录</h1>
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
                <small>
极简云个人版<br>
多样化应用管理方式，多种项目任你开发<br>
分布式应用开关，让您的应用开发更简单<br>
完美实现多用户多应用管理<br>
支持多应用卡密生成：<br>
卡密生成 单码卡密 次数卡密 会员卡密 积分卡密 <br>
卡密管理 卡密长度 卡密封禁 批量生成 批量导出 自定义卡密前缀等<br>
支持多应用多用户管理：<br>
应用备注 应用版本 多级代理价格 免费付费切换 验证用户IP 验证用户设备 应用开关等<br>
<br>
特色：<br>
1、对接：详细的API文档，各种语言对接示例让您的接入更加简单<br>
2、安全：客户端与服务器双向效验，动态数据加密，让您的数据“动起来”<br>
3、API：各种API尽情对接，让您的应用大放光彩<br>
4、快捷：后台一键式开关，应用版本，远程公告，远程更新等，让您更快捷更省心<br>
5、功能：丰富的功能，各类数据统计，人性化的体验，满足您的更多需求<br>
6、其他：内置商城、聊天室、工单等，我们将致力于给您最好的体验，如有任何问题都可以向我们反馈
<br>

<br>
使用方式：<br>
1.注册成为极简云的用户<br>
2.登录用户后台添加应用<br>
3.选择需要的接口进行添加<br>
4.对接自己的相关应用<br>
(会使用GET/POST进行对接)<br>

新增接口备注功能<br>
新增文件备注功能<br>
支持文件API获取备注<br>

新增卡密过期判断<br>
新增清空过期卡密<br>
修复次数卡密问题<br>

新增卡密解绑限制<br>

新增接口到期自动续费<br>
新增生成卡密自动导出<br>
新增多种结构卡密生成<br>
<br>
<br>
极简云验证，致力于为开发者提供一个免费、安全、快捷的验证系统
</small>
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
              <a href="http://www.miibeian.gov.cn/" class="nav-link" target="_blank">备案号：琼ICP备2020004104号</a>
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
<script src="./assets/js/jquery.min.js"></script>
<script src="./assets/js/bootstrap.bundle.min.js"></script>
</body>
</html>