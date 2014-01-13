Name: Chen Wang
UID: 9663831259
User Name: wang565
Submitted files: readme.text, hw2.java, populate.java, createdb.sql, dropdb.sql

Resolution:

1. In the createDB.sql, I create three tables: students, buildings, announcementsystems. I also create SOD_GEO_METADATA and Index for these tables in the createDB.sql.

2. In the dropDB.sql, I delete all the three tables, remove the indexes, and also remove the SDO_GEO_METADATA for each table.

3. In the populate.java, I use JDBC to connect with DB. And before inserting all the datas from the files, I delete all the previous records in each table. 

4. In the hw2.java, I use Jframe, Jlable, BufferedImage, JTextAera, and Jbutton to set up the GUI. In addition, I use SDO_NN to query the nearest or the second nearest
object for each feature. I use SDO_RELATE to query the relationship like anyinteract or disjoint. 
	(1) Whole Region：
		Use SQL: Select shape from buildings
				 Select shape from students
				 Select shape from announcementSystems
				 
	(2)	Point Query：
		Use SQL: Select A.shape from students A where SDO_NN(A.shape,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(479,253, NULL), NULL, NULL),'sdo_num_res=1') = 'TRUE' AND SDO_RELATE(A.shape,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(429,253,529,253,479,303)),'mask=anyinteract') = 'TRUE'
				 Select A.shape from buildings A where SDO_NN(A.shape,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(479,253, NULL), NULL, NULL),'sdo_num_res=1') = 'TRUE' AND SDO_RELATE(A.shape,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(429,253,529,253,479,303)),'mask=anyinteract') = 'TRUE'
				 where SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(479,253, NULL), NULL, NULL) represents the point we click.
				 where SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(429,253,529,253,479,303)) repesents the circle we select.

	(3) Range Query:
		Use SQL: Select A.shape from buildings A where SDO_RELATE(A.shape,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(324.0,178.0,270.0,372.0,532.0,431.0,561.0,225.0,324.0,178.0)),'mask=anyinteract') = 'TRUE'
				 where DO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(324.0,178.0,270.0,372.0,532.0,431.0,561.0,225.0,324.0,178.0)) is the range we select
				 
	(4) Surrounding Students Query:
		Use SQL: Select A.shape from students A,announcementSystems B where SDO_NN(B.center,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(530,363, NULL), NULL, NULL),'sdo_num_res=1') = 'TRUE' AND SDO_RELATE(A.shape,B.shape,'mask=INSIDE') = 'TRUE'
				 where SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(530,363, NULL), NULL, NULL) represents the point we select.
			
	(5)	Emergency Query:
		Use SQL: Select B.shape from announcementSystems B where SDO_NN(B.center,SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(519,384, NULL), NULL, NULL),'sdo_num_res=1') = 'TRUE'
				 We use the above sql to get the circle nearest to the point we select.
				 Select A1.announcementsystemid,S.shape from announcementsystems A1,students S where SDO_NN(A1.center,S.shape,'sdo_num_res=2') = 'TRUE' AND SDO_RELATE(S.shape,SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 4), SDO_ORDINATE_ARRAY(398.0,450.0,498.0,450.0,448.0,400.0)),'mask=INSIDE') = 'TRUE' AND 'a7helen' <> A1.announcementsystemid
				 We user the above sql to know which student should go to which new AS.

Compile and Run:

1. For pouplate.java:

(1). Put ojdbc6.jar, sdoapi.jar, students.xy, buildings.xy, announcementsystems.xy in the same directory as populate.java

(2). compile: javac -classpath ";sdoapi.jar;ojdbc6.jar" populate.java

(3). run: java -classpath ";sdoapi.jar;ojdbc6.jar" populate buildings.xy students.xy announcementsystems.xy



2. For hw2.java:

(1). Put ojdbc6.jar, sdoapi.jar, map.jpg in the same directory as hw2.java

(2). complie: javac -classpath ";sdoapi.jar;ojdbc6.jar" hw2.java

(3). run: java -classpath ";sdoapi.jar;ojdbc6.jar" hw2
