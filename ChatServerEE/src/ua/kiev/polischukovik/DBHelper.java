package ua.kiev.polischukovik;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DBHelper {
	private static final String DRIVER_SQLITE_JDBC = "org.sqlite.JDBC";
	private static final String DB_FILENAME = "data.db";
	private static final String JDBC_SQLITE_DATA_DB = "jdbc:sqlite:" + DB_FILENAME;	
	private static Connection conn = null;
	private static Statement sqlStatement;

	public static void init() {
		int reconnectAttempt = 0;
		createConnection();
		try {
			createDB();
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find sqlite driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("SQL exception:");
			e.printStackTrace();
		}
		
	}

	public static boolean checkCredentials(String login, String password) {
		List<Account> list = null;
		
		try {
			list = getAccounts().stream().filter(t -> t.getLogin().equals(login)).collect(Collectors.toList());
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find sqlite driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("SQL exception:");
			e.printStackTrace();
		}
		
		if(list == null || list.size() == 0){
			return false;
		}
		
		return list.get(0).getPassword().equals(password);				
	}

	public static boolean createAccount(String login, String password) {		
		List<Account> list = null;
		
		try {
			list = getAccounts().stream().filter(t -> t.getLogin().equals(login)).collect(Collectors.toList());
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find sqlite driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("SQL exception:");
			e.printStackTrace();
		}
		
		if(list == null || list.size() == 0){
			try {
				addAccount(login, password);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
	
	public static boolean createConnection() {
		try {
			Class.forName(DRIVER_SQLITE_JDBC);
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find sqlite driver");
			return false;
		}
		
		try{
			File dbFile = new File(DB_FILENAME);
			if(!dbFile.exists()){
				dbFile.createNewFile();
			}
			conn = DriverManager.getConnection(JDBC_SQLITE_DATA_DB);
		}catch(SQLException e){
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error creating file");
			e.printStackTrace();
		}
		   
		return conn != null;
	   }
	
	public static void createDB() throws ClassNotFoundException, SQLException{
		if(conn != null){
			sqlStatement = conn.createStatement();
			sqlStatement.execute("CREATE TABLE if not exists ACCOUNTS (ID INTEGER PRIMARY KEY AUTOINCREMENT, LOGIN text, PASSWORD text);");
		}		
	}
	
	public static void addAccount(String login, String password) throws SQLException{
		sqlStatement.execute(String.format("INSERT INTO ACCOUNTS (LOGIN, PASSWORD) VALUES ('%s', '%s'); ", login, password));
	}
	
	public static ArrayList<Account> getAccounts() throws ClassNotFoundException, SQLException {
		ResultSet resSet = sqlStatement.executeQuery("SELECT ID, LOGIN, PASSWORD FROM ACCOUNTS");
		ArrayList<Account> accounts = new ArrayList<>();
		
		while(resSet.next()) {
			int id = resSet.getInt("ID");
			String  login = resSet.getString("LOGIN");
			String  password = resSet.getString("PASSWORD");
			accounts.add(new Account(login, password));
		}
		
		return accounts;
	}
	
	public static void CloseDB() throws ClassNotFoundException, SQLException {
		conn.close();
		sqlStatement.close();
	}

}
