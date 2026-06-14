DROP TABLE IF EXISTS `yixi_appfile`;
CREATE TABLE `yixi_appfile` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `uid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '归属用户',
  `appid` int(10) NOT NULL COMMENT '应用ID',
  `type` enum('lanzou','other') NOT NULL COMMENT '云端类型',
  `file_url` text COMMENT '外链地址',
  `lanzou_pass` text COMMENT '蓝奏云密码',
  `addtime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `state` enum('y','n') DEFAULT 'y' COMMENT '状态',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `yixi_appkm`;
CREATE TABLE `yixi_appkm` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `upid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '归属用户',
  `appid` int(10) NOT NULL COMMENT '应用ID',
  `kami` varchar(32) NOT NULL COMMENT '卡密',
  `type` enum('code','vip','fen','single','svipcard') DEFAULT 'vip' COMMENT '卡密类型',
  `note` varchar(255) DEFAULT NULL,
  `amount` int(10) NOT NULL COMMENT '卡密对应的值',
  `user` varchar(64) DEFAULT NULL COMMENT '使用者',
  `use_time` bigint(11) DEFAULT NULL COMMENT '使用时间',
  `end_time` bigint(11) DEFAULT NULL COMMENT '结束时间',
  `addtime` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  `km_use` enum('y','n') DEFAULT 'n' COMMENT '是否使用',
  `km_change` int(10) NOT NULL COMMENT '解绑次数',
  `km_time` varchar(10) DEFAULT NULL COMMENT '单码卡密类型',
  `user_ip` varchar(15) DEFAULT NULL COMMENT '使用者IP',
  `km_change_time` bigint(11) DEFAULT NULL COMMENT '卡密解绑时间',
  `state` enum('y','n') DEFAULT 'y' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `yixi_apps`;
CREATE TABLE `yixi_apps` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '应用ID',
  `appkey` varchar(32) NOT NULL COMMENT 'APPKEY',
  `name` varchar(80) NOT NULL COMMENT '应用名称',
  `img` text NOT NULL COMMENT '应用图标',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `app_gg` text NOT NULL COMMENT '应用公告',
  `version` varchar(255) NOT NULL COMMENT '应用版本',
  `version_info` text NOT NULL COMMENT '应用版本信息',
  `switch` enum('y','n') DEFAULT 'y' COMMENT '是否付费',
  `ipauth` enum('y','n') DEFAULT 'y' COMMENT '是否验证IP',
  `mi_state` enum('y','n') DEFAULT 'y' COMMENT '加密控制',
  `mi_type` int(10) DEFAULT '0' COMMENT '加密类型',
  `mi_sign` enum('y','n') DEFAULT 'y' COMMENT '是否签名',
  `km_unmachine` enum('y','n') DEFAULT 'y' COMMENT '卡密解绑限制设备',
  `mi_sign_in` enum('y','n') DEFAULT 'y' COMMENT '签名是否放DATA里',
  `print_sign` enum('y','n') DEFAULT 'n' COMMENT '调试模式',
  `mi_time` int(10) DEFAULT '10' COMMENT '时间差效验',
  `rc4_key` varchar(255) DEFAULT NULL COMMENT 'RC4秘钥',
  `active` enum('y','n') DEFAULT 'y' COMMENT '是否运行',
  `logon_check_in` enum('y','n') DEFAULT 'y' COMMENT '登录校验设备信息',
  `sqprice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应用卡密销售价格',
  `sqprice2` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应用代理商添加卡密价格',
  `sqprice3` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应用超管添加卡密价格',
  `sqsprice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应用代理商销售价格',
  `sqsprice2` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应用超管添加代理商价格',
  `cgprice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应用超管销售价格',
  `app_update_must` enum('y','n') DEFAULT 'n' COMMENT '应用强制更新',
  `app_update_url` text COMMENT '应用更新地址',
  `app_update_show` text COMMENT '应用更新内容',
  `app_update_url_type` enum('lanzou','other') NOT NULL COMMENT '云端类型',
  `lanzou_pass` text COMMENT '蓝奏云密码',
  `km_change_time` int(10) DEFAULT '24' COMMENT '卡密换绑时间间隔',
  `km_change_num` int(10) DEFAULT '1' COMMENT '卡密解绑扣时长',
  `single_km_change_num` int(10) DEFAULT '1' COMMENT '次数卡密解绑扣次数',
  `km_change` int(10) DEFAULT '3' COMMENT '卡密可解绑次数',
  `longuse_km_change` int(10) DEFAULT '3' COMMENT '永久卡可解绑次数',
  `hourprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `dayprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `weekprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monthprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `seasonprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `yearprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `longuseprice` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total` varchar(32) NOT NULL DEFAULT '0' COMMENT '累计调用次数',
  `date` datetime NOT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`appkey`),
  KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `yixi_config`;
