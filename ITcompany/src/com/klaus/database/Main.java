package com.klaus.database;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Main {

	// MENU strings
	private static final String _7_ALL_DEPENDENTS_AGE_PARENTS_NAME_PARENTS_SENIORITY_WHERE_PARENT_IS_FEMALE = "F7 All Dependents: Age, parents name, parents seniority - where parent is female";
	private static final String _6_ALL_EMPLOYEES_CONTATENATING_STRINGS = "F6 All employees: Contatenating strings";
	private static final String _5_ALL_EMPLOYEES_DOB_DEPARTMENT_NAME_MANAGER_S_NAME_WITH_LESS_THAN_5_YEARS_TO_RETIRE = "F5 All employees: DOB, department name, manager's name - with less than 5 years to retire";
	private static final String _4_ALL_FEMALE_EMPLOYEES_SALARY_AND_TAX = "F4 All Female employees: salary and tax";
	private static final String _3_SSN_NAMES_DEPARTMENT_NUMBER_OF_DEPENDENTS_TOTAL_MONTHLY_SALARY = "F3 SSN, names, Department, number of dependents, total monthly salary";
	private static final String _2_PROJECTS_WITH_MORE_THAN_2_EMPLOYEES_WORKING_ON_IT = "F2 Projects with more than 2 employees working on it";
	private static final String _1_ALL_EMPLOYEES_MANAGER_S_NAME_YEARS_EMPLOYED = "F1 All employees: manager's name, years_employed";

	// DB variables
	private static Connection connect = null;
	private static Statement statement = null;
	private static ResultSet resultSet = null;
	private static PreparedStatement preparedStatement = null;
	

	public static final String userName = "klaus";
	public static final String userPass = "klaus";

	private static JMenuItem item1 = new JMenuItem(
			_1_ALL_EMPLOYEES_MANAGER_S_NAME_YEARS_EMPLOYED);
	private static JMenuItem item2 = new JMenuItem(
			_2_PROJECTS_WITH_MORE_THAN_2_EMPLOYEES_WORKING_ON_IT);
	private static JMenuItem item3 = new JMenuItem(
			_3_SSN_NAMES_DEPARTMENT_NUMBER_OF_DEPENDENTS_TOTAL_MONTHLY_SALARY);
	private static JMenuItem item4 = new JMenuItem(
			_4_ALL_FEMALE_EMPLOYEES_SALARY_AND_TAX);
	private static JMenuItem item5 = new JMenuItem(
			_5_ALL_EMPLOYEES_DOB_DEPARTMENT_NAME_MANAGER_S_NAME_WITH_LESS_THAN_5_YEARS_TO_RETIRE);
	private static JMenuItem item6 = new JMenuItem(
			_6_ALL_EMPLOYEES_CONTATENATING_STRINGS);
	private static JMenuItem item7 = new JMenuItem(
			_7_ALL_DEPENDENTS_AGE_PARENTS_NAME_PARENTS_SENIORITY_WHERE_PARENT_IS_FEMALE);

	private static JMenuItem table1 = new JMenuItem("Employee");
	private static JMenuItem table2 = new JMenuItem("Department");
	private static JMenuItem table3 = new JMenuItem("Projects");
	private static JMenuItem table4 = new JMenuItem("Dependents");
	private static JMenuItem table5 = new JMenuItem("Work_on");
	private static JMenuItem table6 = new JMenuItem("Salary");
	
	static Object[][] data;
	
	static Object[][] overAgeData;
	static String[] overAgeColumn;

	private static JTable table;
	private static JFrame mainGUI;
	private static String[] columnNames;
	private static String[] columnNamesRaw;
	private static JScrollPane scrollPane;
	private static JLabel rowsLabel;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Login frame = new Login();
			frame.setSize(300, 100);
			frame.setLocation(800, 400);
			frame.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
//		makeGUI();
	}

	public static void readDataBase(String SQLstatement, boolean checkAge) throws Exception {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/itcompany?"
							+ "user=root&password=1234");
			// Statements allow to issue SQL queries to the database
			preparedStatement = connect.prepareStatement(SQLstatement);
			// Result set get the result of the SQL query
			// resultSet = statement.executeQuery(SQLstatement);
			resultSet = preparedStatement.executeQuery();
			// statement = connect.createStatement();
			// // Result set get the result of the SQL query
			// resultSet = statement.executeQuery(SQLstatement);
			writeResultSet(resultSet, checkAge);
		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}
	}

	public static void makeGUI() {
		mainGUI = new JFrame();
		mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainGUI.setTitle("IT company");
		mainGUI.setSize(1200, 600);
		mainGUI.setLocation(200, 200);

		mainGUI.setLayout(new BorderLayout());
		JMenuBar menuBar = new JMenuBar();
		JMenu menu1 = new JMenu("Queries");
		setupMenuItems();
		menu1.add(item1);
		menu1.add(item2);
		menu1.add(item3);
		menu1.add(item4);
		menu1.add(item5);
		menu1.add(item6);
		menu1.add(item7);
		
		JMenu menu2 = new JMenu("Tables");
		setupTableItems();
		menu2.add(table1);
		menu2.add(table2);
		menu2.add(table3);
		menu2.add(table4);
		menu2.add(table5);
		menu2.add(table6);
		
		menuBar.add(menu2);
		menuBar.add(menu1);
		mainGUI.setJMenuBar(menuBar);
		
		JLabel label = new JLabel("Logged in as: Klaus");
		mainGUI.add(label, BorderLayout.PAGE_START);
		rowsLabel = new JLabel("");
		mainGUI.add(rowsLabel, BorderLayout.PAGE_END);
		
		mainGUI.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_F1){
					item1.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_F2){
					item2.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_F3){
					item3.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_F4){
					item4.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_F5){
					item5.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_F6){
					item6.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_F7){
					item7.doClick();
				}else if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
					System.exit(0);
				}
				
			}
		});
		mainGUI.setFocusable(true);
		mainGUI.setVisible(true);
		
		controlAgeOfEmployees();
	}

	private static void controlAgeOfEmployees() {
		try {
			readDataBase("Select * from employee where age > 45", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void makeTable() {
		// table should be updated
		if (table != null) {
			mainGUI.remove(scrollPane);
			mainGUI.remove(rowsLabel);
			mainGUI.revalidate();
		}
		table = new JTable(data, columnNames);
		table.setEnabled(false); //WORKS, but then no one can copy the data
		
		scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		mainGUI.add(scrollPane, BorderLayout.CENTER);
		rowsLabel = new JLabel("Number of rows: "+data.length);
		mainGUI.add(rowsLabel, BorderLayout.PAGE_END);
		mainGUI.revalidate();
	}

	private static void setupMenuItems() {
		item1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item1.getText());
				try {
					String SQLstatement = "select E.SSN, E.LName, E.Fname, (case when (right(E.SSN, 1)%2=0) then 'Female' else 'Male' end) Sex, E.DeptNo, (select mgr_name from (select E.DeptNo ID, concat_ws(' ', E.FName, E.LName) mgr_name, D.DNumber num from employee E, department D where E.SSN=D.Mgr_SSN) something where num=E.DeptNo) Managers_Name, year(curdate())-year(Emp_start_Date)-(right(curdate(),5)<right(Emp_start_Date,5)) Employed_Years from employee E, department D where D.DNumber=E.DeptNo order by DeptNo;";
					readDataBase(SQLstatement, false);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		item2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item2.getText());
				try {
					String SQLstatement = "select SSN, ProjectNo, (select sum from (select sum(Total_hours_week)+sum(Overtime) sum, W.SSN tSSN from work_on W group by SSN) sums where tSSN=W.SSN) Total_hours_worked from work_on W group by SSN, ProjectNo having ((select count from (select count(SSN) count, SSN tSSN from work_on group by SSN) counts where tSSN=W.SSN)>1) order by SSN";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		item3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item3.getText());
				try {
					String SQLstatement = "select E.SSN, E.FName, E.LName, D.DeptName, E.No_of_dependent, S.Mnth_salary from employee E, department D, salary S where No_of_Dependent>=2 and D.DNumber=E.DeptNo and E.SSN=S.SSN and S.Hrs_worked_wkly>=25;";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		item4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item4.getText());
				try {
					String SQLstatement = "select E.SSN, E.LName, E.FName, E.No_of_Dependent, ((select ttl_hours from (select SSN ssn1, sum(total_hours_week) ttl_hours from Work_on W group by SSN) TOTAL where TOTAL.ssn1=E.SSN) * 4) +(select QU from (select SSN LU, sum(Overtime) QU from Work_on W group by W.SSN) TOTAL where TOTAL.LU=E.SSN) Hours_work_pr_month, (select mnth_salary from (select S.SSN, S.Mnth_salary from salary S) temp where temp.SSN=E.SSN) Monthly_salary, (select Total_Mnth_Tax from (select S.SSN, S.Total_Mnth_Tax from salary S) temp where temp.SSN=E.SSN)*12 Annual_tax_contribution from employee E, salary S, work_on W where right(E.SSN, 1)%2=0 and W.SSN=E.SSN group by E.SSN;";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		item5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item5.getText());
				try {
					String SQLstatement = "select E.SSN, Date_format(E.BDate, '%b-%d, %Y') Date_Of_Birth, D.DeptName, (select mgr_name from (select E.DeptNo ID, concat_ws(' ', E.FName, E.LName) mgr_name, D.DNumber num from employee E, department D where E.SSN=D.Mgr_SSN) something where num=E.DeptNo) Managers_Name, year(curdate())-year(Emp_start_Date)-(right(curdate(),5)<right(Emp_start_Date,5)) Employed_Years from employee E, department D where D.DNumber=E.DeptNo and (year(curdate())-year(Bdate)-(right(curdate(),5)<right(BDate,5)))>40;";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		item6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item6.getText());
				try {
					String SQLstatement = "select SSN, concat(BDate, right(FName, 3)) Birth_date_and_name_concatenated, length(FName) Length_of_first_name, lpad(FName, 20, '*') left_padded_name from employee;";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		item7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item7.getText());
				try {
					String SQLstatement = "select DepName, year(curdate())-year(depDOB)-(right(curdate(),5)<right(depDOB,5)) Age, (select parent_name from (select concat_ws(' ', E.FName, E.LName) parent_name, E.SSN num from employee E, dependents D where D.EmpSSN=E.SSN) tempTab  where num=D.EmpSSN group by D.EmpSSN) Parents_name from dependents D, employee E where E.SSN=D.EmpSSN and right(E.SSN, 1)%2=0;";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void writeResultSet(ResultSet resultSet, boolean checkAge)
			throws SQLException {
		// ResultSet is initially before the first data set
		java.sql.ResultSetMetaData meta = resultSet.getMetaData();
		// get the dimensions of the result
		int noColumns = meta.getColumnCount();
		resultSet.last();
		int noRows = resultSet.getRow();
		resultSet.beforeFirst();

		System.out.println("Number of columns: " + noColumns);
		System.out.println("Number of rows: " + noRows);
		
		columnNamesRaw = new String[noColumns];
		columnNames = new String[noColumns];
		for (int i = 1; i <= noColumns; i++) {
			String columnName = meta.getColumnName(i);
			System.out.println(columnName);
			columnNamesRaw[i - 1] = columnName;
			columnName = updateColumnName(columnName);
			columnNames[i - 1] = columnName.replaceAll("_", " ");
		}
		System.out.println("");

		data = new Object[noRows][noColumns];
		int i = 0;
		while (resultSet.next()) {
			//
			String[] tempArr = new String[noColumns];
			// save the row/tupple in one array (all Strings!!)
			for (int k = 0; k < noColumns; k++) {
				tempArr[k] = resultSet.getString(columnNamesRaw[k]);
			}
			data[i] = tempArr;
			tempArr = null;
			i++;
		}
		if(!checkAge){
			makeTable();
			System.out.println("AFTER MAKE TABLE");
		}else if (noRows>0){
			overAgeData = data;
			overAgeColumn = columnNamesRaw;
			makeAlert(noRows);
		}
		// }
	}// END writeResultSet

	private static void makeAlert(int noRows) {
		for(int i = 0; i< noRows; i++){
			System.out.println(""+i+" - ALERT!");
			JOptionPane.showMessageDialog(mainGUI, "ALERT\n"+
			"Employee:\n"+data[i][2]+"  "+data[i][0]+" "+data[i][1]+
			"\nIs over 45 years of age and needs to be let go!");
		}
}

	private static String updateColumnName(String columnName) {
		if (columnName.equalsIgnoreCase("FName")) {
			columnName = "First name";
		} else if (columnName.equalsIgnoreCase("LName")) {
			columnName = "Last name";
		} else if (columnName.equalsIgnoreCase("DeptName")) {
			columnName = "Department name";
		} else if (columnName.equalsIgnoreCase("mnth_salary")) {
			columnName = "Monthly salary";
		}
		return columnName;
	}

	// You need to close the resultSet
	private static void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	private static void setupTableItems() {
		table1.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String SQLstatement = "select * from employee";
					readDataBase(SQLstatement, false);
	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		table2.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String SQLstatement = "select * from department";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		table3.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String SQLstatement = "select * from projects";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		table4.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String SQLstatement = "select * from dependents";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		table5.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String SQLstatement = "select * from  work_on";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		table6.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(item6.getText());
				try {
					String SQLstatement = "select * from salary";
					readDataBase(SQLstatement, false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
