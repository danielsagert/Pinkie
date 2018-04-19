package de.altenerding.biber.pinkie.presentation.member;

import de.altenerding.biber.pinkie.business.login.boundary.AuthenticateService;
import de.altenerding.biber.pinkie.business.members.bounday.MemberService;
import de.altenerding.biber.pinkie.business.members.entity.Access;
import de.altenerding.biber.pinkie.business.members.entity.Member;
import de.altenerding.biber.pinkie.business.members.entity.Role;
import de.altenerding.biber.pinkie.business.notification.boundary.NotificationService;
import net.bootsfaces.utils.FacesMessages;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ViewScoped
public class PasswordBean implements Serializable {

	private AuthenticateService authenticateService;
	private NotificationService notificationService;
	private MemberService memberService;
	private Logger logger;
	private long memberId;
	private Member member;
	private String passwordRetype;
	private String passwordOld;
	private String passwordNew;
	private String email;

	public void initMember() {
		member = memberService.getMemberById(memberId);
	}

	@Access(role = Role.ADMIN)
	public String resetPassword() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);

		try {
			if (!validateRetypePassword()) {
				return "/secure/admin/editMemberPassword.xhtml?faces-redirect=true&includeViewParams=true&memberId=" + memberId;
			}

			authenticateService.setOnetimePassword(member.getEmail(), passwordNew);

			notificationService.sendPasswortResetEmail(member, passwordNew);

			FacesMessages.info(member.getFullName(), "Passwort neu gesetzt");

			return "/secure/admin/listMembers.xhtml?faces-redirect=true";
		} catch (Exception e) {
			logger.info("Error while resetting password", e);
			FacesMessages.error("Es ist ein Fehler beim Setzten des Passworts aufgetreten");
			return "/secure/admin/editMemberPassword.xhtml?faces-redirect=true&includeViewParams=true&memberId=" + memberId;
		}
	}

	@Access(role = Role.MEMBER)
	public String changePassword() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);

		if (!validateRetypePassword()) {
			return "/secure/profile/changePassword.xhtml?" +
					"faces-redirect=true&includeViewParams=true&memberId=" + memberId;
		} else {
			try {
				authenticateService.changePassword(member.getEmail(), passwordOld, passwordNew);

				FacesMessages.info(member.getFullName(), "Passwort geändert");

				return "/secure/profile/profile.xhtml?faces-redirect=true&includeViewParams=true&memberId=" + memberId;
			} catch (Exception e) {
				logger.error("Error while changing password", e);
				return "/secure/profile/changePassword.xhtml?" +
						"faces-redirect=true&includeViewParams=true&memberId=" + memberId;
			}
		}
	}

	public String requestOneTimePassword() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);

		Member member = memberService.getMemberByEmail(email);

		if (member == null) {
			FacesMessages.error("Kein Nutzer mit dieser E-Mail Adresse verfügbar");
			return "";
		}

		if (StringUtils.isEmpty(member.getPrivateEmail())) {
			FacesMessages.error("Dieses Mitglied hat keine private E-Mail Adresse hinterlegt. Bitte den Admin kontaktieren");
			return "";
		}

		try {
			String oneTimePassword = authenticateService.changeForgotPassword(member.getEmail());
			notificationService.sendPasswortResetEmail(member, oneTimePassword);
		} catch (Exception e) {
			logger.error("Error creating one time password", e);
			FacesMessages.error("Es ist ein Fehler beim senden des Passworts aufgetreten");
			return "";
		}


		FacesMessages.info("Es wurde ein neues Passwort an deine private E-Mail Adresse gesendet");
		return "startpage";
	}

	public boolean hasPrivateEmail() {
		return StringUtils.isEmpty(member.getPrivateEmail());
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean validateRetypePassword() {
		if (!StringUtils.equals(passwordNew, passwordRetype)) {
			FacesMessages.error("Die eingegebenen Passwörter stimmen nicht überein!");
			return false;
		}
		return true;
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Inject
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Inject
	public void setAuthenticateService(AuthenticateService authenticateService) {
		this.authenticateService = authenticateService;
	}

	@Inject
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	public String getPasswordOld() {
		return passwordOld;
	}

	public void setPasswordOld(String passwordOld) {
		this.passwordOld = passwordOld;
	}

	public String getPasswordRetype() {
		return passwordRetype;
	}

	public void setPasswordRetype(String passwordRetype) {
		this.passwordRetype = passwordRetype;
	}

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	public String getPasswordNew() {
		return passwordNew;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
