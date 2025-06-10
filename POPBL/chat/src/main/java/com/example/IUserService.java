package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IUserService extends Remote {
    boolean register(String username, String role, IUser callback) throws RemoteException;

    boolean login(String username, IUser callback) throws RemoteException;

    String getRole(String username) throws RemoteException; // Para saber el rol tras login

    // Gestión de casas
    int crearCasa(String vendedor, String descripcion) throws RemoteException;

    List<Casa> listarCasas() throws RemoteException;

    void addCasaFavorita(String comprador, int casaId) throws RemoteException;

    void eliminarCasaFavorita(String comprador, int casaId) throws RemoteException;

    List<Casa> listarCasasFavoritas(String comprador) throws RemoteException;

    // Gestión de chat
    String crearChat(int casaId, String comprador) throws RemoteException;

    void enviarMensaje(String chatId, ChatMessage mensaje) throws RemoteException;

    List<ChatMessage> obtenerHistorial(String chatId) throws RemoteException;

    List<String> obtenerChatsUsuario(String username) throws RemoteException;

    void usuarioSaleDeChat(String chatId, String username) throws RemoteException;

    void usuarioEntraEnChat(String chatId, String username) throws RemoteException;
}