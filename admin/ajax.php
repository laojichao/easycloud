<?php
include_once '../includes/common.php';
$act=isset($_GET['act'])?daddslashes($_GET['act']):null;

@header('Content-Type: application/json; charset=UTF-8');

function jc_hm($len = 1)
{
    $str = "***REMOVED***";
    $strlen = strlen($str);
    $randstr = "";
    for ($i = 0; $i < $len; $i++) {
        $randstr .= $str[mt_rand(0, $strlen - 1)];
    }
    return $randstr;
}

switch($act){
case 'sso':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $uid=intval($_GET['uid']);
    $userrow=$DB->get_row("select * from yixi_user where uid='$uid' limit 1");
    if(!$userrow)exit('{"code":-1,"msg":"该用户记录不存在！"}');
    $session=md5($userrow['user'].$userrow['pwd'].$password_hash);
    $expiretime=TIMESTAMP+604800;
    $token=authcode("{$uid}\t{$session}\t{$expiretime}", 'ENCODE', SYS_KEY);
    setcookie("user_auth_token", $token, TIMESTAMP + 604800, '/user');
    exit('{"code":0,"msg":"登录用户成功！"}');
break;
case 'count':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $count=$DB->count("SELECT count(*) FROM yixi_tixian WHERE status=0");
    $count2=$DB->count("SELECT count(*) FROM yixi_workorder WHERE status=0");
    exit('{"code":0,"count":'.$count.',"count2":'.$count2.'}');
break;
case 'qdcount':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $day=date("Y-m-d");
    $lastday = date("Y-m-d",strtotime("-1 day"));
    $count1=$DB->count("SELECT count(*) FROM yixi_qiandao WHERE date='$day'");
    $count2=$DB->count("SELECT count(*) FROM yixi_qiandao WHERE date='$lastday'");
    $count3=$DB->count("SELECT count(*) FROM yixi_qiandao");
    $count4=$DB->count("SELECT sum(reward) FROM yixi_qiandao WHERE date='$day'");
    $count5=$DB->count("SELECT sum(reward) FROM yixi_qiandao WHERE date='$lastday'");
    $count6=$DB->count("SELECT sum(reward) FROM yixi_qiandao");
    $result=array("count1"=>$count1,"count2"=>$count2,"count3"=>$count3,"count4"=>round($count4,2),"count5"=>round($count5,2),"count6"=>round($count6,2));
    exit(json_encode($result));
break;

case 'addapp':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $name = daddslashes($_POST['name']);
    $img = daddslashes($_POST['img']);
    $version = daddslashes($_POST['version']);
    $appcount=$DB->count("SELECT count(*) FROM yixi_apps WHERE uid='".$userrow['uid']."'");
    $row = $DB->get_row("select * from yixi_apps where name='" . $name . "' AND uid='{$userrow['uid']}' limit 1");
    if(!$name)exit('{"code":-1,"msg":"请输入应用名称！"}');
	if($row)exit('{"code":-1,"msg":"该应用名称已存在！"}');
    $sql = "insert into `yixi_apps` (`appkey`,`name`,`img`,`version`,`active`,`total`,`date`) values ('".random(16)."','" . $name . "','" . $img . "','" . $version . "','y','0','" . $date . "')";
    if ($DB->query($sql)) {
        exit('{"code":0,"msg":"添加成功！"}');
    } else {
        exit('{"code":-1,"msg":"添加失败！' . $DB->error().'"}');
    }
break;
case 'getfileNote': //查看文件备注
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $rows=$DB->get_row("select * from yixi_appfile where id='$id' limit 1");
    if(!$rows)
        exit('{"code":-1,"msg":"当前文件好像不存在！"}');
    $data = '<div class="card-body">';
    $data .= '<form class="form-horizontal">';
	$data .= '<div class="form-group mb-3"><label for="example-input-normal" style="font-weight: 500">文件备注：</label><input type="text" id="filenote" value="'.$rows['note'].'" placeholder="输入该文件备注" class="form-control"></div>';
    $data .= '<input type="submit" id="save" onclick="savefilenote('.$id.')" class="btn btn-block btn-xs btn-outline-success" value="保存">';
    $data .= '</form>';
    $data .= '</div>';
    $result=array("code"=>0,"msg"=>"succ","data"=>$data);
    exit(json_encode($result));
break;
case 'editfileNote':
    $id=intval($_POST['id']);
    $filenote = daddslashes($_POST['filenote']);
    $row=$DB->get_row("select * from yixi_appfile where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前文件不存在！"}');
    }
    $sql="update `yixi_appfile` set `note` ='{$filenote}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"保存成功"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'editappfile':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $type = daddslashes($_POST['type']);
    $file_url = daddslashes($_POST['file_url']);
	$id = intval($_POST['id']);
    $proid = intval($_POST['proid']);
	$lanzou_pass = daddslashes($_POST['lanzou_pass']);
	$preg = "/^http(s)?:\\/\\/.+/";
	if(!$type){
            exit('{"code":-1,"msg":"请选择对应云端！"}');
        }
	if(!$file_url){
            exit('{"code":-1,"msg":"请输入外链地址！"}');
        }		
    if(!preg_match($preg,$file_url)){
            exit('{"code":-1,"msg":"请正确填写http/https"}');
	    }
	if(!$proid){
            exit('{"code":-1,"msg":"请选择您对应的应用！"}');
        }
    $program = $DB->get_row("select * from yixi_apps where id='" . $proid . "' limit 1");
        if (!$program) {
            exit('{"code":-1,"msg":"该应用不存在！"}');
        }
	$check_file = $DB->get_row("select * from yixi_appfile where file_url='" . $file_url . "' AND appid='" . $proid . "' AND lanzou_pass='" . $lanzou_pass . "' limit 1");
        if ($check_file) {
            exit('{"code":-1,"msg":"该外链已存在！"}');
        }
     if ($DB->query("UPDATE yixi_appfile set type='{$type}',appid='{$proid}',file_url='{$file_url}',lanzou_pass='{$lanzou_pass}' WHERE id='{$id}'")) {
        exit('{"code":0,"msg":"编辑文件成功"}');
     } else {
        exit('{"code":-1,"msg":"编辑文件失败！' . $DB->error().'"}');
     }
