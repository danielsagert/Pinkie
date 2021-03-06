package de.altenerding.biber.pinkie.presentation.filter;

import de.altenerding.biber.pinkie.business.login.boundary.AuthenticateService;
import de.altenerding.biber.pinkie.business.members.entity.Member;
import de.altenerding.biber.pinkie.business.members.entity.Role;
import de.altenerding.biber.pinkie.presentation.session.UserSessionBean;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/secure/dean/*", "/secure/referee/*", "/secure/admin/*"}, filterName = "AdminFilter")
public class AdminFilter implements Filter{

	@Inject
	private AuthenticateService authenticateService;
	@Inject
	private UserSessionBean userSessionBean;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		Member member = userSessionBean.getMember();
		if (authenticateService.authenticateLoggedInMemberRole(member, Role.ADMIN)) {
			chain.doFilter(request, response);
		} else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	@Override
	public void destroy() {

	}
}
