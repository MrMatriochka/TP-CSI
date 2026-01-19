package fr.miage.service;

import fr.miage.domain.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class OfferServicePersistence {
    private OfferServicePersistence() {}

    // Format lignes:
    // CURRENT|<degreeName>|<yearIndex>
    // DEGREE|name|type|maxStudents|ectsTotal|yearCount
    // YEAR|degreeName|yearIndex
    // UE|name|ects|cm|td|tp
    // TEACHER|lastName|firstName
    // ASSIGN|ueName|teacherLastName|hours

    public static void save(OfferService service, Path path) throws IOException {
        Files.createDirectories(path.getParent());

        List<String> lines = new ArrayList<>();

        // CURRENT
        if (service.getCurrentDegree() != null && service.getCurrentYear() != null) {
            lines.add("CURRENT|" + service.getCurrentDegree().getName() + "|" + service.getCurrentYear().getIndex());
        }

        // DEGREES + YEARS + UEs
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

        // TEACHERS
        for (Teacher t : service.getAllTeachers()) {
            lines.add("TEACHER|" + t.getLastName() + "|" + t.getFirstName());
        }

        // ASSIGNMENTS
        for (Assignment a : service.getAllAssignments()) {
            lines.add("ASSIGN|" + a.getUe().getName() + "|" + a.getTeacher().getLastName() + "|" + a.getHours());
        }

        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void loadInto(OfferService service, Path path) throws IOException {
        if (!Files.exists(path)) return;

        List<String> lines = Files.readAllLines(path);

        service.reset();

        // On reconstruit degrés/years/ues, puis teachers, puis assignments, puis current
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
                    // CURRENT|MIAGE|1
                    if (p.length >= 3) {
                        pendingCurrentDegree = p[1];
                        pendingCurrentYear = Integer.parseInt(p[2]);
                    }
                }
                case "DEGREE" -> {
                    // DEGREE|name|type|maxStudents|ectsTotal|yearCount
                    String name = p[1];
                    String typeStr = p[2];
                    int maxStudents = Integer.parseInt(p[3]);
                    int ectsTotal = Integer.parseInt(p[4]);
                    int yearCount = Integer.parseInt(p[5]);

                    // utilise le service pour créer (ça recrée les years)
                    service.createDegree(name, typeStr, yearCount, maxStudents, ectsTotal);

                    currentDegreeInFile = service.getDegree(name);
                    currentYearInFile = null;
                }
                case "YEAR" -> {
                    // YEAR|degreeName|yearIndex
                    String degreeName = p[1];
                    int idx = Integer.parseInt(p[2]);

                    currentDegreeInFile = service.getDegree(degreeName);
                    if (currentDegreeInFile != null) {
                        currentYearInFile = currentDegreeInFile.getYears().get(idx - 1);
                    }
                }
                case "UE" -> {
                    // UE|name|ects|cm|td|tp
                    String ueName = p[1];
                    int ects = Integer.parseInt(p[2]);
                    int cm = Integer.parseInt(p[3]);
                    int td = Integer.parseInt(p[4]);
                    int tp = Integer.parseInt(p[5]);

                    // Pour créer une UE, ton service exige un currentDegree/currentYear.
                    // Ici on force le contexte courant au degré/année qu'on vient de lire.
                    if (currentDegreeInFile != null) {
                        service.selectDegree(currentDegreeInFile.getName());
                        if (currentYearInFile != null) {
                            service.selectYear(currentYearInFile.getIndex());
                        }
                        service.createUE(ueName, ects, cm, td, tp);
                    }
                }
                case "TEACHER" -> {
                    // TEACHER|last|first
                    service.createTeacher(p[1], p[2]);
                }
                case "ASSIGN" -> {
                    // ASSIGN|ueName|teacherLastName|hours
                    String ueName = p[1];
                    String teacherLast = p[2];
                    int hours = Integer.parseInt(p[3]);
                    service.assign(ueName, teacherLast, hours);
                }
                default -> {
                    // ignore lignes inconnues pour robustesse
                }
            }
        }

        // Restore current context at end
        if (pendingCurrentDegree != null) {
            service.selectDegree(pendingCurrentDegree);
            if (pendingCurrentYear != null) service.selectYear(pendingCurrentYear);
        }
    }
}
