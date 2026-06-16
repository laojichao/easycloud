-- H2-compatible schema for dev profile
-- Converted from MySQL install/simple.sql

CREATE TABLE IF NOT EXISTS yixi_appfile (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uid INT NOT NULL DEFAULT 0,
  appid INT NOT NULL,
  type VARCHAR(10) NOT NULL DEFAULT 'other',
  file_url TEXT,
  lanzou_pass TEXT,
  addtime TIMESTAMP,
  state VARCHAR(1) DEFAULT 'y',
  note VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_appfile_appid ON yixi_appfile(appid);

CREATE TABLE IF NOT EXISTS yixi_appkm (
  id INT AUTO_INCREMENT PRIMARY KEY,
  upid INT NOT NULL DEFAULT 0,
  appid INT NOT NULL,
  kami VARCHAR(32) NOT NULL,
  type VARCHAR(10) DEFAULT 'vip',
  note VARCHAR(255),
  amount INT NOT NULL DEFAULT 0,
  user VARCHAR(64),
  use_time BIGINT,
  end_time VARCHAR(20),
  addtime TIMESTAMP,
  km_use VARCHAR(1) DEFAULT 'n',
  km_change INT NOT NULL DEFAULT 0,
  km_time VARCHAR(10),
  user_ip VARCHAR(15),
  km_change_time BIGINT,
  state VARCHAR(1) DEFAULT 'y'
);
-- 卡密查询高频索引：API登录按 (kami, appid) 查询，管理后台按 appid/type/state/km_use 筛选
CREATE INDEX IF NOT EXISTS idx_appkm_kami_appid ON yixi_appkm(kami, appid);
CREATE INDEX IF NOT EXISTS idx_appkm_appid ON yixi_appkm(appid);
CREATE INDEX IF NOT EXISTS idx_appkm_state ON yixi_appkm(state);
CREATE INDEX IF NOT EXISTS idx_appkm_km_use ON yixi_appkm(km_use);

CREATE TABLE IF NOT EXISTS yixi_apps (
  id INT AUTO_INCREMENT PRIMARY KEY,
  appkey VARCHAR(32) NOT NULL,
  name VARCHAR(80) NOT NULL,
  img TEXT,
  note VARCHAR(255),
  app_gg TEXT,
  version VARCHAR(255),
  version_info TEXT,
  switch VARCHAR(1) DEFAULT 'y',
  ipauth VARCHAR(1) DEFAULT 'y',
  mi_state VARCHAR(1) DEFAULT 'y',
  mi_type INT DEFAULT 0,
  mi_sign VARCHAR(1) DEFAULT 'y',
  km_unmachine VARCHAR(1) DEFAULT 'y',
  mi_sign_in VARCHAR(1) DEFAULT 'y',
  print_sign VARCHAR(1) DEFAULT 'n',
  mi_time INT DEFAULT 10,
  rc4_key VARCHAR(255),
  mi_rsa_private_key TEXT,
  active VARCHAR(1) DEFAULT 'y',
  logon_check_in VARCHAR(1) DEFAULT 'y',
  sqprice DECIMAL(10,2) DEFAULT 0.00,
  sqprice2 DECIMAL(10,2) DEFAULT 0.00,
  sqprice3 DECIMAL(10,2) DEFAULT 0.00,
  sqsprice DECIMAL(10,2) DEFAULT 0.00,
  sqsprice2 DECIMAL(10,2) DEFAULT 0.00,
  cgprice DECIMAL(10,2) DEFAULT 0.00,
  app_update_must VARCHAR(1) DEFAULT 'n',
  app_update_url TEXT,
  app_update_show TEXT,
  app_update_url_type VARCHAR(10) DEFAULT 'other',
  lanzou_pass TEXT,
  km_change_time INT DEFAULT 24,
  km_change_num INT DEFAULT 1,
  single_km_change_num INT DEFAULT 1,
  km_change INT DEFAULT 3,
  longuse_km_change INT DEFAULT 3,
  hourprice DECIMAL(10,2) DEFAULT 0.00,
  dayprice DECIMAL(10,2) DEFAULT 0.00,
  weekprice DECIMAL(10,2) DEFAULT 0.00,
  monthprice DECIMAL(10,2) DEFAULT 0.00,
  seasonprice DECIMAL(10,2) DEFAULT 0.00,
  yearprice DECIMAL(10,2) DEFAULT 0.00,
  longuseprice DECIMAL(10,2) DEFAULT 0.00,
  total VARCHAR(32) DEFAULT '0',
  date TIMESTAMP NOT NULL
);
-- PHP schema: UNIQUE KEY `key` (appkey)
CREATE UNIQUE INDEX IF NOT EXISTS uk_apps_appkey ON yixi_apps(appkey);

CREATE TABLE IF NOT EXISTS yixi_config (
  k VARCHAR(32) PRIMARY KEY,
  v TEXT
);

CREATE TABLE IF NOT EXISTS yixi_log (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uid VARCHAR(150),
  type VARCHAR(20),
  data TEXT,
  ip VARCHAR(20),
  city VARCHAR(20),
  date TIMESTAMP NOT NULL
);
-- 日志按日期查询（今日统计）频繁，需要索引
CREATE INDEX IF NOT EXISTS idx_log_date ON yixi_log(date);
CREATE INDEX IF NOT EXISTS idx_log_type ON yixi_log(type);

CREATE TABLE IF NOT EXISTS yixi_cache (
  k VARCHAR(32) PRIMARY KEY,
  v TEXT
);

CREATE TABLE IF NOT EXISTS yixi_sig (
  id INT AUTO_INCREMENT PRIMARY KEY,
  upid INT NOT NULL DEFAULT 0,
  appid INT NOT NULL,
  token VARCHAR(32),
  appsign VARCHAR(64),
  vpn VARCHAR(1) DEFAULT 'n',
  vpntype INT DEFAULT 1,
  safetype INT DEFAULT 1,
  addtime TIMESTAMP
);

-- 支付订单表
CREATE TABLE IF NOT EXISTS yixi_payment_order (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(32) NOT NULL UNIQUE,
  uid BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  pay_type VARCHAR(10) NOT NULL,
  status VARCHAR(10) NOT NULL DEFAULT 'pending',
  trade_no VARCHAR(64),
  create_time TIMESTAMP NOT NULL,
  pay_time TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_payment_order_uid ON yixi_payment_order(uid);
CREATE INDEX IF NOT EXISTS idx_payment_order_status ON yixi_payment_order(status);

-- 支付配置默认值（存储在 yixi_config 中）
MERGE INTO yixi_config (k, v) VALUES ('wxpay_appid', '');
MERGE INTO yixi_config (k, v) VALUES ('wxpay_mchid', '');
MERGE INTO yixi_config (k, v) VALUES ('wxpay_key', '');
MERGE INTO yixi_config (k, v) VALUES ('qqpay_appid', '');
MERGE INTO yixi_config (k, v) VALUES ('qqpay_mchid', '');
MERGE INTO yixi_config (k, v) VALUES ('qqpay_key', '');
MERGE INTO yixi_config (k, v) VALUES ('pay_notify_url', '');

-- 签到表 - 对应 yixi_qiandao
CREATE TABLE IF NOT EXISTS yixi_qiandao (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid BIGINT NOT NULL,
  date DATE NOT NULL,
  reward DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  addtime TIMESTAMP NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_qiandao_uid_date ON yixi_qiandao(uid, date);

-- 工单表 - 对应 yixi_workorder
CREATE TABLE IF NOT EXISTS yixi_workorder (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  reply TEXT,
  status INT NOT NULL DEFAULT 0,
  addtime TIMESTAMP NOT NULL,
  replytime TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_workorder_uid ON yixi_workorder(uid);
CREATE INDEX IF NOT EXISTS idx_workorder_status ON yixi_workorder(status);

-- 邀请返利表 - 对应 yixi_invitelog
CREATE TABLE IF NOT EXISTS yixi_invitelog (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid BIGINT NOT NULL,
  qq VARCHAR(64),
  type VARCHAR(32),
  money DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  bz VARCHAR(255),
  creation_time TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_invitelog_uid ON yixi_invitelog(uid);

-- 积分记录表 - 对应 yixi_points
CREATE TABLE IF NOT EXISTS yixi_points (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid BIGINT NOT NULL,
  point DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  order_id VARCHAR(64),
  action VARCHAR(32),
  addtime TIMESTAMP NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_points_uid ON yixi_points(uid);

-- 提现表 - 对应 yixi_tixian
CREATE TABLE IF NOT EXISTS yixi_tixian (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  uid BIGINT NOT NULL,
  account VARCHAR(128) NOT NULL,
  name VARCHAR(64) NOT NULL,
  money DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  realmoney DECIMAL(10,2),
  status INT NOT NULL DEFAULT 0,
  type VARCHAR(32),
  addtime TIMESTAMP NOT NULL,
  endtime TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tixian_uid ON yixi_tixian(uid);
CREATE INDEX IF NOT EXISTS idx_tixian_status ON yixi_tixian(status);

-- 用户表 - 对应 PHP yixi_user
CREATE TABLE IF NOT EXISTS yixi_user (
  uid INT AUTO_INCREMENT PRIMARY KEY,
  user VARCHAR(64) NOT NULL,
  pwd VARCHAR(64) NOT NULL,
  rmb DECIMAL(10,2) DEFAULT 0.00,
  qq VARCHAR(20) DEFAULT NULL,
  email VARCHAR(100) DEFAULT NULL,
  ip VARCHAR(45) DEFAULT NULL,
  regdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_yixi_user ON yixi_user(user);

-- Default admin config (matching PHP install/simple.sql)
-- IMPORTANT: Change admin_pwd immediately after first login!
-- Generate hash with: echo -n "your_password!@#%!s!0" | md5sum
MERGE INTO yixi_config (k, v) VALUES ('admin_user', 'admin');
MERGE INTO yixi_config (k, v) VALUES ('admin_pwd', 'PLEASE_SET_VIA_INSTALL_SCRIPT');
MERGE INTO yixi_config (k, v) VALUES ('sitename', '极简云验证');
MERGE INTO yixi_config (k, v) VALUES ('kfqq', '123456');
MERGE INTO yixi_config (k, v) VALUES ('template', 'default');
MERGE INTO yixi_config (k, v) VALUES ('checkin_reward', '0.1');
MERGE INTO yixi_config (k, v) VALUES ('access_token', '');
MERGE INTO yixi_config (k, v) VALUES ('adminlogin', '');
MERGE INTO yixi_config (k, v) VALUES ('admin_login_open', '1');
MERGE INTO yixi_config (k, v) VALUES ('admin_qq', '');
MERGE INTO yixi_config (k, v) VALUES ('admin_qqloginsm_open', '1');
MERGE INTO yixi_config (k, v) VALUES ('admin_qqlogin_open', '0');
MERGE INTO yixi_config (k, v) VALUES ('admin_remote_login_open', '1');
MERGE INTO yixi_config (k, v) VALUES ('admin_send_type', '0');
MERGE INTO yixi_config (k, v) VALUES ('defendid', '3');
MERGE INTO yixi_config (k, v) VALUES ('description', '极简云验证提供应用管理，API数据接口调用服务平台');
MERGE INTO yixi_config (k, v) VALUES ('email', '');
MERGE INTO yixi_config (k, v) VALUES ('email_temp', '1');
MERGE INTO yixi_config (k, v) VALUES ('footer', '');
MERGE INTO yixi_config (k, v) VALUES ('gxqm', '');
MERGE INTO yixi_config (k, v) VALUES ('icp', '');
MERGE INTO yixi_config (k, v) VALUES ('index_notice', '');
MERGE INTO yixi_config (k, v) VALUES ('index_open', '1');
MERGE INTO yixi_config (k, v) VALUES ('invite_money', '1');
MERGE INTO yixi_config (k, v) VALUES ('invite_rebate_open', '0');
MERGE INTO yixi_config (k, v) VALUES ('invite_time', 'week');
MERGE INTO yixi_config (k, v) VALUES ('keywords', '极简云验证提供应用管理，API数据接口调用服务平台');
MERGE INTO yixi_config (k, v) VALUES ('orgname', '');
MERGE INTO yixi_config (k, v) VALUES ('phone', '');
MERGE INTO yixi_config (k, v) VALUES ('qunhao', '');
MERGE INTO yixi_config (k, v) VALUES ('tenpay_api', '2');
MERGE INTO yixi_config (k, v) VALUES ('title', '不止于此');
MERGE INTO yixi_config (k, v) VALUES ('ui_background', '3');
MERGE INTO yixi_config (k, v) VALUES ('version', '2001');

-- 应用用户关联表 - 对应 yixi_appuser
CREATE TABLE IF NOT EXISTS yixi_appuser (
  id INT AUTO_INCREMENT PRIMARY KEY,
  appid INT NOT NULL,
  uid INT NOT NULL,
  status VARCHAR(1) DEFAULT 'y',
  addtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_appuser_appid ON yixi_appuser(appid);
CREATE INDEX IF NOT EXISTS idx_appuser_uid ON yixi_appuser(uid);

-- 用户接口关联表 - 对应 yixi_userjk
CREATE TABLE IF NOT EXISTS yixi_userjk (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uid INT NOT NULL,
  appid INT NOT NULL,
  api_name VARCHAR(50) DEFAULT NULL,
  status VARCHAR(1) DEFAULT 'y',
  addtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_userjk_uid ON yixi_userjk(uid);
CREATE INDEX IF NOT EXISTS idx_userjk_appid ON yixi_userjk(appid);

-- 站点/分站表 - 对应 yixi_site
CREATE TABLE IF NOT EXISTS yixi_site (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uid INT NOT NULL,
  domain VARCHAR(200) DEFAULT NULL,
  sitename VARCHAR(100) DEFAULT NULL,
  endtime TIMESTAMP DEFAULT NULL,
  status VARCHAR(1) DEFAULT 'y',
  addtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_site_uid ON yixi_site(uid);

-- 程序/产品表 - 对应 yixi_program
CREATE TABLE IF NOT EXISTS yixi_program (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  version VARCHAR(50) DEFAULT NULL,
  download_url TEXT,
  status VARCHAR(1) DEFAULT 'y',
  addtime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 平台消息表 - 对应 yixi_message（管理员发布的平台通知消息）
CREATE TABLE IF NOT EXISTS yixi_message (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  type VARCHAR(20) DEFAULT 'system',
  content TEXT,
  addtime DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);
