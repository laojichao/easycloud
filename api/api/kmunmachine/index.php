<?php
/*
Name:卡密解绑
Version:1.0
Author:忆惜验证系统
*/
	if(!isset($app_res) or !is_array($app_res))out(100);//如果需要调用应用配置请先判断是否加载app配置
	//if($app_res['logon_state']=='n')out(103,$app_res['logon_notice'],$app_res);//判断是否可登录
	//if($app_res['logon_way'] != 1)out(163,$app_res);//不允许卡密登录
	
	$kami = isset($data_arr['kami']) && !empty($data_arr['kami']) ? purge($data_arr['kami']) : out(148,$app_res);//卡密为空
	$markcode = isset($data_arr['markcode']) && !empty($data_arr['markcode']) ? purge($data_arr['markcode']) : out(112,$app_res);//机器码
	if($app_res['switch'] == 'n')out(201,'解绑失败,无权限',$app_res);//免费模式禁止解绑
	
	$res_kami = $DB->get_row("select * from yixi_appkm where kami='" . $kami . "' AND appid='".$appid."' limit 1");
	

	if(!$res_kami)out(149,$app_res);//卡密不存在
	
	if($res_kami['state'] == 'n')out(151,$app_res);//卡密禁用
	//判断是否开启卡密解绑限制
	if($app_res['km_unmachine'] == 'y'){
	if($res_kami['user'] != $markcode)out(201,'解绑失败,无权限',$app_res);//机器码不一致
	}

	if(empty($res_kami['use_time']))out(202,'全新卡密,无需解绑',$app_res);//未使用卡密

	if($res_kami['type'] == 'code'){
			if($res_kami['km_time'] == 'longuse'){//判断是否为永久卡
				if($res_kami['km_change'] >= $app_res['longuse_km_change'])out(201,'解绑失败,可解绑次数不足',$app_res);//判断永久卡可解绑次数
				if(!empty($res_kami['km_change_time'])){if((time() - $res_kami['km_change_time']) < (3600 * $app_res['km_change_time']))out(201,'解绑失败,换绑时间不足',$app_res);}//换绑时间不足
				$res = $DB->query("update yixi_appkm set user='',user_ip='',km_change=km_change+1,km_change_time='".time()."' where id='{$res_kami['id']}'");//更新卡密信息
				if(!$res)out(201,'卡密解绑失败',$app_res);
				out(200,'卡密解绑成功',$app_res);
			}elseif($res_kami['km_time'] == 'vipcard'){//判断是否为贵宾卡
				out(202,'贵宾卡无需解绑',$app_res);
			}else{
			if($res_kami['end_time'] < time())out(201,'解绑失败,卡密时长不足',$app_res);//时长不足
			if(!empty($res_kami['km_change_time'])){if((time() - $res_kami['km_change_time']) < (3600 * $app_res['km_change_time']))out(201,'解绑失败,换绑时间不足',$app_res);}//换绑时间不足
			$vip = $res_kami['end_time'] - 3600 * $app_res['km_change_num'];//卡密解绑扣时长
			}
			if($res_kami['km_change'] >= $app_res['km_change'])out(201,'解绑失败,可解绑次数不足',$app_res);//判断卡密可解绑次数
			$res = $DB->query("update yixi_appkm set user='',user_ip='',use_time='".time()."',end_time='".$vip."',km_change=km_change+1,km_change_time='".time()."' where id='{$res_kami['id']}'");//更新卡密信息
			if(!$res)out(201,'卡密解绑失败',$app_res);
			out(200,'卡密解绑成功',$app_res);
		}
	if($res_kami['type'] == 'single'){
		if($res_kami['amount'] == 999999999){
			if($res_kami['km_change'] >= $app_res['km_change'])out(201,'解绑失败,可解绑次数不足',$app_res);//判断卡密可解绑次数
			if(!empty($res_kami['km_change_time'])){if((time() - $res_kami['km_change_time']) < (3600 * $app_res['km_change_time']))out(201,'解绑失败,换绑时间不足',$app_res);}//换绑时间不足
			$res = $DB->query("update yixi_appkm set user='',user_ip='',km_change=km_change+1,km_change_time='".time()."' where id='{$res_kami['id']}'");//更新卡密信息
			out(200,'卡密解绑成功',$app_res);
		}else{
			if($res_kami['amount'] < $app_res['single_km_change_num'])out(201,'次数不足',$app_res);//次数不足
			$vip = $res_kami['amount'] -  $app_res['single_km_change_num'];
			}
		if($res_kami['km_change'] >= $app_res['km_change'])out(201,'解绑失败,可解绑次数不足',$app_res);//判断卡密可解绑次数
		if(!empty($res_kami['km_change_time'])){if((time() - $res_kami['km_change_time']) < (3600 * $app_res['km_change_time']))out(201,'解绑失败,换绑时间不足',$app_res);}//换绑时间不足
		$res = $DB->query("update yixi_appkm set user='',user_ip='',use_time='".time()."',amount='".$vip."',km_change=km_change+1,km_change_time='".time()."' where id='{$res_kami['id']}'");//更新卡密信息
		if(!$res)out(201,'卡密解绑失败',$app_res);
			out(200,'卡密解绑成功',$app_res);
	}

	
?>