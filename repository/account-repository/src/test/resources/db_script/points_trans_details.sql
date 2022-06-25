truncate table points_trans_details;

insert into `points_trans_details`(`trans_no`, `customer_id`, `source_trans_no`, `points_type_no`, `end_date`,
                                   `points_amount`, `merchant_no`, `cost_line`, `clearing_amt`)
VALUES ('849779142793625600', '844703788571820032', '849779142793625600', '844700918006939648', '2021-06-10 22:32:15',
        20, NULL, 0, 0),
       ('849779142810402816', '844703788571820032', '849779142810402816', '844700918006939648', '2021-06-10 22:32:15',
        70, NULL, 0, 0);