Create Database  DWH;
Use DWH;

drop table if exists `fact_table`;
drop table if exists `cust_dim`;
drop table if exists `prod_dim`;
drop table if exists `store_dim`;
drop table if exists `supplier_dim`;
drop table if exists `time_dim`;

CREATE TABLE prod_dim(
	prod_id VARCHAR(50) PRIMARY KEY,
	prod_name VARCHAR(50),
    price float
);

CREATE TABLE cust_dim(
	cust_id VARCHAR(50) PRIMARY KEY,
    cust_name VARCHAR(50)
    
);

CREATE TABLE store_dim(
	store_id VARCHAR(50) PRIMARY KEY, 
	store_name VARCHAR(50)
);

CREATE TABLE supplier_dim(
	supplier_id VARCHAR(50) PRIMARY KEY,
    supplier_name VARCHAR(50)
);

CREATE TABLE time_dim(
	time_id VARCHAR(50) PRIMARY KEY,
    time date
);


CREATE TABLE fact_table(
	transaction_id INT auto_increment,
	prod_id VARCHAR(50),
    cust_id VARCHAR(50),
    store_id VARCHAR(50),
    supplier_id VARCHAR(50),
    time_id VARCHAR(50),
    quantity int,
    sale float,
    PRIMARY KEY(transaction_id,prod_id,cust_id,store_id,supplier_id,time_id),
     FOREIGN KEY (prod_id) REFERENCES prod_dim(prod_id),
      FOREIGN KEY (cust_id) REFERENCES cust_dim(cust_id),
       FOREIGN KEY (store_id) REFERENCES store_dim(store_id),
        FOREIGN KEY (supplier_id) REFERENCES supplier_dim(supplier_id),
         FOREIGN KEY (time_id) REFERENCES time_dim(time_id)
);
