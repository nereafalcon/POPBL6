package com.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class BarrioPriceStatsParallel {

    private static final String DISTRICT_KEY = "district";
    private static final String PRICEBYAREA_KEY = "priceByArea";

    // Nueva interfaz funcional que permite lanzar checked exceptions
    @FunctionalInterface
    interface TareaConException {
        void run() throws Exception;
    }

    public static void main(String[] args) throws Exception {
        String csvPath = "paralelizacion\\src\\main\\resources\\idealista_toda_españa_2025-05-29.csv";
        int maxLines = 150 * 500;
        List<Map<String, String>> viviendas = loadCSV(csvPath, maxLines);

        normalizarDatos(viviendas);

        medirTiempo("stream secuencial", () -> calcularStatsStream(viviendas, false));

        medirTiempo("stream paralelo", () -> calcularStatsStream(viviendas, true));

        int[] hilos = { 1, 2, 4, 8 };
        for (int threads : hilos) {
            medirTiempo("ExecutorService (" + threads + " hilos)", () -> calcularStatsExecutor(viviendas, threads));
        }

        medirTiempo("balanceo de carga (4 bloques)", () -> calcularStatsChunking(viviendas, 4));

        medirTiempo("ForkJoinPool (4 hilos)", () -> calcularStatsForkJoin(viviendas, 4));
    }

    public static void normalizarDatos(List<Map<String, String>> viviendas) {
        for (Map<String, String> v : viviendas) {
            if (v.get(DISTRICT_KEY) != null)
                v.put(DISTRICT_KEY, v.get(DISTRICT_KEY).trim().toLowerCase());
        }
    }

    public static Map<String, DoubleSummaryStatistics> calcularStatsStream(List<Map<String, String>> viviendas,
            boolean paralelo) {
        Stream<Map<String, String>> stream = paralelo ? viviendas.parallelStream() : viviendas.stream();
        return stream
                .filter(v -> safeParseDouble(v.get(PRICEBYAREA_KEY)) < Double.MAX_VALUE && v.get(DISTRICT_KEY) != null
                        && !v.get(DISTRICT_KEY).isEmpty())
                .collect(Collectors.groupingBy(
                        v -> v.get(DISTRICT_KEY),
                        Collectors.summarizingDouble(v -> safeParseDouble(v.get(PRICEBYAREA_KEY)))));
    }

    public static void calcularStatsExecutor(List<Map<String, String>> viviendas, int threads) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        Map<String, List<Map<String, String>>> grouped = viviendas.stream()
                .filter(v -> safeParseDouble(v.get(PRICEBYAREA_KEY)) < Double.MAX_VALUE && v.get(DISTRICT_KEY) != null
                        && !v.get(DISTRICT_KEY).isEmpty())
                .collect(Collectors.groupingBy(v -> v.get(DISTRICT_KEY)));

        Map<String, DoubleSummaryStatistics> stats = new ConcurrentHashMap<>();
        List<Future<?>> futures = new ArrayList<>();

        for (String barrio : grouped.keySet()) {
            List<Map<String, String>> lista = grouped.get(barrio);
            futures.add(pool.submit(() -> {
                DoubleSummaryStatistics s = lista.stream()
                        .mapToDouble(v -> safeParseDouble(v.get(PRICEBYAREA_KEY)))
                        .summaryStatistics();
                stats.put(barrio, s);
            }));
        }
        for (Future<?> f : futures)
            f.get();
        pool.shutdown();
        if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }
        // printStats("ExecutorService (" + threads + " hilos)", stats);
    }

    public static void calcularStatsChunking(List<Map<String, String>> viviendas, int bloques) throws Exception {
        int chunkSize = (int) Math.ceil((double) viviendas.size() / bloques);
        List<List<Map<String, String>>> chunks = new ArrayList<>();
        for (int i = 0; i < viviendas.size(); i += chunkSize) {
            chunks.add(viviendas.subList(i, Math.min(i + chunkSize, viviendas.size())));
        }

        ExecutorService poolBalanced = Executors.newFixedThreadPool(bloques);
        List<Future<Map<String, DoubleSummaryStatistics>>> balancedFutures = new ArrayList<>();

        for (List<Map<String, String>> chunk : chunks) {
            balancedFutures.add(poolBalanced.submit(() -> {
                Map<String, DoubleSummaryStatistics> localStats = new HashMap<>();
                for (Map<String, String> v : chunk) {
                    String barrio = v.get(DISTRICT_KEY);
                    double priceByArea = safeParseDouble(v.get(PRICEBYAREA_KEY));
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
        // printStats("Balanceo de Carga", balancedStats);
    }

    public static void calcularStatsForkJoin(List<Map<String, String>> viviendas, int threads) throws Exception {
        ForkJoinPool forkJoinPool = new ForkJoinPool(threads);
        try {
            Map<String, DoubleSummaryStatistics> forkJoinStats = forkJoinPool.submit(() -> viviendas.parallelStream()
                    .filter(v -> safeParseDouble(v.get(PRICEBYAREA_KEY)) < Double.MAX_VALUE
                            && v.get(DISTRICT_KEY) != null)
                    .collect(Collectors.groupingBy(
                            v -> v.get(DISTRICT_KEY),
                            Collectors.summarizingDouble(v -> safeParseDouble(v.get(PRICEBYAREA_KEY))))))
                    .get();
            // printStats("ForkJoinPool", forkJoinStats);
        } finally {
            forkJoinPool.shutdown();
        }
    }

    // Cambiado para usar la interfaz funcional que permite Exception
    public static void medirTiempo(String descripcion, TareaConException tarea) throws Exception {
        long start = System.currentTimeMillis();
        tarea.run();
        long end = System.currentTimeMillis();
        System.out.println("Tiempo " + descripcion + ": " + (end - start) + " ms");
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

    public static double safeParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }
}
