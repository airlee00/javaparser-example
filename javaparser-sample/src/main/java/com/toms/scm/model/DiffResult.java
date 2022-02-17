package com.toms.scm.model;

public class DiffResult {

	private String contents ="";
	private int line =0 ;

	public DiffResult() {
	}

	public DiffResult(String contents, int line) {
		super();
		this.contents = contents;
		this.line = line;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}
	@Override
	public String toString() {
		//return "DiffResult [contents=" + contents + ", line=" + line + "]";
		return "DiffResult [ line=" + line + "]";
	}


}
