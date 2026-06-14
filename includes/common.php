<?php  
error_reporting(0);
if (defined('IN_CRONLITE')) {
    return null;
}
define('CACHE_FILE',0);
define('IN_CRONLITE',true);
define('SYSTEM_ROOT',dirname(__FILE__).'/');
define('ROOT',dirname(SYSTEM_ROOT).'/');
define('TEMPLATE_ROOT',ROOT.'template/');
define('TIMESTAMP',time());
define('API_MULU','api/api/');//api目录
define("CACHE_DIR",'includes/cache'); //下载缓存目录
define("PACKAGE_DIR",'includes/download'); //程序安装包目录
define('FCPATH', str_replace("\\",'/', dirname(dirname(__FILE__)).'/')); // 网站根目录
date_default_timezone_set('PRC');
$date=date('Y-m-d H:i:s');
include_once(SYSTEM_ROOT.'base.php');
session_start();
;
header('Cache-Control: no-store, no-cache, must-revalidate');
error_reporting(0);
;
header('Pragma: no-cache');
error_reporting(0);
if (($is_defend==true && CC_Defender!=0)|| CC_Defender==3) {
    if ((!isset($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH'])!='xmlhttprequest')) {
        include_once(SYSTEM_ROOT.'txprotect.php');
    }
	if ((CC_Defender==2 && check_spider()==false && $is_api==false) || CC_Defender==3) {
        cc_defender();
    }
}
$scriptpath=str_replace('\\','/',$_SERVER['SCRIPT_NAME']);
$sitepath=substr($scriptpath,0,strrpos($scriptpath,'/'));
$siteurl=($_SERVER['SERVER_PORT']==443 ? 'https://' : 'http://').$_SERVER['HTTP_HOST'].$sitepath.'/';
$authurl=($_SERVER['SERVER_PORT']==443 ? 'https://' : 'http://').$_SERVER['HTTP_HOST'].'/';
if (is_file(SYSTEM_ROOT."360safe/360webscan.php")) {
    require_once(SYSTEM_ROOT."360safe/360webscan.php");
    //require_once(SYSTEM_ROOT."360safe/xss.php");
}
require(ROOT.'config.php');
if ((!defined('SQLITE') && !$dbconfig['user'] || !$dbconfig['pwd'] || !$dbconfig['dbname'])) {
    header('Content-type:text/html;charset=utf-8');
exit('<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>温馨提示</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="/favicon.ico" />
    <style type="text/css">
        *{box-sizing:border-box;margin:0;padding:0;font-family:Lantinghei SC,Open Sans,Arial,Hiragino Sans GB,Microsoft YaHei,"微软雅黑",STHeiti,WenQuanYi Micro Hei,SimSun,sans-serif;-webkit-font-smoothing:antialiased}
        body{padding:70px 0;background:#edf1f4;font-weight:400;font-size:1pc;-webkit-text-size-adjust:none;color:#333}
        a{outline:0;color:#3498db;text-decoration:none;cursor:pointer}
        .system-message{margin:20px 5%;padding:40px 20px;background:#fff;box-shadow:1px 1px 1px hsla(0,0%,39%,.1);text-align:center}
        .system-message h1{margin:0;margin-bottom:9pt;color:#444;font-weight:400;font-size:40px}
        .system-message .jump,.system-message .image{margin:20px 0;padding:0;padding:10px 0;font-weight:400}
        .system-message .jump{font-size:14px}
        .system-message .jump a{color:#333}
        .system-message p{font-size:9pt;line-height:20px}
        .system-message .btn{display:inline-block;margin-right:10px;width:138px;height:2pc;border:1px solid #44a0e8;border-radius:30px;color:#44a0e8;text-align:center;font-size:1pc;line-height:2pc;margin-bottom:5px;}
        .success .btn{border-color:#69bf4e;color:#69bf4e}
        .error .btn{border-color:#ff8992;color:#ff8992}
        .info .btn{border-color:#3498db;color:#3498db}
        .copyright p{width:100%;color:#919191;text-align:center;font-size:10px}
        .system-message .btn-grey{border-color:#bbb;color:#bbb}
        .clearfix:after{clear:both;display:block;visibility:hidden;height:0;content:"."}
        @media (max-width:768px){body {padding:20px 0;}}
            @media (max-width:480px){.system-message h1{font-size:30px;}}
    </style>
</head>
<body>
<div class="system-message error">
    <div class="image">
        <img src="/assets/img/error.svg" alt="" width="150" />
    </div>
    <h1>您还未安装！</h1>
            <p class="jump">
            页面将在 <span id="wait">3</span> 秒后自动跳转</p>
        <p class="clearfix">
        <a href="/" class="btn btn-grey">返回上一页</a>
                   <a id="href" href="/install" class="btn btn-primary">立即跳转</a>
            </p>
</div>
    <script type="text/javascript">
        (function () {
            var wait = document.getElementById(\'wait\');
            var interval = setInterval(function () {
                var time = --wait.innerHTML;
                if (time <= 0) {
                    location.href = "/install";
                    clearInterval(interval);
                }
            }, 1000);
        })();
    </script>
</body>
</html>');
    exit(0);
}
include_once(SYSTEM_ROOT.'db.class.php');
$DB=new DB($dbconfig['host'],$dbconfig['user'],$dbconfig['pwd'],$dbconfig['dbname'],$dbconfig['port']);
if ($DB->query('select * from yixi_config where 1')==false) {
    header('Content-type:text/html;charset=utf-8');
exit('<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>温馨提示</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="/favicon.ico" />
    <style type="text/css">
        *{box-sizing:border-box;margin:0;padding:0;font-family:Lantinghei SC,Open Sans,Arial,Hiragino Sans GB,Microsoft YaHei,"微软雅黑",STHeiti,WenQuanYi Micro Hei,SimSun,sans-serif;-webkit-font-smoothing:antialiased}
        body{padding:70px 0;background:#edf1f4;font-weight:400;font-size:1pc;-webkit-text-size-adjust:none;color:#333}
        a{outline:0;color:#3498db;text-decoration:none;cursor:pointer}
        .system-message{margin:20px 5%;padding:40px 20px;background:#fff;box-shadow:1px 1px 1px hsla(0,0%,39%,.1);text-align:center}
        .system-message h1{margin:0;margin-bottom:9pt;color:#444;font-weight:400;font-size:40px}
        .system-message .jump,.system-message .image{margin:20px 0;padding:0;padding:10px 0;font-weight:400}
        .system-message .jump{font-size:14px}
        .system-message .jump a{color:#333}
        .system-message p{font-size:9pt;line-height:20px}
        .system-message .btn{display:inline-block;margin-right:10px;width:138px;height:2pc;border:1px solid #44a0e8;border-radius:30px;color:#44a0e8;text-align:center;font-size:1pc;line-height:2pc;margin-bottom:5px;}
        .success .btn{border-color:#69bf4e;color:#69bf4e}
        .error .btn{border-color:#ff8992;color:#ff8992}
        .info .btn{border-color:#3498db;color:#3498db}
        .copyright p{width:100%;color:#919191;text-align:center;font-size:10px}
        .system-message .btn-grey{border-color:#bbb;color:#bbb}
        .clearfix:after{clear:both;display:block;visibility:hidden;height:0;content:"."}
        @media (max-width:768px){body {padding:20px 0;}}
            @media (max-width:480px){.system-message h1{font-size:30px;}}
    </style>
</head>
<body>
<div class="system-message error">
    <div class="image">
        <img src="/assets/img/error.svg" alt="" width="150" />
    </div>
    <h1>您还未安装！</h1>
            <p class="jump">
            页面将在 <span id="wait">3</span> 秒后自动跳转</p>
        <p class="clearfix">
        <a href="/" class="btn btn-grey">返回上一页</a>
                   <a id="href" href="/install" class="btn btn-primary">立即跳转</a>
            </p>
</div>
    <script type="text/javascript">
        (function () {
            var wait = document.getElementById(\'wait\');
            var interval = setInterval(function () {
                var time = --wait.innerHTML;
                if (time <= 0) {
                    location.href = "/install";
                    clearInterval(interval);
                }
            }, 1000);
        })();
    </script>
</body>
</html>');
    exit(0);
}
include(SYSTEM_ROOT.'cache.class.php');
$CACHE=new CACHE();
$conf=$CACHE->pre_fetch();
define('SYS_KEY',$conf['syskey']);
if (($conf['qqjump']==1 && (!strpos($_SERVER['HTTP_USER_AGENT'],'QQ/')===false || !strpos($_SERVER['HTTP_USER_AGENT'],'MicroMessenger')===false))) {
    if ($_GET['open']==1 && !strpos($_SERVER['HTTP_USER_AGENT'],'MicroMessenger')===false) {
        header('Content-Disposition: attachment; filename="load.doc"');
        header('Content-Type: application/vnd.ms-word;charset=utf-8');
    } else {
        header('Content-type:text/html;charset=utf-8');
    }
    include(ROOT.'template/layui/jump.php');
    exit(0);
}
if($conf['wxpay_api']==1 || $conf['wxpay_api']==3){
    define('WXPAY_APPID',$conf['wxpay_appid']);
    define('WXPAY_MCHID',$conf['wxpay_mchid']);
    define('WXPAY_KEY',$conf['wxpay_key']);
    define('WXPAY_APPSECRET',$conf['wxpay_appsecret']);
}
if($conf['qqpay_api']==1){
    define('QQPAY_MCH_ID',$conf['qqpay_mchid']);
    define('QQPAY_MCH_KEY',$conf['qqpay_mchkey']);
}
$password_hash='!@#%!s!0';
include_once(SYSTEM_ROOT.'function.php');
$weburl = isset($_SERVER['HTTP_X_FORWARDED_HOST']) ? $_SERVER['HTTP_X_FORWARDED_HOST'] : (isset($_SERVER['HTTP_HOST']) ? $_SERVER['HTTP_HOST'] : $_SERVER['SERVER_NAME'].($_SERVER['SERVER_PORT']=='80' ? '' : ':'.$_SERVER['SERVER_PORT']));
define('weburl',$weburl);
include_once(SYSTEM_ROOT."version.php");
include_once(SYSTEM_ROOT.'template.class.php');
include_once(SYSTEM_ROOT.'class/Rsa.php');
include_once(SYSTEM_ROOT.'core.func.php');
include_once(SYSTEM_ROOT.'member.php');
include_once(SYSTEM_ROOT.'page.class.php');
include_once(SYSTEM_ROOT.'global.php');

if (!file_exists(ROOT.'install/install.lock') && file_exists(ROOT.'install/index.php')) {
    sysmsg('<h2>检测到无 install.lock 文件</h2><ul><li><font size="4">如果您尚未安装本程序，请<a href="/install/">前往安装</a></font></li><li><font size="4">如果您已经安装本程序，请手动放置一个空的 install.lock 文件到 /install 文件夹下，<b>为了您站点安全，在您完成它之前我们不会工作。</b></font></li></ul><br/><h4>为什么必须建立 install.lock 文件？</h4>它是站点的保护文件，如果检测不到它，就会认为站点还没安装，此时任何人都可以安装/重装极简云验证。<br/><br/>',2,'/install',true);
    exit(0);
}
function x_real_ip() {
    $ip=$_SERVER['REMOTE_ADDR'];
    if (isset($_SERVER['HTTP_X_FORWARDED_FOR']) && preg_match_all('#\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}#s',$_SERVER['HTTP_X_FORWARDED_FOR'],$matches)) {
        foreach($matches[0] as $xip) {
            if (!preg_match('#^(10|172\\.16|192\\.168)\\.#',$xip)) {
                $ip=$xip;
            } else {
                continue;
            }
        }
    } else {
        if (isset($_SERVER['HTTP_CLIENT_IP']) && preg_match('/^([0-9]{1,3}\\.){3}[0-9]{1,3}$/',$_SERVER['HTTP_CLIENT_IP'])) {
            $ip=$_SERVER['HTTP_CLIENT_IP'];
        } else {
            if (isset($_SERVER['HTTP_CF_CONNECTING_IP']) && preg_match('/^([0-9]{1,3}\\.){3}[0-9]{1,3}$/',$_SERVER['HTTP_CF_CONNECTING_IP'])) {
                $ip=$_SERVER['HTTP_CF_CONNECTING_IP'];
            } else {
                if ((isset($_SERVER['HTTP_X_REAL_IP']) && preg_match('/^([0-9]{1,3}\\.){3}[0-9]{1,3}$/',$_SERVER['HTTP_X_REAL_IP']))) {
                    $ip=$_SERVER['HTTP_X_REAL_IP'];
                }
            }
        }
    }
    return $ip;
}
function check_spider() {
    $useragent=strtolower($_SERVER['HTTP_USER_AGENT']);
    if (strpos($useragent,'baiduspider')!==false) {
        return 'baiduspider';
    }
    if (strpos($useragent,'360spider')!==false) {
        return '360spider';
    }
    if (strpos($useragent,'soso')!==false) {
        return 'soso';
    }
    if (strpos($useragent,'bing')!==false) {
        return 'bing';
    }
    if (strpos($useragent,'yahoo')!==false) {
        return 'yahoo';
    }
    if (strpos($useragent,'sohu-search')!==false) {
        return 'Sohubot';
    }
    if (strpos($useragent,'sogou')!==false) {
        return 'sogou';
    }
    if (strpos($useragent,'youdaobot')!==false) {
        return 'YoudaoBot';
    }
    if (strpos($useragent,'yodaobot')!==false) {
        return 'YodaoBot';
    }
    if (strpos($useragent,'robozilla')!==false) {
        return 'Robozilla';
    }
    if (strpos($useragent,'msnbot')!==false) {
        return 'msnbot';
    }
    if (strpos($useragent,'lycos')!==false) {
        return 'Lycos';
    }
    if (!strpos($useragent,'ia_archiver')===false) {
    } else {
        if (!strpos($useragent,'iaarchiver')===false) {
            return 'alexa';
        }
    }
    if (strpos($useragent,'robozilla')!==false) {
        return 'Robozilla';
    }
    if (strpos($useragent,'sitebot')!==false) {
        return 'SiteBot';
    }
    if (strpos($useragent,'mj12bot')!==false) {
        return 'MJ12bot';
    }
    if (strpos($useragent,'gosospider')!==false) {
        return 'gosospider';
    }
    if (strpos($useragent,'gigabot')!==false) {
        return 'Gigabot';
    }
    if (strpos($useragent,'yrspider')!==false) {
        return 'YRSpider';
    }
    if (strpos($useragent,'gigabot')!==false) {
        return 'Gigabot';
    }
    if (strpos($useragent,'jikespider')!==false) {
        return 'jikespider';
    }
    if (strpos($useragent,'etaospider')!==false) {
        return 'EtaoSpider';
    }
    if (strpos($useragent,'foxspider')!==false) {
        return 'FoxSpider';
    }
    if (strpos($useragent,'docomo')!==false) {
        return 'DoCoMo';
    }
    if (strpos($useragent,'yandexbot')!==false) {
        return 'YandexBot';
    }
    if (strpos($useragent,'sinaweibobot')!==false) {
        return 'SinaWeiboBot';
    }
    if (strpos($useragent,'catchbot')!==false) {
        return 'CatchBot';
    }
    if (strpos($useragent,'surveybot')!==false) {
        return 'SurveyBot';
    }
    if (strpos($useragent,'dotbot')!==false) {
        return 'DotBot';
    }
    if (strpos($useragent,'purebot')!==false) {
        return 'Purebot';
    }
    if (strpos($useragent,'ccbot')!==false) {
        return 'CCBot';
    }
    if (strpos($useragent,'mlbot')!==false) {
        return 'MLBot';
    }
    if (strpos($useragent,'adsbot-google')!==false) {
        return 'AdsBot-Google';
    }
    if (strpos($useragent,'ahrefsbot')!==false) {
        return 'AhrefsBot';
    }
    if (strpos($useragent,'spbot')!==false) {
        return 'spbot';
    }
    if (strpos($useragent,'augustbot')!==false) {
        return 'AugustBot';
    }
    return false;
}
function cc_defender() {
    $iptoken=md5(x_real_ip().date('Ymd')).md5(TIMESTAMP.rand(11111,99999));
    if ((!isset($_COOKIE['sec_defend']) || !substr($_COOKIE['sec_defend'],0,32)===substr($iptoken,0,32))) {
        if (!$_COOKIE['sec_defend_time']) {
            $_COOKIE['sec_defend_time']=0;
        }
        $sec_defend_time=$_COOKIE['sec_defend_time']+1;
        include_once(SYSTEM_ROOT.'hieroglyphy.class.php');
        $x=new hieroglyphy();
        $setCookie=$x->hieroglyphyString($iptoken);
        header('Content-type:text/html;charset=utf-8');
        if ($sec_defend_time>=10) {
            exit('浏览器不支持COOKIE或者不正常访问！');
        }
        echo '<html><head><meta http-equiv="pragma" content="no-cache"><meta http-equiv="cache-control" content="no-cache"><meta http-equiv="content-type" content="text/html;charset=utf-8"><title>正在加载中</title><script>function setCookie(name,value){var exp = new Date();exp.setTime(exp.getTime() + 60*60*1000);document.cookie = name + "="+ escape (value).replace(/\\+/g, \'%2B\') + ";expires=" + exp.toGMTString() + ";path=/";}function getCookie(name){var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");if(arr=document.cookie.match(reg))return unescape(arr[2]);else return null;}var sec_defend_time=getCookie(\'sec_defend_time\')||0;sec_defend_time++;setCookie(\'sec_defend\','.$setCookie.');setCookie(\'sec_defend_time\',sec_defend_time);if(sec_defend_time>1)window.location.href="./index.php";else window.location.reload();</script></head><body></body></html>';
        exit(0);
    } elseif (isset($_COOKIE['sec_defend_time'])) {
        setcookie('sec_defend_time', '', TIMESTAMP - 604800, '/');
    }
}
?>