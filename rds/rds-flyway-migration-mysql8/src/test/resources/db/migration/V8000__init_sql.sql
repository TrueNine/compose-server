create
    table
    if not exists test_user_account_table
(
    id
    bigint
    primary
    key
    AUTO_INCREMENT,
    nick_name
    varchar
(
    255
) default null,
    password_enc varchar
(
    1023
) default null,
    email varchar
(
    255
) default null
    );
