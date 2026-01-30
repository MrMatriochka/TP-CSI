package fr.miage.service;

import fr.miage.domain.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class OfferServicePersistence {
    private OfferServicePersistence() {}

    // Format de la save:
    // CURRENT|<degreeName>|<yearIndex>
    // DEGREE|name|type|maxStudents|ectsTotal|yearCount
    // YEAR|degreeName|yearIndex
    // UE|name|ects|cm|td|tp
    // TEACHER|lastName|firstName
    // ASSIGN|ueName|teacherLastName|hours

    public static void save(OfferService service, Path path) throws IOException {
        Files.createDirectories(path.getParent());

        List<String> lines = new ArrayList<>();
        if (service.getCurrentDegree() != null && service.getCurrentYear() != null) {
            lines.add("CURRENT|" + service.getCurrentDegree().getName() + "|" + service.getCurrentYear().getIndex());
        }
        for (Degree d : service.getAllDegrees()) {
            lines.add(String.join("|",
                    "DEGREE",
                    d.getName(),
                    d.getType().toString(),
                    String.valueOf(d.getMaxStudents()),
                    String.valueOf(d.getEctsTotal()),
                    String.valueOf(d.getYears().size())
            ));

            for (Year y : d.getYears()) {
                lines.add("YEAR|" + d.getName() + "|" + y.getIndex());
                for (UE ue : y.getUes()) {
                    lines.add(String.join("|",
                            "UE",
                            ue.getName(),
                            String.valueOf(ue.getEcts()),
                            String.valueOf(ue.getCmHours()),
                            String.valueOf(ue.getTdHours()),
                            String.valueOf(ue.getTpHours())
                    ));
                }
            }
        }

        for (Teacher t : service.getAllTeachers()) {
            lines.add("TEACHER|" + t.getLastName() + "|" + t.getFirstName());
        }

        for (Assignment a : service.getAllAssignments()) {
            lines.add("ASSIGN|" + a.getUe().getName() + "|" + a.getTeacher().getLastName() + "|" + a.getHours());
        }

        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void loadInto(OfferService service, Path path) throws IOException {
        if (!Files.exists(path)) return;

        List<String> lines = Files.readAllLines(path);

        service.reset();
        String pendingCurrentDegree = null;
        Integer pendingCurrentYear = null;

        Degree currentDegreeInFile = null;
        Year currentYearInFile = null;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;

            String[] p = line.split("\\|");
            String tag = p[0];

            switch (tag) {
                case "CURRENT" -> {
                    if (p.length >= 3) {
                        pendingCurrentDegree = p[1];
                        pendingCurrentYear = Integer.parseInt(p[2]);
                    }
                }
                case "DEGREE" -> {
                    String name = p[1];
                    String typeStr = p[2];
                    int maxStudents = Integer.parseInt(p[3]);
                    int ectsTotal = Integer.parseInt(p[4]);
                    int yearCount = Integer.parseInt(p[5]);

                    service.createDegree(name, typeStr, yearCount, maxStudents, ectsTotal);

                    currentDegreeInFile = service.getDegree(name);
                    currentYearInFile = null;
                }
                case "YEAR" -> {
                    String degreeName = p[1];
                    int idx = Integer.parseInt(p[2]);

                    currentDegreeInFile = service.getDegree(degreeName);
                    if (currentDegreeInFile != null) {
                        currentYearInFile = currentDegreeInFile.getYears().get(idx - 1);
                    }
                }
                case "UE" -> {
                    String ueName = p[1];
                    int ects = Integer.parseInt(p[2]);
                    int cm = Integer.parseInt(p[3]);
                    int td = Integer.parseInt(p[4]);
                    int tp = Integer.parseInt(p[5]);

                    if (currentDegreeInFile != null) {
                        service.selectDegree(currentDegreeInFile.getName());
                        if (currentYearInFile != null) service.selectYear(currentYearInFile.getIndex());

                        if (service.getUE(ueName) == null) {
                            service.createUE(ueName, ects, cm, td, tp);
                        } else {
                            currentYearInFile.getUes().add(service.getUE(ueName));
                        }
                    }
                }
                case "TEACHER" -> {
                    service.createTeacher(p[1], p[2]);
                }
                case "ASSIGN" -> {
                    String ueName = p[1];
                    String teacherLast = p[2];
                    int hours = Integer.parseInt(p[3]);
                    service.assign(ueName, teacherLast, hours);
                }
                default -> {
                }
            }
        }

        if (pendingCurrentDegree != null) {
            service.selectDegree(pendingCurrentDegree);
            if (pendingCurrentYear != null) service.selectYear(pendingCurrentYear);
        }
    }
}
