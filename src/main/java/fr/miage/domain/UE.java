package fr.miage.domain;

public class UE {
    private final String name;
    private final int ects;
    private final int cmHours;
    private final int tdHours;
    private final int tpHours;

    public UE(String name, int ects, int cmHours, int tdHours, int tpHours) {
        this.name = name;
        this.ects = ects;
        this.cmHours = cmHours;
        this.tdHours = tdHours;
        this.tpHours = tpHours;
    }

    public String getName() { return name; }
    public int getEcts() { return ects; }
    public int getCmHours() { return cmHours; }
    public int getTdHours() { return tdHours; }
    public int getTpHours() { return tpHours; }

    public int totalHours() {
        return cmHours + tdHours + tpHours;
    }
}
