package de.altenerding.biber.pinkie.business.login.control;

import de.altenerding.biber.pinkie.business.login.entity.Login;
import de.altenerding.biber.pinkie.business.members.entity.Member;
import de.altenerding.biber.pinkie.business.members.entity.Role;
import de.altenerding.biber.pinkie.presentation.login.SessionBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.List;

public class Authenticator implements Serializable{

	private SessionBean sessionBean;
	private SecurityProvider securityProvider;
	private Logger logger;
	@PersistenceContext
	private EntityManager em;

	public boolean login(String alias, String password) throws IOException {
		logger.info("Checking login credentials for alias={}", alias);
		List<Login> logins = em.createNamedQuery("Login.getByAlias", Login.class).setParameter("alias", alias).getResultList();

		if (logins.size() < 1) {
			logger.error("No login credentials found for alias={}", alias);
			return false;
		}

		Login login = logins.get(0);

		if (login.getLoginCount() >= 3) {
			logger.error("Login tries already at {}", login.getLoginCount());
			return false;
		}

		byte[] salt = Base64.getDecoder().decode(login.getSalt());
		String hashedPassword = securityProvider.hashPassword(password.toCharArray(), salt);

		if (StringUtils.equals(login.getPassword(), hashedPassword)) {
			login.setLoginCount(0);
			return true;
		} else {
			login.setLoginCount(login.getLoginCount() + 1);
			return false;
		}
	}

	public boolean authenticateRole(Role role) throws Exception{
		if (!sessionBean.getIsLoggedIn()) {
			return false;
		}

		Member member = sessionBean.getMember();

		if (member.getRole() == Role.ADMIN) {
			return true;
		}

		//noinspection SimplifiableIfStatement
		if (member.getRole() == Role.PRESS && (role == Role.PRESS || role == Role.MEMBER)) {
			return true;
		}

		return member.getRole() == role;

	}

	@Inject
	public void setSecurityProvider(SecurityProvider securityProvider) {
		this.securityProvider = securityProvider;
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Inject
	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}
}
