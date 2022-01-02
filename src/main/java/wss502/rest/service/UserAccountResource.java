package wss502.rest.service;

import java.util.List;

// Prepei na valume ta .jar files sto webapp>WEB-INF>lib gia na ta vriskei
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/* To rest service mas gia diaxirisi tou UserAccount ektos - Tha borousame na to kalesoume apo
 * opou dipote e.g. Android 
 * 
 * Gia na kanei kapoios access to service (e.g., GET request) mas xreizete na kalesei to
 * path: http://localhost/WSS502_RestService/rest/useraccount
 */
@Path("/useraccount")
public class UserAccountResource {

	// Instance tou Service mas
	UserAccountDAO dao = new UserAccountDAO();

	/*
	 * Pernume ola ta user accounts.
	 * 
	 * To Produces(...) mas ipodilonei oti epistrefei ta UserAccounts eite ipo tin
	 * morfi JSON eite ipo tin morfi XML
	 */
	// GET:: http://localhost/WSS502_RestService/rest/useraccount
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<UserAccount> findAll() {
		System.out.println("findAll"); // Sto server side tha to tiposoume
		return dao.findAll();
	}

	/*
	 * Pernoume ta user accounts me to sigkekrimeno firstname
	 * 
	 * Pernei san input to onoma {name} kai @PathParam("name")
	 */
	// GET:: http://localhost/WSS502_RestService/rest/useraccount/search/{name}
	@GET
	@Path("search/{name}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<UserAccount> findByName(@PathParam("name") String name) {
		System.out.println("findByName: " + name);
		return dao.findByName(name);
	}

	@GET
	@Path("exists/{email}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<UserAccount> emailExists(@PathParam("email") String email) {
		System.out.println("emailExists: " + email);
		return dao.emailExists(email);
	}
	
	/*
	 * Dimiourgoume neo user account. Exoun idio url me to proto method all to ena
	 * einai GET kai to allo POST ara den ta berdevei.
	 * 
	 * To Consumes(...) mas ipodilonei oti dexete san input ena UserAccount ipo tin
	 * morfi Json i ipo tin morfi XML.
	 * 
	 * To Produces(...) mas ipodilonei oti epistrefei to neo UserAccount eite ipo
	 * tin morfi JSON eite ipo tin morfi XML
	 */
	// POST:: http://localhost/WSS502_RestService/rest/useraccount
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public UserAccount addUserAccount(UserAccount user_account) {
		System.out.println("creating user_account");
		return dao.addUserAccount(user_account);
	}

	@POST
	@Path("login")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<UserAccount> loginUserAccount(UserAccount user_account) {
		System.out.println("login user_account");
		return dao.loginUserAccount(user_account);
	}
	

	@POST
	@Path("rates")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public void updateRates(String rates) {
		System.out.println("updating rates");
		dao.updateRates(rates);
	}
}