break;
case 'appfile_change':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $aid=$_POST['aid'];
    $checkbox=$_POST['checkbox'];
    $i=0;
    foreach($checkbox as $id){
        if($aid==1){
            $DB->query("update yixi_appfile set state='n' where id='$id' limit 1");
            $i++;
        }elseif($aid==2){
            $DB->query("update yixi_appfile set state='y' where id='$id' limit 1");
            $i++;
        }elseif($aid==3){
            $DB->query("DELETE FROM yixi_appfile WHERE id='$id' limit 1");
                $i++;
     }
	}
    exit('{"code":0,"msg":"成功操作'.$i.'个文件"}');
break;
case 'app_change':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $aid=$_POST['aid'];
    $checkbox=$_POST['checkbox'];
    $i=0;
    foreach($checkbox as $id){
        if($aid==1){
            $DB->query("update yixi_apps set active='n' where id='$id' limit 1");
            $i++;
        }elseif($aid==2){
            $DB->query("update yixi_apps set active='y' where id='$id' limit 1");
            $i++;
        }elseif($aid==3){
            $DB->query("DELETE FROM yixi_apps WHERE id='$id' limit 1");
                $i++;
     }
	}
    exit('{"code":0,"msg":"成功操作'.$i.'个应用"}');
break;
case 'addappkm':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $type = daddslashes($_POST['type']);
    $add_amount = intval($_POST['add_amount']);
    $proid = intval($_POST['proid']);
	$km_time = daddslashes($_POST['km_time']);
	$km_zdy = daddslashes($_POST['km_zdy']);
	$note = daddslashes($_POST['note']);
	$km_length = intval($_POST['km_length']);
    $km_num = intval($_POST['km_num']);
	$kmstructure = intval($_POST['kmstructure']);
	if(($type=='code')&&(!$km_time)){
            exit('{"code":-1,"msg":"请选择单码卡密对应时长！"}');
        }
	if(!$add_amount){
            exit('{"code":-1,"msg":"请输入卡密对应的值！"}');
        }
	if(!$proid){
            exit('{"code":-1,"msg":"请选择您要生成的应用！"}');
        }
	if($km_length>32){
            exit('{"code":-1,"msg":"卡密长度不能大于32位！"}');
        }
     $kmcount=$DB->count("SELECT count(*) FROM yixi_appkm WHERE upid='".$userrow['uid']."'");
     $kmcount=$kmcount+$km_num;
     $program = $DB->get_row("select * from yixi_apps where id='" . $proid . "' limit 1");
        if (!$program) {
            exit('{"code":-1,"msg":"该应用不存在！"}');
        }
		if($kmstructure==1){
		    $kmdata="random";
		}elseif($kmstructure==2){
			$kmdata="RandomStr1";
		}elseif($kmstructure==3){
			$kmdata="RandomStr2";
		}elseif($kmstructure==4){
			$kmdata="RandomStr3";
		}elseif($kmstructure==5){
			$kmdata="RandomStr4";
		}elseif($kmstructure==6){
			$kmdata="RandomStr5";
		}elseif($kmstructure==7){
			$kmdata="RandomStr6";
		}
    for ($i = 0; $i < $km_num; $i++) {
		$km=$km_zdy.$kmdata($km_length);
		$data[]=$km;
        $DB->query("insert into `yixi_appkm` (`upid`,`type`,`appid`,`kami`,`note`,`amount`,`km_time`,`addtime`,`state`) values ('1','".$type."','".$proid."','".$km."','".$note."','".$add_amount."','".$km_time."','".$date."','y')");
    }
		 $cnum = count($data);
          for($i=0;$i<$cnum;$i++){ 
          $output .= $data[$i];
          if ($cnum - 1 > $i) {
          $output .= '&#10;';
        }}
	$date = '<div class="card-body">';
    $date .= '<form class="form-horizontal">';
    $date .= '<div class="form-group mb-3">';
    $date .= '<label for="example-input-normal" style="font-weight: 500">卡密内容：</label>';
    $date .= '<textarea name="km_info" id="km_info" class="form-control" style="height:100px;" lay-verType="tips">';
	$date .= $output.'</textarea></div>';
	$date .= '<span class="btn btn-block btn-xs btn-outline-success" data-clipboard-text="'.$output.'" data-clipboard-action="copy" data-clipboard-target="#btn_code" id="btn_code">复制</span>';
	$date .= '</form>';
    $date .= '</div>';
    $result=array("code"=>1,"msg"=>"成功生成".$i."个卡密","data"=>$date);
    exit(json_encode($result));
break;
case 'addappfile':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $type = daddslashes($_POST['type']);
    $file_url = daddslashes($_POST['file_url']);
    $proid = intval($_POST['proid']);
	$lanzou_pass = daddslashes($_POST['lanzou_pass']);
	$preg = "/^http(s)?:\\/\\/.+/";
	if(!$type){
            exit('{"code":-1,"msg":"请选择对应云端！"}');
        }
	if(!$file_url){
            exit('{"code":-1,"msg":"请输入外链地址！"}');
        }
	if(!preg_match($preg,$file_url)){
            exit('{"code":-1,"msg":"请正确填写http/https"}');
	    }
	if(!$proid){
            exit('{"code":-1,"msg":"请选择您对应的应用！"}');
        }
     $program = $DB->get_row("select * from yixi_apps where id='" . $proid . "' limit 1");
        if (!$program) {
            exit('{"code":-1,"msg":"该应用不存在！"}');
        }
	$check_file = $DB->get_row("select * from yixi_appfile where file_url='" . $file_url . "' AND uid='".$userrow['uid']."' limit 1");
        if ($check_file) {
            exit('{"code":-1,"msg":"该外链已存在！"}');
        }
     $sql = "insert into `yixi_appfile` (`uid`,`type`,`appid`,`file_url`,`lanzou_pass`,`addtime`,`state`) values ('1','" . $type . "','" . $proid . "','" . $file_url . "','" . $lanzou_pass . "','".$date."','y')";
     if ($DB->query($sql)) {
        exit('{"code":0,"msg":"添加文件成功"}');
     } else {
        exit('{"code":-1,"msg":"添加文件失败！' . $DB->error().'"}');
     }
