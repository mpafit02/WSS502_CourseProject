package wss502.rest.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/* Class gia Database Access Object (DAO) meso tou opoiou boroume na paroume access sto UserAccount
 * table apo to Database mas. Diaxirisi tou UserAccount class sto Dabatase
*/
public class UserAccountDAO {

	// Diaxirisi Database - Get all the User Accounts from the database
	public List<UserAccount> findAll() {
		List<UserAccount> list = new ArrayList<UserAccount>();
		Connection c = null;
		String sql = "SELECT * FROM UserAccount ORDER BY user_firstname";
		try {
			// Create a connection
			c = ConnectionHelper.getConnection();
			// Create a statement
			Statement s = c.createStatement();
			// Ola ta rows apo to UserAccounts table
			ResultSet rs = s.executeQuery(sql);

			// Prosfthetume ena neo User Account stin lista mas analoga me ta rows pou
			// peirame
			while (rs.next()) {
				list.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return list;
	}

	// Diaxirisi Database - Find a UserAccount in the Database given his firstname
	public List<UserAccount> findByName(String user_firstname) {

		// Epistrefoume lista giati borei na iparxoun perissoteroi apo ena accounts me
		// afto to firstname
		List<UserAccount> list = new ArrayList<UserAccount>();

		Connection c = null;

		// Xrisi tis entolis LIKE gia na sigkrinume string. To erotimatiko
		// antikathistate me to string mas argotera meto toy PreparedStatement mas. Tha
		// borousame na exoume perissotera apo 1 erotimatika.
		String sql = "SELECT * FROM UserAccount as e " + "WHERE UPPER(user_firstname) LIKE ? "
				+ "ORDER BY user_firstname";

		try {
			c = ConnectionHelper.getConnection();
			// Xrisimopoioume to PreparedStatement anti Statement giati tha to allaksume
			// argotera
			PreparedStatement ps = c.prepareStatement(sql);

			// Antikathistume to 1o erotimatiko me to firstname pou psaxnoume
			ps.setString(1, "%" + user_firstname.toUpperCase() + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return list;
	}

	public List<UserAccount> emailExists(String user_email) {
		List<UserAccount> list = new ArrayList<UserAccount>();

		Connection c = null;

		String sql = "SELECT * FROM UserAccount as e " + "WHERE UPPER(user_email) LIKE ? " + "ORDER BY user_email";

		try {
			c = ConnectionHelper.getConnection();

			PreparedStatement ps = c.prepareStatement(sql);

			ps.setString(1, "%" + user_email.toUpperCase() + "%");

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return list;
	}

	private String encryptPassword(String email, String password) {
		byte[] salt = email.getBytes();
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = factory.generateSecret(spec).getEncoded();
			return new String(hash);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	// Diaxirisi Database - Create a new UserAccount in the database
	public UserAccount addUserAccount(UserAccount user_account) {
		System.out.println("addUserAccount");
		Connection c = null;
		PreparedStatement ps = null;
		try {
			// Create the connection
			c = ConnectionHelper.getConnection();

			// PreparedStatement anti Statement giati tha to allaksume argotera
			ps = c.prepareStatement("INSERT INTO UserAccount "
					+ "(user_firstname, user_lastname, user_email, password) " + "VALUES (?, ?, ?, ?)",
					new String[] { "ID" });

			String hashedPassword = encryptPassword(user_account.getUser_email(), user_account.getPassword());
			// Antikathistume ta erotimatika me ta values tou neou User Account
			ps.setString(1, user_account.getUser_firstname());
			ps.setString(2, user_account.getUser_lastname());
			ps.setString(3, user_account.getUser_email());
			ps.setString(4, hashedPassword);

			// Execute to PreparedStatement
			ps.executeUpdate();

			// Pernume piso to id (stin periptosi mas mono ena exei)
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();

			// Update the id in the returned object. This is important as the value is
			// returned to the client.
			int id = rs.getInt(1);
			user_account.setUser_id(id);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return user_account;
	}

	public List<UserAccount> loginUserAccount(UserAccount user_account) {
		List<UserAccount> list = new ArrayList<UserAccount>();

		Connection c = null;

		String sql = "SELECT * FROM UserAccount as e " + "WHERE UPPER(user_email) LIKE ? AND password LIKE ?";

		try {
			c = ConnectionHelper.getConnection();

			PreparedStatement ps = c.prepareStatement(sql);

			String hashedPassword = encryptPassword(user_account.getUser_email(), user_account.getPassword());

			// System.out.println("Hashed Password: " + hashedPassword);

			ps.setString(1, "%" + user_account.getUser_email().toUpperCase() + "%");
			ps.setString(2, hashedPassword);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(processRow(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			ConnectionHelper.close(c);
		}
		return list;
	}

	public void updateRates(String rates) {
		JSONObject json;
		try {
			System.out.println(rates);
			json = new JSONObject(rates);

			Connection c = null;

			double eur_usd = 1;
			double eur_gbp = 1;
			double usd_eur = 1;
			double gbp_eur = 1;
			try {
				eur_usd = json.getDouble("eur_usd");
				eur_gbp = json.getDouble("eur_gbp");
				usd_eur = json.getDouble("usd_eur");
				gbp_eur = json.getDouble("gbp_eur");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String sql1 = "UPDATE rates SET current_rate = " + eur_usd + " WHERE rateID = 1";
			String sql2 = "UPDATE rates SET current_rate = " + eur_gbp + " WHERE rateID = 2";
			String sql3 = "UPDATE rates SET current_rate = " + usd_eur + " WHERE rateID = 3";
			String sql4 = "UPDATE rates SET current_rate = " + gbp_eur + " WHERE rateID = 4";

			System.out.println(sql1);
			System.out.println(sql2);
			System.out.println(sql3);
			System.out.println(sql4);
			try {
				c = ConnectionHelper.getConnection();
				
			    Statement stmt1  = c.createStatement();
			    Statement stmt2  = c.createStatement();
			    Statement stmt3  = c.createStatement();
			    Statement stmt4  = c.createStatement();
			    
			    stmt1.executeUpdate(sql1);
			    stmt2.executeUpdate(sql2);
			    stmt3.executeUpdate(sql3);
			    stmt4.executeUpdate(sql4);

			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				ConnectionHelper.close(c);
			}
			System.out.println(json);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	// Helper function
	protected UserAccount processRow(ResultSet rs) throws SQLException {
		// Dimiourgoume ena userAccount stin java me vasi ta dedomena enos row apo tin
		// database
		UserAccount user_account = new UserAccount();
		user_account.setUser_id(rs.getInt("user_id"));
		user_account.setUser_firstname(rs.getString("user_firstname"));
		user_account.setUser_lastname(rs.getString("user_lastname"));
		user_account.setUser_email(rs.getString("user_email"));
		user_account.setPassword(rs.getString("password"));
		user_account.setRepeat_password(rs.getString("password"));
		return user_account;
	}

}