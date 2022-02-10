package com.toms.javaparser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Mth {

	private String modifier;
	private String methodName ;
	private String serviceId ;
	private String serviceName ;
	private AtomicInteger loc = new AtomicInteger(0);
	private int locTotal = 0;

	private List<Lsi> api = new ArrayList<>();

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public List<Lsi> getApi() {
		return api;
	}

	public void setApi(List<Lsi> api) {
		this.api = api;
	}

	public void add(Lsi lsi) {
		this.api.add(lsi);
	}


	public AtomicInteger getLoc() {
		return loc;
	}
	public void setLoc(AtomicInteger loc) {
		this.loc = loc;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public int getLocTotal() {
		return locTotal;
	}

	public void setLocTotal(int locTotal) {
		this.locTotal = locTotal;
	}

	@Override
	public String toString() {
		return "\t" + modifier + "\t" + methodName + "\tserviceId=" + serviceId
				+ "\tserviceName=" + serviceName + "\twithoutCommentLineSize=" + loc + "\ttotalLine=" + locTotal + "\tapi=" + api + "\n";
	}

	
//	@Override
//	public String toString() {
//		return "Mth [modifier=" + modifier + ", methodName=" + methodName + ", serviceId=" + serviceId
//				+ ", serviceName=" + serviceName + ", loc=" + loc + ", api=" + api + "]\n";
//	}

}
