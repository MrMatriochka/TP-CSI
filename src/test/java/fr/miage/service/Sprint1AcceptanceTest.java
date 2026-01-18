package fr.miage.service;

import fr.miage.app.CommandExecutor;
import fr.miage.app.Errors;
import fr.miage.service.OfferService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Sprint1AcceptanceTest {

    private List<String> runScript(List<String> lines) {
        var service = new OfferService();
        var exec = new CommandExecutor(service);

        List<String> outputs = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;


            if (trimmed.equalsIgnoreCase("EXIT")) {
                outputs.add("OK: Bye.");
                break;
            }

            var result = exec.executeLine(trimmed);
            outputs.add(result.message());
        }
        return outputs;
    }

    @Test
    void sprint1_happyPath() {
        var out = runScript(List.of(
                "CREATE DEGREE MIAGE Master 2 100 120",
                "SELECT DEGREE MIAGE",
                "SELECT YEAR 1",
                "CREATE UE Algo 15 10 10 10",
                "DISPLAY GRAPH MIAGE",
                "EXIT"
        ));

        assertEquals("OK: Degree created", out.get(0));
        assertEquals("OK: Degree selected", out.get(1));
        assertEquals("OK: Year selected", out.get(2));
        assertEquals("OK: UE created", out.get(3));


        assertTrue(out.get(4).startsWith("OK:"), "DISPLAY should return OK");
        assertTrue(out.get(4).contains("DEGREE MIAGE"), "Graph should contain degree header");
        assertTrue(out.get(4).contains("YEAR 1"), "Graph should contain year 1");
        assertTrue(out.get(4).contains("UE Algo"), "Graph should contain created UE");

        assertEquals("OK: Bye.", out.get(5));
    }

    @Test
    void sprint1_errors_are_homogeneous() {
        var out = runScript(List.of(
                "BLAH",                       // commande inconnue
                "SELECT YEAR 1",              // pas de degree sélectionné
                "CREATE DEGREE X Master two 10 120", // 'two' => invalid number
                "DISPLAY GRAPH UNKNOWN",      // degree not found
                "EXIT"
        ));

        assertEquals(Errors.INVALID_COMMAND, out.get(0));
        assertEquals(Errors.NO_DEGREE_SELECTED, out.get(1));
        assertEquals(Errors.INVALID_NUMBER, out.get(2));
        assertEquals(Errors.DEGREE_NOT_FOUND, out.get(3));
        assertEquals("OK: Bye.", out.get(4));
    }

    @Test
    void sprint1_invalidArguments_examples() {
        var out = runScript(List.of(
                "DISPLAY GRAPH",              // manque le degré
                "CREATE UE Algo 15 10 10",    // manque un argument
                "SELECT DEGREE",              // manque le nom
                "EXIT"
        ));

        assertEquals(Errors.INVALID_ARGUMENTS, out.get(0));
        assertEquals(Errors.INVALID_ARGUMENTS, out.get(1));
        assertEquals(Errors.INVALID_ARGUMENTS, out.get(2));
        assertEquals("OK: Bye.", out.get(3));
    }
}
