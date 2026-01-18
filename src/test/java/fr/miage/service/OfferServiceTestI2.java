package fr.miage.service;

import fr.miage.app.Errors;
import fr.miage.domain.Degree;
import fr.miage.domain.DegreeType;
import fr.miage.render.TextTreeRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OfferServiceTestI2 {

    private OfferService offerService;

    @BeforeEach
    void setUp() {
        offerService = new OfferService();
    }

    @Test
    void createTeacher_ok() {
        var result = offerService.createTeacher("Dupont", "Jean");
        assertTrue(result.ok());
        assertEquals("OK: Teacher created", result.message());
    }

    @Test
    void createTeacher_duplicate_err() {
        offerService.createTeacher("Dupont", "Jean");
        var result = offerService.createTeacher("Dupont", "Paul");
        assertFalse(result.ok());
        assertEquals(Errors.TEACHER_ALREADY_EXISTS, result.message());
    }

}
