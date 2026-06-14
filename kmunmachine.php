<?php
include_once './includes/common.php';
@header('Content-Type: text/html; charset=UTF-8');
if($conf['maintain_open']==1){
	sysmsgs('站点维护中，请谅解',true);
}else{
$loadfile = Template::load('kmunmachine');
include $loadfile;
}
?>