<?php
include_once 'includes/function.php';
include_once 'includes/global.php';
$file = isset($_GET['file']) ? daddslashes($_GET['file']) : 0;
if($file){
  $file=mi_rc4($file,'6ba6cc1043b97685',1);
  $file_name = isset($_GET['filename']) ? daddslashes($_GET['filename']) : rand(111111,999999).'.zip';
if(file_exists($file)){
	$file_size = filesize("$file");
    header("Content-Description: File Transfer");
    header("Content-Type:application/force-download");
    header("Content-Length: {$file_size}");
    header("Content-Disposition:attachment; filename={$file_name}");
    readfile("$file");
}else{
exit("error");
}
}else{
header("Location: ../");
}

?>