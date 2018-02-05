package de.altenerding.biber.pinkie.business.report.control;

import de.altenerding.biber.pinkie.business.report.entity.Report;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ReportProvider {

	@PersistenceContext
	private EntityManager em;
	private Logger logger;

	public List<Report> getReports() {
		logger.info("Loading last 30 reports");
		return getReports(30);
	}

	public List<Report> getLatestReports() {
		logger.info("Loading 5 latest reports");
		return getReports(5);
	}

	private List<Report> getReports(int maxResults) {
		return em.createNamedQuery("Report.findAll", Report.class).setMaxResults(maxResults).getResultList();
	}

	public Report getReportById(long reportId) {
		logger.info("Loading report with id={}", reportId);
		return em.createNamedQuery("Report.findById", Report.class).setParameter("id", reportId).getSingleResult();
	}

	public List<Report> getReportsForTeam(long teamId, long seasonId) {
		logger.info("Getting reports for teamId={} and seasonId={}", teamId, seasonId);
		return em.createNamedQuery("Report.findByTeamIdSeasonID", Report.class)
				.setParameter("teamId", teamId)
				.setParameter("seasonId", seasonId)
				.getResultList();
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}
