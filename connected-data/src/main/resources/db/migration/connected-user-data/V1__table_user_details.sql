CREATE TABLE user_details (
  id bigint NOT NULL,
  first_name varchar(255) DEFAULT NULL,
  last_name varchar(255) DEFAULT NULL,
  birthday date DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT user_details_user_id_fk FOREIGN KEY (id) REFERENCES connected.users (id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- importing and inserting all user_details data from connected db to connected_user_data db
insert into user_details
    select * from connected.user_details;

-- drop imported table in original connected db
drop table connected.user_details;