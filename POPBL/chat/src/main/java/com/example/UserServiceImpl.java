package com.example;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;

public class UserServiceImpl extends UnicastRemoteObject implements IUserService {
    private Map<String, IUser> users = new HashMap<>();
    private Map<String, String> roles = new HashMap<>();
    private Map<Integer, Casa> casas = new HashMap<>();
    private Map<String, Set<Integer>> favoritos = new HashMap<>();
    private Map<String, List<ChatMessage>> chats = new HashMap<>();
    // Mapa: chatId -> Set de usuarios conectados a ese chat
    private Map<String, Set<String>> usuariosEnChat = new HashMap<>();
    private int nextCasaId = 1;

    public UserServiceImpl() throws RemoteException {
    }

    @Override
    public synchronized boolean register(String username, String role, IUser callback) throws RemoteException {
        if (users.containsKey(username))
            return false;
        users.put(username, callback);
        roles.put(username, role);
        return true;
    }

    @Override
    public synchronized boolean login(String username, IUser callback) throws RemoteException {
        if (!users.containsKey(username))
            return false;
        users.put(username, callback);
        return true;
    }

    @Override
    public synchronized String getRole(String username) throws RemoteException {
        return roles.get(username);
    }

    @Override
    public synchronized int crearCasa(String vendedor, String descripcion) throws RemoteException {
        int id = nextCasaId++;
        casas.put(id, new Casa(id, vendedor, descripcion));
        return id;
    }

    @Override
    public synchronized List<Casa> listarCasas() throws RemoteException {
        return new ArrayList<>(casas.values());
    }

    @Override
    public synchronized void addCasaFavorita(String comprador, int casaId) throws RemoteException {
        if (!casas.containsKey(casaId)) {
            throw new RemoteException("La casa con ID " + casaId + " no existe.");
        }
        favoritos.computeIfAbsent(comprador, k -> new HashSet<>()).add(casaId);
    }

    @Override
    public synchronized List<Casa> listarCasasFavoritas(String comprador) throws RemoteException {
        Set<Integer> favs = favoritos.getOrDefault(comprador, new HashSet<>());
        List<Casa> result = new ArrayList<>();
        for (int id : favs) {
            Casa casa = casas.get(id);
            if (casa != null)
                result.add(casa);
        }
        return result;
    }

    @Override
    public synchronized String crearChat(int casaId, String comprador) throws RemoteException {
        Casa casa = casas.get(casaId);
        if (casa == null)
            throw new RemoteException("Casa no existe");
        String vendedor = casa.getVendedor();
        String chatId = "chat-" + comprador + "-" + vendedor + "-" + casaId;
        chats.putIfAbsent(chatId, new ArrayList<>());
        return chatId;
    }

    @Override
    public synchronized void enviarMensaje(String chatId, ChatMessage mensaje) throws RemoteException {
        List<ChatMessage> historial = chats.get(chatId);
        if (historial == null)
            throw new RemoteException("Chat no existe");
        historial.add(mensaje);

        // Extraer comprador y vendedor del chatId
        String[] partes = chatId.split("-");
        String comprador = partes[1];
        String vendedor = partes[2];

        // Notificar solo a los que NO están en el chat
        for (String destinatario : Arrays.asList(comprador, vendedor)) {
            if (!mensaje.getSender().equals(destinatario)) {
                Set<String> enChat = usuariosEnChat.getOrDefault(chatId, new HashSet<>());
                if (!enChat.contains(destinatario)) {
                    IUser receptor = users.get(destinatario);
                    if (receptor != null) {
                        receptor.notify(
                                "Nuevo mensaje en el chat " + chatId + ": \"" + mensaje.getContent() + "\" a las "
                                        + mensaje.getTimestamp());
                    }
                }
            }
        }
    }

    @Override
    public synchronized List<ChatMessage> obtenerHistorial(String chatId) throws RemoteException {
        return new ArrayList<>(chats.getOrDefault(chatId, new ArrayList<>()));
    }

    @Override
    public synchronized List<String> obtenerChatsUsuario(String username) throws RemoteException {
        List<String> result = new ArrayList<>();
        for (String chatId : chats.keySet()) {
            if (chatId.contains(username)) {
                result.add(chatId);
            }
        }
        return result;
    }

    @Override
    public synchronized void eliminarCasaFavorita(String comprador, int casaId) throws RemoteException {
        Set<Integer> favs = favoritos.get(comprador);
        if (favs != null) {
            favs.remove(casaId);
        }
    }

    @Override
    public synchronized void usuarioEntraEnChat(String chatId, String username) throws RemoteException {
        usuariosEnChat.computeIfAbsent(chatId, k -> new HashSet<>()).add(username);
    }

    @Override
    public synchronized void usuarioSaleDeChat(String chatId, String username) throws RemoteException {
        Set<String> set = usuariosEnChat.get(chatId);
        if (set != null)
            set.remove(username);
    }
}