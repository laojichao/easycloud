<?php
/*
Name:卡密登录
Version:1.0
Author:忆惜验证系统
*/
	if(!isset($app_res) or !is_array($app_res))out(100);//如果需要调用应用配置请先判断是否加载app配置
	//if($app_res['logon_state']=='n')out(103,$app_res['logon_notice'],$app_res);//判断是否可登录
	//if($app_res['logon_way'] != 1)out(163,$app_res);//不允许卡密登录
	
	$kami = isset($data_arr['kami']) && !empty($data_arr['kami']) ? purge($data_arr['kami']) : out(148,$app_res);//卡密为空
	$markcode = isset($data_arr['markcode']) && !empty($data_arr['markcode']) ? purge($data_arr['markcode']) : out(112,$app_res);//机器码
	if($app_res['switch'] == 'n'){//判断是否付费，免费则可直接登录
	$kami_info = [
				'kami'=>$kami,
				'vip'=>'4102243200'
			];
	out(200,$kami_info,$app_res);
	}
	$res_kami = $DB->get_row("select * from yixi_appkm where binary kami='" . $kami . "' AND appid='".$appid."' limit 1");
	if(!$res_kami)out(149,$app_res);//卡密不存在
	if($res_kami['user'] != $markcode){
	if((!empty($res_kami['user']))&&$app_res['logon_check_in'] == 'y')out(150,$app_res);//卡密已使用	
	}

	if($res_kami['state'] == 'n')out(151,$app_res);//卡密禁用
	if($app_res['ipauth'] == 'y'){//判断是否验证用户IP
    if((!empty($res_kami['user_ip']))&&($res_kami['user_ip']!=$clientip))out(169,$app_res);//IP不一致
	}
	if(empty($res_kami['user']))$DB->query("update yixi_appkm set user='".$markcode."',user_ip='".$clientip."',km_use='y' where id='{$res_kami['id']}'");//更新使用者信息
	
	if($res_kami['type'] == 'code'){
		    if($res_kami['km_time']=='hour'){//小时
	        $km_code=3600;
            }else if($res_kami['km_time']=='day'){//天
	        $km_code=86400;
            }else if($res_kami['km_time']=='week'){//周
	        $km_code=604800;
            }else if($res_kami['km_time']=='month'){//月
	        $km_code=2592000;
            }else if($res_kami['km_time']=='season'){//季
	        $km_code=7776000;
            }else if($res_kami['km_time']=='year'){//年
	        $km_code=31104000;
            }
		if(empty($res_kami['use_time'])){//全新的卡密
			if($res_kami['km_time'] == 'longuse'){
				$vip = '4102243200';
			}else{
				$vip = time() + $km_code * $res_kami['amount'];
			}
			
			$res = $DB->query("update yixi_appkm set use_time='".time()."',user='".$markcode."',end_time='".$vip."',user_ip='".$clientip."',km_use='y' where id='{$res_kami['id']}'");//更新卡密信息
			//$sql=("update yixi_appkm set use_time='".time()."',user='".$markcode."',end_time='".$vip."',user_ip='".$clientip."' where id='{$res_kami['id']}'");
			//if(!$res)out(201,$vip,$app_res);
			if(!$res)out(201,'登录失败，请重试',$app_res);
			$kami_info = [
				'kami'=>$kami,
				'vip'=>(string)$vip
			];
		}elseif($res_kami['end_time'] == '4102243200' or $res_kami['end_time'] > time()){
			$kami_info = [
				'kami'=>$kami,
				'vip'=>$res_kami['end_time']
			];
		}else{
			out(201,'卡密已到期',$app_res);
		}
		out(200,$kami_info,$app_res);
		if(!$res)out(201,'登录失败，请重试',$app_res);
	}elseif($res_kami['type'] == 'single'){
		if($res_kami['amount'] <= 0)out(201,'卡密已到期',$app_res);
		if(empty($res_kami['use_time'])){//全新的卡密
		if($res_kami['amount'] == 999999999){
			$vip = $res_kami['amount'];
			}else{
				$vip =  $res_kami['amount'] - "1";
			}
			$res = $DB->query("update yixi_appkm set use_time='".time()."',amount='".$vip."',user='".$markcode."',user_ip='".$clientip."',km_use='y' where id='{$res_kami['id']}'");//更新卡密信息
			if(!$res)out(201,'登录失败，请重试',$app_res);
			//返回一小时时长
			$time=time() + 3600;
			$kami_info = [
				'kami'=>$kami,
				'vip'=>"$time"
				];
		}elseif($res_kami['amount'] >= 0){
			$res = $DB->query("update yixi_appkm set amount=amount-1 where id='{$res_kami['id']}'");//更新卡密信息
			if(!$res)out(201,'登录失败，请重试',$app_res);
			$time=time() + 3600;
			$kami_info = [
				'kami'=>$kami,
				'vip'=>"$time"
				];
		}else{
		out(201,'卡密已到期',$app_res);
		}
		out(200,$kami_info,$app_res);
	}



	
?>