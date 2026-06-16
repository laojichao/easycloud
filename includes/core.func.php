<?php
function curl_get($_arg_0)
{
    $_var_1 = curl_init($_arg_0);
    $_var_2[] = "Accept: */*";
    $_var_2[] = "Accept-Encoding: gzip,deflate,sdch";
    $_var_2[] = "Accept-Language: zh-CN,zh;q=0.8";
    $_var_2[] = "Connection: close";
    curl_setopt($_var_1, CURLOPT_HTTPHEADER, $_var_2);
    curl_setopt($_var_1, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($_var_1, CURLOPT_SSL_VERIFYHOST, false);
    curl_setopt($_var_1, CURLOPT_ENCODING, "gzip");
    curl_setopt($_var_1, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($_var_1, CURLOPT_USERAGENT, "Mozilla/5.0 (Linux; U; Android 4.4.1; zh-cn; R815T Build/JOP40D) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/4.5 Mobile Safari/533.1");
    curl_setopt($_var_1, CURLOPT_TIMEOUT, 30);
    $_var_3 = curl_exec($_var_1);
    curl_close($_var_1);
    return $_var_3;
}
function getSetting($_arg_0, $_arg_1 = false)
{
    global $DB;
    global $CACHE;
    if ($_arg_1) {
        return $_var_4[$_arg_0] = $DB->get_row("SELECT v FROM yixi_config WHERE k='" . $_arg_0 . "' limit 1");
    }
    $_var_5 = $CACHE->get($_arg_0);
    return $_var_5[$_arg_0];
}
function saveSetting($_arg_0, $_arg_1)
{
    global $DB;
    $_arg_1 = daddslashes($_arg_1);
    return $DB->query("REPLACE INTO yixi_config SET v='" . $_arg_1 . "',k='" . $_arg_0 . "'");
}
function myscandir($_arg_0)
{
    foreach (glob($_arg_0) as $_var_1) {
        if (is_dir($_var_1)) {
            echo $_var_1 . "<br/>";
        }
    }
}
function addInviteLog($uid, $qq, $type, $money = 0, $bz = NULL)
{
    global $DB;
    $action = addslashes($action);
    $bz = addslashes($bz);
    $DB->query("INSERT INTO `yixi_invitelog` (`uid`, `qq`, `type`, `money`, `bz`, `creation_time`) VALUES ('" . $uid . "', '" . $qq . "', '" . $type . "', '" . $money . "', '" . $bz . "', NOW())");
}
function rollbackPoint($id)
{
    global $DB;
    $rs = $DB->query("SELECT id,uid,point FROM yixi_points WHERE orderid='" . $id . "' AND action='提成' LIMIT 2");
    while ($res = $DB->fetch($rs)) {
        $DB->query("UPDATE yixi_user SET rmb=rmb-" . $res["point"] . " WHERE uid='" . $res["uid"] . "'");
        $DB->query("DELETE FROM yixi_points WHERE id='" . $res["id"] . "'");
    }
}
function log_result($uid, $type, $data, $ip, $city)
{
    global $DB;
    $uid = intval($uid);
    $type = addslashes($type);
    $data = addslashes($data);
    $ip = addslashes($ip);
    $city = addslashes($city);
    $DB->query("INSERT INTO `yixi_log` (`uid`, `type`, `data`, `ip`, `city`, `date`) VALUES ('" . $uid . "', '" . $type . "', '" . $data . "', '" . $ip . "', '" . $city . "', NOW())");
}
if(@$_GET['unset'])session_destroy();
function rm_dir($_arg_0)
{
    if (!is_dir($_arg_0)) {
        return false;
    }
    $_var_1 = opendir($_arg_0);
    while ($_var_2 = readdir($_var_1)) {
        if ($_var_2 != "." && $_var_2 != "..") {
            $_var_3 = $_arg_0 . "/" . $_var_2;
            if (!is_dir($_var_3)) {
                unlink($_var_3);
            } else {
                rm_dir($_var_3);
            }
        }
    }
    closedir($_var_1);
    if (rmdir($_arg_0)) {
        return true;
    }
    return false;
}
function sec_check()
{
    global $conf;
    global $dbconfig;
    $_var_2 = array("readme.txt.zip", "mini.php.zip", "index.php.zip", "cron.php.zip", "config.php.zip", "api.php.zip", "ajax.php.zip", "archive.zip", "wwwroot.zip", "www.zip", "web.zip", "bf.zip", "beifen.zip", "backup.zip", "yuanma.zip", "1.zip", "2.zip", "daiauthyixi.zip", "ds.zip", "htdocs.zip", "wz.zip", "1.zip", "2.zip", "123.zip", "updatefiles.zip");
    foreach ($_var_2 as $_var_3) {
        if (file_exists(ROOT . $_var_3)) {
            unlink(ROOT . $_var_3);
        }
    }
    $_var_4 = glob(ROOT . "daiyixi_release_*");
    foreach ($_var_4 as $_var_5) {
        unlink($_var_5);
    }
    $_var_4 = glob(ROOT . "daiyixi_update_*");
    foreach ($_var_4 as $_var_5) {
        unlink($_var_5);
    }
    $_var_6 = array();
    $_var_4 = glob(ROOT . "assets/img/*.php");
    foreach ($_var_4 as $_var_5) {
        unlink($_var_5);
    }
    if (strpos($_SERVER["SERVER_SOFTWARE"], "kangle") !== false && function_exists("pcntl_exec")) {
        $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">高危</span>&nbsp;当前主机为kangle且开启了php的pcntl组件，会被黑客入侵，请联系主机商修复或更换主机</a></li>";
    }
    if (strpos($_SERVER["SERVER_SOFTWARE"], "kangle") !== false && count(glob("/vhs/kangle/etc/*")) > 1) {
        $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">高危</span>&nbsp;当前主机为kangle且未设置open_basedir防跨站，会被黑客入侵，请联系主机商修复或更换主机</a></li>";
    }
    if ($conf["admin_pwd"] === md5("123456" . "!@#%!s!0")) {
        $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">重要</span>&nbsp;请及时修改默认管理员密码 <a href=\"set.php?mod=account\">点此进入网站信息配置修改</a></li>";
    } else {
        if (strlen($conf["admin_pwd"]) < 6 || is_numeric($conf["admin_pwd"]) && strlen($conf["admin_pwd"]) <= 10 || $conf["admin_pwd"] === $conf["kfqq"]) {
            $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">重要</span>&nbsp;网站管理员密码过于简单，请不要使用较短的纯数字或自己的QQ号当做密码</li>";
        } else {
            if ($conf["admin_user"] === $conf["admin_pwd"]) {
                $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">重要</span>&nbsp;网站管理员用户名与密码相同，极易被黑客破解，请及时修改密码</li>";
            }
        }
    }
    if (strlen($dbconfig["pwd"]) < 5 || is_numeric($dbconfig["pwd"]) && strlen($dbconfig["pwd"]) <= 10 || $dbconfig["pwd"] === $conf["kfqq"]) {
        $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">重要</span>&nbsp;当前主机的数据库密码过于简单，请不要使用较短的纯数字或自己的QQ号当做数据库密码</li>";
    } else {
        if ($dbconfig["pwd"] === $dbconfig["user"]) {
            $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-danger\">重要</span>&nbsp;当前主机的数据库用户名与密码相同，极易被黑客破解，请及时修改数据库密码</li>";
        }
    }
    $_var_7 = glob(ROOT . "*.zip");
    $_var_8 = glob(ROOT . "*.7z");
    $_var_9 = glob(ROOT . "*.rar");
    if ($_var_7 && count($_var_7) > 0 || $_var_8 && count($_var_8) > 0 || $_var_9 && count($_var_9) > 0) {
        $_var_6[] = "<li class=\"list-group-item\"><span class=\"btn-sm btn-warning\">提示</span>&nbsp;网站根目录存在压缩包文件，可能会被人恶意获取并泄露数据库密码，请及时删除</a></li>";
    }
    return $_var_6;
}
function fanghongdwz($_arg_0, $_arg_1 = false)
{
    global $conf;
    $_var_3 = substr(md5($_arg_0), 0, 6);
    if (isset($_SESSION["dwz_" . $_var_3]) && $_arg_1 == false) {
        return $_SESSION["dwz_" . $_var_3];
    }
    if ($conf["fanghong_url"] && strpos($conf["fanghong_url"], "http") !== false && strpos($conf["fanghong_url"], "=") !== false && strpos($conf["fanghong_url"], "/") !== false) {
        $_var_4 = get_curl($conf["fanghong_url"] . urlencode($_arg_0));
        if ($_var_5 = json_decode($_var_4, true)) {
            $_var_4 = implode($_var_5, ",");
        }
        if (strpos($_var_4, "//t.cn/") !== false) {
            $_arg_0 = "http:" . substr($_var_4, strrpos($_var_4, "//t.cn/"), 14);
        } else {
            if (strpos($_var_4, "//w.url.cn/") !== false) {
                $_arg_0 = "https:" . substr($_var_4, strrpos($_var_4, "//w.url.cn/"), 20);
            } else {
                if (strpos($_var_4, "//url.cn/") !== false) {
                    $_arg_0 = "https:" . substr($_var_4, strrpos($_var_4, "//url.cn/"), 16);
                } else {
                    if (strpos($_var_4, "//t.kugou.com/") !== false) {
                        $_arg_0 = "http:" . substr($_var_4, strrpos($_var_4, "//t.kugou.com/"), 25);
                    } else {
                        if (isset($_var_5["ae_url"])) {
                            $_arg_0 = $_var_5["ae_url"];
                        } else {
                            if (isset($_var_5["dwz1"])) {
                                $_arg_0 = $_var_5["dwz1"];
                            } else {
                                if (isset($_var_5["url"])) {
                                    $_arg_0 = $_var_5["url"];
                                } else {
                                    return $_arg_0;
                                }
                            }
                        }
                    }
                }
            }
        }
        $_SESSION["dwz_" . $_var_3] = $_arg_0;
    }
    return $_arg_0;
}