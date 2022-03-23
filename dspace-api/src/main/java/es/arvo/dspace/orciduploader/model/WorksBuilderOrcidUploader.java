package es.arvo.dspace.orciduploader.model;

import java.util.List;

public class WorksBuilderOrcidUploader {

	private List<WorkOrcidUploader> works;

	public WorksBuilderOrcidUploader(List<WorkOrcidUploader> works) {
		this.works = works;
	}

	public List<WorkOrcidUploader> getWorks() {
		return works;
	}

	@Override
	public String toString() {
		String result = "";
		for (WorkOrcidUploader work : works)
			result += work.toString();
		
		return result;
	}

}
