package com.toms.scm.model;

public class ChangedPath {

	private String path;
	private DiffResult diffResult;
	private char type;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public DiffResult getDiffResult() {
		return diffResult;
	}
	public void setDiffResult(DiffResult diffResult) {
		this.diffResult = diffResult;
	}
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChangedPath [path=");
		builder.append(path);
		builder.append(", diffResult=");
		builder.append(diffResult);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	
	
}
