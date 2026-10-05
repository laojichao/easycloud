<?php
/*
 * RSA 私钥不再随仓库分发（历史提交中的明文私钥已从 git 历史清除）。
 * 运行环境请任选一种方式提供：
 *   1) 环境变量 EASYCLOUD_RSA_PRIVATE_KEY
 *   2) includes/class/private.local.php（已在 .gitignore 中忽略）
 */
$private_key = '';
if (is_file(__DIR__ . '/private.local.php')) {
    include_once __DIR__ . '/private.local.php'; // 该文件内自行定义 $private_key
}
$envKey = getenv('EASYCLOUD_RSA_PRIVATE_KEY');
if (!empty($envKey)) {
    $private_key = $envKey;
}
?>
