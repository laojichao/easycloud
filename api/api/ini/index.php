<?php
/*
Name:获取配置
Version:1.0
Author:忆惜验证系统
*/
	if(!isset($app_res) or !is_array($app_res))out(100);//如果需要调用应用配置请先判断是否加载app配置
	$app_bb = $app_res['version'];//APP版本
	$app_bbinfo = $app_res['version_info'];//应用版本信息
	$app_show = $app_res['app_update_show'];//更新内容
	$app_update_must = $app_res['app_update_must'];//应用强制更新
	$api_total = $app_res['total'];//获取接口调用次数
	if ($app_res['app_update_url_type'] == 'lanzou') {
       if ($app_res['lanzou_pass'] != NULL) {
       $app_nurl = lanzou($app_res['app_update_url'],$app_res['lanzou_pass']);
       }else{
       $app_nurl = lanzou($app_res['app_update_url']);
       }
    }else{
       	$app_nurl = $app_res['app_update_url'];
     }
	
	
	$ini_data = [//基本配置
		'version'=>$app_bb,
		'version_info'=>$app_bbinfo,
		'app_update_show'=>$app_show,
		'app_update_url'=>$app_nurl,
		'app_update_must'=>$app_update_must,
		'api_total'=>$api_total
	];
	
	out(200,$ini_data,$app_res);
?>