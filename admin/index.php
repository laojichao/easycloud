<?php
/**
 * 平台
**/
include_once '../includes/common.php';
if($islogin==1){}else sysmsg("您还未登录，请先登录",2,'./login.php',true);
$title='站长管理中心';
include_once './header.php';
$thtime=date("Y-m-d").' 00:00:00';
$app=$DB->count("SELECT count(*) from yixi_apps WHERE 1");
$app1=$DB->count("SELECT count(*) from yixi_apps WHERE date>'$thtime'");
$appkm=$DB->count("SELECT count(*) from yixi_appkm WHERE 1");
$appkm1=$DB->count("SELECT count(*) from yixi_appkm WHERE addtime>'$thtime'");
$remotecount=$DB->count("SELECT count(*) from yixi_log WHERE uid=1 AND type='异地登录'");
$sec_msg = sec_check();
?>
<link rel="stylesheet" href="//lib.baomitu.com/toastr.js/latest/css/toastr.min.css">
<div class="row">
    <div class="col-xl-4">
        <div class="card d-block pt-2 pb-1 text-center">
            <img class="card-img-top m-auto" style="height: 68px;width: 68px;margin:auto;display: block;border-radius: 0.3em;box-shadow: 0px 0px 30px #ccc" src="//q4.qlogo.cn/headimg_dl?dst_uin=<?php echo $admin_qq;?>&spec=640" alt="Card image cap">
            <div class="card-body pb-2">
                <h5 class="card-title">平台官方管理员</h5>
                <p class="card-text text-success"><?php echo qqname($admin_qq) ?> [ UID:1 / 平台站长 ]</p>
            </div>
            <ul class="list-group list-group-flush mb-2">
                <li class="list-group-item"><?php echo $remotecount ?>条异常登录记录，账号切莫外借，否则后果自负哦</li>
            </ul>
            <a class="btn btn-outline-secondary mb-2" href="login.php?logout"><i class="layui-icon layui-icon-logout"></i> 退出登陆</a>
        </div> <!-- end card-->
    </div>
    <div class="col-xl-8">
        <div class="row">
            <div class="col-xl-6">
                <div class="row">
                    <div class="col-lg-6 col-md-6 col-sm-6 col-xl-6" style="width: 50%;">
                        <div class="card widget-flat">
                            <div class="card-body">
                                <div class="float-right" style="width: 30px;height: 30px">
                                    <i class="layui-icon layui-icon-app widget-icon" style="background-color: #02cbe4;color: white;"></i>
                                </div>
                                <h5 class="text-muted font-weight-normal mt-0">应用总数 <a href="applist.php" class="badge badge-danger-lighten">管理</a></h5>
                                <h5 class="mt-2 mb-0" style="font-weight: 300"><?php echo $app ?>个</h5>
                            </div> <!-- end card-body-->
                        </div> <!-- end card-->
                    </div> <!-- end col-->
                    <div class="col-lg-6 col-md-6 col-sm-6 col-xl-6" style="width: 50%">
                        <div class="card widget-flat">
                            <div class="card-body">
                                <div class="float-right" style="width: 30px;height: 30px">
                                    <i class="layui-icon layui-icon-list widget-icon" style="background-color: #40C4FF;color: white;"></i>
                                </div>
                                <h5 class="text-muted font-weight-normal mt-0">今日新增</h5>
                                <h5 class="mt-2 mb-0" style="font-weight: 300"><?php echo $app1 ?>个</h5>
                            </div> <!-- end card-body-->
                        </div> <!-- end card-->
                    </div> <!-- end col-->
					<div class="col-lg-6 col-md-6 col-sm-6 col-xl-6" style="width: 50%;">
                        <div class="card widget-flat">
                            <div class="card-body">
                                <div class="float-right" style="width: 30px;height: 30px">
                                    <i class="layui-icon layui-icon-list widget-icon" style="background-color: #8ed9ff;color: white;;"></i>
                                </div>
                                <h5 class="text-muted font-weight-normal mt-0">卡密总数 <a href="appkmlist.php" class="badge badge-danger-lighten">管理</a></h5>
                                <h5 class="mt-2 mb-0" style="font-weight: 300"><?php echo $appkm ?>个</h5>
                            </div> <!-- end card-body-->
                        </div> <!-- end card-->
                    </div> <!-- end col-->
                    <div class="col-lg-6 col-md-6 col-sm-6 col-xl-6" style="width: 50%">
                        <div class="card widget-flat">
                            <div class="card-body">
                                <div class="float-right" style="width: 30px;height: 30px">
                                    <i class="layui-icon layui-icon-list widget-icon" style="background-color: #1DE9B6;color: white;"></i>
                                </div>
                                <h5 class="text-muted font-weight-normal mt-0">今日新增</h5>
                                <h5 class="mt-2 mb-0" style="font-weight: 300"><?php echo $appkm1 ?>个</h5>
                            </div> <!-- end card-body-->
                        </div> <!-- end card-->
                    </div> <!-- end col-->
                </div>
            </div>
                    
                </div>
            </div>
            <div class="col-xl-6">
                <div class="card">
                    <div class="card-header">
                        安全中心
                    </div>
                    <div class="card-body">
                    <?php
                    foreach($sec_msg as $row){
                        echo $row;
                    }
                    if(count($sec_msg)==0)echo '<li class="list-group-item"><span class="btn-sm btn-success">正常</span>&nbsp;暂未发现网站安全问题</li>';
                    ?>
                    </div>
                </div>
            </div>
            <div class="col-xl-6">
                <div class="card">
            <div class="card-header">版本信息
            </div>
            <div class="card-body layui-text">
              <table class="layui-table">
                <colgroup>
                  <col width="100">
                  <col>
                </colgroup>
                <tbody>
                  <tr>
                    <td>当前版本</td>
                    <td id="current-version"><?php echo $APP_VERSION; ?></td>
                  </tr>
                  <tr>
                    <td>程序版本</td>
                    <td>Build <?=$version;?>&emsp; <a href="https://bbs.simpleteam.cn/forum-post/190.html" target="_blank">更新日志</a></td>
                  </tr>
                  <tr>
                    <td>更新时间</td>
                    <td><?php echo $UPDATE_TIME; ?></td>
                  </tr>
                        <tr>
                            <td>获取渠道</td>
                            <td>
                                <a href="https://bbs.simpleteam.cn/" class="layui-btn layui-btn-sm layui-btn-danger"
                                   target="_blank">获取更新</a>
                                <a href="https://bbs.simpleteam.cn/forum-post/190.html" target="_blank" class="layui-btn layui-btn-sm">立即下载</a>
                            </td>
                        </tr>
                </tbody>
              </table>
            </div>
          </div>
            
            <div class="layui-col-md4 layui-col-sm6">    
        </div> <!-- end row -->
    </div>
</div>
<?php
include_once './bottom.php';
?>
<script src="//lib.baomitu.com/toastr.js/latest/toastr.min.js"></script>