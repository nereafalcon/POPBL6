package com.example;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServidorMain {

    private static final Logger logger = LoggerFactory.getLogger(ServidorMain.class);

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            UserServiceImpl service = new UserServiceImpl();
            Registry registry = LocateRegistry.getRegistry();
            // Registra el servicio RMI con el nombre
            registry.rebind("UserService", service);
            logger.info("Servidor RMI iniciado.");
        } catch (Exception e) {
            logger.error("Error al iniciar el servidor RMI", e);
        }
    }
}
