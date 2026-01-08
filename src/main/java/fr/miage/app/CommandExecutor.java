package fr.miage.app;

import fr.miage.service.OfferService;

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
}
