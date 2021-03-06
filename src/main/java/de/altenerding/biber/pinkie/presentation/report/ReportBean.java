package de.altenerding.biber.pinkie.presentation.report;

import de.altenerding.biber.pinkie.business.file.boundary.FileService;
import de.altenerding.biber.pinkie.business.file.entity.FileCategory;
import de.altenerding.biber.pinkie.business.file.entity.Image;
import de.altenerding.biber.pinkie.business.members.entity.Access;
import de.altenerding.biber.pinkie.business.members.entity.Role;
import de.altenerding.biber.pinkie.business.report.boundary.ReportService;
import de.altenerding.biber.pinkie.business.report.entity.Report;
import de.altenerding.biber.pinkie.presentation.session.UserSessionBean;
import net.bootsfaces.utils.FacesMessages;
import org.apache.logging.log4j.Logger;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class ReportBean implements Serializable {

	private Logger logger;
	private ReportService reportService;

	@Inject
	private UserSessionBean userSessionBean;
	private long reportId;
	private Part file;

	private List<Report> reports;
	private Report report = new Report();
	private FileService fileService;
    private List<Report> latestReports;

    public void initReports() {
		if (reports == null) {
			try {
				reports = reportService.getReports();
			} catch (Exception e) {
				logger.error("Error while loading all reports", e);
				FacesMessages.error("Es ist ein Fehler beim laden der Berichte aufgetreten: " + e.getMessage());
			}
		}
	}

	public void initReport() {
		logger.info("Loading report with id={}", reportId);
		report = reportService.getReportById(reportId);
	}

	@Access(role = Role.PRESS)
	public String saveReport() {
		logger.info("Creating new Report with title={}", report.getTitle());
		String result;
		try {
			report.setAuthor(userSessionBean.getMember());

			if (file != null) {
				Image image = fileService.uploadImage(file, FileCategory.IMAGES_REPORT);
				report.setImage(image);
			}

			reportService.createReport(report);

			FacesMessages.info("Bericht erstellt", "Bitte beachten, dass der Bericht erst noch freigegeben werden muss, bevor er auf der Homepage erscheint");
			result = "/public/news/report.xhtml?faces-redirect=true";
		} catch (Exception e) {
            logger.error("Error while creating report", e);
			FacesMessages.error("Fehler beim speichern");
			result = "/secure/report/reportAdd.xhtml?faces-redirect=true";
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return result;
	}

	@Access(role = Role.PRESS)
	public String removeImage(Report report) {
		logger.info("Removing image from report with id={}", report.getId());
		try {
			report.setImage(null);
			reportService.updateReport(report);
			FacesMessages.info(report.getType().getLabel(), "Bild entfernt");
		} catch (Exception e) {
			logger.error("Error while removing image from report", e);
			FacesMessages.error("Fehler beim entfernen des Bildes");
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "removedReportImage";
	}

	@Access(role = Role.PRESS)
	public String updateReport() {
		logger.info("Updating report with id={}", report.getId());
		String result;
		try {
			if (file != null) {
				Image image = fileService.uploadImage(file, FileCategory.IMAGES_REPORT);
				report.setImage(image);
			}

			reportService.updateReport(report);

			FacesMessages.info(report.getType().getLabel(), "Aktualisiert");
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getFlash().setKeepMessages(true);
			result = "/public/news/report.xhtml?faces-redirect=true";
		} catch (Exception e) {
			logger.error("Error while creating report", e);
			FacesMessages.error("Fehler beim speichern");
			result = "/secure/report/reportEdit.xhtml?faces-redirect=true";
		}
		return result;
	}

	@Access(role = Role.ADMIN)
	public void deleteReport(Report report) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		try {
			reportService.deleteReport(report);
			FacesMessages.info("Bericht gelöscht", report.getTitle());

		} catch (Exception e) {
			logger.error("Error while deleting report with id={}", report.getId());
			FacesMessages.error("Fehler beim löschen des Berichts", report.getTitle());
		}
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Inject
	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	@Inject
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public Report getReport() {
		return report;
	}

	public List<Report> getReports() {
        if (reports == null) {
            reports = reportService.getReports();
        }
		return reports;
	}

	public void setUserSessionBean(UserSessionBean userSessionBean) {
		this.userSessionBean = userSessionBean;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public Part getFile() {
		return file;
	}

    public List<Report> getLatestReports() {
		if (latestReports == null) {
			latestReports = reportService.getLatestReports();
		}
			return latestReports;
    }
}
