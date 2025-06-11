package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IUserServiceTest {

    private IUserService userService;
    private IUser userCallback;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        userCallback = mock(IUser.class);
    }

    @Test
    void testRegisterSuccess() throws RemoteException {
        when(userService.register("user1", "comprador", userCallback)).thenReturn(true);
        assertTrue(userService.register("user1", "comprador", userCallback));
    }

    @Test
    void testLoginSuccess() throws RemoteException {
        when(userService.login("user1", userCallback)).thenReturn(true);
        assertTrue(userService.login("user1", userCallback));
    }

    @Test
    void testGetRole() throws RemoteException {
        when(userService.getRole("user1")).thenReturn("vendedor");
        assertEquals("vendedor", userService.getRole("user1"));
    }

    @Test
    void testCrearCasa() throws RemoteException {
        when(userService.crearCasa("vendedor1", "Casa bonita")).thenReturn(1);
        assertEquals(1, userService.crearCasa("vendedor1", "Casa bonita"));
    }

    @Test
    void testListarCasas() throws RemoteException {
        Casa casa = mock(Casa.class);
        when(userService.listarCasas()).thenReturn(Collections.singletonList(casa));
        List<Casa> casas = userService.listarCasas();
        assertEquals(1, casas.size());
        assertSame(casa, casas.get(0));
    }

    @Test
    void testAddCasaFavorita() throws RemoteException {
        doNothing().when(userService).addCasaFavorita("comprador1", 1);
        userService.addCasaFavorita("comprador1", 1);
        verify(userService).addCasaFavorita("comprador1", 1);
    }

    @Test
    void testEliminarCasaFavorita() throws RemoteException {
        doNothing().when(userService).eliminarCasaFavorita("comprador1", 1);
        userService.eliminarCasaFavorita("comprador1", 1);
        verify(userService).eliminarCasaFavorita("comprador1", 1);
    }

    @Test
    void testListarCasasFavoritas() throws RemoteException {
        Casa casa = mock(Casa.class);
        when(userService.listarCasasFavoritas("comprador1")).thenReturn(Arrays.asList(casa));
        List<Casa> favoritas = userService.listarCasasFavoritas("comprador1");
        assertEquals(1, favoritas.size());
        assertSame(casa, favoritas.get(0));
    }

    @Test
    void testCrearChat() throws RemoteException {
        when(userService.crearChat(1, "comprador1")).thenReturn("chat123");
        assertEquals("chat123", userService.crearChat(1, "comprador1"));
    }

    @Test
    void testEnviarMensaje() throws RemoteException {
        ChatMessage mensaje = mock(ChatMessage.class);
        doNothing().when(userService).enviarMensaje("chat123", mensaje);
        userService.enviarMensaje("chat123", mensaje);
        verify(userService).enviarMensaje("chat123", mensaje);
    }

    @Test
    void testObtenerHistorial() throws RemoteException {
        ChatMessage mensaje = mock(ChatMessage.class);
        when(userService.obtenerHistorial("chat123")).thenReturn(Collections.singletonList(mensaje));
        List<ChatMessage> historial = userService.obtenerHistorial("chat123");
        assertEquals(1, historial.size());
        assertSame(mensaje, historial.get(0));
    }

    @Test
    void testObtenerChatsUsuario() throws RemoteException {
        when(userService.obtenerChatsUsuario("user1")).thenReturn(Arrays.asList("chat1", "chat2"));
        List<String> chats = userService.obtenerChatsUsuario("user1");
        assertEquals(2, chats.size());
        assertEquals("chat1", chats.get(0));
    }

    @Test
    void testUsuarioSaleDeChat() throws RemoteException {
        doNothing().when(userService).usuarioSaleDeChat("chat123", "user1");
        userService.usuarioSaleDeChat("chat123", "user1");
        verify(userService).usuarioSaleDeChat("chat123", "user1");
    }

    @Test
    void testUsuarioEntraEnChat() throws RemoteException {
        doNothing().when(userService).usuarioEntraEnChat("chat123", "user1");
        userService.usuarioEntraEnChat("chat123", "user1");
        verify(userService).usuarioEntraEnChat("chat123", "user1");
    }
}