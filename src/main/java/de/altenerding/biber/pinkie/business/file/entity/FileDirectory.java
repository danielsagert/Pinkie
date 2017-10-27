package de.altenerding.biber.pinkie.business.file.entity;

public enum FileDirectory {
	WEEKLY_IMAGE("weeklyImages"),
	TEAM_IMAGE("teamImages"),
	PROFILE_IMAGE("profileImages"),
	DOCUMTENTS("documents");

	private final String name;

	FileDirectory(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
