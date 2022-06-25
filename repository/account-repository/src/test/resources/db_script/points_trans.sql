truncate table points_trans;
insert into `points_trans`(`trans_no`, `customer_id`, `points_type_no`, `institution_id`, `trans_date`,
                                `trans_time`,`trans_timestamp`, `trans_type_no`, `debit_or_credit`, `end_date`, `points_amount`,
                                `reversed_flag`, `old_trans_no`, `trans_channel`, `merchant_no`, `voucher_type_no`,
                                `voucher_no`, `trans_status`, `description`, `operator`, `sys_trans_no`, `rules_id`,
                                `cost_line`, `clearing_amt`)
VALUES ('849779142810402818', '844703788571820032', '844700918006939648', 844669109470756864, '20210610', '223215',123456,
        '1006', 'C', '2031-06-10 22:32:15', 10, '0', NULL, '001', NULL, 'cust', '844703788571820032', '0', NULL,
        'system', '880527837759537152', NULL, NULL, 0);