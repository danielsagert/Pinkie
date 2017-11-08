package de.altenerding.biber.pinkie.business.login.boundary;

import de.altenerding.biber.pinkie.business.login.control.Authenticator;
import de.altenerding.biber.pinkie.business.login.control.LoginCreator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;

@Stateless
public class LoginService implements Serializable {

	private Authenticator authenticator;
	private LoginCreator loginCreator;

	public boolean login(String alias, String password) throws IOException {
		return authenticator.login(alias, password);
	}

	//Will be used later to set and create password for members
	public void createLogin(String alias, String password) throws Exception {
		loginCreator.createLogin(alias, password);
	}

	@Inject
	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	@Inject
	public void setLoginCreator(LoginCreator loginCreator) {
		this.loginCreator = loginCreator;
	}
}