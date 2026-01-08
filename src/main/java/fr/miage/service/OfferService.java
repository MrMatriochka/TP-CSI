package fr.miage.service;

import fr.miage.app.*;
import fr.miage.domain.*;

import java.util.HashMap;
import java.util.Map;

public class OfferService {

    private final Map<String, Degree> degreesByName = new HashMap<>();
    private final Map<String, UE> uesByName = new HashMap<>();

    private Degree currentDegree;
    private Year currentYear;

    public Result createDegree(String name, String typeStr, int yearCount, int maxStudents, int ectsTotal) {
        if (!NameValidator.isValidName(name)) return Result.err("Invalid name");
        if (degreesByName.containsKey(name)) return Result.err("Degree already exists");

        final DegreeType type;
        try {
            type = DegreeType.fromString(typeStr);
        } catch (IllegalArgumentException e) {
            return Result.err("Invalid degree type");
        }

        if (yearCount != type.durationYears()) return Result.err("Invalid duration for type");
        if (maxStudents <= 0) return Result.err("maxStudents must be > 0");
        if (ectsTotal <= 0) return Result.err("ectsTotal must be > 0");
        if (ectsTotal != yearCount * 60) return Result.err("ectsTotal must equal years*60");

        Degree degree = new Degree(name, type, maxStudents, ectsTotal, yearCount);
        degreesByName.put(name, degree);

        return Result.ok("Degree created");
    }

    public Degree getDegree(String name) {
        return degreesByName.get(name);
    }

    public Result selectDegree(String name) {
        Degree d = degreesByName.get(name);
        if (d == null) return Result.err("Degree not found");
        currentDegree = d;
        currentYear = d.getYears().get(0);
        return Result.ok("Degree selected");
    }

    public Result selectYear(int n) {
        if (currentDegree == null) return Result.err("No degree selected");
        if (n < 1 || n > currentDegree.getYears().size()) return Result.err("Invalid year");
        currentYear = currentDegree.getYears().get(n - 1);
        return Result.ok("Year selected");
    }

    public Result createUE(String name, int ects, int cm, int td, int tp) {
        // 1) Contexte : il faut un diplôme (et une année)
        if (currentDegree == null) return Result.err("No degree selected");
        if (currentYear == null) return Result.err("No year selected");

        // 2) Validation du nom
        if (!NameValidator.isValidName(name)) return Result.err("Invalid name");
        if (uesByName.containsKey(name)) return Result.err("UE already exists");

        // 3) Validation valeurs
        if (ects <= 0) return Result.err("ects must be > 0");
        if (cm < 0 || td < 0 || tp < 0) return Result.err("hours must be >= 0");

        UE ue = new UE(name, ects, cm, td, tp);

        // 4) Contrainte UE : total hours <= 30
        if (ue.totalHours() > 30) return Result.err("UE hours > 30");

        // 5) Contrainte année : max 6 UE
        if (currentYear.getUes().size() >= 6) return Result.err("Max 6 UE/year");

        // 6) Contrainte année : ne pas dépasser 60 ECTS
        int ectsAlready = currentYear.getUes().stream().mapToInt(UE::getEcts).sum();
        if (ectsAlready + ects > 60) return Result.err("ECTS > 60/year");

        // OK : enregistrer UE globale + rattacher à l’année courante
        uesByName.put(name, ue);
        currentYear.getUes().add(ue);

        return Result.ok("UE created");
    }


    public Degree getCurrentDegree() { return currentDegree; }
    public Year getCurrentYear() { return currentYear; }
}
