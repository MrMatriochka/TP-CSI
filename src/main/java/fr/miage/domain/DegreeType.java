package fr.miage.domain;

public enum DegreeType {
    BUT(3), Licence(3), Master(2), LicencePro(1);

    private final int durationYears;

    DegreeType(int durationYears) {
        this.durationYears = durationYears;
    }

    public int durationYears() { return durationYears; }

    public static DegreeType fromString(String s) {
        for (DegreeType t : values()) {
            if (t.name().equalsIgnoreCase(s)) return t;
        }
        throw new IllegalArgumentException("Unknown degree type: " + s);
    }
}
