package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClienteMainTest {

    @Test
    @DisplayName("limpiarDefinicion elimina HTML y entidades")
    void testLimpiarDefinicion() {
        String html = "<b>Definición</b> &amp; ejemplo: esto es un ejemplo. sinónimos: prueba.";
        String result = invokeLimpiarDefinicion(html);
        assertTrue(result.contains("Definición &"), "Debe limpiar etiquetas y entidades");
        assertFalse(result.contains("ejemplo:"), "Debe eliminar 'ejemplo:'");
        assertFalse(result.contains("sinónimos:"), "Debe eliminar 'sinónimos:'");
    }

    @Test
    @DisplayName("extraerHtmlTexto extrae correctamente el campo text")
    void testExtraerHtmlTexto() {
        String json = "{\"parse\":{\"title\":\"casa\",\"text\":\"<div>contenido</div>\"}}";
        String result = invokeExtraerHtmlTexto(json);
        assertEquals("<div>contenido</div>", result);
    }

    @Test
    @DisplayName("obtenerSeccionEspanol devuelve null si no hay sección español")
    void testObtenerSeccionEspanolNull() {
        String html = "<div>Otra sección</div>";
        String result = invokeObtenerSeccionEspanol(html);
        assertNull(result);
    }

    @Test
    @DisplayName("obtenerSeccionEspanol devuelve sección español si existe")
    void testObtenerSeccionEspanolOk() {
        String html = "<h2>Francés</h2><h2>Español</h2>contenido";
        String result = invokeObtenerSeccionEspanol(html);
        assertNotNull(result);
        assertTrue(result.toLowerCase().contains("español"));
    }

    @Test
    @DisplayName("extraerDefiniciones extrae definiciones de HTML")
    void testExtraerDefiniciones() {
        String html = "<dl><dd>Primera definición.</dd><dd>Segunda definición.</dd></dl>";
        String result = invokeExtraerDefiniciones(html, 2);
        assertTrue(result.contains("1. Primera definición."), "Debe contener la primera definición");
        assertTrue(result.contains("2. Segunda definición."), "Debe contener la segunda definición");
    }

    @Test
    @DisplayName("buscarChatPorId encuentra chat por id exacto o sufijo")
    void testBuscarChatPorId() {
        List<String> chats = Arrays.asList("chat-1-2", "chat-3-4");
        assertEquals("chat-1-2", invokeBuscarChatPorId(chats, "chat-1-2"));
        assertEquals("chat-3-4", invokeBuscarChatPorId(chats, "4"));
        assertNull(invokeBuscarChatPorId(chats, "noexiste"));
    }

    // Métodos auxiliares para invocar métodos privados
    private String invokeLimpiarDefinicion(String def) {
        try {
            var m = ClienteMain.class.getDeclaredMethod("limpiarDefinicion", String.class);
            m.setAccessible(true);
            return (String) m.invoke(null, def);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeExtraerHtmlTexto(String htmlRaw) {
        try {
            var m = ClienteMain.class.getDeclaredMethod("extraerHtmlTexto", String.class);
            m.setAccessible(true);
            return (String) m.invoke(null, htmlRaw);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeObtenerSeccionEspanol(String html) {
        try {
            var m = ClienteMain.class.getDeclaredMethod("obtenerSeccionEspanol", String.class);
            m.setAccessible(true);
            return (String) m.invoke(null, html);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeExtraerDefiniciones(String html, int max) {
        try {
            var m = ClienteMain.class.getDeclaredMethod("extraerDefiniciones", String.class, int.class);
            m.setAccessible(true);
            return (String) m.invoke(null, html, max);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeBuscarChatPorId(List<String> chats, String input) {
        try {
            var m = ClienteMain.class.getDeclaredMethod("buscarChatPorId", List.class, String.class);
            m.setAccessible(true);
            return (String) m.invoke(null, chats, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}