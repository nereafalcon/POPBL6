package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class ChatMessageTest {

    @Test
    void testConstructorAndGetters() {
        String sender = "Alice";
        String content = "Hello, World!";
        ChatMessage message = new ChatMessage(sender, content);

        assertEquals(sender, message.getSender());
        assertEquals(content, message.getContent());
        assertNotNull(message.getTimestamp());
        assertTrue(message.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testToStringFormat() {
        String sender = "Bob";
        String content = "Hi!";
        ChatMessage message = new ChatMessage(sender, content);

        String result = message.toString();
        assertTrue(result.contains(sender));
        assertTrue(result.contains(content));
        assertTrue(result.matches("\\[.*\\] " + sender + ": " + content));
    }

    @Test
    void testDifferentMessagesHaveDifferentTimestamps() throws InterruptedException {
        ChatMessage msg1 = new ChatMessage("User1", "First");
        Thread.sleep(10); // Ensure a different timestamp
        ChatMessage msg2 = new ChatMessage("User2", "Second");

        assertNotEquals(msg1.getTimestamp(), msg2.getTimestamp());
    }
}