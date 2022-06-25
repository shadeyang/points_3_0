create TABLE `points_account_info`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `customer_id`           varchar(32)    NOT NULL COMMENT '客户ID',
    `points_type_no`        VARCHAR(32)    NOT NULL COMMENT '积分类型',
    `points_balance`        numeric(32, 2) NOT NULL DEFAULT 0 COMMENT '积分余额',
    `freezing_points`       numeric(32, 2) NOT NULL DEFAULT 0 COMMENT '冻结余额',
    `in_transit_points`     numeric(32, 2) NOT NULL DEFAULT 0 COMMENT '在途余额',
    `points_account_status` char(1)        NOT NULL DEFAULT 0 COMMENT '账户状态 0 可用 1 停用',
    `create_date`           datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`           varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`           datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`           varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_custid_pt` (`customer_id` ASC, `points_type_no` ASC)
)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '积分账户表';
create TABLE `points_trans_expire`
(
    `id`                    bigint(20)     NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `customer_id`           varchar(32)    NOT NULL COMMENT '客户ID',
    `points_type_no`        VARCHAR(32)    NOT NULL COMMENT '积分类型',
	`trans_no`              VARCHAR(32)    NOT NULL COMMENT '积分交易流水',
	`end_date`				datetime	   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '积分过期时间',
    `create_date`           datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`           varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`           datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`           varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    INDEX `idx_custid_pt` (`customer_id` ASC, `points_type_no` ASC, `end_date` ASC, `trans_no` ASC),
	INDEX `idx_end` (`end_date` ASC)
)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '积分到期流水明细表';
create TABLE `points_trans`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `trans_no`        varchar(32)    NOT NULL COMMENT '交易流水',
    `customer_id`     varchar(32)    NOT NULL COMMENT '客户ID',
    `points_type_no`  VARCHAR(32)    NOT NULL COMMENT '积分类型',
    `institution_id`  varchar(32)    NOT NULL COMMENT '机构ID',
    `trans_date`      varchar(8)     NOT NULL COMMENT '交易日期',
    `trans_time`      varchar(8)     NOT NULL COMMENT '交易时间',
    `trans_timestamp` bigint(20) NOT NULL COMMENT '交易时间戳',
    `trans_type_no`   varchar(10)    NOT NULL COMMENT '交易类型',
    `debit_or_credit` char(1)        NOT NULL COMMENT '借贷标志 C借 D贷',
    `end_date`        datetime NULL DEFAULT NULL COMMENT '积分过期时间',
    `points_amount`   numeric(32, 2) NOT NULL COMMENT '交易积分额',
    `reversed_flag`   char(1)        NOT NULL DEFAULT '0' COMMENT '撤销状态 0 非撤销 1 撤销交易',
    `old_trans_no`    varchar(32) NULL DEFAULT NULL COMMENT '原始交易流水',
    `trans_channel`   varchar(5)     NOT NULL DEFAULT '001' COMMENT '交易渠道 001 系统',
    `merchant_no`     varchar(12) NULL DEFAULT NULL COMMENT '商户号',
    `voucher_type_no` varchar(8)     NOT NULL COMMENT '凭证类型',
    `voucher_no`      varchar(64)    NOT NULL COMMENT '凭证号码',
    `trans_status`    char(1)        NOT NULL DEFAULT '0' COMMENT '交易状态 0 成功 1 失败',
    `description`     text NULL COMMENT '交易描述',
    `operator`        varchar(64)    NOT NULL DEFAULT 'system' COMMENT '操作员编号',
    `sys_trans_no`    varchar(64) NULL DEFAULT NULL COMMENT '前置交易流水',
    `rules_id`        int(65) NULL DEFAULT NULL COMMENT '积分规则id',
    `cost_line`       varchar(32) NULL DEFAULT NULL COMMENT '成本负担线',
    `clearing_amt`    numeric(16, 2) NOT NULL DEFAULT 0 COMMENT '清算金额 分单位',
    `create_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`     varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`     varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`trans_no`),
    INDEX             `pk_pointstrans` (`trans_no` ASC, `trans_date` ASC) USING BTREE,
    INDEX             `idx_pointstrans_customers` (`customer_id` ASC) USING BTREE,
    INDEX             `idx_pointstrans_oldtransno` (`old_trans_no` ASC, `trans_status` ASC) USING BTREE,
    INDEX             `idx_pointstrans_typeno` (`points_type_no` ASC, `trans_date` ASC, `reversed_flag` ASC) USING BTREE,
    INDEX             `idx_pointstrans_transdate` (`trans_date` ASC, `customer_id` ASC) USING BTREE,
    INDEX             `idx_pointstrans_reversedflag` (`reversed_flag` ASC, `old_trans_no` ASC) USING BTREE,
    INDEX             `idx_pointstrans_systransno` (`sys_trans_no` ASC) USING BTREE
)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '积分流水表';