break;
case 'editAppNote':
	if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_POST['id']);
    $appnote = daddslashes($_POST['appnote']);
    $row=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前程序不存在！"}');
    }
    $sql="update `yixi_apps` set `note` ='{$appnote}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"保存成功"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'editAppVersion':
	if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_POST['id']);
    $version = daddslashes($_POST['version']);
    $row=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前程序不存在！"}');
    }
    $sql="update `yixi_apps` set `version` ='{$version}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"保存成功"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'editApp_other':
	if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $mi_state = daddslashes($_POST['mi_state']);
    $mi_type = daddslashes($_POST['mi_type']);
    $mi_sign = daddslashes($_POST['mi_sign']);
    $mi_sign_in = daddslashes($_POST['mi_sign_in']);
    $print_sign = daddslashes($_POST['print_sign']);
	$rc4_key = daddslashes($_POST['rc4_key']);
    $mi_time = intval($_POST['mi_time']);
	$km_change = intval($_POST['km_change']);
    $km_change_num = intval($_POST['km_change_num']);
	$km_change_time = intval($_POST['km_change_time']);
	$longuse_km_change = intval($_POST['longuse_km_change']);
	$single_km_change_num = intval($_POST['single_km_change_num']);
    $row=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前应用不存在！"}');
    }
    if ($mi_state == "") {
        exit('{"code":-1,"msg":"请确保应用安全配置选项不为空"}');
    }
    $sql="update `yixi_apps` set `mi_state` ='{$mi_state}',`mi_type` ='{$mi_type}',`mi_sign` ='{$mi_sign}',`mi_sign_in` ='{$mi_sign_in}',`print_sign` ='{$print_sign}',`rc4_key` ='{$rc4_key}',`mi_time` ='{$mi_time}',`km_change_num` ='{$km_change_num}',`km_change` ='{$km_change}',`km_change_time` ='{$km_change_time}',`longuse_km_change` ='{$longuse_km_change}',`single_km_change_num` ='{$single_km_change_num}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"保存成功"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'editApp_update':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $appgg = daddslashes($_POST['appgg']);
    $version = daddslashes($_POST['version']);
    $version_info = daddslashes($_POST['version_info']);
    $app_update_url = daddslashes($_POST['app_update_url']);
    $app_update_show = daddslashes($_POST['app_update_show']);
    $app_update_must = daddslashes($_POST['app_update_must']);
    $type = daddslashes($_POST['type']);
	$lanzou_pass = daddslashes($_POST['lanzou_pass']);
    $row=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前应用不存在！"}');
    }
    if ($version == "" || $app_update_must == "") {
        exit('{"code":-1,"msg":"请确保应用版本号及强制更新不为空"}');
    }
    $sql="update `yixi_apps` set `app_gg` ='{$appgg}',`version` ='{$version}',`version_info` ='{$version_info}',`app_update_url` ='{$app_update_url}',`app_update_show` ='{$app_update_show}',`app_update_url_type` ='{$type}',`lanzou_pass` ='{$lanzou_pass}',`app_update_must` ='{$app_update_must}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"保存成功"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'appedit':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $name = daddslashes($_POST['name']);
    $img = daddslashes($_POST['img']);
    $appgg = daddslashes($_POST['appgg']);
	$active = daddslashes($_POST['active']);
    $version = daddslashes($_POST['version']);
    $version_info = daddslashes($_POST['version_info']);
	$app_update_url = daddslashes($_POST['app_update_url']);
    $app_update_show = daddslashes($_POST['app_update_show']);
    $app_update_must = daddslashes($_POST['app_update_must']);
    $type = daddslashes($_POST['type']);
	$lanzou_pass = daddslashes($_POST['lanzou_pass']);
    if ($name == "" || $img == "") {
        exit('{"code":-1,"msg":"请确保应用名称及应用图标不为空"}');
    }
    if ($row['name'] != $name) {
        $rows = $DB->get_row("select * from yixi_apps where name='" . $name . "' limit 1");
        if ($rows) {
            exit('{"code":-1,"msg":"该应用已存在！"}');
        }
    }
	if ($version == "" || $app_update_must == "") {
        exit('{"code":-1,"msg":"请确保应用版本号及强制更新不为空"}');
    }
    $sql="update `yixi_apps` set `name` ='{$name}',`img` ='{$img}',`app_gg` ='{$appgg}',`version` ='{$version}',`version_info` ='{$version_info}',`active` ='{$active}',`app_update_url` ='{$app_update_url}',`app_update_show` ='{$app_update_show}',`app_update_must` ='{$app_update_must}',`app_update_url_type` ='{$type}',`lanzou_pass` ='{$lanzou_pass}' where `id`='{$id}'";
    if ($DB->query($sql)) {
        exit('{"code":0,"msg":"保存成功！"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'kmedit':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $switchauth = daddslashes($_POST['switchauth']);
    $ipauth = daddslashes($_POST['ipauth']);
	$login_check = daddslashes($_POST['login_check']);
	$km_unmachine = daddslashes($_POST['km_unmachine']);
	$km_change = intval($_POST['km_change']);
    $km_change_num = intval($_POST['km_change_num']);
	$km_change_time = intval($_POST['km_change_time']);
	$longuse_km_change = intval($_POST['longuse_km_change']);
	$single_km_change_num = intval($_POST['single_km_change_num']);
    $sql="update `yixi_apps` set `switch` ='{$switchauth}',`ipauth` ='{$ipauth}',`logon_check_in` ='{$login_check}',`km_unmachine` ='{$km_unmachine}',`km_change_num` ='{$km_change_num}',`km_change` ='{$km_change}',`km_change_time` ='{$km_change_time}',`longuse_km_change` ='{$longuse_km_change}',`single_km_change_num` ='{$single_km_change_num}' where `id`='{$id}'";
    if ($DB->query($sql)) {
        exit('{"code":0,"msg":"保存成功！"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'editApp_safe':
    $id=intval($_GET['id']);
    $mi_state = daddslashes($_POST['mi_state']);
    $mi_type = daddslashes($_POST['mi_type']);
    $mi_sign = daddslashes($_POST['mi_sign']);
    $mi_sign_in = daddslashes($_POST['mi_sign_in']);
    $print_sign = daddslashes($_POST['print_sign']);
	$rc4_key = daddslashes($_POST['rc4_key']);
	$mi_time = intval($_POST['mi_time']);
    $row=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前应用不存在！"}');
    }
    if ($mi_state == "") {
        exit('{"code":-1,"msg":"请确保应用安全配置选项不为空"}');
    }
    $sql="update `yixi_apps` set `mi_state` ='{$mi_state}',`mi_type` ='{$mi_type}',`mi_sign` ='{$mi_sign}',`mi_sign_in` ='{$mi_sign_in}',`print_sign` ='{$print_sign}',`rc4_key` ='{$rc4_key}',`mi_time` ='{$mi_time}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"保存成功"}');
    } else {
        exit('{"code":-1,"msg":"保存失败！' . $DB->error().'"}');
    }
break;
case 'getAppNote': //查看应用备注
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $rows=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$rows)
        exit('{"code":-1,"msg":"当前程序好像不存在！"}');
    $data = '<div class="card-body">';
    $data .= '<form class="form-horizontal">';
    $data .= '<div class="form-group mb-3"><label for="example-input-normal" style="font-weight: 500">应用备注：</label><input type="text" id="appnote" value="'.$rows['note'].'" placeholder="输入该应用备注" class="form-control"></div>';
    $data .= '<input type="submit" id="save" onclick="saveappnote('.$id.')" class="btn btn-block btn-xs btn-outline-success" value="保存">';
    $data .= '</form>';
    $data .= '</div>';
    $result=array("code"=>0,"msg"=>"succ","data"=>$data);
    exit(json_encode($result));
break;
case 'getAppVersion': //查看应用版本
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $rows=$DB->get_row("select * from yixi_apps where id='$id' limit 1");
    if(!$rows)
        exit('{"code":-1,"msg":"当前程序好像不存在！"}');
    $data = '<div class="card-body">';
    $data .= '<form class="form-horizontal">';
    $data .= '<div class="form-group mb-3"><label for="example-input-normal" style="font-weight: 500">应用版本：</label><input type="text" id="version" value="'.$rows['version'].'" placeholder="输入该应用版本" class="form-control"></div>';
    $data .= '<input type="submit" id="save" onclick="saveappversion('.$id.')" class="btn btn-block btn-xs btn-outline-success" value="保存">';
    $data .= '</form>';
    $data .= '</div>';
    $result=array("code"=>0,"msg"=>"succ","data"=>$data);
    exit(json_encode($result));
break;
case 'addnewapp': //新增应用
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $data = '<div class="card-body">';
    $data .= '<form class="form-horizontal">';
	$data .= '<div class="form-group mb-3">';
    $data .= '<label for="example-input-normal" style="font-weight: 500">应用名称：</label>';
    $data .= '<input type="text" name="name" class="form-control" placeholder="输入该应用名称" lay-verType="tips" lay-verify="required"></div>';
    $data .= '<div class="form-group mb-3">';
    $data .= '<input type="file" id="file" onchange="fileUpload()" style="display:none;"/>';
    $data .= '<label for="example-input-normal" style="font-weight: 500">应用图标：<span class="badge badge-success-lighten" onclick="fileView()">查看应用图标</span> <a href="javascript:fileSelect()" class="badge badge-danger-lighten">上传应用图标</a></label>';
    $data .= '<input type="text" name="img" class="form-control" placeholder="上传该应用图标" lay-verType="tips" value="assets/img/Program/default.png" lay-verify="required"></div>';
    $data .= '<div class="form-group mb-3">';
    $data .= '<label for="example-input-normal" style="font-weight: 500">应用版本：</label>';
    $data .= '<input type="text" name="version" class="form-control" lay-verType="tips" value="1.0" lay-verify="required"></div>';
    $data .= '<button type="submit" class="btn btn-block btn-xs btn-outline-success" lay-submit lay-filter="submit_addapp">添 加</button>';
    $data .= '</form>';
    $data .= '</div>';
    $result=array("code"=>0,"msg"=>"succ","data"=>$data);
    exit(json_encode($result));
break;
case 'app_kmjb':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_POST['id']);
    $row=$DB->get_row("SELECT * FROM yixi_appkm WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该卡密不存在！"}');
    }
    $DB->query("update yixi_appkm set user='',user_ip='' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'appkeyChange':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1"); 
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
        }
    $appkey=random(16);
    $sql="update `yixi_apps` set `APPKEY` ='{$appkey}' where `id`='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"更换APPKEY成功！"}');
    } else {
        exit('{"code":-1,"msg":"更换失败！' . $DB->error().'"}');
    }
