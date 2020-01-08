DROP TABLE IF EXISTS region CASCADE;
DROP TABLE IF EXISTS nation CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;

create table region (
   r_regionkey int not null,
   r_name char(55) not null,
   r_comment char(152) not null,
   PRIMARY KEY ( r_regionkey )
);
alter table region set tiflash replica 1;

create table nation (
   n_nationkey int not null,
   n_name char(25) not null,
   n_regionkey int not null,
   n_comment char(152) not null,
   PRIMARY KEY ( n_nationkey )
);
alter table nation set tiflash replica 1;

create table supplier (
   su_suppkey int not null,
   su_name char(25) not null,
   su_address varchar(40) not null,
   su_nationkey int not null,
   su_phone char(15) not null,
   su_acctbal numeric(12,2) not null,
   su_comment char(101) not null,
   PRIMARY KEY ( su_suppkey )
);
alter table supplier set tiflash replica 1;