CREATE TABLE `yixi_config` (
  `k` varchar(32) NOT NULL,
  `v` text,
  PRIMARY KEY (`k`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `yixi_config` VALUES ('access_token',''),('adminlogin','2021-11-12 10:51:57'),('admin_login_open','1'),('admin_pwd','123456'),('admin_qq','***REMOVED***'),('admin_qqloginsm_open','1'),('admin_qqlogin_open','0'),('admin_remote_login_open','1'),('admin_send_type','0'),('admin_user','admin'),('defendid','3'),('description','极简云验证提供应用管理，API数据接口调用服务平台 - 我们致力于为开发者提供稳定、高效的API数据接口服务。'),('email','***REMOVED***@qq.com'),('email_temp','1'),('footer',''),('gxqm','个性还需要签名吗'),('icp','<span>© 2020-2022</span><a href=\"http://www.miibeian.gov.cn/\"                      target=\"_blank\">极简云验证-不止于此</a><span></span>'),('index_notice','因注册用户响应较为积极，现再次决定限时开放注册一星期，一星期后开启邀请注册制。用户交流群：***REMOVED***'),('index_open','1'),('invite_money','1'),('invite_rebate_open','0'),('invite_time','week'),('keywords','极简云验证提供应用管理，API数据接口调用服务平台 - 我们致力于为开发者提供稳定、高效的API数据接口服务。'),('kfqq','123456'),('orgname','<h4>关于 <a href=\"#\" target=\"_blank\">YiXi</a> API</h4>\n                <p>\n                    可可 API 是 <a href=\"https://api.qqua.cn/\" target=\"_blank\">可可</a>\n                    支持并维护的 API 接口项目，致力于为用户提供稳定、快速的免费 API 接口服务平台。\n                </p>\n                <p>\n                    反馈或建议请发送邮件至：***REMOVED***@qq.com\n                </p>'),('phone','***REMOVED***'),('qunhao','***REMOVED***'),('sitename','极简云验证'),('template','default'),('tenpay_api','2'),('title','不止于此'),('ui_background','3'),('version','2001');

DROP TABLE IF EXISTS `yixi_log`;
CREATE TABLE `yixi_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` varchar(150) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL,
  `data` text,
  `ip` varchar(20) DEFAULT NULL,
  `city` varchar(20) DEFAULT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `yixi_cache`;
CREATE TABLE `yixi_cache` (
  `k` varchar(32) NOT NULL,
  `v` text DEFAULT NULL,
  PRIMARY KEY (`k`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `yixi_sig` (
  `id` int(10) NOT NULL,
  `upid` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '归属用户',
  `appid` int(10) NOT NULL COMMENT '应用ID',
  `token` varchar(32) DEFAULT NULL,
  `appsign` varchar(64) DEFAULT NULL COMMENT '软件效验码',
  `vpn` enum('y','n') DEFAULT 'n' COMMENT '抓包检测',
  `vpntype` int(2) DEFAULT '1' COMMENT '检测到vpn措施',
  `safetype` int(2) DEFAULT '1' COMMENT '安全检查措施',
  `addtime` timestamp NULL DEFAULT NULL COMMENT '添加时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;