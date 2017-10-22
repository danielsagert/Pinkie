package de.altenerding.biber.pinkie.presentation.member;

import de.altenerding.biber.pinkie.business.members.bounday.MemberService;
import de.altenerding.biber.pinkie.business.members.entity.Member;
import org.apache.logging.log4j.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class MemberBean implements Serializable {

	private MemberService memberService;
	private Logger logger;

	private Member member;

	public Member getMember() {
		return member;
	}

	@Inject
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}