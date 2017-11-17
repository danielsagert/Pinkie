package de.altenerding.biber.pinkie.presentation.announcement;

import de.altenerding.biber.pinkie.business.file.boundary.FileService;
import de.altenerding.biber.pinkie.business.file.entity.FileCategory;
import de.altenerding.biber.pinkie.business.file.entity.Image;
import de.altenerding.biber.pinkie.business.members.entity.Access;
import de.altenerding.biber.pinkie.business.members.entity.Role;
import de.altenerding.biber.pinkie.business.weeklyimage.boundary.WeeklyImageService;
import de.altenerding.biber.pinkie.business.weeklyimage.entity.WeeklyImage;
import net.bootsfaces.utils.FacesMessages;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.util.List;

@Named
@RequestScoped
public class WeeklyImageBean {

	private Logger logger;
	private Part file;
	private WeeklyImage weeklyImage = new WeeklyImage();
	private List<WeeklyImage> weeklyImages;
	private FileService fileService;
	private WeeklyImageService weeklyImageService;

	@Access(role = Role.PRESS)
	public String saveWeeklyImage() {
		try {
			Image image = fileService.uploadImage(file, FileCategory.IMAGES_MAINPAGE, weeklyImage.getImage().getDescription());
			weeklyImage.setImage(image);
			weeklyImageService.saveWeeklyImage(weeklyImage);
			FacesMessages.info( "Bild der Woche hinzugefügt");
		} catch (Exception e) {
			logger.error("Error while saving announcement with id={}", weeklyImage.getId(), e);
			FacesMessages.error(e.getMessage());
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "editMainpage.xhtml?faces-redirect=true";
	}

	@Access(role = Role.PRESS)
	public String archive(long imageId) {
		weeklyImageService.archiveWeeklyImage(imageId);
		FacesMessages.info( "Bild archiviert");
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "editMainpage.xhtml?faces-redirect=true";
	}

	@Access(role = Role.PRESS)
	public String updateText(WeeklyImage weeklyImage) {
		weeklyImageService.updateText(weeklyImage);
		FacesMessages.info( "Text zum Bild aktualisiert");
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		return "editMainpage.xhtml?faces-redirect=true";
	}

	@Inject
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@Inject
	public void setWeeklyImageService(WeeklyImageService weeklyImageService) {
		this.weeklyImageService = weeklyImageService;
	}

	@Inject
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public void setWeeklyImage(WeeklyImage weeklyImage) {
		this.weeklyImage = weeklyImage;
	}

	public WeeklyImage getWeeklyImage() {
		return weeklyImage;
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	public List<WeeklyImage> getWeeklyImages() {
		if (weeklyImages == null) {
			weeklyImages = weeklyImageService.getlatestWeeklyImages();
		}
		return weeklyImages;
	}

	public void setWeeklyImages(List<WeeklyImage> weeklyImages) {
		this.weeklyImages = weeklyImages;
	}
}
