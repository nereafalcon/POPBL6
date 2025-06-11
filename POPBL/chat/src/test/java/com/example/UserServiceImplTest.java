package com.example;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.rmi.RemoteException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() throws RemoteException {
        userService = new UserServiceImpl();
    }

    @Test
    void testRegisterAndLogin() throws RemoteException {
        IUser userMock = mock(IUser.class);
        assertTrue(userService.register("alice", "comprador", userMock));
        assertFalse(userService.register("alice", "comprador", userMock)); // duplicate
        assertTrue(userService.login("alice", userMock));
        assertFalse(userService.login("bob", userMock)); // not registered
    }

    @Test
    void testGetRole() throws RemoteException {
        IUser userMock = mock(IUser.class);
        userService.register("bob", "vendedor", userMock);
        assertEquals("vendedor", userService.getRole("bob"));
        assertNull(userService.getRole("unknown"));
    }

    @Test
    void testCrearCasaAndListarCasas() throws RemoteException {
        IUser vendedor = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        int casaId = userService.crearCasa("vendedor1", "Bonita casa");
        List<Casa> casas = userService.listarCasas();
        assertEquals(1, casas.size());
        assertEquals(casaId, casas.get(0).getId());
        assertEquals("vendedor1", casas.get(0).getVendedor());
        assertEquals("Bonita casa", casas.get(0).getDescripcion());
    }

    @Test
    void testAddAndListarCasasFavoritas() throws RemoteException {
        IUser vendedor = mock(IUser.class);
        IUser comprador = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        userService.register("comprador1", "comprador", comprador);
        int casaId = userService.crearCasa("vendedor1", "Casa grande");
        userService.addCasaFavorita("comprador1", casaId);
        List<Casa> favoritas = userService.listarCasasFavoritas("comprador1");
        assertEquals(1, favoritas.size());
        assertEquals(casaId, favoritas.get(0).getId());
    }

    @Test
    void testAddCasaFavoritaThrowsIfCasaNotExists() throws RemoteException {
        IUser comprador = mock(IUser.class);
        userService.register("comprador1", "comprador", comprador);
        RemoteException ex = assertThrows(RemoteException.class, () -> {
            userService.addCasaFavorita("comprador1", 999);
        });
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void testEliminarCasaFavorita() throws RemoteException {
        IUser vendedor = mock(IUser.class);
        IUser comprador = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        userService.register("comprador1", "comprador", comprador);
        int casaId = userService.crearCasa("vendedor1", "Casa pequeña");
        userService.addCasaFavorita("comprador1", casaId);
        userService.eliminarCasaFavorita("comprador1", casaId);
        List<Casa> favoritas = userService.listarCasasFavoritas("comprador1");
        assertTrue(favoritas.isEmpty());
    }

    @Test
    void testCrearChatAndObtenerChatsUsuario() throws RemoteException {
        IUser vendedor = mock(IUser.class);
        IUser comprador = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        userService.register("comprador1", "comprador", comprador);
        int casaId = userService.crearCasa("vendedor1", "Casa moderna");
        String chatId = userService.crearChat(casaId, "comprador1");
        assertNotNull(chatId);
        List<String> chatsComprador = userService.obtenerChatsUsuario("comprador1");
        List<String> chatsVendedor = userService.obtenerChatsUsuario("vendedor1");
        assertTrue(chatsComprador.contains(chatId));
        assertTrue(chatsVendedor.contains(chatId));
    }

    @Test
    void testEnviarMensajeAndObtenerHistorial() throws RemoteException {
        IUser vendedor = mock(IUser.class);
        IUser comprador = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        userService.register("comprador1", "comprador", comprador);
        int casaId = userService.crearCasa("vendedor1", "Casa rural");
        String chatId = userService.crearChat(casaId, "comprador1");
        ChatMessage msg = new ChatMessage("comprador1", "Hola"); // Adjusted to match available constructor
        userService.enviarMensaje(chatId, msg);
        List<ChatMessage> historial = userService.obtenerHistorial(chatId);
        assertEquals(1, historial.size());
        assertEquals("Hola", historial.get(0).getContent());
    }

    @Test
    void testEnviarMensajeNotificaAlOtroUsuario() throws Exception {
        IUser vendedor = mock(IUser.class);
        IUser comprador = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        userService.register("comprador1", "comprador", comprador);
        int casaId = userService.crearCasa("vendedor1", "Casa urbana");
        String chatId = userService.crearChat(casaId, "comprador1");
        ChatMessage msg = new ChatMessage("comprador1", "Mensaje para vendedor");
        userService.enviarMensaje(chatId, msg);
        verify(vendedor, atLeastOnce()).notify(contains("Nuevo mensaje en el chat"));
        verify(comprador, never()).notify(anyString());
    }

    @Test
    void testUsuarioEntraYSaleDeChat() throws RemoteException {
        IUser vendedor = mock(IUser.class);
        IUser comprador = mock(IUser.class);
        userService.register("vendedor1", "vendedor", vendedor);
        userService.register("comprador1", "comprador", comprador);
        int casaId = userService.crearCasa("vendedor1", "Casa de campo");
        String chatId = userService.crearChat(casaId, "comprador1");
        userService.usuarioEntraEnChat(chatId, "comprador1");
        userService.usuarioSaleDeChat(chatId, "comprador1");
        // No exception means success
    }
}