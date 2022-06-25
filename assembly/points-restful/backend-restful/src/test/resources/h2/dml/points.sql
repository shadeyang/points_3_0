insert into `points_type`(`points_type_no`, `points_type_name`, `institution_id`, `rate`, `description`)
VALUES ('844700918006939648', '类型1', '844669109470756864', 1, NULL),
       ('844700918006939649', '类型2', '844669109470756864', 1, NULL),
       ('844703788584402944', '类型3', '844669109470756864', 1, NULL),
       ('879857062924779520', '类型4', '844669109470756864', 1, NULL);

insert into `points_account_info`(`customer_id`, `points_type_no`, `points_balance`, `freezing_points`,
                                  `in_transit_points`, `points_account_status`)
VALUES ('844703788571820032', '844700918006939648', 100, 20, 30, '0'),
       ('844703788571820032', '844700918006939649', 200, 20, 0, '0');

insert into `points_trans`(`trans_no`, `customer_id`, `points_type_no`, `institution_id`, `trans_date`,
                                `trans_time`,`trans_timestamp`, `trans_type_no`, `debit_or_credit`, `end_date`, `points_amount`,
                                `reversed_flag`, `old_trans_no`, `trans_channel`, `merchant_no`, `voucher_type_no`,
                                `voucher_no`, `trans_status`, `description`, `operator`, `sys_trans_no`, `rules_id`,
                                `cost_line`, `clearing_amt`)
VALUES ('849779142810402818', '844703788571820032', '844700918006939648', 844669109470756864, '20210610', '223215',123456,
        '1006', 'C', '2031-06-10 22:32:15', 10, '0', NULL, '001', NULL, 'cust', '844703788571820032', '0', NULL,
        'system', '880527837759537152', NULL, NULL, 0);

insert into `points_trans_temp`(`trans_no`, `customer_id`, `points_type_no`, `institution_id`, `trans_date`,
                                `trans_time`, `trans_type_no`, `debit_or_credit`, `end_date`, `points_amount`,
                                `reversed_flag`, `old_trans_no`, `trans_channel`, `merchant_no`, `voucher_type_no`,
                                `voucher_no`, `trans_status`, `description`, `operator`, `sys_trans_no`, `rules_id`,
                                `cost_line`, `clearing_amt`, `trans_flag`, `process_date`)
VALUES ('849779142810402818', '844703788571820032', '844700918006939648', 844669109470756864, '20210610', '223215',
        '1006', 'C', '2031-06-10 22:32:15', 10, '0', NULL, '001', NULL, 'cust', '844703788571820032', '0', NULL,
        'system', NULL, NULL, NULL, 0, '0', NULL);

insert into `points_trans_details`(`trans_no`, `customer_id`, `source_trans_no`, `points_type_no`, `end_date`,
                                   `points_amount`, `merchant_no`, `cost_line`, `clearing_amt`)
VALUES ('849779142793625600', '844703788571820032', '849779142793625600', '844700918006939648', '2021-06-10 22:32:15',
        20, NULL, 0, 0),
       ('849779142810402816', '844703788571820032', '849779142810402816', '844700918006939648', '2021-06-10 22:32:15',
        70, NULL, 0, 0);

insert into `points_cost`(`cost_line`,`institution_id`,`cost_name`)
values ('1','844669109470756864','成本机构');