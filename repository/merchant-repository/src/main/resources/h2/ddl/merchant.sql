CREATE TABLE `merchant` (`id` bigint(20) NOT NULL AUTO_INCREMENT,`merchant_no` varchar(32) NOT NULL COMMENT '商户编号',`merchant_name` varchar(128) NULL DEFAULT '' COMMENT '商户姓名',`contacts` varchar(64) NULL DEFAULT '' COMMENT '联系人姓名',`phone` varchar(32) NULL DEFAULT NULL COMMENT '联系号码',`email` varchar(64) NULL DEFAULT NULL COMMENT '电子邮箱',`address` varchar(256) NULL DEFAULT NULL COMMENT '住址',`institution_id` varchar(32) NULL DEFAULT NULL COMMENT '机构ID',`operator` varchar(64) NULL DEFAULT 'system' COMMENT '操作员',`status` char(1) NOT NULL DEFAULT 0 COMMENT '商户状态 0 可用 1 停用',`create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',`create_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '创建角色',`update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`update_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '修改角色',PRIMARY KEY (`id`),UNIQUE KEY (`merchant_no` ASC),INDEX IDX_INSTITUTION_ID_0 (`institution_id` ASC));
CREATE TABLE `merchant_clearing` (`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',`merchant_no` varchar(12) NULL DEFAULT NULL COMMENT '商户号',`trans_no` varchar(32) NOT NULL COMMENT '交易流水',`trans_date` varchar(8) NOT NULL COMMENT '交易日期',`trans_time` varchar(8) NOT NULL COMMENT '交易时间',`old_trans_no` varchar(32) NULL DEFAULT NULL COMMENT '原始交易流水',`sys_trans_no` varchar(64) NULL DEFAULT NULL COMMENT '前置交易流水',`points_type_no` VARCHAR(32) NOT NULL COMMENT '积分类型',`points_amount` numeric(32,2) NOT NULL COMMENT '交易积分额',`institution_id` varchar(32) NOT NULL COMMENT '机构ID',`reversed_flag` char(1) NOT NULL DEFAULT '0' COMMENT '撤销状态 0 非撤销 1 撤销交易',`clearing_amt` numeric(16,2) NOT NULL DEFAULT 0 COMMENT '清算金额 分单位',`description` text NULL COMMENT '交易描述',`create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`create_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '创建角色',`update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,`update_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '修改角色',PRIMARY KEY (`id`),UNIQUE KEY (`trans_no`),INDEX PK_CLEARING_TRANS_0 (`trans_no` ASC,`trans_date` ASC),INDEX IDX_CLEARING_MERCHANT_NO_0 (`merchant_no` ASC));