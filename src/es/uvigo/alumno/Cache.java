package es.uvigo.alumno;

import java.util.ArrayList;
import java.util.Calendar;

import es.uvigo.det.ro.simpledns.ResourceRecord;


public class Cache {

	private Calendar inputDate = null;
	private int ttl;
	public int i;
	private ArrayList<ResourceRecord> answers = new ArrayList<>();

	public Cache(Calendar inputDate) {
		super();
		this.inputDate = inputDate;
		this. i = 0;
	}

	public boolean notExpired() {

		Calendar expirationDate = (Calendar) inputDate.clone();
		expirationDate.add(Calendar.SECOND, ttl);

		// ESTÁ CADUCADA
		if (expirationDate.before(Calendar.getInstance()))
			return false;

		return true;
	}
	
	public int getActualTTL(){
		long TiempoTTL =inputDate.getTimeInMillis()-Calendar.getInstance().getTimeInMillis() + ttl*1000;
		return (int)(TiempoTTL/1000);
	}

	public void showCache() {
		
		for (ResourceRecord rr : answers) {
			System.out.println("A:" + "   "+ "CACHE"+ "   " + rr.getRRType() + "   " + getActualTTL() + rr.toString());
		}
	}

	public Calendar getInputDate() {
		return inputDate;
	}

	public void setInputDate(Calendar inputDate) {
		this.inputDate = inputDate;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public ArrayList<ResourceRecord> getAnswers() {
		return answers;
	}

	public void setAnswers(ArrayList<ResourceRecord> answers) {
		this.answers = answers;
	}

}
