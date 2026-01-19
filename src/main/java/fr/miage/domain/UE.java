package fr.miage.domain;

public class UE {
    private final String name;
    private int ects;
    private int cmHours;
    private int tdHours;
    private int tpHours;

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

    public void setEcts(int value){ects = value; }
    public void setCmHours(int value){ cmHours = value; }
    public void setTdHours(int value){ tdHours = value; }
    public void setTpHours(int value){ tpHours = value; }

    public int totalHours() {
        return cmHours + tdHours + tpHours;
    }

    public int sessions() {
        int h = totalHours();
        return (h + 1) / 2; // ceil(h/2) en entier
    }
}
