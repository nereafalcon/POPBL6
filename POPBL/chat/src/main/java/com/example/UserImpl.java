package com.example;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserImpl extends UnicastRemoteObject implements IUser {
    private List<String> notificaciones = new ArrayList<>();

    public UserImpl(String username) throws RemoteException {
    }

    @Override
    public void notify(String message) throws RemoteException {
        String noti = "[" + LocalDateTime.now() + "] " + message;
        notificaciones.add(noti);
        System.out.println("[NOTIFICACIÓN] " + noti);
    }

    public List<String> getNotificaciones() {
        return new ArrayList<>(notificaciones);
    }
}