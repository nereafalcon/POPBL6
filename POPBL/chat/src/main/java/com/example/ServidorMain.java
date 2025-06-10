package com.example;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServidorMain {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            UserServiceImpl service = new UserServiceImpl();
            Registry registry = LocateRegistry.getRegistry();
            // Registra el servicio RMI con el nombre
            registry.rebind("UserService", service);
            System.out.println("Servidor RMI iniciado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}