package net.geant.coco.agent.portal.beans;

public class CoCoCoordinates {
	private String id;
	private String  colour;
	
	private int lookupXPos() {
		int x = 0;
		
		if (id.equals("openflow:1")) {
			x = 625;
		}
		
		if (id.equals("openflow:2")) {
			x = 550;
		}
		
		if (id.equals("openflow:3")) {
			x = 375;
		}
		
		if (id.equals("openflow:4")) {
			x = 300;
		}
		
		if (id.equals("site1")) {
			x = 750;
		}
		
		if (id.equals("site2")) {
			x = 600;
		}
		
		if (id.equals("site3")) {
			x = 350;
		}
		
		if (id.equals("site4")) {
			x = 250;
		}
		
		if (id.equals("site7")) {
			x = 365;
		}
		
		if (id.equals("site8")) {
			x = 500;
		}
		
		if (id.equals("uva1")) {
			x = 225;
		}
		
		if (id.equals("uva2")) {
			x = 250;
		}
		
		return x;
	}
	
	private int lookupYPos() {
		int y = 0;
		
		if (id.equals("openflow:1")) {
			y = 350;
		}
		
		if (id.equals("openflow:2")) {
			y = 550;
		}
		
		if (id.equals("openflow:3")) {
			y = 400;
		}
		
		if (id.equals("openflow:4")) {
			y = 550;
		}
		
		if (id.equals("site1")) {
			y = 250;
		}
		
		if (id.equals("site2")) {
			y = 200;
		}
		
		if (id.equals("site3")) {
			y = 650;
		}
		
		if (id.equals("site4")) {
			y = 650;
		}
		
		if (id.equals("site7")) {
			y = 305;
		}
		
		if (id.equals("site8")) {
			y = 350;
		}
		
		if (id.equals("uva1")) {
			y = 350;
		}
		if (id.equals("uva2")) {
			y = 450;
		}
		
		return y;
	}
	
	public CoCoCoordinates() {
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getXpos() {
		return lookupXPos();
	}
	
	public int getYpos() {
		return lookupYPos();
	}
	
	public String getColour() {
		return colour;
	}

}


