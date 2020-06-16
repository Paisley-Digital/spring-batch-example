create table ORDERS
(
    ID            INT auto_increment
        primary key,
    ORDER_ID      VARCHAR(20)   not null,
    ORDER_DATE    DATE          not null,
    PRODUCT_NAME  VARCHAR(255)
);
