truncate table `customer`.`customer`;
truncate table `customer`.`voucher`;
truncate table `system`.`institution`;
truncate table `system`.`sys_info`;
truncate table `account`.`points_type`;
truncate table `account`.`points_account_info`;
truncate table `account`.`points_trans`;
truncate table `account`.`points_trans_details`;
truncate table `account`.`points_trans_expire`;
truncate table `account`.`points_trans_temp`;

insert into `system`.`institution`(`institution_id`, `institution_no`, `institution_name`, `parent_institution_id`,`top_institution_id`, `description`)
VALUES  ('844669109470756864', '100000', '测试顶级机构', '0', '0', '接口测试顶级机构'),
        ('844700918006939650', '100005', '无积分顶级机构', '0', '0', '无积分类型挂靠的顶级机构');
INSERT INTO `system`.`sys_info`(`custno`, `type`, `institution`, `custaddress`, `custrequestip`, `privatekey`, `publickey`,
                       `threedeskey`, `switchconf`)
VALUES ('8888888', '1', '100000', NULL, NULL,
        'jEVuAdhci/fWjpX19OlATmVeDFYPfLS9jkoXLp/gUVDspbVStaoh2S4l42Lwn3eBVAhnXHCiWSbba4h3EIq2Smc4R+35reXkIQvjnHUhefyoL7kriFNJsSyktM4p/8lQXYTWylg9ft+GzWE6Hrj/JYaUACF38dp6y6reellZbUQ04pqloUJ++FZ8dztB5Fzw0gtAbmQzecN5+IgWIfsdjjTmDR1MKf2Cmsws7ukj4H1uqrgcma+p2Rv2MvEug4Y8b3unUjALL58o4I82je9v+A2KRTlK41z2Thsl/NYb+a2yWxsFniCywTLhCKZF8B6sq3T/2Dbad9Pjo/e0+M3VT3w9IqKSFa+h982vP2zf3aoHC6YjiTutOxd9ZDoQUMygVJPGh3GBCOvtNg0pWwBGWL71dZktGqp91fP3FuBt4hqVq/7GmCIJjo2HtnUs6J+8zpcJwz9UnsAOG9lOil7h9vOkybNPeowF2Fz7NXbSkR1HDRqTV12RU1IDXT6W1bRvBXMgq7YCGkXW/WKQ8qO/XTLYZQYkk9nDTfWL341Xucq1R6yltb3YI7axliX2mcckzpCPbMXmeIFwi/Wjbhug7MpakN4vck+TEzK/HSHJ+zBmhLHHKuORaZu474tD0IRTxIJlJHbctc+F3mtCnVZYjl6qSdw8ym61UQnS/O6pkbAKqscQYVtDmVlkVT/QcSxGPPPqrytLvh5Fis/rgFTIi8aFlH8Q+Eixt7VLPdbkziH/sk1OjcKbX0c4t2Q0vDiec6oZ7wiBkym1rvGjnKNU4EQmWDqAoVeSLrBfjhUn9ohj6qSkLBYRrsEmYen1JomiE6UZuWcQCEshkqCJD4S4UQ==',
        'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZLAS7LGJtH1cK0yeQFXJwP4GsVaLItiHM6VyW+RTVHY+GidXQ7hg5aztu7LynlZ1mE075USeILrwgsh7cvCQdX9L06ryEu01u3t26x19rdi4vTdRSZ0cDhxEzPDVtoSRayP6ou4S7F6V+khEwGXKLeszJONricVU9KBi7t1KtYwIDAQAB',
        '786212346632999728488777', 'rest:');

insert into `account`.`points_type`(`points_type_no`, `points_type_name`, `institution_id`, `rate`, `description`)
VALUES ('844700918006939648', '类型1', '844669109470756864', 1, NULL),
       ('844700918006939649', '类型2', '844669109470756864', 1, NULL),
       ('844703788584402944', '类型3', '844669109470756864', 1, NULL),
       ('879857062924779520', '类型4', '844669109470756864', 1, NULL);
