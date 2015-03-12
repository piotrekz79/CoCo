package net.geant.coco.agent.portal.utils;

import java.util.ArrayList;
import java.util.List;

public class CoCoVPN {
	private static int counter = 0;
	
	private int id = 0;
	private int vpnVlanId = 0;
	private String sites[];
	private List<CoCoLink> edges = new ArrayList<CoCoLink>();
	
	/*
	 * Create empty VPN
	 */
	public CoCoVPN() {
		counter++;
		id = counter;
		vpnVlanId = 500 + counter;
	}
	
	public int getId() {
		return(id);
	}

	public String[] getSites() {
		return sites;
	}

	public void setSites(String[] sites) {
		this.sites = sites;
	}

	public int getVpnVlanId() {
		return vpnVlanId;
	}

	public List<CoCoLink> getEdges() {
		return edges;
	}

	public void setEdges(List<CoCoLink> edges) {
		this.edges = edges;
	}
	
	public void resetCounter() {
		this.counter = 0;
	}
	
}