break;
case 'uploadappimg':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($_POST['do']=='upload'){
        $filename = $_FILES['file']['name'];
        $ext = substr($filename, strripos($filename, '.') + 1);
        $arr = array('png', 'jpg', 'gif', 'jpeg', 'webp', 'bmp');
        if (!in_array($ext , $arr)) {
            exit('{"code":-1,"msg":"只支持上传图片文件"}');
        }
		$image = check_img($_FILES['file']['tmp_name']);
        if($image !== 'true'){
	    exit('{"code":-1,"msg":"上传失败，图片可能含有非法数据"}');
        }else{
     	$type = $_POST['type'];
        $filename = $type.'_'.md5_file($_FILES['file']['tmp_name']).'.png';
        $fileurl = 'assets/img/Program/'.$filename;
        if(copy($_FILES['file']['tmp_name'], ROOT.'assets/img/Program/'.$filename)){
            exit('{"code":0,"msg":"succ","url":"'.$fileurl.'"}');
        }else{
            exit('{"code":-1,"msg":"上传失败，请确保有本地写入权限"}');
        }	
        }
    }
    exit('{"code":-1,"msg":"null"}');
break;
case 'appdel':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $sql="DELETE FROM yixi_apps WHERE id='$id' limit 1";
    if($DB->query($sql)){
		$DB->query("DELETE FROM yixi_appuser WHERE appid='{$id}'");
		$DB->query("DELETE FROM yixi_appkm WHERE appid='{$id}'");
		$DB->query("DELETE FROM yixi_userjk WHERE appid='{$id}'");
        exit('{"code":0,"msg":"删除成功！"}');
    } else {
        exit('{"code":-1,"msg":"删除失败！' . $DB->error().'"}');
    }
