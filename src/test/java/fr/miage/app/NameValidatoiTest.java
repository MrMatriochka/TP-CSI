package fr.miage.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameValidatorTest {

    @Test
    void reservedKeywordsAreRejected() {
        assertTrue(NameValidator.isReserved("CREATE"));
        assertTrue(NameValidator.isReserved("exit"));

        assertFalse(NameValidator.isReserved("MIAGE"));
    }

    @Test
    void validNameRules() {
        assertFalse(NameValidator.isValidName(null));
        assertFalse(NameValidator.isValidName(" "));
        assertFalse(NameValidator.isValidName("GET"));

        assertTrue(NameValidator.isValidName("MIAGE"));
        assertTrue(NameValidator.isValidName("Algo"));
    }
}