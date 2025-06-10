package com.example;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Slave {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        System.out.println("Slave: Esperando tareas...");

        channel.basicConsume(TASK_QUEUE_NAME, false, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            List<Map<String, String>> chunk = deserializeChunk(message);

            // Procesar el bloque de datos
            Map<String, DoubleSummaryStatistics> stats = chunk.stream()
                    .filter(v -> safeParseDouble(v.get("priceByArea")) < Double.MAX_VALUE && v.get("district") != null)
                    .collect(Collectors.groupingBy(
                            v -> v.get("district"),
                            Collectors.summarizingDouble(v -> safeParseDouble(v.get("priceByArea")))));

            System.out.println("Slave: Procesado bloque de datos.");
            printStats(stats);

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }, consumerTag -> {
        });
    }

    private static List<Map<String, String>> deserializeChunk(String message) {
        // Deserializar el bloque de datos (puedes usar JSON o cualquier formato)
        return new ArrayList<>(); // Simplificado para el ejemplo
    }

    private static double safeParseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    private static void printStats(Map<String, DoubleSummaryStatistics> stats) {
        stats.forEach((barrio, summary) -> {
            System.out.printf("Barrio: %-30s | N: %5d | Media: %10.2f €/m² | Min: %10.2f | Max: %10.2f%n",
                    barrio, summary.getCount(), summary.getAverage(), summary.getMin(), summary.getMax());
        });
    }
}