break;
case 'appkmlist_table':
$kw = daddslashes($_GET['kw']);
$appid =intval($_GET['appid']);
if (isset($_GET['appid']))$row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$appid}' limit 1");
if ($_GET['use']==1) {
		$usesql = " `km_use`= 'n'";
		$used = '未使用';
    }else if ($_GET['use']==2) {
		$usesql = " `km_use`= 'y'";
		$used = '已使用';
    }else if ($_GET['use']==3) {
		$usesql = " `end_time`< '".time()."' or `amount`< '1'";
		$used = '已过期';
    }
$appidsql = " AND `appid`='{$appid}'";

if(isset($_GET['appid'])&&($_GET['use'])) {
	$sql = $usesql.$appidsql;
    $numrows=$DB->count("SELECT count(*) from yixi_appkm WHERE {$sql}");
    $con='包含 '.$row['name'].$used.' 的共有 <b>'.$numrows.'</b> 个卡密';
}elseif(isset($_GET['appid'])) {
	$sql = " `appid`='{$appid}'";
    $numrows=$DB->count("SELECT count(*) from yixi_appkm WHERE `appid`='{$appid}'");
    $con='包含 '.$row['name'].' 的共有 <b>'.$numrows.'</b> 个卡密';
}elseif(isset($_GET['use'])) {
    $sql=$usesql;
    $numrows=$DB->count("SELECT count(*) from yixi_appkm WHERE {$sql}");
    $con='包含 '.$used.' 的共有 <b>'.$numrows.'</b> 个卡密';
}elseif(isset($_GET['kw'])&&($_GET['type'])) {
    if($_GET['type']==1)
        $sql=($_GET['method']==1)?" `id` LIKE '%{$kw}%'":" `id`='{$kw}'";
	elseif($_GET['type']==2)
        $sql=($_GET['method']==1)?" `kami` LIKE '%{$kw}%'":" `kami`='{$kw}'";
	elseif($_GET['type']==3)
        $sql=($_GET['method']==1)?" `note` LIKE '%{$kw}%'":" `note`='{$kw}'";
    else{
        "";
    }
    $numrows=$DB->count("SELECT count(*) from yixi_appkm WHERE {$sql}");
    $con='包含 '.$kw.' 的共有 <b>'.$numrows.'</b> 个卡密';
}elseif(isset($_GET['kw'])) {
    $sql=($_GET['method']==1)?" `kami` LIKE '%{$kw}%'":" `kami`='{$kw}'";
    $numrows=$DB->count("SELECT count(*) from yixi_appkm WHERE {$sql}");
    $con='包含 '.$kw.' 的共有 <b>'.$numrows.'</b> 个卡密';
}else{
    $numrows=$DB->count("SELECT count(*) from yixi_appkm WHERE 1");
    $con='当前共有 <b>'.$numrows.'</b> 个卡密';
	$sql=" 1";
}
    $limit=isset($_GET['limit'])?intval($_GET['limit']):10;
    $page = ((int)$_GET['page'] - 1) * (int)$_GET['limit'];
        $data = $DB->query("SELECT * FROM yixi_appkm WHERE {$sql} order by id desc limit $page,$limit");
        $count = $DB->count("SELECT count(*) FROM yixi_appkm WHERE {$sql}");
        $data_arr = [];
        while ($re = $DB->fetch($data)) {
			$program = $DB->get_row("select * from yixi_apps where id='" . $re['appid'] . "' limit 1");
			$re['name'] = $program['name'];
			$data_arr[] = $re;
        }
		$cnum = count($data_arr);
		for($i=0;$i<$cnum;$i++){ 
			unset($data_arr[$i]['appid']);
			unset($data_arr[$i]['upid']);
        }
        die(json_encode([
            'code' => 0,
            'msg' => 'success',
            'count' => $count,
            'data' => $data_arr
        ]));
break;
case 'app_active':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $active = $row['active'] == 'y' ? 'n' : 'y';
    $DB->query("update yixi_apps set active='$active' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'app_switch':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $switch = $row['switch'] == 'y' ? 'n' : 'y';
    $DB->query("update yixi_apps set switch='$switch' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'app_ipauth':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $ipauth = $row['ipauth'] == 'y' ? 'n' : 'y';
    $DB->query("update yixi_apps set ipauth='$ipauth' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'app_login_check':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_apps WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该应用不存在！"}');
    }
    $logon_check = $row['logon_check_in'] == 'y' ? 'n' : 'y';
    $DB->query("update yixi_apps set logon_check_in='$logon_check' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'app_kmactive':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_appkm WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该卡密不存在！"}');
    }
    $state = $row['state'] == 'y' ? 'n' : 'y';
    $DB->query("update yixi_appkm set state='$state' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'app_kmqk':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqkme':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE upid='1'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqk1':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE km_use='y'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqkme1':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE km_use='y' AND upid='1'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqk2':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE km_use='n'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqkme2':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE km_use='n' AND upid='1'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqk3':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE `end_time`< '".time()."' or `amount`< '1'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_kmqkme3':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if($DB->query("DELETE FROM yixi_appkm WHERE `end_time`< '".time()."' AND upid='1' or `amount`< '1' AND upid='1'")){
        exit('{"code":0,"msg":"清空成功！"}');
    }else{
        exit('{"code":-1,"msg":"清空失败！' . $DB->error().'"}');
    }
