<?php
 if($kami=addslashes($_POST['kami'])){
	 $arr=preg_split('/\s+/',$kami);
	 foreach($arr as $kami){
	 $res = $DB->get_row("select * from yixi_appkm where binary kami='".$kami."' limit 1");
    $sql = "update yixi_appkm set user='',user_ip='' where id='{$res['id']}'";
        if ($DB->query($sql)) {
            $data[]='卡密：'.$kami.' 解绑状态：成功';
        } else {
            $data[]='卡密：'.$kami.' 卡密状态：失败';
        }
   }
   $cnum = count($data);
          for($i=0;$i<$cnum;$i++){ 
          $output .= $data[$i];
          if ($cnum - 1 > $i) {
          $output .= '&#10;';
        }}
	$date = '<div class="card-body">';
    $date .= '<form class="form-horizontal">';
    $date .= '<div class="form-group mb-3">';
    $date .= '<label for="example-input-normal" style="font-weight: 500">解绑结果：</label>';
    $date .= '<textarea name="km_info" id="km_info" class="form-control" style="height:200px;" lay-verType="tips">';
	$date .= $output.'</textarea></div>';
	$date .= '<span class="btn btn-block btn-xs btn-outline-success" data-clipboard-text="'.$output.'" data-clipboard-action="copy" data-clipboard-target="#btn_code" id="btn_code">复制</span>';
	$date .= '</form>';
    $date .= '</div>';
    $result=array("code"=>1,"msg"=>"成功导出".$i."条卡密信息","data"=>$date);
    exit(json_encode($result));
}
if(!isset($_POST['kami'])) {
?>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta name="description" content="<?php echo $conf['sitename']?>,开发文档">
  <title>卡密解绑 - <?php echo $conf['sitename']?></title>
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
              <h1 class="text-white">卡密解绑</h1>
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
              <form method="post" role="form" onSubmit="return cxkm();"> 
                <div class="form-group mb-2">
                  <div class="form-group mb-3">
                   <label for="example-input-normal" style="font-weight: 500">卡密解绑：</label>
                    <textarea name="kami" id="kami" class="form-control" style="height:200px;" placeholder="请在此处输入需要解绑的卡密，解绑多个卡密请一行一个" lay-verType="tips"><?=@$_POST['kami']?></textarea>
					</div>
                </div>         
               <br/>
               <button type="submit"class="btn btn-success form-control">确认查询</button>       
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
  <script src="https://cdn.bootcss.com/clipboard.js/2.0.4/clipboard.js"></script>
  <script type="text/javascript">
  function cxkm() {
	var kami = $("textarea[name='kami']").val();
    var ii = layer.msg('正在解绑中,请稍后...', {icon: 16, time: 10 * 1000});
    $.ajax({
        type: "POST",
        url: "kmunmachine.php",
        data : {kami:kami},
        dataType: "json",
        success: function(data) {
            layer.close(ii);
            if (data.code == 0) {
                layer.msg(data.msg, {icon: 6});
            }else if(data.code == 1){
                layer.open({
                  type: 1,
                  title: '导出结果',
                  skin: 'layui-layer-rim',
                  content: data.data
                });
			layer.msg(data.msg, {icon: 6});
			}else {
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
</script>
  <script>
    var codeClipboard = new ClipboardJS('#btn_code');
   codeClipboard.on('success', function (e) {
        layer.msg("卡密信息复制成功", {icon: 6});
        e.clearSelection();
    });
    codeClipboard.on('error', function (e) {
        layer.msg('复制失败,请手动复制~', {icon: 5});
    });
</script>
</body>
</html>
<?php
}