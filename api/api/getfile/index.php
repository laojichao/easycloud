<?php
/*
Name:获取文件
Version:1.0
Author:忆惜验证系统
*/
	if(!isset($app_res) or !is_array($app_res))out(100);//如果需要调用应用配置请先判断是否加载app配置
	if(isset($data_arr['id'])) {
    $id=intval($data_arr['id']);
    $sql="AND `id`='{$id}'";}
  $rs=$DB->query("SELECT * FROM yixi_appfile WHERE `appid`='{$appid}' AND `state`='y'  {$sql} order by id desc");
  while($res = $DB->fetch($rs)){
       if($res['type'] == 'lanzou'){
       if ($res['lanzou_pass'] != NULL) {
       $lanzou_pass = $res['lanzou_pass'];
       $file_url = lanzou($res['file_url'],$lanzou_pass);
       }else{
       $file_url = lanzou($res['file_url']);
       }
       }else{
	   $file_url = $res['file_url'];
	   }
          $ret[] = [
				'file_url' => $file_url,
				'date' => $res['addtime'],
                'note' => $res['note']
			];
}
  if(!$ret)out(201,'该应用下无外链',$app_res);
		out(200,$ret,$app_res);
	
?>