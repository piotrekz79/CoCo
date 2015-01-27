package nl.surfnet.coco.agent.portal;

public enum NodeType {
	CE("Customer Edge"),
	PE("Provider Edge"),
	P("Provider");
	
	private String name; 
    private NodeType(String name) { 
        this.name = name; 
    } 
    
    @Override 
    public String toString(){ 
        return name; 
    } 
}
