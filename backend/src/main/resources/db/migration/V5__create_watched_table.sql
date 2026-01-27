create table watched (
    id bigint primary key auto_increment,
    user_id bigint not null,
    tmdb_id bigint not null,
    video_type varchar(10) not null,
    poster_path varchar(500) not null,
    title varchar(500) not null,
    foreign key(user_id) references users(id) on delete cascade
);

create table watch_list (
    id bigint primary key auto_increment,
    user_id bigint not null,
    tmdb_id bigint not null,
    video_type varchar(10) not null,
    poster_path varchar(500) not null,
    title varchar(500) not null,
    foreign key(user_id) references users(id) on delete cascade
);