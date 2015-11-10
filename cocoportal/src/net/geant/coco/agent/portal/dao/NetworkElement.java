package net.geant.coco.agent.portal.dao;

import java.util.ArrayList;
import java.util.List;

public class NetworkElement {

	public String name;
	public int id;
	
	public enum NODE_TYPE {
		CUSTOMER, SWITCH, EXTERNAL_AS
	}
	
	public NODE_TYPE nodeType;
	
	private List<NetworkInterface> interfaces;
	
	public NetworkElement() {

	}
	
	public NetworkElement(int id, String name, NODE_TYPE nodeType) {
		this.id = id;
		this.name = name;
		this.nodeType = nodeType;
		interfaces = new ArrayList<NetworkInterface>();
	}
}
