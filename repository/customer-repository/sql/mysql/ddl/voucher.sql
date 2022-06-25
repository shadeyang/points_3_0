create TABLE `voucher`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `customer_id`       VARCHAR(32) NOT NULL COMMENT '客户ID',
    `voucher_type_no`   VARCHAR(16) NOT NULL COMMENT '凭证类型',
    `voucher_no`        VARCHAR(64) NOT NULL COMMENT '凭证号码',
    `voucher_open_date` VARCHAR(20)          DEFAULT NULL COMMENT '凭证使用时间',
    `create_date`       datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_oper`       VARCHAR(16) NOT NULL DEFAULT 'sys' COMMENT '创建角色',
    `update_date`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    `update_oper`       VARCHAR(16) NOT NULL DEFAULT 'sys' COMMENT '修改角色',
    PRIMARY KEY (`id`),
    UNIQUE KEY ( `customer_id`, `voucher_type_no`, `voucher_no` ),
    KEY                 `idx_voucher_no` ( `voucher_no` )
)
ENGINE = INNODB
DEFAULT CHARSET = utf8mb4
COMMENT = '凭证信息表';