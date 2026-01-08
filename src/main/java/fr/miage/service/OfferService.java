package fr.miage.service;

import fr.miage.app.*;
import fr.miage.domain.*;
import fr.miage.render.*;

import java.util.HashMap;
import java.util.Map;

public class OfferService {

    private final Map<String, Degree> degreesByName = new HashMap<>();
    private final Map<String, UE> uesByName = new HashMap<>();

    private Degree currentDegree;
    private Year currentYear;

    private final TextTreeRenderer textTreeRenderer = new TextTreeRenderer();

    public Result createDegree(String name, String typeStr, int yearCount, int maxStudents, int ectsTotal) {
        if (!NameValidator.isValidName(name)) return Result.err(Errors.INVALID_NAME);
        if (degreesByName.containsKey(name)) return Result.err(Errors.DEGREE_ALREADY_EXISTS);

        final DegreeType type;
        try {
            type = DegreeType.fromString(typeStr);
        } catch (IllegalArgumentException e) {
            return Result.err(Errors.INVALID_DEGREE_TYPE);
        }

        if (yearCount != type.durationYears()) return Result.err(Errors.INVALID_TYPE_DURATION);
        if (maxStudents <= 0) return Result.err(Errors.STUDENTS_OVER_0);
        if (ectsTotal <= 0) return Result.err(Errors.ECT_OVER_0);
        if (ectsTotal != yearCount * 60) return Result.err(Errors.ECT_60_PER_YEAR);

        Degree degree = new Degree(name, type, maxStudents, ectsTotal, yearCount);
        degreesByName.put(name, degree);

        return Result.ok("Degree created");
    }

    public Degree getDegree(String name) {
        return degreesByName.get(name);
    }

    public Result selectDegree(String name) {
        Degree d = degreesByName.get(name);
        if (d == null) return Result.err(Errors.DEGREE_NOT_FOUND);
        currentDegree = d;
        currentYear = d.getYears().get(0);
        return Result.ok("Degree selected");
    }

    public Result selectYear(int n) {
        if (currentDegree == null) return Result.err(Errors.NO_DEGREE_SELECTED);
        if (n < 1 || n > currentDegree.getYears().size()) return Result.err(Errors.INVALID_YEAR);
        currentYear = currentDegree.getYears().get(n - 1);
        return Result.ok("Year selected");
    }

    public Result createUE(String name, int ects, int cm, int td, int tp) {
        // 1) Contexte : il faut un diplôme (et une année)
        if (currentDegree == null) return Result.err(Errors.NO_DEGREE_SELECTED);
        if (currentYear == null) return Result.err(Errors.NO_YEAR_SELECTED);

        // 2) Validation du nom
        if (!NameValidator.isValidName(name)) return Result.err(Errors.INVALID_NAME);
        if (uesByName.containsKey(name)) return Result.err(Errors.UE_ALREADY_EXISTS);

        // 3) Validation valeurs
        if (ects <= 0) return Result.err(Errors.ECTS_GT_60);
        if (cm < 0 || td < 0 || tp < 0) return Result.err(Errors.HOURS_OVER_0);

        UE ue = new UE(name, ects, cm, td, tp);

        // 4) Contrainte UE : total hours <= 30
        if (ue.totalHours() > 30) return Result.err(Errors.UE_HOURS_GT_30);

        // 5) Contrainte année : max 6 UE
        if (currentYear.getUes().size() >= 6) return Result.err(Errors.MAX_6_UE);

        // 6) Contrainte année : ne pas dépasser 60 ECTS
        int ectsAlready = currentYear.getUes().stream().mapToInt(UE::getEcts).sum();
        if (ectsAlready + ects > 60) return Result.err(Errors.ECTS_GT_60);

        // OK : enregistrer UE globale + rattacher à l’année courante
        uesByName.put(name, ue);
        currentYear.getUes().add(ue);

        return Result.ok("UE created");
    }

    public Result displayGraph(String degreeName) {
        Degree d = degreesByName.get(degreeName);
        if (d == null) return Result.err(Errors.DEGREE_NOT_FOUND);
        String output = textTreeRenderer.render(d);
        return Result.ok("\n" + output); // \n pour que l'arbre commence ligne suivante
    }

    public Degree getCurrentDegree() { return currentDegree; }
    public Year getCurrentYear() { return currentYear; }
}
