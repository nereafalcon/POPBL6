package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ParallelDistanceCalculator {

    public static class Property {
        String code;
        double latitude;
        double longitude;
        double distance;
		private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");


        public Property(String code, double latitude, double longitude) {
            this.code = code;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "Property{" +
                    "code='" + code + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", distance(km)=" + distance +
                    '}';
        }
    }

    public static List<Property> readPropertiesFromCSV() throws IOException {
        String path = "paralelizacion\\src\\main\\resources\\idealista_toda_españa_2025-05-29.csv";
        List<Property> properties = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(path));
        List<String> ignoredRows = new ArrayList<>(); // Lista para registrar filas ignoradas

        for (int i = 1; i < lines.size(); i++) { // Saltar header
            String[] cols = lines.get(i).split(";");
            if (cols.length < 18) {
                ignoredRows.add("Fila ignorada por menos columnas: " + Arrays.toString(cols));
                continue;
            }
            String code = cols[0].trim();
            try {
                // Intentar convertir latitud y longitud a números
                double lat = parseCoordinate(cols[17].trim());
                double lon = parseCoordinate(cols[18].trim());
                properties.add(new Property(code, lat, lon));
            } catch (NumberFormatException e) {
                // Registrar fila ignorada con más detalles
                ignoredRows.add("Fila ignorada por dato inválido - code: " + code + ", lat: " + cols[17] + ", lon: " + cols[18]);
            }
        }

        // Imprimir las filas ignoradas
        if (!ignoredRows.isEmpty()) {
            System.err.println("Filas ignoradas:");
            ignoredRows.forEach(System.err::println);
        }

        System.out.println("Viviendas cargadas correctamente: " + properties.size());
        return properties;
    }

    // Método para limpiar y convertir coordenadas
    private static double parseCoordinate(String coordinate) throws NumberFormatException {
        try {
            // Intentar convertir directamente
            return Double.parseDouble(coordinate);
        } catch (NumberFormatException e) {
            // Si no es un número, intentar limpiar el texto
			Matcher matcher = DIGIT_PATTERN.matcher(coordinate);
			if (matcher.find()) {
				String cleaned = coordinate.replaceAll("[^\\d.-]", "");
				return Double.parseDouble(cleaned);
			}
            // Si no contiene números, lanzar la excepción
            throw new NumberFormatException("Coordenada inválida: " + coordinate);
        }
    }

    // Fórmula Haversine para distancia en km entre dos puntos geográficos
    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public static void calculateDistancesParallel(List<Property> properties, double refLat, double refLon)
            throws InterruptedException {
        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (Property p : properties) {
            tasks.add(() -> {
                p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon);
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();
    }

    public static void main(String[] args) throws Exception {
        List<Property> properties = readPropertiesFromCSV();

        // Coordenadas del punto de referencia (en grados)
        double refLat = 40.4168; // ejemplo: Madrid centro
        double refLon = -3.7038;

        // 1. Secuencial
        long t1 = System.currentTimeMillis();
        properties.forEach(p -> p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon));
        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo secuencial: " + (t2 - t1) + " ms");

        // 2. Paralelo con parallelStream
        long t3 = System.currentTimeMillis();
        properties.parallelStream().forEach(p -> p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon));
        long t4 = System.currentTimeMillis();
        System.out.println("Tiempo parallelStream: " + (t4 - t3) + " ms");

        // 3. ExecutorService con varios hilos
        for (int threads : new int[] { 1, 2, 4, 8 }) {
            long tIni = System.currentTimeMillis();
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            List<Future<?>> futures = new ArrayList<>();
            for (Property p : properties) {
                futures.add(pool.submit(() -> {
                    p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon);
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
        }

        // 4. Balanceo de carga
        long t5 = System.currentTimeMillis();
        int chunkSize = (int) Math.ceil((double) properties.size() / 4); // Dividir en 4 bloques
        List<List<Property>> chunks = new ArrayList<>();
        for (int i = 0; i < properties.size(); i += chunkSize) {
            chunks.add(properties.subList(i, Math.min(i + chunkSize, properties.size())));
        }

        ExecutorService poolBalanced = Executors.newFixedThreadPool(4);
        List<Future<Void>> balancedFutures = new ArrayList<>();
        for (List<Property> chunk : chunks) {
            balancedFutures.add(poolBalanced.submit(() -> {
                for (Property p : chunk) {
                    p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon);
                }
                return null;
            }));
        }
        for (Future<Void> f : balancedFutures) {
            f.get();
        }
        poolBalanced.shutdown();
        long t6 = System.currentTimeMillis();
        System.out.println("Tiempo balanceo de carga (4 bloques): " + (t6 - t5) + " ms");

        // 5. ForkJoinPool
        long t7 = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        forkJoinPool.submit(() -> properties.parallelStream()
                .forEach(p -> p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon))).get();
        forkJoinPool.shutdown();
        long t8 = System.currentTimeMillis();
        System.out.println("Tiempo ForkJoinPool (4 hilos): " + (t8 - t7) + " ms");

        // 6. ThreadPoolExecutor personalizado
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4, 8, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        long tStart = System.currentTimeMillis();
        List<Future<?>> futures = new ArrayList<>();
        for (Property p : properties) {
            futures.add(executor.submit(() -> {
                p.distance = haversineDistance(p.latitude, p.longitude, refLat, refLon);
            }));
        }
        for (Future<?> f : futures) {
            f.get();
        }
        executor.shutdown();
        long tEnd = System.currentTimeMillis();
        System.out.println("Tiempo ThreadPoolExecutor personalizado: " + (tEnd - tStart) + " ms");

        // 7. Parallel Array
        Property[] propertyArray = properties.toArray(new Property[0]);
        long tStartArray = System.currentTimeMillis();
        Arrays.parallelSetAll(propertyArray, i -> {
            propertyArray[i].distance = haversineDistance(
                propertyArray[i].latitude, propertyArray[i].longitude, refLat, refLon);
            return propertyArray[i];
        });
        long tEndArray = System.currentTimeMillis();
        System.out.println("Tiempo Parallel Array: " + (tEndArray - tStartArray) + " ms");

        // 8. MapReduce
        long tStartMapReduce = System.currentTimeMillis();
        double totalDistance = properties.parallelStream()
            .mapToDouble(p -> haversineDistance(p.latitude, p.longitude, refLat, refLon))
            .sum();
        long tEndMapReduce = System.currentTimeMillis();
        System.out.println("Tiempo MapReduce (suma de distancias): " + (tEndMapReduce - tStartMapReduce) + " ms");

        // Comentar los prints de las propiedades
        // properties.forEach(System.out::println);
    }
}
