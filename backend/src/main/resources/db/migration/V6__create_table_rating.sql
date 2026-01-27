create table rating(
    id bigint primary key auto_increment,
    rating int not null,
    tmdb_id bigint not null,
    user_id bigint not null,
    video_type varchar(10) not null,
    foreign key(user_id) references users(id) on delete cascade
);
