create table email_change_request (
    id bigint primary key auto_increment,
    user_id bigint not null,
    new_email varchar(255) not null,
    token varchar(255) not null,
    created_at timestamp not null default current_timestamp,
    expires_at timestamp not null,
    foreign key(user_id) references users(id) on delete cascade
);