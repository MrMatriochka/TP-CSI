package fr.miage.app;

import fr.miage.service.OfferService;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CommandExecutor {

    private final OfferService offerService;

    public CommandExecutor(OfferService offerService) {
        this.offerService = offerService;
    }

    public Result executeLine(String line) {
        String[] command = line.trim().split("\\s+");
        if (command.length == 0 || command[0].isBlank()) return Result.err(Errors.INVALID_COMMAND);

        String op = command[0].toUpperCase();

        try {
            return switch (op) {
                case "CREATE" -> handleCreate(command);
                case "SELECT" -> handleSelect(command);
                case "DISPLAY" -> handleDisplay(command);
                case "ASSIGN" -> handleAssign(command);
                case "GET" -> handleGet(command);
                case "EDIT" -> handleEdit(command);
                case "TRACE" -> handleTrace(command);
                default -> Result.err(Errors.INVALID_COMMAND);
            };
        } catch (NumberFormatException e) {
            return Result.err(Errors.INVALID_NUMBER);
        } catch (IllegalArgumentException e) {
            return Result.err(Errors.INVALID_COMMAND);
        }
    }

    private Result handleCreate(String[] t) {
        if (t.length < 2) return Result.err(Errors.INVALID_COMMAND);
        String type = t[1].toUpperCase();

        return switch (type) {
            case "DEGREE" -> {
                if (t.length != 7) yield Result.err(Errors.INVALID_ARGUMENTS);
                String name = t[2];
                String degreeType = t[3];
                int years = Integer.parseInt(t[4]);
                int maxStudents = Integer.parseInt(t[5]);
                int ectsTotal = Integer.parseInt(t[6]);
                yield offerService.createDegree(name, degreeType, years, maxStudents, ectsTotal);
            }
            case "UE" -> {
                if (t.length != 7) yield Result.err(Errors.INVALID_ARGUMENTS);
                String name = t[2];
                int ects = Integer.parseInt(t[3]);
                int cm = Integer.parseInt(t[4]);
                int td = Integer.parseInt(t[5]);
                int tp = Integer.parseInt(t[6]);
                yield offerService.createUE(name, ects, cm, td, tp);
            }
            case "TEACHER" -> {
                if (t.length != 4) yield Result.err(Errors.INVALID_ARGUMENTS);
                String lastName = t[2];
                String firstName = t[3];
                yield offerService.createTeacher(lastName, firstName);
            }
            default -> Result.err(Errors.INVALID_ARGUMENTS);
        };
    }

    private Result handleSelect(String[] t) {
        if (t.length < 2) return Result.err(Errors.INVALID_ARGUMENTS);
        String type = t[1].toUpperCase();

        return switch (type) {
            case "DEGREE" -> {
                if (t.length != 3) yield Result.err(Errors.INVALID_ARGUMENTS);
                yield offerService.selectDegree(t[2]);
            }
            case "YEAR" -> {
                if (t.length != 3) yield Result.err(Errors.INVALID_ARGUMENTS);
                yield offerService.selectYear(Integer.parseInt(t[2]));
            }
            default -> Result.err(Errors.INVALID_ARGUMENTS);
        };
    }

    private Result handleDisplay(String[] t) {
        if (t.length != 3) return Result.err(Errors.INVALID_ARGUMENTS);
        if (!t[1].equalsIgnoreCase("GRAPH")) return Result.err(Errors.INVALID_ARGUMENTS);
        return offerService.displayGraph(t[2]);
    }

    private Result handleAssign(String[] t) {
        // ASSIGN UE <ue> <degree> <year>
        if (t.length == 5 && t[1].equalsIgnoreCase("UE")) {
            try {
                String ueName = t[2];
                String degreeName = t[3];
                int year = Integer.parseInt(t[4]);
                return offerService.assignUEToDegreeYear(ueName, degreeName, year);
            } catch (NumberFormatException e) {
                return Result.err(Errors.INVALID_NUMBER);
            }
        }

        // ASSIGN <ue> <teacher> <hours> (ton existant)
        if (t.length != 4) return Result.err(Errors.INVALID_ARGUMENTS);
        try {
            return offerService.assign(t[1], t[2], Integer.parseInt(t[3]));
        } catch (NumberFormatException e) {
            return Result.err(Errors.INVALID_NUMBER);
        }
    }

    private Result handleGet(String[] t) {
        if (t.length != 3) return Result.err(Errors.INVALID_ARGUMENTS);

        String what = t[1].toUpperCase();

        if ("TOTAL".equals(what)) {
            if ("ALL".equalsIgnoreCase(t[2])) return offerService.getTotalAllDegrees();
            return offerService.getTotal(t[2]);
        }

        if ("COVER".equals(what)) {
            if ("ALL".equalsIgnoreCase(t[2])) return offerService.getCoverAllDegrees();
            return offerService.getCover(t[2]);
        }

        return Result.err(Errors.INVALID_ARGUMENTS);

    }

    private Result handleEdit(String[] t) {
        // EDIT UE <name> <ects> <cm> <td> <tp>
        if (t.length != 7) return Result.err(Errors.INVALID_ARGUMENTS);
        if (!t[1].equalsIgnoreCase("UE")) return Result.err(Errors.INVALID_ARGUMENTS);

        String name = t[2];
        int ects = Integer.parseInt(t[3]);
        int cm = Integer.parseInt(t[4]);
        int td = Integer.parseInt(t[5]);
        int tp = Integer.parseInt(t[6]);

        return offerService.editUE(name, ects, cm, td, tp);
    }

    private Result handleTrace(String[] t) {
        if (t.length != 4) return Result.err(Errors.INVALID_ARGUMENTS);
        if (!t[1].equalsIgnoreCase("GRAPH")) return Result.err(Errors.INVALID_ARGUMENTS);

        String degreeName = t[2];
        Path out = Paths.get(t[3]);

        // ✅ on force .png
        if (!out.toString().toLowerCase().endsWith(".png")) {
            return Result.err(Errors.INVALID_ARGUMENTS);
        }

        // ✅ si c'est manifestement un dossier (se termine par / ou \)
        String raw = t[3];
        if (raw.endsWith("/") || raw.endsWith("\\")) {
            return Result.err(Errors.CANNOT_WRITE_FILE);
        }

        return offerService.traceGraph(degreeName, out);
    }




}
