create TABLE `merchant`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `merchant_no`    varchar(32) NOT NULL comment '商户编号',
    `merchant_name`  varchar(128) NULL DEFAULT '' comment '商户姓名',
    `contacts`       varchar(64) NULL DEFAULT '' comment '联系人姓名',
    `phone`          varchar(32) NULL DEFAULT NULL comment '联系号码',
    `email`          varchar(64) NULL DEFAULT NULL comment '电子邮箱',
    `address`        varchar(256) NULL DEFAULT NULL comment '住址',
    `institution_id` varchar(32) NULL DEFAULT NULL comment '机构ID',
    `operator`       varchar(64) NULL DEFAULT 'system' comment '操作员',
    `status`         char(1)     NOT NULL DEFAULT 0 comment '商户状态 0 可用 1 停用',
    `create_date`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    `create_oper`    varchar(16) NOT NULL DEFAULT 'sys' comment '创建角色',
    `update_date`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`    varchar(16) NOT NULL DEFAULT 'sys' comment '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`merchant_no` ASC) USING BTREE,
    INDEX  `idx_institution_id` (`institution_id` ASC) USING BTREE
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
comment = '商户信息表';

create TABLE `merchant_clearing`
(
    `id`              bigint(20)     NOT NULL AUTO_INCREMENT comment 'ID',
		`merchant_no`     varchar(12) NULL DEFAULT NULL comment '商户号',
    `trans_no`        varchar(32)    NOT NULL comment '交易流水',
		`trans_date`      varchar(8)     NOT NULL comment '交易日期',
    `trans_time`      varchar(8)     NOT NULL comment '交易时间',
		`old_trans_no`    varchar(32) NULL DEFAULT NULL comment '原始交易流水',
		`sys_trans_no`    varchar(64) NULL DEFAULT NULL comment '前置交易流水',
		`points_type_no`  VARCHAR(32)    NOT NULL comment '积分类型',
		`points_amount`   numeric(32, 2) NOT NULL comment '交易积分额',
		`institution_id`  varchar(32)    NOT NULL comment '机构ID',
		`reversed_flag`   char(1)        NOT NULL DEFAULT '0' comment '撤销状态 0 非撤销 1 撤销交易',
		`clearing_amt`    numeric(16, 2) NOT NULL DEFAULT 0 comment '清算金额 分单位',
		`description`     text NULL comment '交易描述',
    `create_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `create_oper`     varchar(16)    NOT NULL DEFAULT 'sys' comment '创建角色',
    `update_date`     datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`     varchar(16)    NOT NULL DEFAULT 'sys' comment '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE INDEX (`trans_no`),
    INDEX             `idx_clearing_trans_date` (`trans_date` ASC) USING BTREE,
    INDEX             `idx_clearing_merchant_no` (`merchant_no` ASC) USING BTREE
)
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8mb4
comment = '商户清算表';