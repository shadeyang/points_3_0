truncate table points_account_info;

insert into `points_account_info`(`customer_id`, `points_type_no`, `points_balance`, `freezing_points`,
                                  `in_transit_points`, `points_account_status`)
VALUES ('844703788571820032', '844700918006939648', 100, 20, 30, '0'),
       ('844703788571820032', '844700918006939649', 200, 20, 0, '0');