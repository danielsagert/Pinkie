package de.altenerding.biber.pinkie.business.announcement.entity;

import de.altenerding.biber.pinkie.business.file.entity.FileDirectory;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQueries({
		@NamedQuery(name = "Announcement.findAll", query = "SELECT a FROM Announcement a ORDER BY a.createdOn DESC"),
		@NamedQuery(name = "Announcement.findById", query = "SELECT a FROM Announcement a where a.id = :id")
})
public class Announcement {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(columnDefinition = "VARCHAR")
	private String text;
	@Column(name = "created_on")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdOn;
	@Column(columnDefinition = "varchar")
	private String announcementDocument;
	@Column(columnDefinition = "varchar")
	private String documentDisplayedName;

	@PrePersist
	protected void onPersist() {
		if (createdOn == null) {
			createdOn = new Date();
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getAnnouncementDocument() {
		return announcementDocument;
	}

	public void setAnnouncementDocument(String announcementDocument) {
		this.announcementDocument = announcementDocument;
	}

	public String getFullAnnouncementDocumentPath() {
		return "file/" + FileDirectory.ANNOUNCEMENT_DOCUMENTS.getName() + "/" + announcementDocument;
	}

	public boolean hasReportImage() {
		return StringUtils.isNotBlank(announcementDocument);
	}

	public String getDocumentDisplayedName() {
		return documentDisplayedName;
	}

	public void setDocumentDisplayedName(String announcementDocumentName) {
		this.documentDisplayedName = announcementDocumentName;
	}
}
