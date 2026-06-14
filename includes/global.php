<?php	
function out($code,$msg = null,$mi = null,$t=0,$t2=1) {//输出结果 t=0中文不转义,1则转义&t2为自动换行
	    global $DB,$value,$appid;
		$app_des = $DB->get_row("select * from yixi_apps where id='" . $appid . "' limit 1");
		if($msg && is_array($msg) && isset($msg['mi_state']) && isset($msg['mi_type'])){$mi = $msg;$msg = null;}
		if(!$msg && !is_array($msg)){
			require_once 'msg.php';//返回数组
			$msg = $msg[$code];
		}
		if(DEFAULT_RETURN_TYPE == 0){
			$jdata = array('code'=>$code,'msg'=>$msg,'time'=>time(),'check'=>md5(time().$app_des['appkey'].$value));
			if($t == 0){
			$data = json_encode($jdata,JSON_UNESCAPED_UNICODE|JSON_UNESCAPED_SLASHES);
			}else{
			$data = json_encode($jdata);}
			if($t2 == 1)$data=str_replace("<br>","\n",$data);
			if($mi && is_array($mi) && isset($mi['mi_state']) && isset($mi['mi_type'])){
				if($mi['mi_state'] == 'y' && $mi['mi_type'] ==1){
					$data = mi_rc4($data,$mi['rc4_key']);
				}elseif($mi['mi_state'] == 'y' && $mi['mi_type'] ==2){
					$data = base64_encode($data);
			 	}elseif($mi['mi_state'] == 'y' && $mi['mi_type'] ==3){
					$data = bin2hex(rc4($data,$mi['rc4_key']));
				}elseif($mi['mi_state'] == 'y' && $mi['mi_type'] ==4){
					$data = AES($data,'y'.substr($mi['rc4_key'],0,13).'gg');
				}elseif($mi['mi_state'] == 'y' && $mi['mi_type'] ==5){
					$data = base64_encode($data);
				}
			}
		}
		echo $data;
		exit;
	}

	function foreachArray($array = [], $count = 0){//数组维度判断
		if (!is_array($array)){
			return $count;
		}
		foreach ($array as $value){
			$count++;
			if (!is_array($value)){
				return $count;
			}
			return foreachArray($value, $count);
		}
	}
	
	function Arr_sign($arr,$key,$md5 = true){//数组签名
		unset($arr['sign']);
		unset($arr['app']);
		unset($arr['api']);
		unset($arr['value']);
		unset($arr['PHPSESSID']);
		unset($arr['sec_defend']);
		unset($arr['sidenav-state']);
		$sign='';
		foreach ($arr as $k => $v) {
			$sign = $sign.$k . '='. $v .'&';
		}
		$sign = $sign.$key;
		if($md5){
			return md5($sign);
		}else{
			return $sign;
		}
	}
	
	function out_sign($arr,$key,$md5 = true){//数组签名
		unset($arr['sign']);
		unset($arr['app']);
		unset($arr['api']);
		unset($arr['value']);
		unset($arr['PHPSESSID']);
		unset($arr['sec_defend']);
		unset($arr['sidenav-state']);
		$sign='';
		foreach ($arr as $k => $v) {
			$sign = $sign.$k . '='. $v .'&';
		}
		$sign = $sign.$key;
	    exit($sign);
	}
	
	function txt_Arr($txt){//文本转数组
		$arr = explode('&', $txt);
		$array = [];
		foreach($arr as $value){
			$tmp_arr = explode('=',$value);
			if(is_array($tmp_arr) && count($tmp_arr) == 2){
				$array = array_merge($array,[$tmp_arr[0]=>$tmp_arr[1]]);
			}
		}
		return $array;
	}
	

	function mi_rc4($data,$pwd,$t=0) {//t=0加密，1=解密
		$cipher = '';
		$key[] = "";
		$box[] = "";
		$pwd=mi_rc4_encode($pwd);
		$data=mi_rc4_encode($data);
		$pwd_length = strlen($pwd);
		if($t == 1){
			$data = hex2bin($data);
		}
		$data_length = strlen($data);
		for ($i = 0; $i < 256; $i++) {
			$key[$i] = ord($pwd[$i % $pwd_length]);
			$box[$i] = $i;
		}
		for ($j = $i = 0; $i < 256; $i++) {
			$j = ($j + $box[$i] + $key[$i]) % 256;
			$tmp = $box[$i];
			$box[$i] = $box[$j];
			$box[$j] = $tmp;
		}
		for ($a = $j = $i = 0; $i < $data_length; $i++) {
			$a = ($a + 1) % 256;
			$j = ($j + $box[$a]) % 256;
			$tmp = $box[$a];
			$box[$a] = $box[$j];
			$box[$j] = $tmp;
			$k = $box[(($box[$a] + $box[$j]) % 256)];
			$cipher .= chr(ord($data[$i]) ^ $k);
		}
		if($t == 1){
			return $cipher;
		}else{
			return bin2hex($cipher);
		}
	}
	
	function mi_rc4_encode($str,$turn = 0){//turn=0,utf8转gbk,1=gbk转utf8
		if(is_array($str)){
			foreach($str as $k => $v){
				$str[$k] = array_iconv($v);
			}
			return $str;
		}else{
			if(is_string($str) && $turn == 0){
				return mb_convert_encoding($str,'GBK','UTF-8');
			}elseif(is_string($str) && $turn == 1){
				return mb_convert_encoding($str,'UTF-8','GBK');
			}else{
				return $str;
			}
		}
	}




	function rc4($data, $pwd) {
        $key[]       = "";
        $box[]       = "";
        $pwd_length  = strlen($pwd);
        $data_length = strlen($data);
        $cipher      = '';
        for ($i = 0; $i < 256; $i++) {
                $key[$i] = ord($pwd[$i % $pwd_length]);
                $box[$i] = $i;
        }
        for ($j = $i = 0; $i < 256; $i++) {
                $j       = ($j + $box[$i] + $key[$i]) % 256;
                $tmp     = $box[$i];
                $box[$i] = $box[$j];
                $box[$j] = $tmp;
        }
        for ($a = $j = $i = 0; $i < $data_length; $i++) {
                $a       = ($a + 1) % 256;
                $j       = ($j + $box[$a]) % 256;
                $tmp     = $box[$a];
                $box[$a] = $box[$j];
                $box[$j] = $tmp;
                $k = $box[(($box[$a] + $box[$j]) % 256)];
                $cipher .= chr(ord($data[$i]) ^ $k);
        }
        return $cipher;
}


