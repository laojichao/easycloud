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

-- Default admin config
MERGE INTO yixi_config (k, v) VALUES ('admin_user', 'admin');
MERGE INTO yixi_config (k, v) VALUES ('admin_pwd', '123456');
MERGE INTO yixi_config (k, v) VALUES ('sitename', '极简云验证');
MERGE INTO yixi_config (k, v) VALUES ('kfqq', '123456');
MERGE INTO yixi_config (k, v) VALUES ('template', 'default');
