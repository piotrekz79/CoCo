package nl.surfnet.coco.agent.portal;

public class CoCoTerminationPoint {

	private String id;
	private InterfaceType type;
	
	public CoCoTerminationPoint(String id) {
		this.id = id;
		this.type = InterfaceType.UNI;
	}

	public String getId() {
		return id;
	}

	public InterfaceType getType() {
		return type;
	}

	public void setType(InterfaceType type) {
		this.type = type;
	}

}
