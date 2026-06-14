<?php
if(!defined('IN_CRONLITE'))exit();
$wowoqaqwo=RandomStr1(16);
$clientip=real_ip($conf['ip_type']?$conf['ip_type']:0);

if(isset($_COOKIE["admin_auth_token"])) {
	$token=authcode(daddslashes($_COOKIE['admin_auth_token']), 'DECODE', SYS_KEY);
	list($user, $sid, $expiretime) = explode("\t", $token);
	$session=md5($conf['admin_user'].$conf['admin_pwd'].$password_hash);
	if($session===$sid && $expiretime>TIMESTAMP) {
		$islogin=1;
	}else{
		unset($_COOKIE['admin_auth_token']);
	}
}
?>