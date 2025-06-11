package com.example;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class FilterStatsParallelTest {

    @Test
    void testApplyFilters_AllFiltersMatch() {
        Map<String, String> vivienda = new HashMap<>();
        vivienda.put("priceByArea", "5000");
        vivienda.put("surface", "100");
        vivienda.put("district", "chamberí");

        boolean result = invokeApplyFilters(vivienda, 3000, 10000, 50, 200, "chamberí");
        assertTrue(result);
    }

    @Test
    void testApplyFilters_PriceByAreaTooLow() {
        Map<String, String> vivienda = new HashMap<>();
        vivienda.put("priceByArea", "2000");
        vivienda.put("surface", "100");
        vivienda.put("district", "chamberí");

        boolean result = invokeApplyFilters(vivienda, 3000, 10000, 50, 200, "chamberí");
        assertFalse(result);
    }

    @Test
    void testApplyFilters_SurfaceTooSmall() {
        Map<String, String> vivienda = new HashMap<>();
        vivienda.put("priceByArea", "5000");
        vivienda.put("surface", "30");
        vivienda.put("district", "chamberí");

        boolean result = invokeApplyFilters(vivienda, 3000, 10000, 50, 200, "chamberí");
        assertFalse(result);
    }

    @Test
    void testApplyFilters_DistrictDoesNotMatch() {
        Map<String, String> vivienda = new HashMap<>();
        vivienda.put("priceByArea", "5000");
        vivienda.put("surface", "100");
        vivienda.put("district", "salamanca");

        boolean result = invokeApplyFilters(vivienda, 3000, 10000, 50, 200, "chamberí");
        assertFalse(result);
    }

    @Test
    void testApplyFilters_NullSurface_AllowsNull() {
        Map<String, String> vivienda = new HashMap<>();
        vivienda.put("priceByArea", "5000");
        vivienda.put("district", "chamberí");
        vivienda.put("surface", null);

        boolean result = invokeApplyFilters(vivienda, 3000, 10000, 50, 200, "chamberí");
        assertTrue(result);
    }

    @Test
    void testSafeParseDouble_ValidNumber() {
        double value = invokeSafeParseDouble("123.45");
        assertEquals(123.45, value, 0.0001);
    }

    @Test
    void testSafeParseDouble_InvalidNumber() {
        double value = invokeSafeParseDouble("notANumber");
        assertEquals(Double.MAX_VALUE, value);
    }

    // Helper methods to access private static methods via reflection
    private boolean invokeApplyFilters(Map<String, String> vivienda, double minPriceByArea, double maxPriceByArea,
            double minSurface, double maxSurface, String districtFilter) {
        try {
            var m = FilterStatsParallel.class.getDeclaredMethod("applyFilters", Map.class, double.class, double.class,
                    double.class, double.class, String.class);
            m.setAccessible(true);
            return (boolean) m.invoke(null, vivienda, minPriceByArea, maxPriceByArea, minSurface, maxSurface,
                    districtFilter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double invokeSafeParseDouble(String s) {
        try {
            var m = FilterStatsParallel.class.getDeclaredMethod("safeParseDouble", String.class);
            m.setAccessible(true);
            return (double) m.invoke(null, s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}