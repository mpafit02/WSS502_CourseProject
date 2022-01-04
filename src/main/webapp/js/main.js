// The root URL for the RESTful service
var rootURL = "http://localhost:8080/WSS502_RestService/rest/useraccount";
var loggedIn = false;
// Retrieve wine list when application starts 
// findAll();
$(document).ready(function() {
	console.log("Hidding elements");
	$('#register_container').hide();
	$('#main_container').hide();
});

function updateRates(data) {
	console.log(data);
	var eur_usd = data.rates.USD;
	var eur_gbp = data.rates.GBP;
	var usd_eur = 1 / data.rates.USD;
	var gbp_eur = 1 / data.rates.GBP;

	console.log(eur_usd);
	console.log(eur_gbp);
	console.log(usd_eur);
	console.log(gbp_eur);

	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: rootURL + '/rates',
		dataType: "json",
		data: ratesToJSON(eur_usd, eur_gbp, usd_eur, gbp_eur),
		success: function(data, textStatus, jqXHR) {
			console.log("Rates updated successfully");
		},
		error: function(jqXHR, textStatus, errorThrown) {
		}
	});
}

function findAll() {
	console.log('findAll (GET)' + rootURL);
	$.ajax({
		type: 'GET',
		url: rootURL,
		dataType: "json", // data type of response
		success: renderList
	});
}

function findByName() {
	// Take the name from the field with id #name
	var name = $('#name').val();
	console.log('findByName: ' + rootURL + '/search/' + name);

	// Get a list of users with name equals to the name variable given as input and call renderList to render their details
	$.ajax({
		type: 'GET',
		url: rootURL + '/search/' + name,
		dataType: "json",
		success: renderList
	});
}

function clearForm() {
	var userAccount = {};
	renderDetails(userAccount); // Display empty form
}

function loginPage() {
	$('#register_container').hide();
	$('#login_container').show();
}

function registerPage() {
	$('#register_container').show();
	$('#login_container').hide();
}

function logoutUser() {
	$('#main_container').hide();
	$('#login_container').show();
	$('#inputFrom').val('0');
	$('#inputTo').val('0');
	$('#convert_message').text('');
	$('#inputFromField').val('');
	$('#outputFieldRate').text('');
	$('#outputField').text('0.00');
	loggedIn = false;
}

// Add a new user account by using a POST function
function addUserAccount() {
	console.log('addUserAccount (POST)' + rootURL);

	$("#userAccountForm").submit(function(e) {
		e.preventDefault();
	});
	$('#register_message').text("");
	if ($('#password').val() === $('#repeat_password').val()) {
		// Check if email already exists
		var email = $('#user_email').val();
		$.ajax({
			type: 'GET',
			url: rootURL + '/exists/' + email,
			dataType: "json",
			success: function(data, textStatus, jqXHR) {
				console.log('User emails received');

				var list = data == null ? [] : (data instanceof Array ? data : [data]);

				console.log("Users with this email: " + list.length.toString());

				if (list.length == 0) {
					console.log("Creating user");
					$.ajax({
						type: 'POST',
						contentType: 'application/json',
						url: rootURL,
						dataType: "json",
						data: formToJSON(),
						success: function(data, textStatus, jqXHR) {
							$('#register_message').text("");
							alert('UserAccount created successfully');

							// $('#UserId').val(data.id);

							$('#register_container').hide();
							$('#login_container').show();
						},
						error: function(jqXHR, textStatus, errorThrown) {
						}
					});
				} else {
					console.log("Email already exists");
					$('#register_message').text("Email already exists!");
				}
			},
		});

	} else {
		// alert('Passwords do not match!');
		$('#register_message').text("Password and repeat password do not match!!!");
		// $("#message").val("Repeat password does not match password!");
	}
}

