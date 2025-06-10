package com.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class BarrioPriceStatsParallel {

    public static void main(String[] args) throws Exception {
        String csvPath = "paralelizacion\\src\\main\\resources\\idealista_toda_españa_2025-05-29.csv";
        int maxLines = 150 * 500; // Cambiar valores
        List<Map<String, String>> viviendas = loadCSV(csvPath, maxLines);

        // Normalización de datos: limpiar espacios y pasar a minúsculas
        for (Map<String, String> v : viviendas) {
            if (v.get("district") != null)
                v.put("district", v.get("district").trim().toLowerCase());
        }

        // 1. Secuencial
        long t1 = System.currentTimeMillis();
        Map<String, DoubleSummaryStatistics> statsSecuencial = viviendas.stream()
                .filter(v -> safeParseDouble(v.get("priceByArea")) < Double.MAX_VALUE && v.get("district") != null
                        && !v.get("district").isEmpty())
                .collect(Collectors.groupingBy(
                        v -> v.get("district"),
                        Collectors.summarizingDouble(v -> safeParseDouble(v.get("priceByArea")))));
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo stream secuencial: " + (t2 - t1) + " ms");
        // printStats("Secuencial", statsSecuencial);

        // 2. Paralelo
        long t3 = System.currentTimeMillis();
        Map<String, DoubleSummaryStatistics> statsParalelo = viviendas.parallelStream()
                .filter(v -> safeParseDouble(v.get("priceByArea")) < Double.MAX_VALUE && v.get("district") != null
                        && !v.get("district").isEmpty())
                .collect(Collectors.groupingBy(
                        v -> v.get("district"),
                        Collectors.summarizingDouble(v -> safeParseDouble(v.get("priceByArea")))));
        long t4 = System.currentTimeMillis();
        System.out.println("Tiempo stream paralelo: " + (t4 - t3) + " ms");
        // printStats("Paralelo", statsParalelo);

        // 3. ExecutorService con varios hilos
        for (int threads : new int[] { 1, 2, 4, 8 }) {
            long tIni = System.currentTimeMillis();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            Map<String, List<Map<String, String>>> grouped = viviendas.stream()
                    .filter(v -> safeParseDouble(v.get("priceByArea")) < Double.MAX_VALUE && v.get("district") != null
                            && !v.get("district").isEmpty())
                    .collect(Collectors.groupingBy(v -> v.get("district")));
            Map<String, DoubleSummaryStatistics> stats = new ConcurrentHashMap<>();
            List<Future<?>> futures = new ArrayList<>();
            for (String barrio : grouped.keySet()) {
                List<Map<String, String>> lista = grouped.get(barrio);
                futures.add(pool.submit(() -> {
                    try {
                        DoubleSummaryStatistics s = lista.stream()
                                .mapToDouble(v -> safeParseDouble(v.get("priceByArea")))
                                .summaryStatistics();
                        stats.put(barrio, s);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
            }
            for (Future<?> f : futures)
                f.get();
            pool.shutdown();
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
            long tFin = System.currentTimeMillis();
            System.out.println("Tiempo ExecutorService (" + threads + " hilos): " + (tFin - tIni) + " ms");
            // printStats("ExecutorService (" + threads + " hilos)", stats);
        }

        // Chunking
        long t5 = System.currentTimeMillis();
        int chunkSize = (int) Math.ceil((double) viviendas.size() / 4); // Dividir en 4 bloques
        List<List<Map<String, String>>> chunks = new ArrayList<>();
        for (int i = 0; i < viviendas.size(); i += chunkSize) {
            chunks.add(viviendas.subList(i, Math.min(i + chunkSize, viviendas.size())));
        }

        ExecutorService poolBalanced = Executors.newFixedThreadPool(4);
        List<Future<Map<String, DoubleSummaryStatistics>>> balancedFutures = new ArrayList<>();
        for (List<Map<String, String>> chunk : chunks) {
            balancedFutures.add(poolBalanced.submit(() -> {
                Map<String, DoubleSummaryStatistics> localStats = new HashMap<>();
                for (Map<String, String> v : chunk) {
                    String barrio = v.get("district");
                    double priceByArea = safeParseDouble(v.get("priceByArea"));
                    localStats.computeIfAbsent(barrio, k -> new DoubleSummaryStatistics())
                            .accept(priceByArea);
                }
                return localStats;
            }));
        }

        Map<String, DoubleSummaryStatistics> balancedStats = new ConcurrentHashMap<>();
        for (Future<Map<String, DoubleSummaryStatistics>> future : balancedFutures) {
            Map<String, DoubleSummaryStatistics> localStats = future.get();
            localStats.forEach((barrio, summary) -> balancedStats.merge(barrio, summary, (s1, s2) -> {
                s1.combine(s2);
                return s1;
            }));
        }
        poolBalanced.shutdown();
        long t6 = System.currentTimeMillis();
        System.out.println("Tiempo balanceo de carga (4 bloques): " + (t6 - t5) + " ms");
        // printStats("Balanceo de Carga", balancedStats);

        // 5. ForkJoinPool
        long t7 = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        Map<String, DoubleSummaryStatistics> forkJoinStats = null;
        try {
            forkJoinStats = forkJoinPool.submit(() -> viviendas.parallelStream()
                    .filter(v -> safeParseDouble(v.get("priceByArea")) < Double.MAX_VALUE && v.get("district") != null)
                    .collect(Collectors.groupingBy(
                            v -> v.get("district"),
                            Collectors.summarizingDouble(v -> safeParseDouble(v.get("priceByArea"))))))
                    .get();
        } finally {
            forkJoinPool.shutdown();
        }
        long t8 = System.currentTimeMillis();
        System.out.println("Tiempo ForkJoinPool (4 hilos): " + (t8 - t7) + " ms");
        // printStats("ForkJoinPool", forkJoinStats);
    }

    private static void printStats(String strategy, Map<String, DoubleSummaryStatistics> stats) {
        System.out.println("Estadísticas de precio por m² por barrio (" + strategy + "):");
        stats.forEach((barrio, summary) -> {
            System.out.printf("Barrio: %-30s | N: %5d | Media: %10.2f €/m² | Min: %10.2f | Max: %10.2f%n",
                    barrio, summary.getCount(), summary.getAverage(), summary.getMin(), summary.getMax());
        });
        System.out.println();
    }

    private static List<Map<String, String>> loadCSV(String path, int maxLines)
            throws IOException, com.opencsv.exceptions.CsvValidationException {
        List<Map<String, String>> result = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path))
                .withCSVParser(new com.opencsv.CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] headers = reader.readNext();
            String[] values;
            int lineCount = 0;
            while ((values = reader.readNext()) != null && lineCount < maxLines) {
                if (values.length != headers.length)
                    continue;
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < headers.length; j++) {
                    map.put(headers[j], values[j]);
                }
                result.add(map);
                lineCount++;
            }
        }
        System.out.println("Viviendas cargadas correctamente: " + result.size());
        return result;
    }

    private static double safeParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }
}