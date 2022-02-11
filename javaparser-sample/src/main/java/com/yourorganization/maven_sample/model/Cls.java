package com.yourorganization.maven_sample.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Cls {

	private String packageName;
	private String className ;
	private String comment;
	private  List<Mth> mths = new ArrayList<>();
	private  AtomicInteger current = new AtomicInteger(-1);
	private AtomicInteger loc = new AtomicInteger(0);

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public List<Mth> getMths() {
		return mths;
	}
	public void setMths(List<Mth> mths) {
		this.mths = mths;
	}
	public String getPackageName() {
		return packageName;
	}
	public String getClassName() {
		return className;
	}

	public void add(Mth mth) {
		this.mths.add(mth);
	}

	public AtomicInteger getCurrent() {
		return current;
	}
	public void setCurrent(AtomicInteger current) {
		this.current = current;
	}


	public AtomicInteger getLoc() {
		return loc;
	}
	public void setLoc(AtomicInteger loc) {
		this.loc = loc;
	}
	
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	@Override
	public String toString() {
		return packageName + "." + className + "\t methodSize="  + (current.get() + 1) + "\t lineSize=" + loc + "\t comment=" + comment + " \n" + mths + " ";
	}

}
