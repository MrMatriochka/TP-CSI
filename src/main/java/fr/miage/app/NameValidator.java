package fr.miage.app;

import java.util.Set;

public final class NameValidator {

    private static final Set<String> RESERVED = Set.of(
            "CREATE", "SELECT", "EDIT", "ASSIGN", "GET", "TRACE", "DISPLAY", "EXIT",
            "DEGREE", "UE", "TEACHER", "YEAR", "ALL", "GRAPH", "TOTAL", "COVER"
    );

    private NameValidator() {}

    public static boolean isReserved(String name) {
        if (name == null) return false;
        return RESERVED.contains(name.trim().toUpperCase());
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        String n = name.trim();
        if (n.isEmpty()) return false;
        return !isReserved(n);
    }
}
