create TABLE `sys_info`
(
    `id`            int(11) NOT NULL AUTO_INCREMENT comment 'ID',
    `custno`        varchar(255) NOT NULL comment '商户号',
    `type`          char(1)      NOT NULL DEFAULT '1' comment '接入类型 1 积分 2 电商',
    `institution`   varchar(16)  NOT NULL comment '接入机构编号',
    `custaddress`   varchar(255)          DEFAULT NULL comment '银行服务地址',
    `custrequestip` varchar(255)          DEFAULT NULL comment '银行请求服务地址',
    `privatekey`    varchar(4096)         DEFAULT NULL comment '接收服务公司端私钥',
    `publickey`     varchar(4096)         DEFAULT NULL comment '请求服务银行端公钥',
    `threedeskey`   varchar(32)           DEFAULT NULL comment '3des密钥',
    `switchconf`    varchar(4096)         DEFAULT NULL comment '控制服务开关 域:服务,服务|域:|',
    `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_institution` (`institution`)
)
ENGINE=InnoDB
AUTO_INCREMENT=1
DEFAULT CHARSET=utf8
comment='接入配置表';

create TABLE `sys_dictionary` (
  `id` int(11) NOT NULL AUTO_INCREMENT comment 'ID',
  `dict_key` varchar(128) NOT NULL comment '字典键',
  `dict_lable` varchar(128) NOT NULL comment '字典标签',
  `dict_desc` varchar(255) NOT NULL comment '参数描述',
  `dict_value` varchar(1024) DEFAULT NULL comment '字典标签值',
  `dict_index` int(11) NOT NULL DEFAULT '0' comment '显示顺序',
  `dict_status` char(1) NOT NULL DEFAULT '0' comment '字典标签状态 0 可用 1 停用',
  `dict_module` varchar(16) DEFAULT 'common' comment '字典标签作用应用',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key_lable` (`dict_key`,`dict_lable`,`dict_module`),
  KEY `index_module_status` (`dict_module`,`dict_status`)
)
ENGINE=InnoDB
AUTO_INCREMENT=1
DEFAULT CHARSET=utf8
comment='字典表';

create TABLE `sys_machine` (
  `id` int(11) NOT NULL AUTO_INCREMENT comment 'ID',
  `machine` varchar(128) NOT NULL comment '接入设备',
  `module` varchar(128) NOT NULL comment '接入模块应用',
  `node` varchar(128) DEFAULT NULL comment '接入节点配置',
  `type` varchar(255) NOT NULL comment '接入方式',
  `describe` varchar(1024) CHARACTER SET utf8mb4 DEFAULT NULL,
  `lasttime` varchar(20) DEFAULT NULL comment '最后接入时间',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_machine` (`machine`,`module`)
)
ENGINE=InnoDB
AUTO_INCREMENT=1
DEFAULT CHARSET=utf8
comment='设备表';

create TABLE `sys_parameter` (
  `id` int(11) NOT NULL AUTO_INCREMENT comment 'ID',
  `param_key` varchar(128) NOT NULL comment '参数键',
  `param_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_croatian_ci NOT NULL comment '参数描述',
  `param_value1` varchar(1024) DEFAULT NULL comment '参数值1',
  `param_value2` varchar(1024) DEFAULT NULL comment '参数值2',
  `param_value3` varchar(1024) DEFAULT NULL comment '参数值3',
  `param_status` char(1) NOT NULL DEFAULT '0' comment '参数状态 0 可用 1 停用',
  `param_module` varchar(16) DEFAULT 'common' comment '参数作用应用',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key` (`param_key`,`param_module`),
  KEY `index_module_status` (`param_module`,`param_status`)
)
ENGINE=InnoDB
AUTO_INCREMENT=1
DEFAULT CHARSET=utf8
comment='参数表';
