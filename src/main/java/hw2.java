import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JButton;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFormattedTextField;
import java.awt.TextArea;

public class hw2 extends JFrame {

	private JPanel contentPane;;
	private Connection conn = null;
	private final JLabel lblNewLabel_2 = new JLabel("0");
	private final JLabel label = new JLabel("0");
	private final JLabel lblNewLabel = new JLabel("New label");
	private final JCheckBox chckbxNewCheckBox_2 = new JCheckBox("AS\r\n");
	private final JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Students");
	private final JCheckBox chckbxNewCheckBox = new JCheckBox("Building");
	private final JPanel panel = new JPanel();
	private final JPanel panel_1 = new JPanel();
	private final JRadioButton rdbtnNewRadioButton_1 = new JRadioButton(
			"Whole region");
	private final JRadioButton rdbtnNewRadioButton_2 = new JRadioButton(
			"Point query");
	private final JRadioButton rdbtnNewRadioButton_3 = new JRadioButton(
			"Range Query");
	private final JRadioButton rdbtnNewRadioButton_4 = new JRadioButton(
			"Surrounding Student");
	private final JRadioButton rdbtnNewRadioButton = new JRadioButton(
			"Emergency Query");
	private final JButton btnNewButton = new JButton("Submit Query");
	private final JLabel lblNewLabel_1 = new JLabel("Active Feature Types");
	private int queryCount = 1;
	private final TextArea textArea = new TextArea();
	private final String mapPath = "map.jpg";
	private int[] pointQuerySelectedCord = new int[2];
	private boolean hasPointSelected = false;
	private ArrayList<Integer> rectCords = new ArrayList<Integer>();
	private boolean rectClosed = false;
	private int[] surroundingQueryPoint = new int[2];
	private boolean surroundingPointSelected = false;
	

