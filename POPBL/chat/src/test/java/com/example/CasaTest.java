package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CasaTest {

    @Test
    void testConstructorAndGetters() {
        Casa casa = new Casa(1, "Juan", "Bonita casa en el centro");
        assertEquals(1, casa.getId());
        assertEquals("Juan", casa.getVendedor());
        assertEquals("Bonita casa en el centro", casa.getDescripcion());
    }

    @Test
    void testToString() {
        Casa casa = new Casa(2, "Maria", "Casa con jardín");
        String expected = "Casa{id=2, vendedor='Maria', descripcion='Casa con jardín'}";
        assertEquals(expected, casa.toString());
    }

    @Test
    void testDifferentValues() {
        Casa casa = new Casa(3, "Pedro", "Ático luminoso");
        assertNotEquals(4, casa.getId());
        assertNotEquals("Ana", casa.getVendedor());
        assertNotEquals("Piso oscuro", casa.getDescripcion());
    }
}