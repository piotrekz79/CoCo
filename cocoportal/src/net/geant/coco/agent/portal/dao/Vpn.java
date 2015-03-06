package net.geant.coco.agent.portal.dao;

public class Vpn {
    public Vpn() {

    }

    private int id;
    private String name;
    private int mplsLabel;

    public Vpn(String name, int mplsLabel) {
        super();
        this.name = name;
        this.mplsLabel = mplsLabel;
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

    public int getMplsLabel() {
        return mplsLabel;
    }

    public void setMplsLabel(int mplsLabel) {
        this.mplsLabel = mplsLabel;
    }
}
