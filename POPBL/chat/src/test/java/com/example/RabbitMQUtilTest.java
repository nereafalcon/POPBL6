package com.example;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class RabbitMQUtilTest {

    private static final String TEST_QUEUE = "testQueue";

    @BeforeEach
    void setUp() {
        RabbitMQUtil.purgeQueue(TEST_QUEUE);
    }

    @AfterEach
    void tearDown() {
        RabbitMQUtil.purgeQueue(TEST_QUEUE);
    }

    @Test
    void testSendMessageAndReceiveMessages() throws Exception {
        String testMessage = "Hello, RabbitMQ!";
        CountDownLatch latch = new CountDownLatch(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String received = new String(delivery.getBody(), StandardCharsets.UTF_8);
            assertEquals(testMessage, received);
            latch.countDown();
        };

        RabbitMQUtil.receiveMessages(TEST_QUEUE, deliverCallback);
        RabbitMQUtil.sendMessage(TEST_QUEUE, testMessage);

        boolean received = latch.await(2, TimeUnit.SECONDS);
        assertTrue(received, "Message was not received in time");
    }

    @Test
    void testPurgeQueueRemovesMessages() throws Exception {
        RabbitMQUtil.sendMessage(TEST_QUEUE, "msg1");
        RabbitMQUtil.sendMessage(TEST_QUEUE, "msg2");

        // Purge the queue
        RabbitMQUtil.purgeQueue(TEST_QUEUE);

        // Try to receive a message, should not get any
        CountDownLatch latch = new CountDownLatch(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> latch.countDown();

        RabbitMQUtil.receiveMessages(TEST_QUEUE, deliverCallback);

        boolean received = latch.await(1, TimeUnit.SECONDS);
        assertFalse(received, "Queue should be empty after purge");
    }

    @Test
    void testSendMessageToNonExistentQueueCreatesQueue() {
        String queueName = "nonExistentQueue";
        try {
            RabbitMQUtil.sendMessage(queueName, "test");
            // If no exception, test passes
        } catch (Exception e) {
            fail("Should not throw exception when sending to non-existent queue: " + e.getMessage());
        } finally {
            RabbitMQUtil.purgeQueue(queueName);
        }
    }

    @Test
    void testReceiveMessagesHandlesMultipleMessages() throws Exception {
        String msg1 = "first";
        String msg2 = "second";
        CountDownLatch latch = new CountDownLatch(2);

        RabbitMQUtil.sendMessage(TEST_QUEUE, msg1);
        RabbitMQUtil.sendMessage(TEST_QUEUE, msg2);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> latch.countDown();

        RabbitMQUtil.receiveMessages(TEST_QUEUE, deliverCallback);

        boolean allReceived = latch.await(2, TimeUnit.SECONDS);
        assertTrue(allReceived, "Did not receive all messages");
    }
}