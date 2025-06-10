package com.example;

import com.rabbitmq.client.*;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class Master {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception {
        String csvPath = "paralelizacion\\src\\main\\resources\\idealista_toda_españa_2025-05-29.csv";
        int maxLines = 150 * 5; // Cambia este valor para pruebas empíricas
        List<Map<String, String>> viviendas = loadCSV(csvPath, maxLines);

        // Dividir los datos en bloques
        int chunkSize = (int) Math.ceil((double) viviendas.size() / 3); // Dividir en 3 bloques
        List<List<Map<String, String>>> chunks = new ArrayList<>();
        for (int i = 0; i < viviendas.size(); i += chunkSize) {
            chunks.add(viviendas.subList(i, Math.min(i + chunkSize, viviendas.size())));
        }

        // Conectar a RabbitMQ y enviar los bloques
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

            for (List<Map<String, String>> chunk : chunks) {
                String message = serializeChunk(chunk);
                channel.basicPublish("", TASK_QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Master: Enviado bloque de datos a la cola.");
            }
        }
    }

    private static List<Map<String, String>> loadCSV(String path, int maxLines) throws IOException, com.opencsv.exceptions.CsvValidationException {
        List<Map<String, String>> result = new ArrayList<>();
        try (CSVReader reader = new CSVReaderBuilder(new FileReader(path)).build()) {
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
        return result;
    }

    private static String serializeChunk(List<Map<String, String>> chunk) {
        // Serializar el bloque de datos (puedes usar JSON o cualquier formato)
        return chunk.toString(); // Simplificado para el ejemplo
    }
}