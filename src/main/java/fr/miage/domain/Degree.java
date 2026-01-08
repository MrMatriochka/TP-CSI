package fr.miage.domain;

import java.util.ArrayList;
import java.util.List;

public class Degree {
    private final String name;
    private final DegreeType type;
    private final int maxStudents;
    private final int ectsTotal;
    private final List<Year> years;

    public Degree(String name, DegreeType type, int maxStudents, int ectsTotal, int yearCount) {
        this.name = name;
        this.type = type;
        this.maxStudents = maxStudents;
        this.ectsTotal = ectsTotal;
        this.years = new ArrayList<>();
        for (int i = 1; i <= yearCount; i++) this.years.add(new Year(i));
    }

    public String getName() { return name; }
    public DegreeType getType() { return type; }
    public int getMaxStudents() { return maxStudents; }
    public int getEctsTotal() { return ectsTotal; }
    public List<Year> getYears() { return years; }
}
