package net.geant.coco.agent.portal.rest;

import net.geant.coco.agent.portal.dao.NetworkSite;
import net.geant.coco.agent.portal.dao.Vpn;

public class RestSite {

	private int id;
	private String name;

	 public RestSite() {
	    }
	    
    public RestSite(NetworkSite site) {
    	this.id = site.getId();
    	this.name = site.getName();
    }
	    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "site(" + String.valueOf(id) + "," + name + ")";
	}

}
