package com.toms.scm.model;

import java.util.LinkedList;
import java.util.List;

public class CommitHistory {

	private long revision;
	private String author;
	private String date;
	private String message;
	private List<ChangedPath> changePaths = new LinkedList<>();

	public int totalLine() {
		int line =0;
		for( ChangedPath p :  changePaths) {
			line = line + p.getDiffResult().getLine();
		}
		return line;
	}

	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
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

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "CommitHistory [revision=" + revision + ", author=" + author + ", date=" + date + ", message=" + message
				+ ", changePaths=" + changePaths + ", totalLine()=" + totalLine() + "]";
	}


}
