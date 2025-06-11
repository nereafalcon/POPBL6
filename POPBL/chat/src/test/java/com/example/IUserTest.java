package com.example;

import org.junit.jupiter.api.Test;
import java.rmi.RemoteException;
import static org.junit.jupiter.api.Assertions.*;

class IUserTest {

    // A simple mock implementation of IUser for testing
    static class MockUser implements IUser {
        String lastMessage = null;

        @Override
        public void notify(String message) throws RemoteException {
            lastMessage = message;
        }
    }

    @Test
    void testNotifyReceivesMessage() throws RemoteException {
        MockUser user = new MockUser();
        String testMessage = "Hello, user!";
        user.notify(testMessage);
        assertEquals(testMessage, user.lastMessage);
    }

    @Test
    void testNotifyWithNullMessage() throws RemoteException {
        MockUser user = new MockUser();
        user.notify(null);
        assertNull(user.lastMessage);
    }
}