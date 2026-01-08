package fr.miage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OfferServiceTest {

    private OfferService offerService;

    @BeforeEach
    void setUp() {
         offerService = new OfferService();
    }

    @Test
    void createDegree_ok() {
        var result = offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        assertTrue(result.ok());
    }

    @Test
    void createDegree_wrongTypeDuration_err() {
        var result = offerService.createDegree("MIAGE", "Master", 3, 100, 180);
        assertFalse(result.ok());
    }
}
