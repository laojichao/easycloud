<?php
$bmd = ['ini','notice','getfile'];//白名单接口
$app_res = $DB->get_row("select * from yixi_apps where id='" . $appid . "' limit 1");
if(!$app_res)out(101);//应用不存在
if($app_res['active']=='n')out(102,$app_res);//应用关闭

if($app_res['mi_state'] == 'y' && !in_array($api,$bmd)){//数据已加密
	if($app_res['mi_type'] == 0){//明文模式
		$data_arr = $_REQUEST;//将post或GET数据移交给data_arr
		if($app_res['mi_sign'] == 'y'){//数据签名
			if($sign == '')out(104,$app_res);//签名为空
			
			if($app_res['print_sign'] == 'y')out_sign($data_arr,$app_res['appkey']);//打印签名
			$s = Arr_sign($data_arr,$app_res['appkey']);//生成签名
			if($s != strtolower($sign))out(106,$app_res);//签名有误
		}
		
	}else if($app_res['mi_type'] == 1){//RC4加密
		if($data=='')out(107,$app_res);//数据为空
		$rc4_data = mi_rc4($data,$app_res['rc4_key'],1);//RC4解密
		$data_arr = txt_Arr($rc4_data);//将rc4解密后的数据转为数组移交给data_arr
	}else if($app_res['mi_type'] == 2){//BASE64加密
		if($data=='')out(107,$app_res);//数据为空
		$base64_data = base64_decode($data);//BASE64解密
		$data_arr = txt_Arr($base64_data);//将BASE64解密后的数据转为数组移交给data_arr
	}else if($app_res['mi_type'] == 3){//RC4加密-2
		if($data=='')out(107,$app_res);//数据为空
		$rc4_data2 = rc4(hex2bin($data),$app_res['rc4_key']);//RC4解密
		$data_arr = txt_Arr($rc4_data2);//将rc4解密后的数据转为数组移交给data_arr
	}else if($app_res['mi_type'] == 4){//RSA加密
		if($data=='')out(107,$app_res);//数据为空
		$rsa_data = RSA_SMI($data,$app_res['mi_rsa_private_key'],1);//RSA私钥解密
		$data_arr = txt_Arr($rsa_data);//将rsa解密后的数据转为数组移交给data_arr
	}
	
	    if(!$value)$value=$data_arr['value'];//获取value值
	    
		if($app_res['mi_sign'] == 'y'){//数据签名
		if($app_res['mi_sign_in'] == 'y'){
			if($data_arr['sign'] == '')out(104,$app_res);//签名为空
			if($app_res['print_sign'] == 'y')out_sign($data_arr,$app_res['appkey']);//打印签名
			$s = Arr_sign($data_arr,$app_res['appkey']);//生成签名
			if($s != strtolower($data_arr['sign']))out(106,$app_res);//签名有误
		}else{
		if($sign == '')out(104,$app_res);//签名为空
		    if($app_res['print_sign'] == 'y')out_sign($data_arr,$app_res['appkey']);//打印签名
			$s = Arr_sign($data_arr,$app_res['appkey']);//生成签名
			if($s != strtolower($sign))out(106,$app_res);//签名有误
		}
		}
		
		
	if($app_res['mi_time'] > 0){
		if(!isset($data_arr['t']))out(108,$app_res);//没有时间变量
		$sign_t = time() - intval($data_arr['t']);//服务器时间-客户端时间，对比时间差
		if($sign_t > $app_res['mi_time'])out(105,$app_res);//客户端时间小于服务器时间
	}
}else{
	$data_arr = $_REQUEST;//将post或GET数据移交给data_arr
}


?>