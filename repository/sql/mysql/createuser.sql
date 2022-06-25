create user 'points_3'@'%' identified by 'points123!@#';

create schema account default character set utf8mb4 collate utf8mb4_general_ci;
grant select,insert,update,delete,create,alter,drop on account.* to points_3;

create schema customer default character set utf8mb4 collate utf8mb4_general_ci;
grant select,insert,update,delete,create,alter,drop on customer.* to points_3;

create schema `system` default character set utf8mb4 collate utf8mb4_general_ci;
grant select,insert,update,delete,create,alter,drop on `system`.* to points_3;

create schema `merchant` default character set utf8mb4 collate utf8mb4_general_ci;
grant select,insert,update,delete,create,alter,drop on `merchant`.* to points_3;