function loginUserAccount() {
	console.log('loginUserAccount (POST) ' + rootURL + "/login");

	$("#loginForm").submit(function(e) {
		e.preventDefault();
	});

	console.log("Login user");
	$.ajax({
		type: 'POST',
		contentType: 'application/json',
		url: rootURL + "/login",
		dataType: "json",
		data: formToJSONLogin(),
		success: function(data, textStatus, jqXHR) {
			console.log(data)
			// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
			var list = data == null ? [] : (data instanceof Array ? data : [data]);

			console.log(list)
			console.log(list.length)
			console.log(list.length == 1)
			if (list.length == 1) {
				// Clear the html list with id #userAccountsList
				$('#userAccountsListMain li').remove();
				// Create the new list in html
				$.each(list, function(index, userAccount) {
					$('#welcomeUser').text('Hello ' + userAccount.user_firstname + '!');

					alert(userAccount.user_firstname + ' logged in successfully');
				});

				$.getJSON('http://api.exchangeratesapi.io/v1/latest?access_key=44701c74d0733496222c23ea28102e9c&format=1&symbols=USD,GBP').success(updateRates);

				$('#login_container').hide();
				$('#main_container').show();

				$('#user_email_login').val("");
				$('#password_login').val("");
				loggedIn = true;

			} else {
				alert('Wrong credentials!');
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
		}
	});
}

function convertCurrency() {

	$("#convertForm").submit(function(e) {
		e.preventDefault();
	});

	var fromC = $('#inputFrom').find(":selected").val();
	var toC = $('#inputTo').find(":selected").val();

	var rate = 1;
	var toRequest = true;
	var comboName = "";
	$('#convert_message').text('');
	var amount = $('#inputFromField').val();

	if (amount === "") {
		$('#convert_message').text('Please provide an amount to convert!');
		toRequest = false;
	} else if (fromC === "EUR" && toC === "USD") {
		comboName = "eur_usd";
	} else if (fromC === "EUR" && toC === "GBP") {
		comboName = "eur_gbp";
	} else if (fromC === "USD" && toC === "EUR") {
		comboName = "usd_eur";
	} else if (fromC === "GBP" && toC === "EUR") {
		comboName = "gbp_eur";
	} else {
		$('#convert_message').text('Not supported rates!');
		toRequest = false;
	}

	if (toRequest) {
		$.ajax({
			type: 'GET',
			url: rootURL + '/rates/' + comboName,
			dataType: "json",
			success: function(data, textStatus, jqXHR) {
				console.log(data);
				rate = data;
				var result = amount * rate;
				$('#outputField').text(result.toFixed(2));
				$('#outputFieldRate').text(" rate: " + rate.toFixed(2));
			},
		});
	}
}

// Convert the array of UserAccounts to an html list
function renderList(data) {
	// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
	var list = data == null ? [] : (data instanceof Array ? data : [data]);

	// Clear the html list with id #userAccountsList
	$('#userAccountsList li').remove();
	// Create the new list in html
	$.each(list, function(index, userAccount) {
		$('#userAccountsList').append('<li><a href="#" data-identity="' + userAccount.user_id + '">' + userAccount.user_firstname + '</a></li>');
		// Display details of each UserAccount in html
		renderDetails(userAccount);
	});
}

// Update ta values sta sigkekrimena ids
function renderDetails(userAccount) {
	// $('#UserId').val(userAccount.user_id);
	$('#user_firstname').val(userAccount.user_firstname);
	$('#user_lastname').val(userAccount.user_lastname);
	$('#user_email').val(userAccount.user_email);
	$('#password').val(userAccount.password);
	$('#repeat_password').val(userAccount.repeat_password);
}

function rateToJSON(comboName) {
	return JSON.stringify({
		"combo_name": comboName,
	});
}

function ratesToJSON(eur_usd, eur_gbp, usd_eur, gbp_eur) {
	return JSON.stringify({
		"eur_usd": eur_usd,
		"eur_gbp": eur_gbp,
		"usd_eur": usd_eur,
		"gbp_eur": gbp_eur,
	});
}

// Helper function to serialize all the form fields into a JSON string
function formToJSON() {
	return JSON.stringify({
		"user_firstname": $('#user_firstname').val(),
		"user_lastname": $('#user_lastname').val(),
		"user_email": $('#user_email').val(),
		"password": $('#password').val(),
		"repeat_password": $('#repeat_password').val()
	});
}

// Helper function to serialize all the form fields into a JSON string
function formToJSONLogin() {
	return JSON.stringify({
		"user_email": $('#user_email_login').val(),
		"password": $('#password_login').val(),
	});
}