create TABLE `points_trans_temp`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `trans_no`        varchar(32)    NOT NULL COMMENT '交易流水',
    `customer_id`     varchar(32)    NOT NULL COMMENT '客户ID',
    `points_type_no`  VARCHAR(32)    NOT NULL COMMENT '积分类型',
    `institution_id`  varchar(32)    NOT NULL COMMENT '机构ID',
    `trans_date`      varchar(8)     NOT NULL COMMENT '交易日期',
    `trans_time`      varchar(8)     NOT NULL COMMENT '交易时间',
    `trans_type_no`   varchar(10)    NOT NULL COMMENT '交易类型',
    `debit_or_credit` char(1)        NOT NULL COMMENT '借贷标志 C借 D贷',
    `end_date`        datetime NULL DEFAULT NULL COMMENT '积分过期时间',
    `points_amount`   numeric(32, 2) NOT NULL COMMENT '交易积分额',
    `reversed_flag`   char(1)        NOT NULL DEFAULT '0' COMMENT '撤销状态 0 非撤销 1 撤销交易',
    `old_trans_no`    varchar(32) NULL DEFAULT NULL COMMENT '原始交易流水',
    `trans_channel`   varchar(5)     NOT NULL DEFAULT '001' COMMENT '交易渠道 001 系统',
    `merchant_no`     varchar(12) NULL DEFAULT NULL COMMENT '商户号',
    `voucher_type_no` varchar(8)     NOT NULL COMMENT '凭证类型',
    `voucher_no`      varchar(64)    NOT NULL COMMENT '凭证号码',
    `trans_status`    char(1)        NOT NULL DEFAULT '0' COMMENT '交易状态 0 成功 1 失败',
    `description`     text NULL COMMENT '交易描述',
    `operator`        varchar(64)    NOT NULL DEFAULT 'system' COMMENT '操作员编号',
    `sys_trans_no`    varchar(64) NULL DEFAULT NULL COMMENT '前置交易流水',
    `rules_id`        int(65) NULL DEFAULT NULL COMMENT '积分规则id',
    `cost_line`       varchar(32) NULL DEFAULT NULL COMMENT '成本负担线',
    `clearing_amt`    numeric(16, 2) NOT NULL DEFAULT 0 COMMENT '清算金额 分单位',
    `trans_flag`      char(1)        NOT NULL DEFAULT '0' COMMENT '账务处理标志 0 待处理 1 已处理',
    `process_date`    datetime NULL COMMENT '处理时间',
    `create_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`     varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`     varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`trans_no`),
    INDEX             `index_trans_flag` (`trans_flag` ASC) USING BTREE
)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '积分流水异步处理中间临时表';

create TABLE `points_trans_details`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `trans_no`        varchar(32)    NOT NULL COMMENT '交易流水',
    `customer_id`     varchar(32)    NOT NULL COMMENT '客户ID',
    `source_trans_no` varchar(32)    NOT NULL COMMENT '来源交易流水',
    `points_type_no`  VARCHAR(32)    NOT NULL COMMENT '积分类型',
    `end_date`        datetime       NOT NULL COMMENT '积分过期时间',
    `points_amount`   numeric(32, 2) NOT NULL COMMENT '交易积分额',
    `merchant_no`     varchar(12) NULL DEFAULT NULL COMMENT '商户号',
    `cost_line`       varchar(32) NOT NULL COMMENT '成本负担线',
    `clearing_amt`    numeric(16, 2) NOT NULL DEFAULT 0 COMMENT '清算金额 分单位',
    `create_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`     varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`     varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    INDEX             `idx_pointstransdetails_customers` (`customer_id` ASC) USING BTREE,
    INDEX             `idx_pointstransdetails_transno` (`trans_no` ASC, `source_trans_no` ASC) USING BTREE,
    INDEX             `idx_pointstransdetails_sourcetransno` (`source_trans_no` ASC) USING BTREE,
    INDEX             `idx_pointstransdetails_typeno` (`points_type_no` ASC) USING BTREE
)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '积分流水明细表';

create TABLE `points_type`
(
    `id`               INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `points_type_no`   VARCHAR(32)    NOT NULL COMMENT '积分类型编号',
    `points_type_name` VARCHAR(64)    NOT NULL COMMENT '积分类型名称',
    `institution_id`   VARCHAR(32)    NOT NULL COMMENT '机构ID',
    `rate`             NUMERIC(32, 2) NOT NULL DEFAULT 1 COMMENT '积分价值率',
    `description`      VARCHAR(50)             DEFAULT NULL COMMENT '描述',
    `create_date`      datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`      varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`      datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`      varchar(16)    NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE KEY ( `points_type_no` )
)
ENGINE = INNODB
DEFAULT CHARSET = utf8mb4
COMMENT = '积分类型表';

CREATE TABLE `points_cost`
(
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cost_line` varchar(32) NOT NULL COMMENT '成本负担线',
  `institution_id` varchar(32) NOT NULL COMMENT '机构ID',
  `cost_name` varchar(32) NOT NULL COMMENT '成本负担名',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `create_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '创建角色',
  `update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '修改角色',
  PRIMARY KEY (`id`),
  KEY `idx_cost_line` (`cost_line`) USING BTREE,
  UNIQUE KEY ( `institution_id`,`cost_line` )
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COMMENT='积分成本设置表';
