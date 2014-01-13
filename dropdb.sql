
drop index announcement_idx;
drop index announcementCenter_idx;
drop index students_idx;
drop index buildings_idx;


drop table students;
drop table buildings;
drop table announcementsystems;

delete from USER_SDO_GEOM_METADATA U where U.table_name = 'ANNOUNCEMENTSYSTEMS' AND U.column_name = 'SHAPE';
delete from USER_SDO_GEOM_METADATA U where U.table_name = 'ANNOUNCEMENTSYSTEMS' AND U.column_name = 'CENTER';
delete from USER_SDO_GEOM_METADATA U where U.table_name = 'BUILDINGS' AND U.column_name = 'SHAPE';
delete from USER_SDO_GEOM_METADATA U where U.table_name = 'STUDENTS' AND U.column_name = 'SHAPE';

