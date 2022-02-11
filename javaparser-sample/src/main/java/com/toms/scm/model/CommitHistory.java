package com.toms.scm.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CommitHistory {

	private long revision;
	private Date date;
	private String message;
	private List<ChangedPath> changePaths = new LinkedList<>();
	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<ChangedPath> getChangePaths() {
		return changePaths;
	}
	public void setChangePaths(List<ChangedPath> changePaths) {
		this.changePaths = changePaths;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CommitHistory [revision=");
		builder.append(revision);
		builder.append(", date=");
		builder.append(date);
		builder.append(", message=");
		builder.append(message);
		builder.append(", changePaths=");
		builder.append(changePaths);
		builder.append("]");
		return builder.toString();
	}

}
