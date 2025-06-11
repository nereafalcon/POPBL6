package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class BarrioPriceStatsParallelTest {

    private List<Map<String, String>> viviendas;

    @BeforeEach
    void setUp() {
        viviendas = new ArrayList<>();
        viviendas.add(new HashMap<>(Map.of("district", "Centro", "priceByArea", "2000")));
        viviendas.add(new HashMap<>(Map.of("district", "Centro", "priceByArea", "3000")));
        viviendas.add(new HashMap<>(Map.of("district", "Norte", "priceByArea", "1500")));
        viviendas.add(new HashMap<>(Map.of("district", "Norte", "priceByArea", "2500")));
        viviendas.add(new HashMap<>(Map.of("district", "Sur", "priceByArea", "1000")));
        viviendas.add(new HashMap<>(Map.of("district", "Sur", "priceByArea", ""))); // Invalid price
        viviendas.add(new HashMap<>(Map.of("district", "", "priceByArea", "500"))); // Invalid district
        viviendas.add(new HashMap<>(Map.of("district", null, "priceByArea", "500"))); // Null district
    }

    @Test
    void testNormalizarDatos() {
        viviendas.get(0).put("district", "  Centro  ");
        viviendas.get(1).put("district", "NORTE");
        BarrioPriceStatsParallel.normalizarDatos(viviendas);
        assertEquals("centro", viviendas.get(0).get("district"));
        assertEquals("norte", viviendas.get(1).get("district"));
    }

    @Test
    void testCalcularStatsStreamSecuencial() {
        BarrioPriceStatsParallel.normalizarDatos(viviendas);
        Map<String, DoubleSummaryStatistics> stats = BarrioPriceStatsParallel.calcularStatsStream(viviendas, false);
        assertEquals(3, stats.size());
        assertEquals(2, stats.get("centro").getCount());
        assertEquals(2500.0, stats.get("centro").getAverage());
        assertEquals(2, stats.get("norte").getCount());
        assertEquals(2000.0, stats.get("norte").getAverage());
        assertEquals(1, stats.get("sur").getCount());
        assertEquals(1000.0, stats.get("sur").getAverage());
    }

    @Test
    void testCalcularStatsStreamParalelo() {
        BarrioPriceStatsParallel.normalizarDatos(viviendas);
        Map<String, DoubleSummaryStatistics> stats = BarrioPriceStatsParallel.calcularStatsStream(viviendas, true);
        assertEquals(3, stats.size());
        assertEquals(2, stats.get("centro").getCount());
        assertEquals(2500.0, stats.get("centro").getAverage());
        assertEquals(2, stats.get("norte").getCount());
        assertEquals(2000.0, stats.get("norte").getAverage());
        assertEquals(1, stats.get("sur").getCount());
        assertEquals(1000.0, stats.get("sur").getAverage());
    }

    @Test
    void testSafeParseDouble() {
        assertEquals(123.45, BarrioPriceStatsParallel.safeParseDouble("123.45"));
        assertEquals(Double.MAX_VALUE, BarrioPriceStatsParallel.safeParseDouble("notANumber"));
        assertEquals(Double.MAX_VALUE, BarrioPriceStatsParallel.safeParseDouble(""));
        assertEquals(Double.MAX_VALUE, BarrioPriceStatsParallel.safeParseDouble(null));
    }

    @Test
    void testCalcularStatsExecutor() throws Exception {
        BarrioPriceStatsParallel.normalizarDatos(viviendas);
        // Should not throw and should process all valid districts
        BarrioPriceStatsParallel.calcularStatsExecutor(viviendas, 2);
    }

    @Test
    void testCalcularStatsChunking() throws Exception {
        BarrioPriceStatsParallel.normalizarDatos(viviendas);
        // Should not throw and should process all valid districts
        BarrioPriceStatsParallel.calcularStatsChunking(viviendas, 2);
    }

    @Test
    void testCalcularStatsForkJoin() throws Exception {
        BarrioPriceStatsParallel.normalizarDatos(viviendas);
        // Should not throw and should process all valid districts
        BarrioPriceStatsParallel.calcularStatsForkJoin(viviendas, 2);
    }

    @Test
    void testMedirTiempoRunsTask() throws Exception {
        AtomicBoolean called = new AtomicBoolean(false);
        BarrioPriceStatsParallel.medirTiempo("test", () -> called.set(true));
        assertTrue(called.get());
    }
}