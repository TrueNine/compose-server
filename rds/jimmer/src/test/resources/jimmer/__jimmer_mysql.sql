create database jimmer_demo;
use jimmer_demo;

create table book_store
(
    id      bigint unsigned not null auto_increment primary key,
    name    varchar(50)     not null,
    website varchar(100)
) engine = innodb;
alter table book_store
    auto_increment = 100;
alter table book_store
    add constraint business_key_book_store
        unique (name)
;

create table book
(
    id       bigint unsigned not null auto_increment primary key,
    name     varchar(50)     not null,
    edition  integer         not null,
    price    numeric(10, 2)  not null,
    /* highlight-next-line */
    store_id bigint unsigned
) engine = innodb;
alter table book_store
    auto_increment = 100;
alter table book
    add constraint business_key_book
        unique (name, edition)
;
alter table book
    add constraint fk_book__book_store
        /* 从书籍指向书店的外键 */
        /* highlight-next-line */
        foreign key (store_id)
            references book_store (id)
;

create table author
(
    id            bigint unsigned not null auto_increment primary key,
    first_name    varchar(25)     not null,
    last_name     varchar(25)     not null,
    gender        char(1)         not null,
    created_time  datetime        not null,
    modified_time datetime        not null
) engine = innodb;
alter table author
    auto_increment = 100;
alter table author
    add constraint business_key_author
        unique (first_name, last_name)
;
alter table author
    add constraint ck_author_gender
        check (gender in ('M', 'F'));

/*
 * 两个实体之间的多对多关系，在ORM实现中，需要用数据库中间表实现。
 *
 * 此表存储书籍和作者之间的多对多关联，这是关联表，而非实体表，在上面的UML中并没有对应实体。
 */
/* highlight-next-line */
create table book_author_mapping
(
    book_id   bigint unsigned not null,
    author_id bigint unsigned not null
) engine = innodb;
alter table book_author_mapping
    add constraint pk_book_author_mapping
        primary key (book_id, author_id)
;
alter table book_author_mapping
    add constraint fk_book_author_mapping__book
        /* highlight-next-line */
        foreign key (book_id)
            references book (id)
            on delete cascade
;
alter table book_author_mapping
    add constraint fk_book_author_mapping__author
        /* highlight-next-line */
        foreign key (author_id)
            references author (id)
            on delete cascade
;

create table tree_node
(
    node_id   bigint unsigned not null auto_increment primary key,
    name      varchar(20)     not null,
    /* highlight-next-line */
    parent_id bigint unsigned
) engine = innodb;
alter table tree_node
    auto_increment = 100;
alter table tree_node
    add constraint business_key_tree_node
        unique (parent_id, name);
alter table tree_node
    add constraint fk_tree_node__parent
        /* 从子节点指向父节点的外键 */
        /* highlight-next-line */
        foreign key (parent_id)
            references tree_node (node_id);

insert into book_store(id, name)
values (1, 'O''REILLY'),
       (2, 'MANNING')
;

insert into book(id, name, edition, price, store_id)
values (1, 'Learning GraphQL', 1, 50, 1),
       (2, 'Learning GraphQL', 2, 55, 1),
       (3, 'Learning GraphQL', 3, 51, 1),

       (4, 'Effective TypeScript', 1, 73, 1),
       (5, 'Effective TypeScript', 2, 69, 1),
       (6, 'Effective TypeScript', 3, 88, 1),

       (7, 'Programming TypeScript', 1, 47.5, 1),
       (8, 'Programming TypeScript', 2, 45, 1),
       (9, 'Programming TypeScript', 3, 48, 1),

       (10, 'GraphQL in Action', 1, 80, 2),
       (11, 'GraphQL in Action', 2, 81, 2),
       (12, 'GraphQL in Action', 3, 80, 2)
;

insert into author(id, first_name, last_name, gender, created_time, modified_time)
values (1, 'Eve', 'Procello', 'F', now(), now()),
       (2, 'Alex', 'Banks', 'M', now(), now()),
       (3, 'Dan', 'Vanderkam', 'M', now(), now()),
       (4, 'Boris', 'Cherny', 'M', now(), now()),
       (5, 'Samer', 'Buna', 'M', now(), now())
;

insert into book_author_mapping(book_id, author_id)
values (1, 1),
       (2, 1),
       (3, 1),

       (1, 2),
       (2, 2),
       (3, 2),

       (4, 3),
       (5, 3),
       (6, 3),

       (7, 4),
       (8, 4),
       (9, 4),

       (10, 5),
       (11, 5),
       (12, 5)
;

insert into tree_node(node_id, name, parent_id)
values (1, 'Home', null),
       (2, 'Food', 1),
       (3, 'Drinks', 2),
       (4, 'Coca Cola', 3),
       (5, 'Fanta', 3),
       (6, 'Bread', 2),
       (7, 'Baguette', 6),
       (8, 'Ciabatta', 6),
       (9, 'Clothing', 1),
       (10, 'Woman', 9),
       (11, 'Casual wear', 10),
       (12, 'Dress', 11),
       (13, 'Miniskirt', 11),
       (14, 'Jeans', 11),
       (15, 'Formal wear', 10),
       (16, 'Suit', 15),
       (17, 'Shirt', 15),
       (18, 'Man', 9),
       (19, 'Casual wear', 18),
       (20, 'Jacket', 19),
       (21, 'Jeans', 19),
       (22, 'Formal wear', 18),
       (23, 'Suit', 22),
       (24, 'Shirt', 22)
;
