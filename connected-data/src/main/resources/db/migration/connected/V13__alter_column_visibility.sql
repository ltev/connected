alter table posts drop check posts_chk_1;
alter table posts modify column visibility tinyint NOT NULL check (visibility between 0 and 4);
update posts set visibility = visibility + 1 where visibility >= 1;