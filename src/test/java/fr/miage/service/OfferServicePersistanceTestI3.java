package fr.miage.service;

import fr.miage.service.OfferService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class OfferServicePersistanceTestI3 {

    @Test
    void save_then_load_restores_state() throws Exception {
        // 1) Arrange : état initial
        var s1 = new OfferService();
        s1.createDegree("MIAGE", "Master", 2, 100, 120);
        s1.selectDegree("MIAGE");
        s1.selectYear(1);
        s1.createUE("Algo", 15, 10, 10, 10); // 30h
        s1.createTeacher("Dupont", "Jean");
        s1.assign("Algo", "Dupont", 10); // cover 33%

        // 2) Save dans un fichier temporaire
        Path tmpDir = Files.createTempDirectory("offer-save-test");
        Path saveFile = tmpDir.resolve("save.txt");
        OfferServicePersistence.save(s1, saveFile);

        assertTrue(Files.exists(saveFile));
        assertTrue(Files.size(saveFile) > 0);

        // 3) Act : nouveau service, load
        var s2 = new OfferService();
        OfferServicePersistence.loadInto(s2, saveFile);

        // 4) Assert : mêmes résultats logiques
        var totalUe = s2.getTotal("Algo");
        assertTrue(totalUe.ok());
        assertTrue(totalUe.message().contains("Algo total hours = 30"));

        var totalTeacher = s2.getTotal("Dupont");
        assertTrue(totalTeacher.ok());
        assertTrue(totalTeacher.message().contains("Dupont total hours = 10"));

        var coverUe = s2.getCover("Algo");
        assertTrue(coverUe.ok());
        assertTrue(coverUe.message().contains("Algo cover = 33%"));

        // Contexte restauré (si tu sauvegardes CURRENT)
        assertNotNull(s2.getCurrentDegree());
        assertEquals("MIAGE", s2.getCurrentDegree().getName());
        assertNotNull(s2.getCurrentYear());
        assertEquals(1, s2.getCurrentYear().getIndex());
    }
}
