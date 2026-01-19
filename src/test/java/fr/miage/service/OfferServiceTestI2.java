package fr.miage.service;

import fr.miage.app.Errors;
import fr.miage.domain.Degree;
import fr.miage.domain.DegreeType;
import fr.miage.domain.UE;
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
    @Test
    void assign_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.createTeacher("Dupont", "Jean");

        var result = offerService.assign("Algo", "Dupont", 8);
        assertTrue(result.ok());
        assertEquals("OK: Assignment created", result.message());
    }

    @Test
    void assign_teacherNotFound_err() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10);

        var result = offerService.assign("Algo", "Dupont", 8);
        assertFalse(result.ok());
        assertEquals(Errors.TEACHER_NOT_FOUND, result.message());
    }

    @Test
    void assign_ueNotFound_err() {
        offerService.createTeacher("Dupont", "Jean");

        var result = offerService.assign("Algo", "Dupont", 8);
        assertFalse(result.ok());
        assertEquals(Errors.UE_NOT_FOUND, result.message());
    }

    @Test
    void assign_rejectsTeacherHoursOver90_err() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.createTeacher("Dupont", "Jean");

        // 90h déjà (on triche en assignant 30h sur 3 UE)
        offerService.createUE("UE2", 15, 10, 10, 10);
        offerService.createUE("UE3", 15, 10, 10, 10);
        offerService.createUE("UE4", 15, 10, 10, 10);

        assertTrue(offerService.assign("Algo", "Dupont", 30).ok());
        assertTrue(offerService.assign("UE2", "Dupont", 30).ok());
        assertTrue(offerService.assign("UE3", "Dupont", 30).ok());

        var result = offerService.assign("UE4", "Dupont", 1);
        assertFalse(result.ok());
        assertEquals(Errors.MAX_TEACHER_HOURS_90, result.message());
    }

    @Test
    void assign_rejectsUEOverAssigned_err() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.createTeacher("Dupont", "Jean");

        assertTrue(offerService.assign("Algo", "Dupont", 30).ok());

        var result = offerService.assign("Algo", "Dupont", 1);
        assertFalse(result.ok());
        assertEquals(Errors.ASSIGNMENT_ALREADY_EXISTS, result.message()); // doublon interdit
    }

    @Test
    void getTotal_teacher_ok() {
        offerService.createTeacher("Dupont", "Jean");

        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.assign("Algo", "Dupont", 8);

        var result = offerService.getTotal("Dupont");
        assertTrue(result.ok());
        assertEquals("OK: Dupont total hours = 8", result.message());
    }

    @Test
    void getTotal_ue_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h

        var result = offerService.getTotal("Algo");
        assertTrue(result.ok());
        assertEquals("OK: Algo total hours = 30", result.message());
    }

    @Test
    void getTotal_degree_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.selectYear(2);
        offerService.createUE("Bd", 15, 10, 10, 0); // 20h

        var result = offerService.getTotal("MIAGE");
        assertTrue(result.ok());
        assertEquals("OK: MIAGE total hours = 50", result.message());
    }

    @Test
    void getTotalAllDegrees_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.selectYear(2);
        offerService.createUE("Bd", 15, 10, 10, 0); // 20h -> 50

        offerService.createDegree("INFO", "Master", 2, 100, 120);
        offerService.selectDegree("INFO");
        offerService.createUE("Sys", 15, 10, 10, 10); // 30h
        offerService.selectYear(2);
        offerService.createUE("Net", 15, 10, 10, 10); // 30h -> 60

        var result = offerService.getTotalAllDegrees();
        assertTrue(result.ok());
        assertTrue(result.message().contains("INFO = 60"));
        assertTrue(result.message().contains("MIAGE = 50"));
    }

    @Test
    void getCover_ue_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // planned 30

        offerService.createTeacher("Dupont", "Jean");
        offerService.assign("Algo", "Dupont", 15); // assigned 15 -> 50%

        var result = offerService.getCover("Algo");
        assertTrue(result.ok());
        assertEquals("OK: Algo cover = 50%", result.message());
    }

    @Test
    void getCover_degree_ok_weighted() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");

        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.createUE("Bd", 15, 10, 10, 0);    // 20h (Year1) total planned=50

        offerService.createTeacher("Dupont", "Jean");
        offerService.assign("Algo", "Dupont", 15); // assigned=15
        offerService.assign("Bd", "Dupont", 5);    // assigned=20 -> 20/50=40%

        var result = offerService.getCover("MIAGE");
        assertTrue(result.ok());
        assertEquals("OK: MIAGE cover = 40%", result.message());
    }

    @Test
    void getCoverAllDegrees_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h

        offerService.createTeacher("Dupont", "Jean");
        offerService.assign("Algo", "Dupont", 15); // 50%

        offerService.createDegree("INFO", "Master", 2, 100, 120);
        offerService.selectDegree("INFO");
        offerService.createUE("Sys", 15, 10, 10, 10); // 30h
        // no assignment => 0%

        var result = offerService.getCoverAllDegrees();
        assertTrue(result.ok());
        assertTrue(result.message().contains("INFO = 0%"));
        assertTrue(result.message().contains("MIAGE = 50%"));
    }

    @Test
    void ue_sessions_roundUp() {
        var ue = new UE("Algo", 15, 10, 10, 5); // 25h
        assertEquals(13, ue.sessions());
    }

    @Test
    void editUE_ok() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h, 15 ects

        var result = offerService.editUE("Algo", 12, 8, 8, 8); // 24h
        assertTrue(result.ok());
        assertEquals("OK: UE updated", result.message());
    }

    @Test
    void editUE_rejectsOver30Hours() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10);

        var result = offerService.editUE("Algo", 12, 20, 10, 5); // 35h
        assertFalse(result.ok());
        assertEquals(Errors.UE_HOURS_GT_30, result.message());
    }

    @Test
    void editUE_rejectsEctsOver60InYear() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("UE1", 40, 10, 10, 10);
        offerService.createUE("UE2", 20, 10, 10, 10); // year total = 60

        var result = offerService.editUE("UE2", 30, 10, 10, 10); // would become 70
        assertFalse(result.ok());
        assertEquals(Errors.ECTS_GT_60, result.message());
    }

    @Test
    void editUE_rejectsAssignedHoursGreaterThanNewTotal() {
        offerService.createDegree("MIAGE", "Master", 2, 100, 120);
        offerService.selectDegree("MIAGE");
        offerService.createUE("Algo", 15, 10, 10, 10); // 30h
        offerService.createTeacher("Dupont", "Jean");
        offerService.assign("Algo", "Dupont", 20);

        var result = offerService.editUE("Algo", 15, 5, 5, 5); // new total 15 < assigned 20
        assertFalse(result.ok());
        assertEquals(Errors.EDIT_BREAKS_ASSIGNMENTS, result.message());
    }




}
