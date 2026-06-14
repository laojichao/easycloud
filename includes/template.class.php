<?php
if(!defined('IN_CRONLITE'))exit();
class Template {

	static public function getList(){
		$dir = TEMPLATE_ROOT;
		$dirArray[] = NULL;
        if (false != ($handle = opendir($dir))) {
            $i = 0;
            while (false !== ($file = readdir($handle))) {
                if ($file != "." && $file != ".." && !strpos($file, ".")) {
                    $dirArray[$i] = $file;
                    $i++;
                }
            }
            closedir($handle);
        }
        return $dirArray;
	}

	static public function load($name = 'index'){
		global $conf;
		$template = $conf['template']?$conf['template']:'layui';
		if(!preg_match('/^[a-zA-Z0-9]+$/',$name))exit('error');
		$filename = TEMPLATE_ROOT.$template.'/'.$name.'.php';
		$filename_default = TEMPLATE_ROOT.'layui/'.$name.'.php';
		if(file_exists($filename)){
			return $filename;
		}elseif(file_exists($filename_default)){
			return $filename_default;
		}else{
			exit(sysmsg2("Template file not found",true));
		}
	}
	
	static public function doc($dir = ''){
		global $conf;
		$template = $conf['template']?$conf['template']:'YiXi';
		if(!preg_match('/^[a-zA-Z0-9]+$/',$dir))exit(sysmsg2("error",true));
		$filename = TEMPLATE_ROOT.$template.'/doc/'.$dir;
		if(is_dir($filename)){
			return $filename;
		}else{
			exit(sysmsg2("Doc file not found",true));
		}
	}
	
	static public function api($dir = ''){
		if(!preg_match('/^[a-zA-Z0-9]+$/',$dir))exit(sysmsg2("error",true));
		$filename = ROOT.API_MULU.'/'.$dir;
		if(is_dir($filename)){
			return $filename;
		}else{
			exit(sysmsg2("API not found",true));
		}
	}

	static public function exists($template){
		$filename = TEMPLATE_ROOT.$template.'/index.php';
		if(file_exists($filename)){
			return true;
		}else{
			return false;
		}
	}
}
