package fr.miage.service;

import fr.miage.domain.Degree;
import fr.miage.domain.DegreeType;
import fr.miage.render.TextTreeRenderer;
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

    @Test
    void createDegree_reservedKeyword_create() {
        var result = offerService.createDegree("CREATE", "Master", 2, 100, 120);
        assertFalse(result.ok());
    }

    @Test
    void createDegree_reservedKeyword_get() {
        var result = offerService.createDegree("GET", "Master", 2, 100, 120);
        assertFalse(result.ok());
    }

    @Test
    void selectDegree_setsCurrentDegree() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        var result = offerService.selectDegree("MIAGE");
        assertTrue(result.ok());
        assertNotNull(offerService.getCurrentDegree());
    }

    @Test
    void selectDegree_defaultsYear1() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        assertNotNull(offerService.getCurrentYear());
        assertEquals(1, offerService.getCurrentYear().getIndex());
    }

    @Test
    void selectYear_requiresDegree() {
        var result = offerService.selectYear(1);
        assertFalse(result.ok());
    }

    @Test
    void selectYear_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        var result = offerService.selectYear(2);
        assertTrue(result.ok());
        assertEquals(2, offerService.getCurrentYear().getIndex());
    }

    @Test
    void createUE_requiresContext() {
        var result = offerService.createUE("Algo", 10, 10, 10, 10);
        assertFalse(result.ok());
    }

    @Test
    void createUE_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");

        var result = offerService.createUE("Algo", 15, 10, 10, 10);
        assertTrue(result.ok());
    }

    @Test
    void createUE_rejectsMoreThan30Hours() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");

        var result = offerService.createUE("Algo", 15, 20, 10, 5);
        assertFalse(result.ok());
    }

    @Test
    void createUE_rejectsEctsAbove60EvenBefore6UE() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");

        assertTrue(offerService.createUE("UE1", 40, 10, 10, 10).ok());
        assertTrue(offerService.createUE("UE2", 20, 10, 10, 10).ok());

        var result = offerService.createUE("UE3", 1, 1, 0, 0);
        assertFalse(result.ok());
    }

    @Test
    void createUE_rejects7thUEInYear() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");

        for (int i = 1; i <= 6; i++) {
            assertTrue(offerService.createUE("UE" + i, 1, 10, 10, 10).ok());
        }

        var result = offerService.createUE("UE7", 1, 1, 0, 0);
        assertFalse(result.ok());
    }

    @Test
    void render_containsDegreeAndYear() {
        Degree d = new Degree("MIAGE", DegreeType.Master, 100, 120, 2);
        var renderer = new TextTreeRenderer();

        String s = renderer.render(d);

        assertTrue(s.contains("DEGREE MIAGE"));
        assertTrue(s.contains("YEAR 1"));
        assertTrue(s.contains("YEAR 2"));
        System.out.println(s.toString());
    }

}