function AES($encryptStr,$cdkey) {
    $iv = "0102030405060708";//密钥偏移量
        $module = mcrypt_module_open(MCRYPT_RIJNDAEL_128, '', MCRYPT_MODE_CBC, $iv);
        mcrypt_generic_init($module, $cdkey, $iv);
        $block = mcrypt_get_block_size(MCRYPT_RIJNDAEL_128, MCRYPT_MODE_CBC);
        $pad = $block - (strlen($encryptStr) % $block);
        $encryptStr .= str_repeat(chr($pad), $pad);
        $encrypted = mcrypt_generic($module, $encryptStr);
        mcrypt_generic_deinit($module);
        mcrypt_module_close($module);
      return  bin2hex($encrypted);
}



function lanzou($url,$pwd=null){
if (empty($url)) {
    return("未提交URL");
}
    $b = 'com/';
    $c = '/';
    $id = GetBetween($url, $b, $c);
    $d = 'https://www.lanzoui.com/tp/' . $id;
    $lanzou = curl($d);
    if (strpos($lanzou,'文件取消分享了') || empty($lanzou))return "文件取消分享了";
    else{
        if (strpos($lanzou,'输入密码') && empty($pwd))return "未填写分享密码";
        else{
            preg_match('/var domianload = \'(.*?)\';/', $lanzou, $down1);
            preg_match('/domianload \+ \'(.*?)\'/', $lanzou, $down2);
            preg_match('/var downloads = \'(.*?)\'/', $lanzou, $down3);
            if (!empty($down2)){
                $download = getRedirect($down1[1] . $down2[1]);
            }
            else{
                $download = getRedirect($down1[1] . $down3[1]);
            }
            if (!empty($pwd)) {
                preg_match('/sign\':\'(.*?)\'/', $lanzou, $sign);
                $post_data = array('action' => 'downprocess', 'sign' => $sign[1], 'p' => $pwd);
                $pwdurl = send_post('https://wwa.lanzoui.com/ajaxm.php', $post_data);
                $obj = json_decode($pwdurl, true);
                $download = getRedirect($obj['dom'] . '/file/' . $obj['url']);
            }
            if(strpos($pwdurl,'"zt":0') !== false)return "分享密码不正确";
        }
    }
     return $download;
}
function send_post($url, $post_data)
{
    $postdata = http_build_query($post_data);
    $options = array('http' => array(
        'method' => 'POST',
        'header' => 'Referer: https://www.lanzoui.com/\\r\\n' . 'Accept-Language:zh-CN,zh;q=0.9\\r\\n',
        'content' => $postdata,
        'timeout' => 15 * 60,
    ));
    $context = stream_context_create($options);
    $result = file_get_contents($url, false, $context);
    return $result;
}

function curl($url){
	$header[] = "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    $header[] = "Accept-Encoding: gzip, deflate, sdch, br";
    $header[] = "Accept-Language: zh-CN,zh;q=0.8";
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
	curl_setopt($ch, CURLOPT_USERAGENT, "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5376e Safari/8536.25");
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($ch, CURLOPT_ENCODING, 'gzip,deflate');
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);
	curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE); 
	curl_setopt($ch, CURLOPT_TIMEOUT, 5);
	$output = curl_exec($ch);
	curl_close($ch);
	return $output;
}
function GetBetween($content, $start, $end)
{
    $r = explode($start, $content);
    if (isset($r[1])) {
        $r = explode($end, $r[1]);
        return $r[0];
    }
    return '';
}

function getRedirect($url,$ref=''){
        $headers = array(
            'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8',
            'Accept-Encoding: gzip, deflate',
            'Accept-Language: zh-CN,zh;q=0.9',
            'Cache-Control: no-cache',
            'Connection: keep-alive',
            'Pragma: no-cache',
            'Upgrade-Insecure-Requests: 1',
        );
        $curl = curl_init();
        curl_setopt($curl, CURLOPT_URL, $url);
        curl_setopt($curl, CURLOPT_HTTPHEADER,$headers);
        if ($ref) {
            curl_setopt($curl, CURLOPT_REFERER, $ref);
        }
        curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
        curl_setopt($curl, CURLINFO_HEADER_OUT, true);
        curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
        $data = curl_exec($curl);
        $url=curl_getinfo($curl);
        curl_close($curl);
        return $url["redirect_url"];
    }
?>