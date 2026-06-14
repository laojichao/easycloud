<?php
/*
Name:获取应用公告
Version:1.0
Author:忆惜验证系统
*/
	if(!isset($app_res) or !is_array($app_res))out(100);//如果需要调用应用配置请先判断是否加载app配置
	$app_gg = $app_res['app_gg'];//应用公告
	
	
	$ini_data = [//基本配置
		'app_gg'=>$app_gg,
	];
	
	out(200,$ini_data,$app_res);
?>