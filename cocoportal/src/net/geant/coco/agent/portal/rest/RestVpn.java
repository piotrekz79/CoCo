package net.geant.coco.agent.portal.rest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

import net.geant.coco.agent.portal.dao.Vpn;
 
public class RestVpn implements Serializable{
 
    private static final long serialVersionUID = -7788619177798333712L;
     
    private int id;
    private String name;
    private List<RestSite> sites;
    
    public RestVpn() {
    }
    
    public RestVpn(Vpn vpn) {
    	this.id = vpn.getId();
    	this.name = vpn.getName();
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
    
    public List<RestSite> getSites() {
        return sites;
    }
    
    public void setSites(List<RestSite> sites) {
        this.sites = sites;
    }
    
    /*
    @JsonSerialize(using=DateSerializer.class)
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }*/
     
     
}