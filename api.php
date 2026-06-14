<?php
//php防注入和XSS攻击通用过滤.
$_GET     && SafeFilter($_GET);
$_POST    && SafeFilter($_POST);
$_COOKIE  && SafeFilter($_COOKIE);

function SafeFilter (&$arr){
  $ra=Array('/([\x00-\x08,\x0b-\x0c,\x0e-\x19])/','/script/','/javascript/','/vbscript/','/expression/','/applet/','/meta/','/xml/','/blink/','/link/','/style/','/embed/','/object/','/frame/','/layer/','/title/','/bgsound/','/base/','/onload/','/onunload/','/onchange/','/onsubmit/','/onreset/','/onselect/','/onblur/','/onfocus/','/onabort/','/onkeydown/','/onkeypress/','/onkeyup/','/onclick/','/ondblclick/','/onmousedown/','/onmousemove/','/onmouseout/','/onmouseover/','/onmouseup/','/onunload/');
  if (is_array($arr)){
    foreach ($arr as $key => $value){
      if(!is_array($value)){
        if (!get_magic_quotes_gpc()){             //不对magic_quotes_gpc转义过的字符使用addslashes(),避免双重转义。
          $value=addslashes($value);           //给单引号（'）、双引号（"）、反斜线（\）与 NUL（NULL 字符）加上反斜线转义
        }
        $value=preg_replace($ra,'',$value);     //删除非打印字符，粗暴式过滤xss可疑字符串
        $arr[$key]     = htmlentities(strip_tags($value)); //去除 HTML 和 PHP 标记并转换为 HTML 实体
      }else{
        SafeFilter($arr[$key]);
      }
    }
  }
}
$is_api==true;
include_once './includes/common.php';
$api = isset($_GET['api']) ? purge($_GET['api'],true,false) : null;
$default = isset($_GET['other'])?purge($_GET['other']):'index';
$sign = isset($_POST['sign']) ? (purge($_POST['sign'])) : (isset($_GET['sign']) ? purge($_GET['sign']) : '');//数据签名
$appid = isset($_GET['app']) ? intval($_GET['app']) : 0;//appid
$data = isset($_POST['data']) ? (purge($_POST['data'])) : (isset($_GET['data']) ? purge($_GET['data']) : '');//加密数据
$value = isset($_POST['value']) ? (purge($_POST['value'])) : (isset($_GET['value']) ? purge($_GET['value']) : '');//随机值
Template::api($api);//检测api文件目录是否存在
if($api){
	if(file_exists(FCPATH.API_MULU.$api.'/index.php')){
	    $app_des = $DB->get_row("select * from yixi_apps where id='" . $appid . "' limit 1");
		if(!empty($appid)){require FCPATH.'api/app.php';}
        $DB->query("update `yixi_apps` set `total`=`total`+1 where `id`='" . $appid . "'");
		require FCPATH.API_MULU.$api.'/'.$default.'.php';
	}else{
		sysmsg2("未提交相关参数",true);
	}
}else{
header("Location: ../");
}

?>