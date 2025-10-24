create table point
(
    id bigserial not null primary key,
    latitude numeric(10,7) not null,
    longitude numeric(10,7) not null,
    elevation numeric(10,7)
);

create table station
(
    id bigserial not null primary key,
    osm_id bigserial not null,
    name varchar(250),
    network varchar(250),
    type varchar(50),
    point_id bigint,
    foreign key (point_id) references point (id)
);

create table route
(
    id bigserial not null primary key,
    name varchar(250),
    description varchar(1000),
    waypoints_number smallint,
    length numeric(8,1),
    ascent numeric(5,1),
    descent numeric(5,1),
    relations_processing_status varchar(50) not null
);

create table waypoint
(
    id bigserial not null primary key,
    index smallint not null,
    route_id bigint not null,
    point_id bigint not null,
    comment varchar(1000),
    foreign key (route_id) references route (id),
    foreign key (point_id) references point (id)
);
create table relation
(
    id bigserial not null primary key,
    route_id bigint not null,
    type varchar(50) not null,
    station_id bigint,
    distance numeric(8,1),
    foreign key (route_id) references route (id),
    foreign key (station_id) references station (id)
);



