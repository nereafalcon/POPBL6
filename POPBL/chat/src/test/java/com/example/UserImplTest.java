package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.rmi.RemoteException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserImplTest {

    private UserImpl user;

    @BeforeEach
    void setUp() throws RemoteException {
        user = new UserImpl("testuser");
    }

    @Test
    void testNotifyAddsNotification() throws RemoteException {
        String message = "Hello, World!";
        user.notify(message);

        List<String> notifications = user.getNotificaciones();
        assertEquals(1, notifications.size());
        assertTrue(notifications.get(0).contains(message));
        assertTrue(notifications.get(0).contains("["));
        assertTrue(notifications.get(0).contains("]"));
    }

    @Test
    void testMultipleNotifications() throws RemoteException {
        user.notify("First message");
        user.notify("Second message");

        List<String> notifications = user.getNotificaciones();
        assertEquals(2, notifications.size());
        assertTrue(notifications.get(0).contains("First message"));
        assertTrue(notifications.get(1).contains("Second message"));
    }

    @Test
    void testGetNotificacionesReturnsCopy() throws RemoteException {
        user.notify("Test message");
        List<String> notifications = user.getNotificaciones();
        notifications.add("Fake notification");

        // The original list should not be affected
        List<String> original = user.getNotificaciones();
        assertEquals(1, original.size());
        assertFalse(original.contains("Fake notification"));
    }
}