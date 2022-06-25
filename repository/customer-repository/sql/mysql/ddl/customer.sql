create TABLE `customer`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `customer_id`    varchar(32) NOT NULL COMMENT '客户ID',
    `customer_name`  varchar(128) NULL DEFAULT '' COMMENT '客户姓名',
    `gender`         char(1) NULL DEFAULT '2' COMMENT '客户性别 0 男 1 女 2 未知',
    `phone`          varchar(32) NULL DEFAULT NULL COMMENT '联系号码',
    `email`          varchar(64) NULL DEFAULT NULL COMMENT '电子邮箱',
    `address`        varchar(256) NULL DEFAULT NULL COMMENT '住址',
    `birthdate`      varchar(8) NULL DEFAULT NULL COMMENT '生日',
    `institution_id` varchar(32) NULL DEFAULT NULL COMMENT '机构ID',
    `operator`       varchar(64) NULL DEFAULT 'system' COMMENT '操作员',
    `customer_lvl`   char(1) NULL DEFAULT NULL COMMENT '客户等级',
    `create_date`    datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_oper`    varchar(16) NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`    varchar(16) NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`, `customer_id`),
    UNIQUE INDEX (`customer_id` ASC) USING BTREE,
    INDEX            `idx_phone` (`phone` ASC) USING BTREE
)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COMMENT = '客户信息表';