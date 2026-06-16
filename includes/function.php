<?php
function sysmsge($msg = '未知的异常', $die = true) {
    echo "  \r\n    <!DOCTYPE html>\r\n    <html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"zh-CN\">\r\n    <head>\r\n        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\r\n        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n        <title>站点提示信息</title>\r\n        <style type=\"text/css\">\r\nhtml{background:#eee}body{background:#fff;color:#333;font-family:\"微软雅黑\",\"Microsoft YaHei\",sans-serif;margin:2em auto;padding:1em 2em;max-width:700px;-webkit-box-shadow:10px 10px 10px rgba(0,0,0,.13);box-shadow:10px 10px 10px rgba(0,0,0,.13);opacity:.8}h1{border-bottom:1px solid #dadada;clear:both;color:#666;font:24px \"微软雅黑\",\"Microsoft YaHei\",,sans-serif;margin:30px 0 0 0;padding:0;padding-bottom:7px}#error-page{margin-top:50px}h3{text-align:center}#error-page p{font-size:9px;line-height:1.5;margin:25px 0 20px}#error-page code{font-family:Consolas,Monaco,monospace}ul li{margin-bottom:10px;font-size:9px}a{color:#21759B;text-decoration:none;margin-top:-10px}a:hover{color:#D54E21}.button{background:#f7f7f7;border:1px solid #ccc;color:#555;display:inline-block;text-decoration:none;font-size:9px;line-height:26px;height:28px;margin:0;padding:0 10px 1px;cursor:pointer;-webkit-border-radius:3px;-webkit-appearance:none;border-radius:3px;white-space:nowrap;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;-webkit-box-shadow:inset 0 1px 0 #fff,0 1px 0 rgba(0,0,0,.08);box-shadow:inset 0 1px 0 #fff,0 1px 0 rgba(0,0,0,.08);vertical-align:top}.button.button-large{height:29px;line-height:28px;padding:0 12px}.button:focus,.button:hover{background:#fafafa;border-color:#999;color:#222}.button:focus{-webkit-box-shadow:1px 1px 1px rgba(0,0,0,.2);box-shadow:1px 1px 1px rgba(0,0,0,.2)}.button:active{background:#eee;border-color:#999;color:#333;-webkit-box-shadow:inset 0 2px 5px -3px rgba(0,0,0,.5);box-shadow:inset 0 2px 5px -3px rgba(0,0,0,.5)}table{table-layout:auto;border:1px solid #333;empty-cells:show;border-collapse:collapse}th{padding:4px;border:1px solid #333;overflow:hidden;color:#333;background:#eee}td{padding:4px;border:1px solid #333;overflow:hidden;color:#333}\r\n        </style>\r\n    </head>\r\n    <body id=\"error-page\">\r\n        ";
    echo "<h3>站点提示信息</h3>";
    echo $msg;
    echo "    </body>\r\n    </html>\r\n    ";
    if ($die == true) {
        exit(0);
    }
}
function sysmsg2($msg = '未知的异常',$die = true,$sitename = "极简验证系统") {
    ?>  
    <h3><html> <head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><?php echo $sitename;?></title>
<style type="text/css"> html{background:#eee}body{background:#fff;color:#333;font-family:"微软雅黑","Microsoft YaHei",sans-serif;margin:50em auto;padding:2em 0em;max-width:700px;-webkit-box-shadow:10px 10px 10px rgba(0,0,0,.13);box-shadow:10px 10px 10px rgba(0,0,0,.13);opacity:.8}h1{border-bottom:1px solid #dadada;clear:both;color:#666;font:24px "微软雅黑","Microsoft YaHei",,sans-serif;margin:30px 0 0 0;padding:0;padding-bottom:7px}#error-page{margin-top:50px}h3{text-align:center}#error-page p{font-size:9px;line-height:1.5;margin:25px 0 20px}#error-page code{font-family:Consolas,Monaco,monospace}ul li{margin-bottom:10px;font-size:9px}a{color:#21759B;text-decoration:none;margin-top:-10px}a:hover{color:#D54E21}.button{background:#f7f7f7;border:1px solid #ccc;color:#555;display:inline-block;text-decoration:none;font-size:9px;line-height:26px;height:28px;margin:0;padding:0 10px 1px;cursor:pointer;-webkit-border-radius:3px;-webkit-appearance:none;border-radius:3px;white-space:nowrap;-webkit-box-sizing:border-box;-moz-box-sizing:border-box;box-sizing:border-box;-webkit-box-shadow:inset 0 1px 0 #fff,0 1px 0 rgba(0,0,0,.08);box-shadow:inset 0 1px 0 #fff,0 1px 0 rgba(0,0,0,.08);vertical-align:top}.button.button-large{height:29px;line-height:28px;padding:0 12px}.button:focus,.button:hover{background:#fafafa;border-color:#999;color:#222}.button:focus{-webkit-box-shadow:1px 1px 1px rgba(0,0,0,.2);box-shadow:1px 1px 1px rgba(0,0,0,.2)}.button:active{background:#eee;border-color:#999;color:#333;-webkit-box-shadow:inset 0 2px 5px -3px rgba(0,0,0,.5);box-shadow:inset 0 2px 5px -3px rgba(0,0,0,.5)}table{table-layout:auto;border:1px solid #333;empty-cells:show;border-collapse:collapse}th{padding:4px;border:1px solid #333;overflow:hidden;color:#333;background:#eee}td{padding:4px;border:1px solid #333;overflow:hidden;color:#333}         </style>     </head>     
<body id="error-page" style="text-align: center;">         
<h3 style="color:black"><?php echo $sitename;?></h3>
<hr>
<?php echo $msg;?>
</br>
</br>
<h4>All right Reserved <a href="../"><?php echo $sitename;?></a></h4>
</body>     
</html>
</h3>
    <?php
    if ($die == true) {
        exit;
    }
}
function get_curl($url,$post=0,$referer=0,$cookie=0,$header=0,$ua=0,$nobaody=0){
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL,$url);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, false);
    $httpheader[] = "Accept: */*";
    $httpheader[] = "Accept-Encoding: gzip,deflate,sdch";
    $httpheader[] = "Accept-Language: zh-CN,zh;q=0.8";
    $httpheader[] = "Connection: close";
    curl_setopt($ch, CURLOPT_TIMEOUT, 30);
    if($post){
        curl_setopt($ch, CURLOPT_POST, 1);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $post);
    }
    curl_setopt($ch, CURLOPT_HTTPHEADER, $httpheader);
    if($header){
        curl_setopt($ch, CURLOPT_HEADER, TRUE);
    }
    if($cookie){
        curl_setopt($ch, CURLOPT_COOKIE, $cookie);
    }
    if($referer){
        if($referer==1){
            curl_setopt($ch, CURLOPT_REFERER, 'http://m.qzone.com/infocenter?g_f=');
        }else{
            curl_setopt($ch, CURLOPT_REFERER, $referer);
        }
    }
    if($ua){
        curl_setopt($ch, CURLOPT_USERAGENT,$ua);
    }else{
        curl_setopt($ch, CURLOPT_USERAGENT,'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36');
    }
    if($nobaody){
        curl_setopt($ch, CURLOPT_NOBODY,1);
    }
    curl_setopt($ch, CURLOPT_ENCODING, "gzip");
    curl_setopt($ch, CURLOPT_RETURNTRANSFER,1);
    $ret = curl_exec($ch);
    curl_close($ch);
    return $ret;
}
function real_ip($type=0){
$ip = $_SERVER['REMOTE_ADDR'];
if($type<=0 && isset($_SERVER['HTTP_X_FORWARDED_FOR']) && preg_match_all('#\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}#s', $_SERVER['HTTP_X_FORWARDED_FOR'], $matches)) {
    foreach ($matches[0] AS $xip) {
        if (filter_var($xip, FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 | FILTER_FLAG_NO_PRIV_RANGE | FILTER_FLAG_NO_RES_RANGE)) {
            $ip = $xip;
            break;
        }
    }
} elseif ($type<=0 && isset($_SERVER['HTTP_CLIENT_IP']) && filter_var($_SERVER['HTTP_CLIENT_IP'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 | FILTER_FLAG_NO_PRIV_RANGE | FILTER_FLAG_NO_RES_RANGE)) {
    $ip = $_SERVER['HTTP_CLIENT_IP'];
} elseif ($type<=1 && isset($_SERVER['HTTP_CF_CONNECTING_IP']) && filter_var($_SERVER['HTTP_CF_CONNECTING_IP'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 | FILTER_FLAG_NO_PRIV_RANGE | FILTER_FLAG_NO_RES_RANGE)) {
    $ip = $_SERVER['HTTP_CF_CONNECTING_IP'];
} elseif ($type<=1 && isset($_SERVER['HTTP_X_REAL_IP']) && filter_var($_SERVER['HTTP_X_REAL_IP'], FILTER_VALIDATE_IP, FILTER_FLAG_IPV4 | FILTER_FLAG_NO_PRIV_RANGE | FILTER_FLAG_NO_RES_RANGE)) {
    $ip = $_SERVER['HTTP_X_REAL_IP'];
}
return $ip;
}
function get_ip_city($ip)
{
    $url = 'http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=';
    $city = get_curl($url . $ip);
    $city = mb_convert_encoding($city, "UTF-8", "GB2312");
    $city = json_decode($city, true);
    if ($city['city']) {
        $location = $city['pro'].$city['city'];
    } else {
        $location = $city['pro'];
    }
    if($location){
        return $location;
    }else{
        return false;
    }
}
function GetHttp() {
if (isset($_SERVER['HTTPS']) && ('1' == $_SERVER['HTTPS'] || 'on' == strtolower($_SERVER['HTTPS']))) {
return "https://";
} elseif (isset($_SERVER['SERVER_PORT']) && ('443' == $_SERVER['SERVER_PORT'] )) {
return "https://";
}
return "http://";
}
function send_mail($to, $sub, $msg) {
    global $conf;
    include_once ROOT . "includes/smtp.class.php";
    if(!$conf['mail_name'] || !$conf['mail_smtp'] || !$conf['mail_pwd'])return false;
    $From = $conf['mail_name'];
    $Host = $conf['mail_smtp'];
    $Port = $conf['mail_port'];
    $SMTPAuth = 1;
    $Username = $conf['mail_name'];
    $Password = $conf['mail_pwd'];
    $Nickname = $conf['sitename'];
    $SSL = $conf['mail_port']==465?1:0;
    $mail = new SMTP($Host , $Port , $SMTPAuth , $Username , $Password , $SSL);
    $mail->att = array();
    if($mail->send($to , $From , $sub , $msg, $Nickname)) {
        return true;
    } else {
        return $mail->log;
    }
}
function purge($string,$trim = true,$filter = true,$force = 0, $strip = FALSE) {//递归addslashes  对参数进行净化
		$encode = mb_detect_encoding($string,array("ASCII","UTF-8","GB2312","GBK","BIG5"));
		if($encode != 'UTF-8'){
			$string = iconv($encode,'UTF-8',$string);
		}
		if($trim){$string=preg_replace('/\s+/','',$string);}
		if($filter){
			$farr = array(
				"/<(\\/?)(script|i?frame|style|html|body|title|link|meta|object|\\?|\\%)([^>]*?)>/isU",
				"/(<[^>]*)on[a-zA-Z]+\s*=([^>]*>)/isU",
				"/select |insert |and |or |create |update |delete |alter |count |\'|\/\*|\*|\.\.\/|\.\/|\^|union |into |load_file|outfile |dump/is"
			);
			$string = preg_replace($farr,'',$string);
		}
		!defined('MAGIC_QUOTES_GPC') && define('MAGIC_QUOTES_GPC', get_magic_quotes_gpc());
		if(!MAGIC_QUOTES_GPC || $force) {
			if(is_array($string)) {
				foreach($string as $key => $val) {
					$string[$key] = purge($val, $force, $strip);
				}
			} else {
				$string = addslashes($strip ? stripslashes($string) : $string);
			}
		}
		
		return $string;
}
function youfas($title,$code)
{
    global $DB, $conf;
    include_once SYSTEM_ROOT."emailTemp.php";
    $name = "email".$conf['email_temp'];
    $text = $name($title,$code,$conf,$date);
    return $text;
}
function send_sms($phone, $code, $scope='reg'){
    global $conf;
    if($scope == 'reg'){
        $moban = $conf['sms_tpl_reg'];
    }elseif($scope == 'login'){
        $moban = $conf['sms_tpl_login'];
    }elseif($scope == 'find'){
        $moban = $conf['sms_tpl_find'];
    }elseif($scope == 'edit'){
        $moban = $conf['sms_tpl_edit'];
    }
    if($conf['sms_api']==1){
        include_once ROOT . "includes/sms.tencentutil.class.php";
        include_once ROOT . "includes/sms.tencent.class.php";
        $ssender = new TencentSms($conf['sms_appid'], $conf['sms_appkey']);
        $params = array($code, $conf['sitename']);
        $smsSign = $conf['sms_sign'];
        $result = $ssender->sendWithParam("86", $phone, $moban, $params, $smsSign, "", "");
        $arr = json_decode($result,true);
        if(array_key_exists('result',$arr) && $arr['result']==0){
            return true;
        }else{
            return $arr['errmsg'];
        }
    }elseif($conf['sms_api']==2){
        include_once ROOT . "includes/sms.aliyun.class.php";
        $sms = new Aliyun($conf['sms_appid'], $conf['sms_appkey']);
        $arr = $sms->send($phone, $code, $moban, $conf['sms_sign'], $conf['sitename']);
        if(array_key_exists('Code',$arr) && $arr['Code']=='OK'){
            return true;
        }else{
            return $arr['Message'];
        }
    }else{
        $app=$conf['sitename'];
        $url = 'http://api.978w.cn/yzmsms/index/appkey/'.$conf['sms_appkey'].'/phone/'.$phone.'/moban/'.$moban.'/app/'.$app.'/code/'.$code;
        $data=get_curl($url);
        $arr=json_decode($data,true);
        if($arr['status']=='200'){
            return true;
        }else{
            return $arr['error_msg_zh'];
        }
    }
}
function daddslashes($string, $force = 0, $strip = FALSE) {
    !defined('MAGIC_QUOTES_GPC') && define('MAGIC_QUOTES_GPC', get_magic_quotes_gpc());
    if(!MAGIC_QUOTES_GPC || $force) {
        if(is_array($string)) {
            foreach($string as $key => $val) {
                $string[$key] = daddslashes($val, $force, $strip);
            }
        } else {
            $string = addslashes($strip ? stripslashes($string) : $string);
        }
    }
    return $string;
}

function strexists($string, $find) {
    return !(strpos($string, $find) === FALSE);
}

function dstrpos($string, $arr) {
    if(empty($string)) return false;
    foreach((array)$arr as $v) {
        if(strpos($string, $v) !== false) {
            return true;
        }
    }
    return false;
}

function checkmobile() {
    $useragent = strtolower($_SERVER['HTTP_USER_AGENT']);
    $ualist = array('android', 'midp', 'nokia', 'mobile', 'iphone', 'ipod', 'blackberry', 'windows phone');
    if((dstrpos($useragent, $ualist) || strexists($_SERVER['HTTP_ACCEPT'], "VND.WAP") || strexists($_SERVER['HTTP_VIA'],"wap"))){
        return true;
    }else{
        return false;
    }
}
function checkEmail($value)
{
    if (preg_match("/^[\w\.\-]+@\w+([\.\-]\w+)*\.\w+$/", $value) && strlen($value) <= 60) {
        return true;
    } else {
        return false;
    }
}
/**
 * 取中间文本
 * @param unknown $str
 * @param number $leftStr
 * @param number $rightStr
 */
function getSubstr($str, $leftStr, $rightStr)
{
    $left = strpos($str, $leftStr);
    $right = strpos($str, $rightStr,$left);
    if($left < 0 or $right < $left) return '';
    return substr($str, $left + strlen($leftStr), $right-$left-strlen($leftStr));
}
function authcode($string, $operation = 'DECODE', $key = '', $expiry = 0) {
    $ckey_length = 4;
    $key = md5($key ? $key : ENCRYPT_KEY);
    $keya = md5(substr($key, 0, 16));
    $keyb = md5(substr($key, 16, 16));
    $keyc = $ckey_length ? ($operation == 'DECODE' ? substr($string, 0, $ckey_length): substr(md5(microtime()), -$ckey_length)) : '';
    $cryptkey = $keya.md5($keya.$keyc);
    $key_length = strlen($cryptkey);
    $string = $operation == 'DECODE' ? base64_decode(substr($string, $ckey_length)) : sprintf('%010d', $expiry ? $expiry + time() : 0).substr(md5($string.$keyb), 0, 16).$string;
    $string_length = strlen($string);
    $result = '';
    $box = range(0, 255);
    $rndkey = array();
    for($i = 0; $i <= 255; $i++) {
        $rndkey[$i] = ord($cryptkey[$i % $key_length]);
    }
    for($j = $i = 0; $i < 256; $i++) {
        $j = ($j + $box[$i] + $rndkey[$i]) % 256;
        $tmp = $box[$i];
        $box[$i] = $box[$j];
        $box[$j] = $tmp;
    }
    for($a = $j = $i = 0; $i < $string_length; $i++) {
        $a = ($a + 1) % 256;
        $j = ($j + $box[$a]) % 256;
        $tmp = $box[$a];
        $box[$a] = $box[$j];
        $box[$j] = $tmp;
        $result .= chr(ord($string[$i]) ^ ($box[($box[$a] + $box[$j]) % 256]));
    }
    if($operation == 'DECODE') {
        if((substr($result, 0, 10) == 0 || substr($result, 0, 10) - time() > 0) && substr($result, 10, 16) == substr(md5(substr($result, 26).$keyb), 0, 16)) {
            return substr($result, 26);
        } else {
            return '';
        }
    } else {
        return $keyc.str_replace('=', '', base64_encode($result));
    }
}
function create_guid($authcode = '',$str = '') {
      $data = siteurl;
      $data .= $_SERVER['SERVER_ADDR'];
      $data .= $authcode;
      $data .= $_SERVER['SERVER_PORT'];
      $data .= php_uname('s').php_uname('r');
      $data .= php_uname();
      $data .= $_SERVER['PROCESSOR_IDENTIFIER'];
	  $data .= $_SERVER['SERVER_SOFTWARE'];
      $hash = strtoupper(hash('ripemd128', $str . $guid . md5($data)));
      $guid = 
          substr($hash, 0, 8) .
          '-' .
          substr($hash, 8, 4) .
          '-' .
          substr($hash, 12, 4) .
          '-' .
          substr($hash, 16, 4) .
          '-' .
          substr($hash, 20, 12) 
		  ;
      return $guid;
     }
// 检测上传图片是否包含有非法代码
function check_img($img) {
    $status = 0;
    $tips = array(
        "0" => "文件没问题",
        "5" => "文件有毒",
        "-1" => "文件没有上传"
    );
    if (file_exists($img)) {
        $resource = fopen($img, 'rb');
        $fileSize = filesize($img);
        fseek($resource, 0);
        if ($fileSize > 512) { // 取头和尾
            $hexCode = bin2hex(fread($resource, 512));
            fseek($resource, $fileSize - 512);
            $hexCode .= bin2hex(fread($resource, 512));
        } else { // 取全部
            $hexCode = bin2hex(fread($resource, $fileSize));
        }
        fclose($resource);
        /* 匹配16进制中的 <% ( ) %> */
        /* 匹配16进制中的 <? ( ) ?> */
        /* 匹配16进制中的 <script | /script> 大小写亦可 */
        if (preg_match("/(3c25.*?28.*?29.*?253e)|(3c3f.*?28.*?29.*?3f3e)|(3C534352495054)|(2F5343524950543E)|(3C736372697074)|(2F7363726970743E)/is", $hexCode)) {
            //$status = 5;
			return 'false';
        }
    } else {
        //$status = -1;
		return 'null';
    }
    //return $tips[$status];
	return 'true';
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
function get_rand($proArr)
{
    $result = "";
    $proSum = array_sum($proArr);
    foreach ($proArr as $key => $proCur) {
        $randNum = mt_rand(1, $proSum);
        if ($randNum <= $proCur) {
            $result = $key;
            break;
        }
        $proSum -= $proCur;
    }
    unset($proArr);
    return $result;
}
function sub_str($str, $length = 0, $append = true)
{
    $str = trim($str);
    $strlength = strlen($str);
    if ($length == 0 || $length >= $strlength) {
        return $str; //截取长度等于0或大于等于本字符串的长度，返回字符串本身
    } elseif ($length < 0) //如果截取长度为负数
    {
        $length = $strlength + $length; //那么截取长度就等于字符串长度减去截取长度
        if ($length < 0) {
            $length = $strlength; //如果截取长度的绝对值大于字符串本身长度，则截取长度取字符串本身的长度
        }
    }
    if (function_exists('mb_substr')) {
        $newstr = mb_substr($str, 0, $length, EC_CHARSET);
    } elseif (function_exists('iconv_substr')) {
        $newstr = iconv_substr($str, 0, $length, EC_CHARSET);
    } else {
        //$newstr = trim_right(substr($str, 0, $length));
        $newstr = substr($str, 0, $length);
    }
    if ($append && $str != $newstr) {
        $newstr .= '...';
    }
    return $newstr;
}
function diffBetweenTwoDays ($day1, $day2)
{
  $second1 = strtotime($day1);
  $second2 = strtotime($day2);
   
  if ($second1 < $second2) {
    $tmp = $second2;
    $second2 = $second1;
    $second1 = $tmp;
  }
  return ($second1 - $second2) / 86400;
}
function convertUrlQuery($query){
    $queryParts = explode('&', $query);
    $params     = array();
    foreach ($queryParts as $param) {
        $item             = explode('=', $param);
        $params[$item[0]] = $item[1];
    }
    return $params;
}
function showmsg($name = '温馨提示', $content = '未知的异常', $type = 4, $url = false, $true = true)
{
    global $conf;
    @header('Content-Type: text/html; charset=UTF-8');
    switch ($type) {
        case 1: #成功
            $panel = "btn-outline-success"; #绿色
            $bg = 'bg-success';
            break;
        case 2: #失败
            $panel = "btn-outline-warning"; #蓝色
            $bg = 'bg-warning';
            break;
        case 3: #普通
            $panel = "btn-outline-primary";
            $bg = 'bg-primary';
            break;
        case 4: #警告
            $panel = "btn-outline-danger"; #红色
            $bg = 'bg-danger';
            break;
        default:
            $panel = "btn-outline-primary";
            $bg = 'bg-primary';
            break;
    }
    echo '<div class="wrapper" style="padding: 1em">
              <div class="row">
                  <div class="col-xl-6 m-auto text-left">
                      <div class="card">
                          <h5 class="card-header ' . $bg . ' text-white">' . $name . '</h5>
                          <div class="card-body imagtes">
                              <p class="card-text font-16">' . $content . '</p>';
    if ($true == true) {
        if ($url <> '') {
            echo '<a href="' . $url . '" class="btn ' . $panel . ' btn-sm btn-block">进入下一页</a>';
        } else {
            echo '<a href="javascript:history.back(-1)" class="btn ' . $panel . ' btn-sm btn-block">返回上一页</a>';
        }
    }
    echo '
                          </div>
                          <!-- end card-body-->
                      </div>
                      <!-- end card-->
                  </div>
                  <!-- end col-->
              </div>
          </div>
      </body>
  </html>';
  include_once "bottom.php";
  exit();
}
function sysmsg($msg = '未知的异常',$type = 2,$url = '/',$die = true,$back = false)
{
switch($type)
{
case 1:
    $panel="success";
break;
case 2:
    $panel="error";
break;
}

echo '<!DOCTYPE html>
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
<div class="system-message '.$panel.'">
    <div class="image">
        <img src="/assets/img/'.$panel.'.svg" alt="" width="150" />
    </div>
    <h1>'.$msg.'</h1>
        <p class="jump">页面将在 <span id="wait">3</span> 秒后自动跳转</p>
        <p class="clearfix">';
        if ($back) {
            echo '<a href="'.$back.'" class="btn btn-grey">返回上一页</a>';
        }else{
            echo '<a href="javascript:history.back(-1)" class="btn btn-grey">返回上一页</a>';
        }
        echo '<a id="href" href="'.$url.'" class="btn btn-primary">立即跳转</a>
    </p>
</div>
    <script type="text/javascript">
        (function () {
            var wait = document.getElementById(\'wait\');
            var interval = setInterval(function () {
                var time = --wait.innerHTML;
                if (time <= 0) {
                    location.href = "'.$url.'";
                    clearInterval(interval);
                }
            }, 1000);
        })();
    </script>
</body>
</html>';
    if ($die == true) {
        exit;
    }
}

function sysmsgback($msg = '未知的异常',$type = 2)
{
switch($type)
{
case 1:
    $panel="success";
break;
case 2:
    $panel="error";
break;
}

echo '<!DOCTYPE html>
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
<div class="system-message '.$panel.'">
    <div class="image">
        <img src="/assets/img/'.$panel.'.svg" alt="" width="150" />
    </div>
    <h1>'.$msg.'</h1>
        <p class="jump">页面将在 <span id="wait">3</span> 秒后自动跳转</p>
        <p class="clearfix">';
            echo '<a href="javascript:history.back(-1)" class="btn btn-grey">返回上一页</a>';
        echo '<a id="href" href="javascript:history.back(-1)" class="btn btn-primary">立即跳转</a>
    </p>
</div>
    <script type="text/javascript">
        (function () {
            var wait = document.getElementById(\'wait\');
            var interval = setInterval(function () {
                var time = --wait.innerHTML;
                if (time <= 0) {
                    javascript:history.back(-1)
                    clearInterval(interval);
                }
            }, 1000);
        })();
    </script>
</body>
</html>';
exit;
}

if(!function_exists("is_https")){
    function is_https() {
        if(isset($_SERVER['SERVER_PORT']) && $_SERVER['SERVER_PORT'] == 443){
            return true;
        }elseif(isset($_SERVER['HTTPS']) && (strtolower($_SERVER['HTTPS']) == 'on' || $server['HTTPS'] == '1')){
            return true;
        }elseif(isset($_SERVER['HTTP_X_CLIENT_SCHEME']) && $_SERVER['HTTP_X_CLIENT_SCHEME'] == 'https'){
            return true;
        }elseif(isset($_SERVER['HTTP_X_FORWARDED_PROTO']) && $_SERVER['HTTP_X_FORWARDED_PROTO'] == 'https'){
            return true;
        }elseif(isset($_SERVER['REQUEST_SCHEME']) && $_SERVER['REQUEST_SCHEME'] == 'https'){
            return true;
        }elseif(isset($_SERVER['HTTP_EWS_CUSTOME_SCHEME']) && $_SERVER['HTTP_EWS_CUSTOME_SCHEME'] == 'https'){
            return true;
        }
        return false;
    }
}
function getwxdwz($channel,$longurl) {
    $id = $channel['appid'];
    $secret = $channel['appsecret'];
    $url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=".$id."&secret=".$secret;
    $token = getAccessToken($url);
    $arr = array('action'=>'long2short', 'long_url'=>$longurl);
    $post = json_encode($arr);
    $url = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=".$token."";
    $result = get_curl($url, $post);
    $arr = json_decode($result, true);
    if(@array_key_exists('errcode',$arr) && $arr['errcode']==0){
        return $arr['short_url'];
    }else{
        return false;
    }
}
function getAccessToken($url) {  
    // access_token 应该全局存储与更新，以下代码以写入到文件中做示例
    $data = json_decode(@file_get_contents(SYSTEM_ROOT."access_token.json"), true);
    if ($data['expire_time'] < time()) {
        // 如果是企业号用以下URL获取access_token
        $output = get_curl($url);
        $res = json_decode($output, true);
        $access_token = $res['access_token'];
        if ($access_token) {
            $data['expire_time'] = time() + 600;
            $data['access_token'] = $access_token;
            file_put_contents(SYSTEM_ROOT."access_token.json", $data);
        }
    } else {
        $access_token = $data['access_token'];
    }
    return $access_token;
}
function getdwz($longurl){
    $url = preg_replace('/^(http|https|thunder|qqdl|ed2k|Flashget|qbrowser):\/\//i', '', $longurl);
    $url = 'https://buluo.qq.com/cgi-bin/bar/extra/gen_short_url?urls=[%22'.urlencode($url).'%22]&r=0.9179819480050355';
    $cookie = 'BULUO_TICKET=VcvoIeiMSTp1KfAJmdHdrhk87ypMYF3oykq9YIxprKLZBFoZtsNFkbm-LePkqkL1hKRqtUpBTV-eqF_4hSqOpBO3sB0; BL_ID=o3ztss1vi1w-QaMZ2MTlJveJZjNA';
    $data = get_curl($url,0,'https://buluo.qq.com/',$cookie);
    $arr = json_decode($data,true);
    $short = $arr['result']['ls'][0]['url_code'];
    if(!$short){
        return false;
    }else{
        return($short);
    }
}

function qrcodelogin($image){
    $url='http://api.cccyun.cc/api/qrcode_noauth.php';
    $post='image='.urlencode($image);
    $data = get_curl($url,$post);
    $arr=json_decode($data,true);
    if(array_key_exists('code',$arr) && $arr['code']==1){
        $result=array("code"=>0,"msg"=>"succ","url"=>$arr['url']);
    }elseif(array_key_exists('msg',$arr)){
        $result=array("code"=>-1,"msg"=>$arr['msg']);
    }else{
        $result=array("code"=>-1,"msg"=>$data);
    }
    return $result;
}
function qqname($qq){
    $urlPre ='http://r.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?g_tk=1518561325&uins=';
    $data = file_get_contents($urlPre . $qq);
    $data = iconv("GB2312", "UTF-8", $data);
    $pattern = '/portraitCallBack\\((.*)\\)/is';
    preg_match($pattern, $data, $result);
    $result = $result[1];
    $result = json_decode($result, true);
    $qqname=$result["$qq"][6];
    if(!$qqname){
        $name='未知昵称';
    }else{
        $name=$qqname;
    }
    return $name;
}
function okBaidu($url){
    $url='http://www.baidu.com/s?wd='.$url;
    $curl=curl_init();
    curl_setopt($curl,CURLOPT_URL,$url);
    curl_setopt($curl,CURLOPT_RETURNTRANSFER,1);
    $rs=curl_exec($curl);
    curl_close($curl);
    if(!strpos($rs,'提交网址')){
        echo '<span class="btn-xs btn-success">已收录</span>';
    }else{
        echo '<a  href="https://ziyuan.baidu.com/linksubmit/url?sitename=http%3A%2F%2F'.dqurl.'" target="_blank"><span class="btn-xs btn-info">未收录</span></a>';
    }
}
function checkRefererHost(){
    if(!$_SERVER['HTTP_REFERER'])return false;
    $url_arr = parse_url($_SERVER['HTTP_REFERER']);
    $http_host = $_SERVER['HTTP_HOST'];
    if(strpos($http_host,':'))$http_host = substr($http_host, 0, strpos($http_host, ':'));
    return $url_arr['host'] === $http_host;
}
function RandomStr1($length){//随机大小写字母混合
$str ='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
$strlen =52;
while($length > $strlen){
$str .= $str;
$strlen +=52;
   }
 $str = str_shuffle($str);
 return substr($str,0,$length);
}
function RandomStr2($length){//随机小写字母
$str ='abcdefghijklmnopqrstuvwxyz';
$strlen =36;
while($length > $strlen){
$str .= $str;
$strlen +=36;
   }
 $str = str_shuffle($str);
 return substr($str,0,$length);
}
function RandomStr3($length){//随机大写字母
$str ='ABCDEFGHIJKLMNOPQRSTUVWXYZ';
$strlen =36;
while($length > $strlen){
$str .= $str;
$strlen +=36;
   }
 $str = str_shuffle($str);
 return substr($str,0,$length);
}
function RandomStr4($length){//随机数字
$str ='0123456789';
$strlen =10;
while($length > $strlen){
$str .= $str;
$strlen +=10;
   }
 $str = str_shuffle($str);
 return substr($str,0,$length);
}
function RandomStr5($length){//随机小写字母+数字
$str ='0123456789abcdefghijklmnopqrstuvwxyz';
$strlen =46;
while($length > $strlen){
$str .= $str;
$strlen +=46;
   }
 $str = str_shuffle($str);
 return substr($str,0,$length);
}
function RandomStr6($length){//随机大写字母+数字
$str ='0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
$strlen =46;
while($length > $strlen){
$str .= $str;
$strlen +=46;
   }
 $str = str_shuffle($str);
 return substr($str,0,$length);
}
?>