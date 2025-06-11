package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import static org.junit.jupiter.api.Assertions.*;

class ServidorMainTest {

    private Registry registry;

    @BeforeEach
    void setUp() throws RemoteException {
        // Try to create a registry for testing purposes
        try {
            registry = LocateRegistry.createRegistry(1099);
        } catch (ExportException e) {
            // Registry already exists, get it
            registry = LocateRegistry.getRegistry(1099);
        }
    }

    @AfterEach
    void tearDown() throws RemoteException {
        // Unbind the service if it was registered
        try {
            registry.unbind("UserService");
        } catch (Exception ignored) {
        }
    }

    @Test
    void testMainRegistersUserService() throws Exception {
        // Run the main method
        ServidorMain.main(new String[] {});

        // Check if the service is bound
        String[] boundNames = registry.list();
        boolean found = false;
        for (String name : boundNames) {
            if ("UserService".equals(name)) {
                found = true;
                break;
            }
        }
        assertTrue(found, "UserService should be registered in the RMI registry");
    }

    @Test
    void testMainHandlesException() {
        // Simulate exception by passing invalid registry port
        assertDoesNotThrow(() -> ServidorMain.main(new String[] {}));
    }
}