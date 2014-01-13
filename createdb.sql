create table buildings(
  buildingID char(50),
  buildingName char(50),
  shape SDO_GEOMETRY,
  primary key (buildingID)
);


create table students(
  studentID char(50),
  shape SDO_GEOMETRY,
  primary key (studentID)
);


create table announcementSystems(
  announcementSystemID char(50),
  shape SDO_GEOMETRY,
  center SDO_GEOMETRY,
  primary key (announcementSystemID)
);


INSERT INTO USER_SDO_GEOM_METADATA VALUES ('buildings', 'shape', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL);

INSERT INTO USER_SDO_GEOM_METADATA VALUES ('students', 'shape', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL);

INSERT INTO USER_SDO_GEOM_METADATA VALUES ('announcementSystems', 'shape', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL);

INSERT INTO USER_SDO_GEOM_METADATA VALUES ('announcementSystems', 'center', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL);

CREATE INDEX buildings_idx ON buildings (shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX students_idx ON students (shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX announcement_idx ON announcementSystems (shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

CREATE INDEX announcementCenter_idx ON announcementSystems (center) INDEXTYPE IS MDSYS.SPATIAL_INDEX;

