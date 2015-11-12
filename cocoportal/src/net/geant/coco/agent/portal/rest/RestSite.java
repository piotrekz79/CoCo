package net.geant.coco.agent.portal.rest;

import net.geant.coco.agent.portal.dao.NetworkSite;
import net.geant.coco.agent.portal.dao.Vpn;

public class RestSite {

	private String id;
	private String name;

	 public RestSite() {
	    }
	    
    public RestSite(NetworkSite site) {
    	this.id = String.valueOf(site.getId());
    	this.name = site.getName();
    }
	    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
