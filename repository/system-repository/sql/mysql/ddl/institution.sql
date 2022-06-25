create TABLE `institution` (
`id` INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`institution_id` VARCHAR ( 32 ) NOT NULL COMMENT '机构ID',
`institution_no` VARCHAR ( 32 ) NOT NULL COMMENT '机构编号',
`institution_name` VARCHAR ( 64 ) NOT NULL COMMENT '机构名称',
`parent_institution_id` VARCHAR ( 32 ) NOT NULL DEFAULT '0' COMMENT '父级机构ID',
`top_institution_id` VARCHAR ( 32 ) NOT NULL DEFAULT '0' COMMENT '顶级机构ID',
`description` VARCHAR ( 512 ) DEFAULT NULL COMMENT '机构描述',
`create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`create_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '创建角色',
`update_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
`update_oper` varchar(16) NOT NULL DEFAULT 'sys' COMMENT '修改角色',
PRIMARY KEY ( `id` ),
UNIQUE KEY ( `institution_no`, `top_institution_id` ),
KEY `idx_top_institution_id` ( `top_institution_id` )
)
ENGINE = INNODB
DEFAULT CHARSET = utf8
COMMENT = '机构信息表';