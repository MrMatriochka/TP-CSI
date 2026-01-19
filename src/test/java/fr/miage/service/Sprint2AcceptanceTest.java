package fr.miage.service;

import fr.miage.app.CommandExecutor;
import fr.miage.app.Errors;
import fr.miage.service.OfferService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Sprint2AcceptanceTest {

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
    void sprint2_happyPath_total_cover_edit() {
        var out = runScript(List.of(
                "CREATE DEGREE MIAGE Master 2 100 120",
                "SELECT DEGREE MIAGE",
                "SELECT YEAR 1",
                "CREATE UE Algo 15 10 10 10",       // planned 30h
                "CREATE TEACHER Dupont Jean",
                "ASSIGN Algo Dupont 10",            // assigned 10h => cover 33%
                "GET TOTAL Algo",
                "GET TOTAL Dupont",
                "GET COVER Algo",
                "EDIT UE Algo 15 8 8 8",            // planned 24h, assigned 10 => cover 41%
                "GET TOTAL Algo",
                "GET COVER Algo",
                "GET TOTAL MIAGE",
                "GET COVER MIAGE",
                "GET TOTAL ALL",
                "GET COVER ALL",
                "EXIT"
        ));

        assertEquals("OK: Degree created", out.get(0));
        assertEquals("OK: Degree selected", out.get(1));
        assertEquals("OK: Year selected", out.get(2));
        assertEquals("OK: UE created", out.get(3));
        assertEquals("OK: Teacher created", out.get(4));
        assertEquals("OK: Assignment created", out.get(5));

        assertEquals("OK: Algo total hours = 30", out.get(6));
        assertEquals("OK: Dupont total hours = 10", out.get(7));
        assertEquals("OK: Algo cover = 33%", out.get(8)); // 10/30=33

        assertEquals("OK: UE updated", out.get(9));

        assertEquals("OK: Algo total hours = 24", out.get(10));
        assertEquals("OK: Algo cover = 41%", out.get(11)); // 10/24=41

        assertEquals("OK: MIAGE total hours = 24", out.get(12));
        assertEquals("OK: MIAGE cover = 41%", out.get(13));

        assertTrue(out.get(14).startsWith("OK:"), "TOTAL ALL should be OK");
        assertTrue(out.get(15).startsWith("OK:"), "COVER ALL should be OK");

        assertEquals("OK: Bye.", out.get(16));
    }

    @Test
    void sprint2_errors_are_homogeneous() {
        var out = runScript(List.of(
                "GET COVER Unknown",              // name invalide
                "ASSIGN Algo Dupont 10",          // UE not found (pas créée)
                "EDIT UE Algo 15 20 10 5",         // UE not found
                "CREATE DEGREE MIAGE Master 2 100 120",
                "SELECT DEGREE MIAGE",
                "CREATE UE Algo 15 10 10 10",
                "CREATE TEACHER Dupont Jean",
                "ASSIGN Algo Dupont 0",            // hours <=0
                "EXIT"
        ));

        assertEquals(Errors.INVALID_NAME, out.get(0));

        assertEquals(Errors.UE_NOT_FOUND, out.get(1));
        assertEquals(Errors.UE_NOT_FOUND, out.get(2));

        assertEquals("OK: Degree created", out.get(3));
        assertEquals("OK: Degree selected", out.get(4));
        assertEquals("OK: UE created", out.get(5));
        assertEquals("OK: Teacher created", out.get(6));

        assertEquals(Errors.HOURS_OVER_0, out.get(7));
        assertEquals("OK: Bye.", out.get(8));
    }
}
