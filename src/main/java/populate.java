import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;


public class populate {
	
	public static void main(String[] args) throws SQLException, IOException{
		if(args.length <= 0){
			System.out.println("Please input valid args!");
		}else{
			for(int i = 0; i < args.length; i++){
				if(args[i].equalsIgnoreCase("buildings.xy")){
					loadBuilding();
				}else if(args[i].equalsIgnoreCase("students.xy")){
					loadStudents();
				}else if(args[i].equalsIgnoreCase("announcementSystems.xy")){
					loadAnnounces();
				}
			}
			System.out.println("Data is populated!");
		}
		
		/*Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:CSCI585", "system", "1986511wc");
			stmt = conn.createStatement();
			String query = "select shape from buildings";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				@SuppressWarnings("deprecation")
				STRUCT st = (oracle.sql.STRUCT) rs.getObject("shape");
			     //convert STRUCT into geometry
			    JGeometry j_geom = JGeometry.load(st);
				//String dname = rs.getString("buildingid");
				//System.out.println(dname);
			    for(double a : j_geom.getOrdinatesArray()){
			    	System.out.print(a + " ");
			    }
			    System.out.println("end");
			}
		}finally{
			conn.close();
			stmt.close();
		}*/
	}
	
	private static void loadBuilding() throws SQLException, IOException{
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:orcl", "system", "1986511wc");
			stmt = conn.createStatement();
			stmt.executeUpdate("delete from buildings");
			stmt.close();
			BufferedReader br = new BufferedReader(new FileReader("./buildings.xy"));
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(insertBuildingBuilder(line));
				stmt = conn.createStatement();
				stmt.executeUpdate(insertBuildingBuilder(line));
				stmt.close();
			}
			/*stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select table_name from USER_SDO_GEOM_METADATA U where U.table_name = 'BUILDINGS' AND U.column_name = 'SHAPE'");
			
			if(!rs.next()){
				stmt.close();
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO USER_SDO_GEOM_METADATA VALUES ('buildings', 'shape', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL)");
				stmt.close();
			}else{
				stmt.close();
			}
			stmt = conn.createStatement();
			stmt.executeUpdate("drop index buildings_idx");
			stmt.close();
			stmt = conn.createStatement();
			stmt.executeUpdate("CREATE INDEX buildings_idx ON buildings (shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX");
			stmt.close();
			System.out.println("there");*/
			br.close();
		}finally{
			conn.close();
		}
	}
	
	private static void loadStudents() throws SQLException, IOException{
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:orcl", "system", "1986511wc");
			
			stmt = conn.createStatement();
			stmt.executeUpdate("delete from students");
			stmt.close();
			
			BufferedReader br = new BufferedReader(new FileReader("students.xy"));
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(insertStudentsBuilder(line));
				stmt = conn.createStatement();
				stmt.executeUpdate(insertStudentsBuilder(line));
				stmt.close();
			}
			/*stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select table_name from USER_SDO_GEOM_METADATA U where U.table_name = 'STUDENTS' AND U.column_name = 'SHAPE'");
			if(!rs.next()){
				stmt.close();
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO USER_SDO_GEOM_METADATA VALUES ('students', 'shape', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL)");
				stmt.close();
			}else{
				stmt.close();
			}
			stmt = conn.createStatement();
			stmt.executeUpdate("drop index students_idx");
			stmt.close();
			stmt = conn.createStatement();
			stmt.executeUpdate("CREATE INDEX students_idx ON students (shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX");
			stmt.close();*/
			br.close();
		}finally{
			conn.close();
		}
	}
	
	private static void loadAnnounces() throws SQLException, IOException{
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:orcl", "system", "1986511wc");
			//stmt = conn.createStatement();
			//String query = "select nickname from tweetUser t where t.location like '%Los Angeles%'";
			//ResultSet rs = stmt.executeQuery(query);
			//while (rs.next()) {
				//String dname = rs.getString("nickname");
				//System.out.println(dname);
			//}
			
			stmt = conn.createStatement();
			stmt.executeUpdate("delete from announcementSystems");
			stmt.close();
			
			BufferedReader br = new BufferedReader(new FileReader("announcementSystems.xy"));
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				//System.out.println(insertAnnounceBuilder(line));
				stmt = conn.createStatement();
				stmt.executeUpdate(insertAnnounceBuilder(line));
				stmt.close();
			}
			br.close();
			/*stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select table_name from USER_SDO_GEOM_METADATA U where U.table_name = 'ANNOUNCEMENTSYSTEMS' AND U.column_name = 'SHAPE'");
			if(!rs.next()){
				stmt.close();
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO USER_SDO_GEOM_METADATA VALUES ('announcementSystems', 'shape', SDO_DIM_ARRAY(SDO_DIM_ELEMENT('X', 0, 900, 0.005), SDO_DIM_ELEMENT('Y', 0, 900, 0.005)), NULL)");
				stmt.close();
			}else{
				stmt.close();
			}
			stmt = conn.createStatement();
			stmt.executeUpdate("drop index announcement_idx ");
			stmt.close();
			stmt = conn.createStatement();
			stmt.executeUpdate("CREATE INDEX announcement_idx ON announcementSystems (shape) INDEXTYPE IS MDSYS.SPATIAL_INDEX");
			stmt.close();*/
		}finally{
			conn.close();
		}
	}
	
	private static String insertBuildingBuilder(String line){
		String[] elements = line.split(",");
		String buildingID = elements[0];
		String buildingName = elements[1];
		String cordinates = "";
		for(int i = 3; i < elements.length; i++){
			cordinates += elements[i] + ",";
		}
		cordinates += elements[3] + ",";
		cordinates += elements[4];
		return generalBuildingInfo(buildingID, buildingName, cordinates);
	}
	
	private static String generalBuildingInfo(String id, String name, String cordinates){
		return "INSERT INTO buildings VALUES('" + id + "','" + name + "',SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + cordinates + ")))";		
	}
	
	private static String insertStudentsBuilder(String line){
		String[] elements = line.split(",");
		String studentId = elements[0];
		String cordinates = elements[1] + "," + elements[2];
		return generalStudentInfo(studentId, cordinates);
	}
	
	private static String generalStudentInfo(String id, String cordinates){
		return "INSERT INTO students VALUES('" + id + "', SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + cordinates + ", NULL), NULL, NULL))";		
	}
	
	private static String insertAnnounceBuilder(String line){
		String[] elements = line.split(",");
		String announceId = elements[0];
		int x = Integer.parseInt(elements[1].replaceAll("\\s",""));
		int y = Integer.parseInt(elements[2].replaceAll("\\s",""));
		int r = Integer.parseInt(elements[3].replaceAll("\\s",""));
		String cordinates = String.valueOf(x - r) + "," + String.valueOf(y) + ",";
		cordinates += String.valueOf(x + r) + "," + String.valueOf(y) + ",";
		cordinates += String.valueOf(x) + "," + String.valueOf(y - r);
		String center = elements[1] + "," + elements[2];
		return generalAnouncementInfo(announceId, cordinates, center);
	}
	
	private static String generalAnouncementInfo(String id, String cordinates, String center){
		return "INSERT INTO announcementSystems VALUES('" + id + "',SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(" + cordinates + ")), SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + center + ", NULL), NULL, NULL)" + ")";
	}
}
