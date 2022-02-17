package com.yourorganization.maven_sample.model;

public class Lsi {

	private String apiCode;
	private String apiUrl;
	public String getApiCode() {
		return apiCode;
	}
	public void setApiCode(String apiCode) {
		this.apiCode = apiCode;
	}
	public String getApiUrl() {
		return apiUrl;
	}
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	@Override
	public String toString() {
		return "apiCode=" + apiCode + "\t apiUrl=" + apiUrl ;
	}

}
