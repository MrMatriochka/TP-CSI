package fr.miage.service;

import fr.miage.app.NameValidator;
import fr.miage.app.Result;
import fr.miage.domain.Degree;
import fr.miage.domain.DegreeType;

import java.util.HashMap;
import java.util.Map;

public class OfferService {

    private final Map<String, Degree> degreesByName = new HashMap<>();

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
}
