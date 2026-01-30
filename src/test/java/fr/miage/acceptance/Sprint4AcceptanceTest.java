package fr.miage.acceptance;

import fr.miage.app.CommandExecutor;
import fr.miage.app.Errors;
import fr.miage.service.OfferService;
import fr.miage.service.OfferServicePersistence;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class Sprint4AcceptanceTest {

    @Test
    void sprintI4_full_acceptance_script() {
        var service = new OfferService();
        var exec = new CommandExecutor(service);

        assertTrue(exec.executeLine("CREATE DEGREE D1 Master 2 100 120").ok());
        assertTrue(exec.executeLine("CREATE DEGREE D2 Master 2 100 120").ok());
        assertTrue(exec.executeLine("SELECT DEGREE D1").ok());
        assertTrue(exec.executeLine("SELECT YEAR 1").ok());
        assertTrue(exec.executeLine("CREATE UE IA 15 10 10 10").ok());

        var m1 = exec.executeLine("ASSIGN UE IA D2 1");
        assertTrue(m1.ok(), m1.message());

        var g2 = exec.executeLine("DISPLAY GRAPH D2");
        assertTrue(g2.ok());
        assertTrue(g2.message().contains("DEGREE D2"));
        assertTrue(g2.message().contains("UE IA"));

        var dup = exec.executeLine("ASSIGN UE IA D2 1");
        assertFalse(dup.ok());
        assertEquals(Errors.UE_ALREADY_EXISTS, dup.message());

        var badDegree = exec.executeLine("ASSIGN UE IA UNKNOWN 1");
        assertFalse(badDegree.ok());
        assertEquals(Errors.DEGREE_NOT_FOUND, badDegree.message());

        var badUe = exec.executeLine("ASSIGN UE UNKNOWN D2 1");
        assertFalse(badUe.ok());
        assertEquals(Errors.UE_NOT_FOUND, badUe.message());

        var badYear = exec.executeLine("ASSIGN UE IA D2 99");
        assertFalse(badYear.ok());
        assertEquals(Errors.INVALID_YEAR, badYear.message());

        var del = exec.executeLine("EDIT UE IA 0 0 0 0");
        assertTrue(del.ok(), del.message());

        var g1after = exec.executeLine("DISPLAY GRAPH D1");
        assertTrue(g1after.ok());
        assertFalse(g1after.message().contains("UE IA"), "UE IA should be removed from D1");

        var g2after = exec.executeLine("DISPLAY GRAPH D2");
        assertTrue(g2after.ok());
        assertFalse(g2after.message().contains("UE IA"), "UE IA should be removed from D2");

        var assignAfterDelete = exec.executeLine("ASSIGN UE IA D2 1");
        assertFalse(assignAfterDelete.ok());
        assertEquals(Errors.UE_NOT_FOUND, assignAfterDelete.message());

        var delUnknown = exec.executeLine("EDIT UE UNKNOWN 0 0 0 0");
        assertFalse(delUnknown.ok());
        assertEquals(Errors.UE_NOT_FOUND, delUnknown.message());

        assertTrue(exec.executeLine("SELECT DEGREE D1").ok());
        assertTrue(exec.executeLine("SELECT YEAR 1").ok());
        assertTrue(exec.executeLine("CREATE UE Python 15 10 10 10").ok());
        assertTrue(exec.executeLine("CREATE TEACHER Dupont Jean").ok());
        assertTrue(exec.executeLine("ASSIGN Python Dupont 10").ok());

        var del2 = exec.executeLine("EDIT UE Python 0 0 0 0");
        assertTrue(del2.ok(), del2.message());

        var totalTeacher = exec.executeLine("GET TOTAL Dupont");
        assertTrue(totalTeacher.ok());
        assertTrue(totalTeacher.message().contains("Dupont total hours = 0"),
                "After UE deletion, assignments should be removed => teacher total back to 0");
    }

    @Test
    void save_load_preserves_mutualized_ue() throws Exception {
        Path dir = Files.createTempDirectory("save-load-mutual");
        Path save = dir.resolve("save.txt");

        var s1 = new OfferService();
        var e1 = new CommandExecutor(s1);

        assertTrue(e1.executeLine("CREATE DEGREE D1 Master 2 100 120").ok());
        assertTrue(e1.executeLine("CREATE DEGREE D2 Master 2 100 120").ok());
        assertTrue(e1.executeLine("SELECT DEGREE D1").ok());
        assertTrue(e1.executeLine("SELECT YEAR 1").ok());
        assertTrue(e1.executeLine("CREATE UE IA 15 10 10 10").ok());
        assertTrue(e1.executeLine("ASSIGN UE IA D2 1").ok());

        OfferServicePersistence.save(s1, save);
        assertTrue(Files.exists(save));
        assertTrue(Files.size(save) > 0);

        var s2 = new OfferService();
        OfferServicePersistence.loadInto(s2, save);
        var e2 = new CommandExecutor(s2);

        var g1 = e2.executeLine("DISPLAY GRAPH D1");
        assertTrue(g1.ok());
        assertTrue(g1.message().contains("UE IA"));

        var g2 = e2.executeLine("DISPLAY GRAPH D2");
        assertTrue(g2.ok());
        assertTrue(g2.message().contains("UE IA"));
    }
}