break;
case 'app_fileactive':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_appfile WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该文件不存在！"}');
    }
    $state = $row['state'] == 'y' ? 'n' : 'y';
    $DB->query("update yixi_appfile set state='$state' where id='{$id}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'appuser_active':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $uid=intval($_GET['uid']);
    $row=$DB->get_row("SELECT * FROM yixi_appuser WHERE uid='{$uid}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该用户记录不存在！"}');
    }
    $status = $row['status'] == y ? n : y;
    $DB->query("update yixi_appuser set status='$status' where uid='{$uid}'");
    exit('{"code":0,"msg":"succ"}');
break;
case 'appkm_del':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_appkm WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该卡密不存在！"}');
    }
    $sql="DELETE FROM yixi_appkm WHERE id='$id' limit 1";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"删除成功！"}');
    } else {
        exit('{"code":-1,"msg":"删除失败！' . $DB->error().'"}');
    }
break;
case 'appfile_del':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("SELECT * FROM yixi_appfile WHERE id='{$id}' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"该文件不存在！"}');
    }
    $sql="DELETE FROM yixi_appfile WHERE id='$id' limit 1";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"删除成功！"}');
    } else {
        exit('{"code":-1,"msg":"删除失败！' . $DB->error().'"}');
    }
break;
case 'appkm_change':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $aid=intval($_POST['aid']);
    $checkbox=$_POST['checkbox'];
    $i=0;
    foreach($checkbox as $id){
        if($aid==1){
            $DB->query("update yixi_appkm set state='n' where id='$id' limit 1");
            $i++;
        }elseif($aid==2){
            $DB->query("update yixi_appkm set state='y' where id='$id' limit 1");
            $i++;
        }elseif($aid==3){
            $DB->query("DELETE FROM yixi_appkm WHERE id='$id' limit 1");
            $i++;
        }elseif($aid==4){
			$res=$DB->get_row("SELECT * FROM yixi_appkm WHERE id='$id' limit 1");
			$data[]=$res['kami'];
            $i++;
        }elseif($aid==10){
            $DB->query("update yixi_appkm set user='',user_ip='' where id='{$id}' limit 1");
            $i++;
        }
    }
	if(($aid==4)or($aid==5)){
	if($aid==5){
	$i=$DB->count("SELECT count(*) from yixi_appkm WHERE 1");
	$rs=$DB->query("SELECT * FROM yixi_appkm WHERE 1 order by id desc");
	while($res = $DB->fetch($rs))
    {
		$data[]=$res['kami'];
	} 
    }
		 $cnum = count($data);
          for($i=0;$i<$cnum;$i++){ 
          $output .= $data[$i];
          if ($cnum - 1 > $i) {
          $output .= '&#10;';
        }}
	$date = '<div class="card-body">';
    $date .= '<form class="form-horizontal">';
    $date .= '<div class="form-group mb-3">';
    $date .= '<label for="example-input-normal" style="font-weight: 500">卡密内容：</label>';
    $date .= '<textarea name="km_info" id="km_info" class="form-control" style="height:100px;" lay-verType="tips">';
	$date .= $output.'</textarea></div>';
	$date .= '<span class="btn btn-block btn-xs btn-outline-success" data-clipboard-text="'.$output.'" data-clipboard-action="copy" data-clipboard-target="#btn_code" id="btn_code">复制</span>';
	$date .= '</form>';
    $date .= '</div>';
    $result=array("code"=>1,"msg"=>"成功导出".$i."个卡密","data"=>$date);
    exit(json_encode($result));
	}else if(($aid==6)or($aid==7)){
    $amount=intval($_POST['amount']);
	$km_time=daddslashes($_POST['km_time']);
	 if($km_time=='hour'){//小时
	        $km_code=3600 * $amount;
            }else if($km_time=='day'){//天
	        $km_code=86400 * $amount;
            }else if($km_time=='week'){//周
	        $km_code=604800 * $amount;
            }else if($km_time=='month'){//月
	        $km_code=2592000 * $amount;
            }else if($km_time=='season'){//季
	        $km_code=7776000 * $amount;
            }else if($km_time=='year'){//年
	        $km_code=31104000 * $amount;
      }
	  if($aid==6){
		foreach($checkbox as $id){
            $DB->query("update yixi_appkm set `end_time`=`end_time`+".$km_code." where id='$id' AND type='code' limit 1");
            $i++;
         }
	   }else if($aid==7){
		   $i=$DB->count("SELECT count(*) from yixi_appkm WHERE end_time!=4102243200 AND type='code'");
	       $rs=$DB->query("SELECT * FROM yixi_appkm WHERE end_time!=4102243200 AND type='code' order by id desc");
	       while($res = $DB->fetch($rs)){
		   $DB->query("update yixi_appkm set `end_time`=`end_time`+".$km_code." where id='{$res['id']}' AND type='code' limit 1");
	       }
	   }
	}else if(($aid==8)or($aid==9)){
    $amount=intval($_POST['amount']);
	$km_time=daddslashes($_POST['km_time']);
	 if($km_time=='hour'){//小时
	        $km_code=3600 * $amount;
            }else if($km_time=='day'){//天
	        $km_code=86400 * $amount;
            }else if($km_time=='week'){//周
	        $km_code=604800 * $amount;
            }else if($km_time=='month'){//月
	        $km_code=2592000 * $amount;
            }else if($km_time=='season'){//季
	        $km_code=7776000 * $amount;
            }else if($km_time=='year'){//年
	        $km_code=31104000 * $amount;
      }
	  if($aid==8){
		foreach($checkbox as $id){
            $DB->query("update yixi_appkm set `end_time`=`end_time`-".$km_code." where id='$id' AND type='code' limit 1");
            $i++;
         }
	   }else if($aid==9){
		   $i=$DB->count("SELECT count(*) from yixi_appkm WHERE end_time!=4102243200 AND type='code'");
	       $rs=$DB->query("SELECT * FROM yixi_appkm WHERE end_time!=4102243200 AND type='code' order by id desc");
	       while($res = $DB->fetch($rs)){
		   if($res['end_time'] != '4102243200')
		   $DB->query("update yixi_appkm set `end_time`=`end_time`-".$km_code." where id='{$res['id']}' AND type='code' limit 1");
	       }
	   }
	}else if($aid==11){
		   $i=$DB->count("SELECT count(*) from yixi_appkm WHERE 1");
	       $rs=$DB->query("SELECT * FROM yixi_appkm WHERE 1 order by id desc");
	       while($res = $DB->fetch($rs)){
		   $DB->query("update yixi_appkm set user='',user_ip='' where id='{$id}'");
	       }
	   }
    exit('{"code":0,"msg":"成功操作'.$i.'个卡密"}');