	/**
	 * Launch the application.
	 * 
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					hw2 frame = new hw2();
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void setConnection() throws Exception{
		try {
			conn = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1521:orcl", "system",
					"1986511wc");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new Exception("Fail to setup DB connection", e);
		}
	}
	
	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public hw2() throws Exception {
		setTitle("Name: Chen Wang   ID: 9663831259");
		setConnection();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.addWindowListener(new WindowListener(){

			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
			
			}

			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.out.println("implementing clean up!");
				try {
					conn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					throw new RuntimeException("Fail to close DB connection!", e1);
				}
			}

			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}
			
		});
		
		
		setBounds(100, 100, 1203, 760);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		initialImage();
		initialCheckBoxs();
		initialRadioButtons();
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					sumbitHandler();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(864, 535, 278, 33);
		contentPane.add(btnNewButton);
		lblNewLabel_1.setBounds(864, 11, 227, 29);
		contentPane.add(lblNewLabel_1);
		initialXYDisplayer();
		textArea.setBounds(22, 646, 757, 50);
		contentPane.add(textArea);
	}

	private void sumbitHandler() throws IOException, SQLException {
		File file = new File(mapPath);
		BufferedImage image = ImageIO.read(file);
		Graphics2D g2d = image.createGraphics();
		lblNewLabel.setIcon(new ImageIcon(image));
		if (rdbtnNewRadioButton_1.isSelected()) {

			queryWholeRegion(g2d);
		} else if (rdbtnNewRadioButton_2.isSelected()) {
			queryPoint(g2d);
		} else if (rdbtnNewRadioButton_3.isSelected()) {
			queryRange(g2d);
		} else if (rdbtnNewRadioButton_4.isSelected()) {
			querySurroundingStudent(g2d);
		} else if (rdbtnNewRadioButton.isSelected()) {
			queryEmergency(g2d);
		} else {
			textArea.append("No radioButton is selected!\n");
		}
	}
	
	private void queryEmergency(Graphics2D g2d) throws SQLException{
		if(!surroundingPointSelected){
			textArea.append("You have not close a point!\n");
			return;
		}
		String selectedAS = null;
		HashMap<String, Color> colorMap = new HashMap<String, Color>();
		
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("drop index announcementCenter_idx");
		stmt.close();
		stmt = conn.createStatement();
		stmt.executeUpdate("CREATE INDEX announcementCenter_idx ON announcementSystems (center) INDEXTYPE IS MDSYS.SPATIAL_INDEX");
		stmt.close();
		String sixCords = null;
		String sqlNearest = getSelectSQL(new String[]{"announcementSystems B"}, new String[]{"B.announcementsystemid, B.shape"}, new String[]{generateSDO_NN("B.center", generatePointGeoType(surroundingQueryPoint), 1) + " = 'TRUE'"});
		for	(CordWithID cords : getAllCircleCordsWithID(sqlNearest)) {
				drawCircleAndSquare(cords.cord, g2d, Color.red, 15);
				colorMap.put(cords.id, Color.yellow);
				sixCords = cords.sixCords;
				selectedAS = cords.id;
				selectedAS = selectedAS.replaceAll(" ","");
		}
		LinkedList<Color> colorPool = new LinkedList<Color>(Arrays.asList(Color.yellow, Color.blue, Color.green, Color.magenta, Color.pink, Color.white));
		textArea.append("Query " + queryCount++
				+ " : Select announcementsystemid, shape from announcementSystems\n");
		for (CordWithID cords : getAllCircleCordsWithID("Select announcementsystemid, shape from announcementSystems")) {
			if(!colorMap.containsKey(cords.id)){
				drawCircleAndSquare(cords.cord, g2d, colorPool.getFirst(), 15);
				colorMap.put(cords.id, colorPool.getFirst());
				colorPool.removeFirst();
			}
		}
		String[] retParams = new String[]{"A1.announcementsystemid", "S.shape"};
		String[] tables = new String[]{"announcementsystems A1", "students S"};
		//String subClause = getSelectSQL(new String[]{"announcementsystems A3"}, new String[]{"A3.announcementsystemid"}, new String[]{generateSDO_NN("A3.shape", "S.shape", 2) + " = 'TRUE'"});
		String[] whereClause = new String[]{generateSDO_NN("A1.center","S.shape", 2) + " = 'TRUE' AND "
				+ generateSDO_RELATE("S.shape", "SDO_GEOMETRY(2003, NULL, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 4), SDO_ORDINATE_ARRAY(" + sixCords + "))", "INSIDE") + " = 'TRUE' AND "
				+ "'"
				+ selectedAS 
				+ "' <> A1.announcementsystemid"
				};//generateSDO_NN("A1.shape", generatePointGeoType(surroundingQueryPoint), 1) + " = 'TRUE' AND "
		//String sqlEmergency = getSelectSQL(new String[]{"students S","announcementsystems A"}, new String[]{"A.shape", "A.announcementsystemid", "S.studentid"}, new String[]{"(S.studentid = 'p10' or S.studentid = 'p11') AND " + generateSDO_NN("A.shape","S.shape", 2) + " = 'TRUE'"});
		String sqlEmergency = getSelectSQL(tables, retParams, whereClause);
		textArea.append("Query " + queryCount++
				+ ": " + sqlEmergency + "\n");
		for (CordWithID cords : getAllPointCordsWithID(sqlEmergency)) {
			//System.out.println(cords.id);
			drawPoint(cords.cord, g2d, colorMap.get(cords.id), 10);
		}
	}
	
	private void querySurroundingStudent(Graphics2D g2d) throws SQLException{
		if(!surroundingPointSelected){
			textArea.append("You have not close a point!\n");
			return;
		}
		
		String sqlNearest = getSelectSQL(new String[]{"announcementSystems B"}, new String[]{"B.shape"}, new String[]{generateSDO_NN("B.center", generatePointGeoType(surroundingQueryPoint), 1) + " = 'TRUE'"});
		for (double[] cords : getAllCircleCords(sqlNearest)) {
			drawCircleAndSquare(cords, g2d, Color.red, 15);
		}
		
		String sql = getSelectSQL(new String[]{"students A", "announcementSystems B"}, new String[]{"A.shape"}, new String[]{generateSDO_NN("B.center", generatePointGeoType(surroundingQueryPoint), 1) + " = 'TRUE' AND " + generateSDO_RELATE("A.shape", "B.shape", "INSIDE") + " = 'TRUE'"});
		textArea.append("Query " + queryCount++
				+ ": " + sql + "\n");
		for (double[] cords : getAllPointCords(sql)) {
			drawPoint(cords, g2d, Color.green, 10);
		}
	}
	
	private void queryRange(Graphics2D g2d) throws IOException, SQLException {
		if(!rectClosed || rectCords.size() <= 0){
			textArea.append("You have not close your region or there is no region selected!\n");
			return;
		}
		//drawCircleAndSquare(new double[]{pointQuerySelectedCord[0], pointQuerySelectedCord[1], 50}, g2d, Color.red, 5);
		double[] temp = new double[rectCords.size()];
		for(int i = 0; i < temp.length; i++){
			temp[i] = rectCords.get(i);
		}
		drawRectangle(temp, g2d, Color.red);
		if (!chckbxNewCheckBox_2.isSelected()
				&& !chckbxNewCheckBox_1.isSelected()
				&& !chckbxNewCheckBox.isSelected()) {
			textArea.append("No feature is selected!\n");
			return;
		}//getSQLAnyIntersetWithCircule("announcementSystems", pointQuerySelectedCord)
		if (chckbxNewCheckBox_2.isSelected()) {
			
			String[] params = new String[]{"A.shape"};
			String[] tables = new String[]{"announcementSystems A"};
			String[] whereClause = new String[]{generateSDO_RELATE("A.shape", generateRectGeoType(temp), "anyinteract") + " = 'TRUE'"};			
			String sqlInteract = getSelectSQL(tables, params, whereClause);
			textArea.append("Query " + queryCount++
					+ ": " + sqlInteract + "\n");
			for (double[] cords : getAllCircleCords(sqlInteract)) {
				drawCircleAndSquare(cords, g2d, Color.red, 15);
			}
		}
		if (chckbxNewCheckBox_1.isSelected()) {
			String sqlInteract = getSelectSQL(new String[]{"students A"}, new String[]{"A.shape"}, new String[]{generateSDO_RELATE("A.shape", generateRectGeoType(temp), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlInteract + "\n");
			for (double[] cords : getAllPointCords(sqlInteract)) {
				drawPoint(cords, g2d, Color.green, 10);
			}
		}
		if (chckbxNewCheckBox.isSelected()) {
			String sqlInteract = getSelectSQL(new String[]{"buildings A"}, new String[]{"A.shape"}, new String[]{generateSDO_RELATE("A.shape", generateRectGeoType(temp), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlInteract + "\n");
			for (double[] cords : getAllRectangleCords(sqlInteract)) {
				drawRectangle(cords, g2d, Color.yellow);
			}
		}
	}

	private void queryPoint(Graphics2D g2d) throws IOException, SQLException {
		if(!hasPointSelected){
			textArea.append("No point is selected!\n");
			return;
		}
		drawCircleAndSquare(new double[]{pointQuerySelectedCord[0], pointQuerySelectedCord[1], 50}, g2d, Color.red, 5);
		if (!chckbxNewCheckBox_2.isSelected()
				&& !chckbxNewCheckBox_1.isSelected()
				&& !chckbxNewCheckBox.isSelected()) {
			textArea.append("No feature is selected!\n");
			return;
		}//getSQLAnyIntersetWithCircule("announcementSystems", pointQuerySelectedCord)
		if (chckbxNewCheckBox_2.isSelected()) {
			String[] params = new String[]{"A.shape"};
			String[] tables = new String[]{"announcementSystems A"};
			String[] whereClause = new String[]{generateSDO_RELATE("A.shape", generateCirculeGeoType(pointQuerySelectedCord, 50), "anyinteract") + " = 'TRUE'"};			
			String sqlInteract = getSelectSQL(tables, params, whereClause);
			textArea.append("Query " + queryCount++
					+ ": " + sqlInteract + "\n");
			for (double[] cords : getAllCircleCords(sqlInteract)) {
				drawCircleAndSquare(cords, g2d, Color.green, 15);
			}
			String sqlNearest = getSelectSQL(new String[]{"announcementSystems B"}, new String[]{"B.shape"}, new String[]{generateSDO_NN("B.shape", generatePointGeoType(pointQuerySelectedCord), 1) + " = 'TRUE' AND " + generateSDO_RELATE("B.shape", generateCirculeGeoType(pointQuerySelectedCord, 50), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlNearest + "\n");
			for (double[] cords : getAllCircleCords(sqlNearest)) {
				drawCircleAndSquare(cords, g2d, Color.yellow, 15);
			}
		}
		if (chckbxNewCheckBox_1.isSelected()) {
			String sqlInteract = getSelectSQL(new String[]{"students A"}, new String[]{"A.shape"}, new String[]{generateSDO_RELATE("A.shape", generateCirculeGeoType(pointQuerySelectedCord, 50), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlInteract + "\n");
			for (double[] cords : getAllPointCords(sqlInteract)) {
				drawPoint(cords, g2d, Color.green, 10);
			}
			String sqlNearest = getSelectSQL(new String[]{"students A"}, new String[]{"A.shape"}, new String[]{generateSDO_NN("A.shape", generatePointGeoType(pointQuerySelectedCord), 1) + " = 'TRUE' AND " + generateSDO_RELATE("A.shape", generateCirculeGeoType(pointQuerySelectedCord, 50), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlNearest + "\n");
			for (double[] cords : getAllPointCords(sqlNearest)) {
				drawPoint(cords, g2d, Color.yellow, 10);
			}
		}
		if (chckbxNewCheckBox.isSelected()) {
			String sqlInteract = getSelectSQL(new String[]{"buildings A"}, new String[]{"A.shape"}, new String[]{generateSDO_RELATE("A.shape", generateCirculeGeoType(pointQuerySelectedCord, 50), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlInteract + "\n");
			for (double[] cords : getAllRectangleCords(sqlInteract)) {
				drawRectangle(cords, g2d, Color.green);
			}
			String sqlNearest = getSelectSQL(new String[]{"buildings A"}, new String[]{"A.shape"}, new String[]{generateSDO_NN("A.shape", generatePointGeoType(pointQuerySelectedCord), 1) + " = 'TRUE' AND " + generateSDO_RELATE("A.shape", generateCirculeGeoType(pointQuerySelectedCord, 50), "anyinteract") + " = 'TRUE'"});
			textArea.append("Query " + queryCount++
					+ ": " + sqlNearest + "\n");
			for (double[] cords : getAllRectangleCords(sqlNearest)) {
				drawRectangle(cords, g2d, Color.yellow);
			}
		}
	}
	
	
	private String getSelectSQL(String[] tableNames, String[] requiredParas, String[] whereClause){
		StringBuilder sql = new StringBuilder();
		sql.append("Select ");
		for(int i = 0; i < requiredParas.length; i++){
			sql.append(requiredParas[i]);
			if(i != requiredParas.length - 1){
				sql.append(",");
			}
		}
		sql.append(" from ");
		for(int i = 0; i < tableNames.length; i++){
			sql.append(tableNames[i]);
			if(i != tableNames.length - 1){
				sql.append(",");
			}
		}
		sql.append(" where ");
		for(int i = 0; i < whereClause.length; i++){
			sql.append(whereClause[i]);
			if(i != whereClause.length - 1){
				sql.append(",");
			}
		}
		return sql.toString();
	}
	
	private String generateCirculeGeoType(int[] center, int r){
		StringBuilder cords = new StringBuilder();
		cords.append(center[0] - r);
		cords.append(",");
		cords.append(center[1]);
		cords.append(",");
		cords.append(center[0] + r);
		cords.append(",");
		cords.append(center[1]);
		cords.append(",");
		cords.append(center[0]);
		cords.append(",");
		cords.append(center[1] + r);
		return "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(" + cords.toString() + "))";
	}
	
	private String generateRectGeoType(double[] cords){
		StringBuilder cordsStr = new StringBuilder();
		for(int i = 0; i < cords.length; i++){
			cordsStr.append(cords[i]);
			if(i != cords.length - 1){
				cordsStr.append(",");
			}
		}
		return "SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(" + cordsStr.toString() + "))";
	}
	
	private String generatePointGeoType(int[] center){
		StringBuilder cords = new StringBuilder();
		cords.append(center[0]);
		cords.append(",");
		cords.append(center[1]);
		return "SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE(" + cords.toString() + ", NULL), NULL, NULL)";
	}
	
	private String generateSDO_NN(String geoPara1, String geoPara2, int num){
		StringBuilder res = new StringBuilder();
		res.append("SDO_NN(");
		res.append(geoPara1);
		res.append(",");
		res.append(geoPara2);
		res.append(",");
		res.append("'sdo_num_res=" + num + "')");
		return res.toString();
	}
	
	private String generateSDO_RELATE(String geoPara1, String geoPara2, String mask){
		StringBuilder res = new StringBuilder();
		res.append("SDO_RELATE(");
		res.append(geoPara1);
		res.append(",");
		res.append(geoPara2);
		res.append(",");
		res.append("'mask=");
		res.append(mask);
		res.append("')");
		return res.toString();
	}

	@SuppressWarnings("deprecation")
	private void queryWholeRegion(Graphics2D g2d) throws IOException,
			SQLException {
		if (!chckbxNewCheckBox_2.isSelected()
				&& !chckbxNewCheckBox_1.isSelected()
				&& !chckbxNewCheckBox.isSelected()) {
			textArea.append("No feature is selected!\n");
			return;
		}
		if (chckbxNewCheckBox_2.isSelected()) {
			textArea.append("Query " + queryCount++
					+ " : Select shape from announcementSystems\n");
			for (double[] cords : getAllCircleCords("Select shape from announcementSystems")) {
				drawCircleAndSquare(cords, g2d, Color.red, 15);
			}
		}
		if (chckbxNewCheckBox_1.isSelected()) {
			textArea.append("Query " + queryCount++
					+ " : Select shape from students\n");
			for (double[] cords : getAllPointCords("Select shape from students")) {
				drawPoint(cords, g2d, Color.green, 10);
			}
		}
		if (chckbxNewCheckBox.isSelected()) {
			textArea.append("Query " + queryCount++
					+ " : Select shape from buildings\n");
			for (double[] cords : getAllRectangleCords("Select shape from buildings")) {
				drawRectangle(cords, g2d, Color.yellow);
			}
		}
	}

	private void initialXYDisplayer() {
		JLabel lblX = new JLabel("X\uFF1A");
		lblX.setBounds(22, 606, 26, 14);
		contentPane.add(lblX);
		lblNewLabel_2.setBounds(47, 606, 46, 14);
		contentPane.add(lblNewLabel_2);
		JLabel lblY = new JLabel("Y:");
		lblY.setBounds(94, 606, 46, 14);
		contentPane.add(lblY);
		label.setBounds(119, 606, 46, 14);
		contentPane.add(label);
	}

	private void deSelectRadionButtions(JRadioButton except) throws IOException {
		if (except != rdbtnNewRadioButton_1) {
			rdbtnNewRadioButton_1.setSelected(false);
		}
		if (except != rdbtnNewRadioButton_2) {
			rdbtnNewRadioButton_2.setSelected(false);
		}
		if (except != rdbtnNewRadioButton_3) {
			rdbtnNewRadioButton_3.setSelected(false);
		}
		if (except != rdbtnNewRadioButton_4) {
			rdbtnNewRadioButton_4.setSelected(false);
		}
		if (except != rdbtnNewRadioButton) {
			rdbtnNewRadioButton.setSelected(false);
		}
		rectCords.clear();
		surroundingPointSelected = false;
		File file = new File(mapPath);
		BufferedImage image = ImageIO.read(file);
		lblNewLabel.setIcon(new ImageIcon(image));
	}

	private void initialRadioButtons() {
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(864, 190, 278, 293);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		rdbtnNewRadioButton_1.setBounds(31, 36, 178, 23);
		panel_1.add(rdbtnNewRadioButton_1);
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deSelectRadionButtions(rdbtnNewRadioButton_1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		rdbtnNewRadioButton_2.setBounds(31, 74, 178, 23);
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hasPointSelected = false;
				try {
					deSelectRadionButtions(rdbtnNewRadioButton_2);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_1.add(rdbtnNewRadioButton_2);
		rdbtnNewRadioButton_3.setBounds(31, 111, 178, 23);
		rdbtnNewRadioButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deSelectRadionButtions(rdbtnNewRadioButton_3);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_1.add(rdbtnNewRadioButton_3);
		rdbtnNewRadioButton_4.setBounds(31, 151, 178, 23);
		rdbtnNewRadioButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deSelectRadionButtions(rdbtnNewRadioButton_4);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_1.add(rdbtnNewRadioButton_4);
		rdbtnNewRadioButton.setBounds(31, 188, 178, 23);
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deSelectRadionButtions(rdbtnNewRadioButton);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panel_1.add(rdbtnNewRadioButton);
	}

	private void initialImage() throws SQLException, IOException {
		File file = new File(mapPath);
		BufferedImage image = ImageIO.read(file);
		lblNewLabel.setIcon(new ImageIcon(image));
		lblNewLabel.setBounds(10, 0, 830, 595);
		contentPane.add(lblNewLabel);
		lblNewLabel.addMouseMotionListener(new MouseHandler());
		lblNewLabel.addMouseListener(new MouseHandler());
	}

	private void initialCheckBoxs() {
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(864, 51, 278, 100);
		contentPane.add(panel);
		panel.setLayout(null);
		chckbxNewCheckBox_2.setBounds(6, 7, 234, 40);
		panel.add(chckbxNewCheckBox_2);
		chckbxNewCheckBox_1.setBounds(6, 60, 109, 33);
		panel.add(chckbxNewCheckBox_1);
		chckbxNewCheckBox.setBounds(150, 56, 122, 40);
		panel.add(chckbxNewCheckBox);
	}

	private void drawPoint(double[] cordinates, Graphics2D g, Color color,
			int squareR) {
		g.setColor(color);
		g.fillRect((int) cordinates[0] - squareR / 2, (int) cordinates[1]
				- squareR / 2, squareR, squareR);
	}

	private ArrayList<double[]> getAllPointCords(String query)
			throws SQLException {
		ArrayList<double[]> res = new ArrayList<double[]>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				@SuppressWarnings("deprecation")
				STRUCT st = (oracle.sql.STRUCT) rs.getObject("shape");
				JGeometry j_geom = JGeometry.load(st);
				double[] pointCord = new double[2];
				pointCord[0] = j_geom.getJavaPoint().getX();
				pointCord[1] = j_geom.getJavaPoint().getY();
				res.add(pointCord);
			}
		} finally {
			stmt.close();
		}
		return res;
	}
	
	private ArrayList<CordWithID> getAllPointCordsWithID(String query)
			throws SQLException {
		ArrayList<CordWithID> res = new ArrayList<CordWithID>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				@SuppressWarnings("deprecation")
				STRUCT st = (oracle.sql.STRUCT) rs.getObject("shape");
				JGeometry j_geom = JGeometry.load(st);
				double[] pointCord = new double[2];
				pointCord[0] = j_geom.getJavaPoint().getX();
				pointCord[1] = j_geom.getJavaPoint().getY();
				res.add(new CordWithID(pointCord, rs.getString("announcementsystemid")));
			}
		} finally {
			stmt.close();
		}
		return res;
	}

	private void drawRectangle(double[] cordinates, Graphics2D g, Color color) {
		g.setColor(color);
		int currentX = (int) cordinates[0];
		int currentY = (int) cordinates[1];
		for (int i = 1; i < cordinates.length / 2; i++) {
			int nextX = (int) cordinates[i * 2];
			int nextY = (int) cordinates[i * 2 + 1];
			g.drawLine(currentX, currentY, nextX, nextY);
			currentX = nextX;
			currentY = nextY;
		}
	}

	private ArrayList<double[]> getAllRectangleCords(String query)
			throws SQLException {
		ArrayList<double[]> res = new ArrayList<double[]>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				@SuppressWarnings("deprecation")
				STRUCT st = (oracle.sql.STRUCT) rs.getObject("shape");
				// convert STRUCT into geometry
				JGeometry j_geom = JGeometry.load(st);
				// String dname = rs.getString("buildingid");
				// System.out.println(dname);
				res.add(j_geom.getOrdinatesArray());
			}
		} finally {
			stmt.close();
		}
		return res;
	}


	private ArrayList<double[]> getAllCircleCords(String query)
			throws SQLException {
		ArrayList<double[]> res = new ArrayList<double[]>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				@SuppressWarnings("deprecation")
				STRUCT st = (oracle.sql.STRUCT) rs.getObject("shape");
				// convert STRUCT into geometry
				JGeometry j_geom = JGeometry.load(st);
				// String dname = rs.getString("buildingid");
				// System.out.println(dname);
				double[] threeCords = j_geom.getOrdinatesArray();
				double[] oneCord = new double[3];
				oneCord[0] = threeCords[4];
				oneCord[1] = threeCords[1];
				oneCord[2] = threeCords[4] - threeCords[0];
				res.add(oneCord);
			}
		} finally {
			stmt.close();
		}
		return res;
	}
	
	private class CordWithID{
		public double[] cord;
		public String id;
		public String sixCords = null;
		public CordWithID(double[] cord, String id){
			this.cord = cord;
			this.id = id;
		}
	}
	
	private String getSixCordsStr(double[] cords){
		StringBuilder strBuilder = new StringBuilder();
		for(int i = 0; i < cords.length; i++){
			strBuilder.append(cords[i]);
			if(i != cords.length - 1){
				strBuilder.append(",");
			}
		}
		return strBuilder.toString();
	}
	
	private ArrayList<CordWithID> getAllCircleCordsWithID(String query)
			throws SQLException {
		ArrayList<CordWithID> res = new ArrayList<CordWithID>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				@SuppressWarnings("deprecation")
				STRUCT st = (oracle.sql.STRUCT) rs.getObject("shape");
				// convert STRUCT into geometry
				JGeometry j_geom = JGeometry.load(st);
				// String dname = rs.getString("buildingid");
				// System.out.println(dname);
				double[] threeCords = j_geom.getOrdinatesArray();
				double[] oneCord = new double[3];
				oneCord[0] = threeCords[4];
				oneCord[1] = threeCords[1];
				oneCord[2] = threeCords[4] - threeCords[0];
				CordWithID cid = new CordWithID(oneCord, rs.getString("announcementsystemid"));
				cid.sixCords = getSixCordsStr(threeCords);
				res.add(cid);
			}
		} finally {
			stmt.close();
		}
		return res;
	}

	private void drawCircleAndSquare(double[] cordinates, Graphics2D g,
			Color color, int squareR) {
		g.setColor(color);
		g.drawOval((int) cordinates[0] - (int) cordinates[2],
				(int) cordinates[1] - (int) cordinates[2],
				(int) cordinates[2] * 2, (int) cordinates[2] * 2);
		g.fillRect((int) cordinates[0] - squareR / 2, (int) cordinates[1]
				- squareR / 2, squareR, squareR);
	}

	private class MouseHandler extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			if (rdbtnNewRadioButton_2.isSelected()) {
				hasPointSelected = true;
				pointQuerySelectedCord[0] = e.getX();
				pointQuerySelectedCord[1] = e.getY();
				File file = new File(mapPath);
				BufferedImage image = null;
				try {
					image = ImageIO.read(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				lblNewLabel.setIcon(new ImageIcon(image));
				drawCircleAndSquare(new double[]{pointQuerySelectedCord[0], pointQuerySelectedCord[1], 50}, image.createGraphics(), Color.red, 5);
			}else if(rdbtnNewRadioButton_3.isSelected()){
				if(e.getButton() == MouseEvent.BUTTON1){
					rectClosed = false;
					File file = new File(mapPath);
					BufferedImage image = null;
					try {
						image = ImageIO.read(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					lblNewLabel.setIcon(new ImageIcon(image));
					rectCords.add(e.getX());
					rectCords.add(e.getY());
					double[] temp = new double[rectCords.size()];
					for(int i = 0; i < temp.length; i++){
						temp[i] = rectCords.get(i);
					}
					drawRectangle(temp, image.createGraphics(), Color.red);
				}else if(e.getButton() == MouseEvent.BUTTON3){
					rectClosed = true;
					File file = new File(mapPath);
					BufferedImage image = null;
					try {
						image = ImageIO.read(file);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					lblNewLabel.setIcon(new ImageIcon(image));
					if(rectCords.size() > 0){
						rectCords.add(rectCords.get(0));
						rectCords.add(rectCords.get(1));
					}
					double[] temp = new double[rectCords.size()];
					for(int i = 0; i < temp.length; i++){
						temp[i] = rectCords.get(i);
					}
					drawRectangle(temp, image.createGraphics(), Color.red);
				}
			}else if(rdbtnNewRadioButton_4.isSelected() || rdbtnNewRadioButton.isSelected()){
				surroundingPointSelected  = true;
				surroundingQueryPoint[0] = e.getX();
				surroundingQueryPoint[1] = e.getY();
				File file = new File(mapPath);
				BufferedImage image = null;
				try {
					image = ImageIO.read(file);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				lblNewLabel.setIcon(new ImageIcon(image));
				String sqlNearest = getSelectSQL(new String[]{"announcementSystems B"}, new String[]{"B.shape"}, new String[]{generateSDO_NN("B.center", generatePointGeoType(surroundingQueryPoint), 1) + " = 'TRUE'"});
				textArea.append("Query " + queryCount++
						+ ": " + sqlNearest + "\n");
				try {
					for (double[] cords : getAllCircleCords(sqlNearest)) {
						drawCircleAndSquare(cords, image.createGraphics(), Color.red, 15);
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			//drawCircleAndSquare(new double[]{e.getX(), e.getY(), 50}, image.createGraphics(), Color.red, 5);
		}

		public void mouseMoved(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			lblNewLabel_2.setText(String.valueOf(x));
			label.setText(String.valueOf(y));
		}

		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}
	}
}
