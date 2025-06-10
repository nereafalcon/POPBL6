package com.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQUtil {
    private static final String HOST = "localhost";

    public static void sendMessage(String queue, String message) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void receiveMessages(String queue, DeliverCallback deliverCallback)
            throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(queue, false, false, false, null);
        // autoAck = false
        channel.basicConsume(queue, false, (consumerTag, delivery) -> {
            deliverCallback.handle(consumerTag, delivery);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }, consumerTag -> {
        });
    }

    public static void purgeQueue(String queueName) {
        try (Connection connection = getConnection();
                Channel channel = connection.createChannel()) {
            // Consume y descarta todos los mensajes pendientes
            GetResponse response;
            do {
                response = channel.basicGet(queueName, true);
            } while (response != null);
        } catch (Exception e) {
            // Puede que la cola no exista aún, ignora el error
        }
    }

    private static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        return factory.newConnection();
    }
}