break;
case 'set':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    foreach($_POST as $k=>$v){
        saveSetting($k, $v);
    }
    $ad=$CACHE->clear();
    if($ad)exit('{"code":0,"msg":"succ"}');
    else exit('{"code":-1,"msg":"保存设置失败['.$DB->error().']"}');
break;
case 'getMessage': //查看平台通知
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id=intval($_GET['id']);
    $row=$DB->get_row("select * from yixi_message where id='$id' limit 1");
    if(!$row){
        exit('{"code":-1,"msg":"当前通知不存在！"}');
    }
    $result=array("code"=>0,"msg"=>"succ","title"=>$row['title'],"type"=>$row['type'],"content"=>$row['content'],"date"=>$row['addtime']);
    exit(json_encode($result));
break;
case 'iptype':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $result = [['name'=>'0_X_FORWARDED_FOR', 'ip'=>real_ip(0), 'city'=>get_ip_city(real_ip(0))],['name'=>'1_X_REAL_IP', 'ip'=>real_ip(1), 'city'=>get_ip_city(real_ip(1))],['name'=>'2_REMOTE_ADDR', 'ip'=>real_ip(2), 'city'=>get_ip_city(real_ip(2))]];
    exit(json_encode($result));
break;
case 'defend':
    $defendid=$_POST['defendid'];
    $file="<?php\\r\\n//防CC模块设置\\r\\ndefine('CC_Defender', ".$defendid.");\\r\\n?>";
    file_put_contents(SYSTEM_ROOT.'base.php',$file);
    $result=array("code"=>0,"msg"=>"保存成功！");
    exit(json_encode($result));
break;
case 'cleanbom':
    $filename=ROOT.'config.php';
    $contents=file_get_contents($filename);
    $charset[1]=substr($contents,0,1);
    $charset[2]=substr($contents,1,1);
    $charset[3]=substr($contents,2,1);
    if (ord($charset[1])==239 && ord($charset[2])==187) {
        $rest=substr($contents,3);
        file_put_contents($filename,$rest);
        $result=array("code"=>0,"msg"=>"找到BOM并已自动去除！");
    }else {
        $result=array("code"=>-1,"msg"=>"没有找到BOM！");
    }
    exit(json_encode($result));
break;
case 'site_endtime':
    $id=intval($_POST['id']);
    $num=intval($_POST['num']);
    $row=$DB->get_row("select * from yixi_site where id='$id' limit 1");
    if($row['endtime']>date("Y-m-d")) {
if($conf['auth_time_type']==2){
    $endtime=date("Y-m-d", strtotime("+{$num} years", strtotime($row['endtime'])));
}elseif($conf['auth_time_type']==1){
    $endtime=date("Y-m-d", strtotime("+{$num} months", strtotime($row['endtime'])));
}else{
    $endtime=date("Y-m-d", strtotime("+{$num} days", strtotime($row['endtime'])));
}
}else{
if($conf['auth_time_type']==2){
    $endtime=date("Y-m-d", strtotime("+{$num} years"));
}elseif($conf['auth_time_type']==1){
    $endtime=date("Y-m-d", strtotime("+{$num} months"));
}else{
    $endtime=date("Y-m-d", strtotime("+{$num} days"));
}
}
    $sql="update yixi_site set endtime='$endtime' where id='{$id}'";
    if($DB->query($sql)){
        exit('{"code":0,"msg":"续时成功"}');
    }else{
        exit('{"code":-1,"msg":"续时失败！' . $DB->error().'"}');
    }
break;
case 'mailtest':
    $mail_name=($conf['mail_recv'] ? $conf['mail_recv'] : $conf['mail_name']);
    $sub = '邮件发送测试。';
    if (!empty($mail_name)) {
        $text = '这是一封测试邮件！<br/><br/>来自：'.$siteurl;
        $msg = youfas($sub,$text);
        $result=send_mail($mail_name,$msg);
        if ($result==1) {
            exit('{"code":0,"msg":"邮件发送成功！"}');
        } else {
            exit('{"code":-1,"msg":"邮件发送失败！"}');
        }
    } else {
        exit('{"code":-1,"msg":"您还未设置邮箱！"}');
    }
break;
case 'optim':
    $rs=$DB->query("SHOW TABLES FROM `".$dbconfig['dbname'].'`');
    while ($row = $DB->fetch($rs)) {
        $DB->query('OPTIMIZE TABLE  `'.$dbconfig['dbname'].'`.`'.$row[0].'`');
    }
    exit('{"code":0,"msg":"已成功优化所有数据表"}');
break;
case 'repair':
    $rs=$DB->query("SHOW TABLES FROM `".$dbconfig['dbname'].'`');
    while ($row = $DB->fetch($rs)) {
        $DB->query('REPAIR TABLE  `'.$dbconfig['dbname'].'`.`'.$row[0].'`');
    }
    exit('{"code":0,"msg":"已成功修复所有数据表"}');
