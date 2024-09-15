alter table
    if exists api_call_record
    add column if not exists req_path      text        default null,
    add column if not exists req_method    varchar(63) default null,
    add column if not exists req_datetime  timestamp   default null,
    add column if not exists resp_datetime timestamp   default null,
    add column if not exists req_protocol  varchar(63) default null;

create
    index if not exists api_call_record_req_path_idx on
    api_call_record (req_path);
