package de.altenerding.biber.pinkie.presentation.referee;

import de.altenerding.biber.pinkie.business.file.boundary.FileService;
import de.altenerding.biber.pinkie.business.file.entity.FileDirectory;
import de.altenerding.biber.pinkie.business.file.entity.FileMapping;
import de.altenerding.biber.pinkie.business.referee.boundary.RefereeService;
import de.altenerding.biber.pinkie.business.referee.entity.Referee;
import net.bootsfaces.utils.FacesMessages;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Named
@RequestScoped
public class RefereeBean implements Serializable {

	private static final String REFEREE_IMAGE_GROUP_PICTURE_KEY = "referees.group.picture";
	private static final String REFEREES_PAGE_NAME = "referees.xhtml";
	@ManagedProperty(value = "#{param.refereeId}")
	private long refereeId;
	private Logger logger;
	private RefereeService refereeService;
	private FileService fileService;
	private List<Referee> referees;
	private Referee referee = new Referee();
	private Part file;
	private FileMapping fileMapping;

	public void initReferee() {
		logger.info("Initializing referee");
		referee = refereeService.getRefereeById(refereeId);
	}

	public String updateReferee() {
		logger.info("Updating referee");
		String result;
		try {
			Referee referee = refereeService.getRefereeById(refereeId);

			if (!this.referee.getMember().equals(referee.getMember())) {
				referee.setMember(this.referee.getMember());
				referee.setRefereeImage(null);
			}

			if (file != null) {
				String fileName = fileService.uploadImage(file, FileDirectory.PROFILE_IMAGE);
				referee.setRefereeImage(fileName);
			}

			refereeService.updateReferee(referee);
			FacesMessages.info("Schiedsrichter aktualisieret");
			result = "referees.xhtml?faces-redirect=true";
		} catch (Exception e) {
			logger.error("Error while updating referee", e);
			FacesMessages.error("Es ist ein Fehler beim aktualisieren aufgetreten");
			result = "refereeEdit?faces-redirect=true&includeViewParams=true&refereeId=" + refereeId;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return result;
	}

	public String createReferee() {
		refereeService.createReferee(this.referee);
		FacesMessages.info("Schiedsrichter erstellt");
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "referees.xhtml?faces-redirect=true";
	}

	public String updateRefereesOrder() {
		logger.info("Updating referees order");
		refereeService.updateReferees(referees);
		FacesMessages.info("Reihenfolge aktualisiert");
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "referees.xhtml?faces-redirect=true";
	}

	public String archiveReferee(Referee referee) {
		logger.info("Archiving referee");
		referee.setArchivedOn(new Date());
		refereeService.updateReferee(referee);
		FacesMessages.info("Schiedsrichter archiviert");
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "refereesEdit.xhtml?faces-redirect=true";
	}

	public void uploadRefereeGroupImage() throws Exception {
		logger.info("Uploading new group image for referees");
		String fileName = fileService.uploadImage(file, FileDirectory.IMAGES);
		FileMapping fileMapping = new FileMapping();
		fileMapping.setImageFilePath(fileName);
		fileMapping.setKey(REFEREE_IMAGE_GROUP_PICTURE_KEY);
		fileMapping.setPage(REFEREES_PAGE_NAME);
		fileService.replaceFileMapping(fileMapping);
	}

	@Inject
	public void setRefereeService(RefereeService refereeService) {
		this.refereeService = refereeService;
	}

	@Inject
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public List<Referee> getReferees() {
		if (referees == null) {
			referees = refereeService.getCurrentReferees();
		}
		return referees;
	}

	public void setReferees(List<Referee> referees) {
		this.referees = referees;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public long getRefereeId() {
		return refereeId;
	}

	public void setRefereeId(long refereeId) {
		this.refereeId = refereeId;
	}

	public Referee getReferee() {
		return referee;
	}

	public void setReferee(Referee referee) {
		this.referee = referee;
	}

	public void setFileMapping(FileMapping fileMapping) {
		this.fileMapping = fileMapping;
	}

	public FileMapping getFileMapping() {
		if (fileMapping == null) {
			fileMapping = fileService.getFileMappingbyKeyPage(REFEREES_PAGE_NAME, REFEREE_IMAGE_GROUP_PICTURE_KEY);
		}

		return fileMapping;
	}
}
