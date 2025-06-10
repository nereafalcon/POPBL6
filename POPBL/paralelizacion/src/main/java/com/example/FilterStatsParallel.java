package com.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class FilterStatsParallel {
    public static void main(String[] args) throws Exception {
        String csvPath = "paralelizacion\\src\\main\\resources\\idealista_toda_españa_2025-05-29.csv";
        int maxLines = 150 * 500; // Cambia este valor para pruebas empíricas
        List<Map<String, String>> viviendas = loadCSV(csvPath, maxLines);

        // Normalización de datos: limpiar espacios y pasar a minúsculas
        for (Map<String, String> v : viviendas) {
            if (v.get("district") != null)
                v.put("district", v.get("district").trim().toLowerCase());
        }

        // Filtros definidos por el usuario
        double minPriceByArea = 3000.0; // Precio mínimo por m²
        double maxPriceByArea = 100000.0; // Precio máximo por m²
        double minSurface = 50.0; // Superficie mínima
        double maxSurface = 2000.0; // Superficie máxima
        String districtFilter = "chamberí"; // Distrito específico (en minúsculas)

        // 1. Secuencial
        long t1 = System.currentTimeMillis();
        List<Map<String, String>> filteredSecuencial = viviendas.stream()
                .filter(v -> applyFilters(v, minPriceByArea, maxPriceByArea, minSurface, maxSurface, districtFilter))
                .collect(Collectors.toList());
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo stream secuencial con filtros: " + (t2 - t1) + " ms");
        System.out.println("Resultados filtrados (Secuencial): " + filteredSecuencial.size());
/*
        System.out.println("Resultados filtrados (Secuencial):");
        filteredSecuencial.forEach(v -> {
            System.out.println("PropertyCode: " + v.get("propertyCode") +
                               ", Price: " + v.get("price") +
                               ", Size: " + v.get("size") +
                               ", Rooms: " + v.get("rooms") +
                               ", District: " + v.get("district") +
                               ", PriceByArea: " + v.get("priceByArea"));
        });*/

        // 2. Paralelo
        long t3 = System.currentTimeMillis();
        List<Map<String, String>> filteredParalelo = viviendas.parallelStream()
                .filter(v -> applyFilters(v, minPriceByArea, maxPriceByArea, minSurface, maxSurface, districtFilter))
                .collect(Collectors.toList());
        long t4 = System.currentTimeMillis();
        System.out.println("Tiempo stream paralelo con filtros: " + (t4 - t3) + " ms");
        /*System.out.println("Resultados filtrados (Paralelo): " + filteredParalelo.size());

        System.out.println("Resultados filtrados (Paralelo):");
        filteredParalelo.forEach(v -> {
            System.out.println("PropertyCode: " + v.get("propertyCode") +
                               ", Price: " + v.get("price") +
                               ", Size: " + v.get("size") +
                               ", Rooms: " + v.get("rooms") +
                               ", District: " + v.get("district") +
                               ", PriceByArea: " + v.get("priceByArea"));
        });*/

        // 3. ExecutorService con varios hilos
        for (int threads : new int[] { 1, 2, 4, 8 }) {
            long tIni = System.currentTimeMillis();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            List<Future<List<Map<String, String>>>> futures = new ArrayList<>();
            int chunkSize = (int) Math.ceil((double) viviendas.size() / threads);
            for (int i = 0; i < viviendas.size(); i += chunkSize) {
                List<Map<String, String>> chunk = viviendas.subList(i, Math.min(i + chunkSize, viviendas.size()));
                futures.add(pool.submit(() -> chunk.stream()
                        .filter(v -> applyFilters(v, minPriceByArea, maxPriceByArea, minSurface, maxSurface, districtFilter))
                        .collect(Collectors.toList())));
            }

            List<Map<String, String>> filteredExecutor = new ArrayList<>();
            for (Future<List<Map<String, String>>> future : futures) {
                filteredExecutor.addAll(future.get());
            }
            pool.shutdown();
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
            long tFin = System.currentTimeMillis();
            System.out.println("Tiempo ExecutorService (" + threads + " hilos) con filtros: " + (tFin - tIni) + " ms");
            /*System.out.println("Resultados filtrados (ExecutorService " + threads + " hilos): " + filteredExecutor.size());

            System.out.println("Resultados filtrados (ExecutorService " + threads + " hilos):");
            filteredExecutor.forEach(v -> {
                System.out.println("PropertyCode: " + v.get("propertyCode") +
                                   ", Price: " + v.get("price") +
                                   ", Size: " + v.get("size") +
                                   ", Rooms: " + v.get("rooms") +
                                   ", District: " + v.get("district") +
                                   ", PriceByArea: " + v.get("priceByArea"));
            });*/
        }

        // 4. ForkJoinPool
        long t5 = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        List<Map<String, String>> filteredForkJoin = forkJoinPool.submit(() ->
                viviendas.parallelStream()
                        .filter(v -> applyFilters(v, minPriceByArea, maxPriceByArea, minSurface, maxSurface, districtFilter))
                        .collect(Collectors.toList())
        ).get();
        forkJoinPool.shutdown();
        long t6 = System.currentTimeMillis();
        System.out.println("Tiempo ForkJoinPool con filtros: " + (t6 - t5) + " ms");
        /*
        System.out.println("Resultados filtrados (ForkJoinPool): " + filteredForkJoin.size());

        System.out.println("Resultados filtrados (ForkJoinPool):");
        filteredForkJoin.forEach(v -> {
            System.out.println("PropertyCode: " + v.get("propertyCode") +
                               ", Price: " + v.get("price") +
                               ", Size: " + v.get("size") +
                               ", Rooms: " + v.get("rooms") +
                               ", District: " + v.get("district") +
                               ", PriceByArea: " + v.get("priceByArea"));
        });*/
    }

    private static boolean applyFilters(Map<String, String> vivienda, double minPriceByArea, double maxPriceByArea,
                                        double minSurface, double maxSurface, String districtFilter) {
        double priceByArea = safeParseDouble(vivienda.get("priceByArea"));
        double surface = safeParseDouble(vivienda.get("surface"));
        String district = vivienda.get("district");

        // Normalizar el campo "district" para evitar problemas de mayúsculas/minúsculas
        if (district != null) {
            district = district.trim().toLowerCase().replaceAll("[^a-záéíóúñ ]", "");
        }

        // Manejar valores null en "surface"
        boolean surfaceMatches = (vivienda.get("surface") == null) || (surface >= minSurface && surface <= maxSurface);

        return priceByArea >= minPriceByArea && priceByArea <= maxPriceByArea &&
               surfaceMatches &&
               (districtFilter == null || (district != null && district.contains(districtFilter)));
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