break;
case 'add_key':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $money = $conf['add_key_rmb'];
    $key = random(32);
    saveSetting('api_key',$key);
    $ad=$CACHE->clear();
    if($ad)exit('{"code":0,"msg":"APIKEY生成成功"}');
    else exit('{"code":-1,"msg":"APIKEY生成失败['.$DB->error().']"}');
break;
case 'api_key':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if(isset($_SESSION['api_key']) && $_SESSION['api_key']>TIMESTAMP-600){
        exit('{"code":-1,"msg":"请勿频繁重置！"}');
    }
    $key = random(32);
    saveSetting('api_key',$key);
    $ad=$CACHE->clear();
    if($ad){
        $_SESSION['api_key']=TIMESTAMP;
        exit('{"code":0,"msg":"APIKEY重置成功"}');
    }else{
        exit('{"code":-1,"msg":"APIKEY重置失败['.$DB->error().']"}');
    }
break;
case 'api_ip':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $data=trim($_POST['data']);
    saveSetting('api_iplist',$data);
    $ad=$CACHE->clear();
    if($ad)exit('{"code":0,"msg":"设置成功"}');
    else exit('{"code":-1,"msg":"设置失败['.$DB->error().']"}');
break;
case 'api_iplist':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $result=array("code"=>0,"msg"=>"succ","data"=>$conf['api_iplist']);
    exit(json_encode($result));
break;
case 'get_apijk':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $proid=intval($_POST['proid']);
    if(!$proid){
        exit('{"code":-1,"msg":"请选择您要生成的程序！"}');
    }
    $program = $DB->get_row("select * from yixi_program where id='" . $proid . "' limit 1");
    if (!$program) {
        exit('{"code":-1,"msg":"该程序不存在！"}');
    }
    $result=array("code"=>0,"msg"=>"succ","api_jk"=>$authurl.'api/cloud_api.php?act=cloud_auth&proid='.$proid.'&name=授权站点名称&qq=授权QQ&url=授权域名&ip=服务器ip&key='.$conf['api_key']);
    exit(json_encode($result));
break;
case 'get_user_apijk':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $proid=intval($_POST['proid']);
    if(!$proid){
        exit('{"code":-1,"msg":"请选择您要生成的程序！"}');
    }
    $program = $DB->get_row("select * from yixi_program where id='" . $proid . "' limit 1");
    if (!$program) {
        exit('{"code":-1,"msg":"该程序不存在！"}');
    }
    $result=array("code"=>0,"msg"=>"succ","apisqs_jk"=>$authurl.'api/cloud_api.php?act=cloud_user&proid='.$proid.'&power=1&user=登录用户名&pwd=登录密码&qq=联系QQ&email=绑定邮箱&ip=服务器ip&key='.$conf['api_key'],"apicg_jk"=>$authurl.'api/cloud_api.php?act=cloud_user&proid='.$proid.'&power=2&user=登录用户名&user=登录密码&qq=联系QQ&email=绑定邮箱&ip=服务器ip&key='.$conf['api_key']);
    exit(json_encode($result));
break;
case 'transfer':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    $id = intval($_POST['id']);
    if(!$conf['user_daifu'])exit(json_encode(array('code'=>0,'msg'=>'请先在用户设置开启代付接口')));
    if(!$conf['transfer_id'] || !$conf['transfer_key'] || !$conf['transfer_check'] || !$_SESSION["transfer_pass"])exit(json_encode(array('code'=>0,'msg'=>'请先配置好自动转账接口信息')));
    $res = $DB->get_row("SELECT * FROM yixi_tixian WHERE id='$id' AND status=0");
    if (!$res) exit(json_encode(array('code'=>0,'msg'=>'记录不存在或状态不是待处理！')));
    if ($res['type'].'' == '1') {
        $type = '3';
    }elseif ($res['type'].'' == '0') {
        $type = '1';
    }else{
        $type = $res['type'];
    }
    $param = ['api_id'=>trim($conf['transfer_id']),'money'=>$res['realmoney'],'payee_type'=>$type,'payee_account'=>$res['account'],'payee_name'=>$res['name'],'realname'=>$conf['transfer_check'],'timestamp'=>TIMESTAMP,'pay_pass'=>$_SESSION["transfer_pass"],];
    $param['sign'] = getSign($param, trim($conf['transfer_key']));
    $data = get_curl('https://api.fcypay.com/transfer', $param);
    $json = json_decode($data,true);
    if (isset($json['code']) && $json['code']) {
        if(!$DB->query("update yixi_tixian set status=1,endtime=NOW() where id='$id'")) {
            exit(json_encode(array('code'=>0,'msg'=>'汇款成功!但是结算记录状态改变失败！')));
        }
        exit(json_encode(array('code'=>1,'msg'=>'汇款成功')));
    }else{
        exit(json_encode(array('code'=>0,'msg'=>isset($json['msg'])?$json['msg']:'对接平台未知错误')));
    }
break;
case 'transfer_config':
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    if(!$conf['user_daifu'])exit(json_encode(array('code'=>0,'msg'=>'请先在分站设置开启代付接口')));
    if (!$_POST['id'] || !$_POST['key'] || !$_POST['pass']) exit(json_encode(['code'=>0,'msg'=>'请填写完整']));
    if ($_POST['check'] !== 'NO_CHECK' && $_POST['check'] !== 'FORCE_CHECK') exit(json_encode(['code'=>0,'msg'=>'验证选项错误']));
    saveSetting('transfer_id',$_POST['id']);
    saveSetting('transfer_key',$_POST['key']);
    saveSetting('transfer_check',$_POST['check']);
    $CACHE->clear();
    $_SESSION["transfer_pass"] = md5($_POST['pass']);
    $_SESSION["transfer"] = true;
    exit(json_encode(['code'=>1,'msg'=>'保存成功']));
break;
default:
    if($islogin!=1)exit('{"code":-1,"msg":"未登录！"}');
    exit('{"code":-4,"msg":"No Act"}');
break;
}