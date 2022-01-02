package wss502.rest.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

// Connect with a Database
public class ConnectionHelper {

	// Url tou database mas
	private String url;
	private static ConnectionHelper instance;

	// Constructor
	private ConnectionHelper() {

		String driver = null;
		try {
			// ------- 1os tropos (hardcoded)--------

			/*
			 * // Kanoume load to driver pou mas dinei tin dinatotita na enothoume me tin
			 * vasi dedomenon
			 * 
			 * Class.forName("com.mysql.jdbc.Driver");
			 * 
			 * // Define to url tis vasis mas (onoma tis vasis einai WSS502_DB) url =
			 * 
			 * "jdbc:mysql://localhost/WSS502_DB";
			 */

			// ------- 2os tropos (pio sostos - oxi hardcoded) -------

			// Diavazo ta dedomena mou apo to 2SS502_RestServices.properties file
			ResourceBundle bundle = ResourceBundle.getBundle("WSS502_RestService");
			driver = bundle.getString("jdbc.driver");
			Class.forName(driver);
			url = bundle.getString("jdbc.url");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Get to connection me to database gia na to xrisimopoiisoume se alli klasi
	public static Connection getConnection() throws SQLException {
		// Mono ena instance prepei na iparxei tou ConnectionHelper class giafto
		// elegxume an ine null
		if (instance == null) {
			instance = new ConnectionHelper();
		}
		try {
			// Username: root, Password: root
			System.out.println("Trying to connect root root " + instance.url);
			return DriverManager.getConnection(instance.url, "root", "");
		} catch (SQLException e) {
			throw e;
		}
	}

	// Close the database connection
	public static void close(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}