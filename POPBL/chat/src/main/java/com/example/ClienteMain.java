package com.example;

import java.rmi.Naming;
import java.util.List;
import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClienteMain {
    private static final String COMPRADOR = "comprador";
    private static final String VENDEDOR = "vendedor";
    private static final String SALIR = "salir";

    public static void main(String[] args) {
        try {
            IUserService userService = (IUserService) Naming.lookup("//localhost/UserService");
            Scanner sc = new Scanner(System.in);

            // Login o registro
            UserSession session = loginOrRegister(userService, sc);

            System.out.println("Bienvenido, " + session.username + " (" + session.role + ")");

            boolean salir = false;
            while (!salir) {
                if (VENDEDOR.equals(session.role)) {
                    salir = menuVendedor(userService, sc, session.username, session.user);
                } else if (COMPRADOR.equals(session.role)) {
                    salir = menuComprador(userService, sc, session.username, session.user);
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Estructura para mantener la sesión de usuario
    private static class UserSession {
        String username, role;
        UserImpl user;

        UserSession(String username, String role, UserImpl user) {
            this.username = username;
            this.role = role;
            this.user = user;
        }
    }

    // Lógica de login/registro
    private static UserSession loginOrRegister(IUserService userService, Scanner sc) throws Exception {
        boolean loggedIn = false;
        String username = null, role = null;
        UserImpl user = null;
        while (!loggedIn) {
            System.out.print("¿Registrar (r) o Login (l)? ");
            String opcion = sc.nextLine();
            System.out.print("Nombre de usuario: ");
            username = sc.nextLine();
            user = new UserImpl(username);
            boolean ok;
            if (opcion.equalsIgnoreCase("r")) {
                role = pedirRol(sc);
                ok = userService.register(username, role, user);
                if (ok) {
                    System.out.println("Registrado correctamente como " + role + ".");
                    loggedIn = true;
                } else {
                    System.out.println("Usuario ya existe. Intenta con otro nombre o haz login.");
                }
            } else {
                ok = userService.login(username, user);
                if (ok) {
                    role = userService.getRole(username);
                    System.out.println("Login correcto. Rol: " + role);
                    loggedIn = true;
                } else {
                    System.out.println("Usuario no registrado. Prueba a registrarte.");
                }
            }
        }
        return new UserSession(username, role, user);
    }

    private static String pedirRol(Scanner sc) {
        String inputRole = "";
        while (!inputRole.equals(COMPRADOR) && !inputRole.equals(VENDEDOR)) {
            System.out.print("Rol (comprador/vendedor): ");
            inputRole = sc.nextLine().toLowerCase();
            if (!inputRole.equals(COMPRADOR) && !inputRole.equals(VENDEDOR)) {
                System.out.println("Rol no válido. Debe ser 'comprador' o 'vendedor'.");
            }
        }
        return inputRole;
    }

    // Menú vendedor
    private static boolean menuVendedor(IUserService userService, Scanner sc, String username, UserImpl user)
            throws Exception {
        System.out.println("1. Crear casa");
        System.out.println("2. Listar casas");
        System.out.println("3. Ver mis chats");
        System.out.println("7. Ver notificaciones");
        System.out.println("8. Chatbot de definiciones");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
        switch (sc.nextLine()) {
            case "1":
                System.out.print("Descripción de la casa: ");
                int id = userService.crearCasa(username, sc.nextLine());
                System.out.println("Casa creada con ID: " + id);
                break;
            case "2":
                userService.listarCasas().forEach(System.out::println);
                break;
            case "3":
                verYChatear(userService, sc, username, VENDEDOR);
                break;
            case "7":
                mostrarNotificaciones(user);
                break;
            case "8":
                chatbotDefiniciones(sc);
                break;
            case "0":
                return true;
            default:
                System.out.println("Opción no válida.");
        }
        return false;
    }

    // Menú comprador
    private static boolean menuComprador(IUserService userService, Scanner sc, String username, UserImpl user)
            throws Exception {
        System.out.println("1. Listar casas");
        System.out.println("2. Añadir casa a favoritos");
        System.out.println("3. Ver mis chats");
        System.out.println("4. Iniciar chat con vendedor");
        System.out.println("5. Ver casas favoritas");
        System.out.println("6. Eliminar casa de favoritos");
        System.out.println("7. Ver notificaciones");
        System.out.println("8. Chatbot de definiciones");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
        switch (sc.nextLine()) {
            case "1":
                userService.listarCasas().forEach(System.out::println);
                break;
            case "2":
                System.out.print("ID de la casa a añadir a favoritos: ");
                try {
                    userService.addCasaFavorita(username, Integer.parseInt(sc.nextLine()));
                    System.out.println("Casa añadida a favoritos.");
                } catch (Exception ex) {
                    System.out.println("No se pudo añadir: " + ex.getMessage());
                }
                break;
            case "3":
                verYChatear(userService, sc, username, COMPRADOR);
                break;
            case "4":
                System.out.print("ID de la casa para chatear: ");
                int chatCasaId = Integer.parseInt(sc.nextLine());
                boolean existe = userService.listarCasas().stream().anyMatch(c -> c.getId() == chatCasaId);
                if (!existe) {
                    System.out.println("La casa con ese ID no existe.");
                    break;
                }
                String chatId = userService.crearChat(chatCasaId, username);
                System.out.println("Chat creado con ID: " + chatId);
                entrarEnChat(chatId, userService, sc, username);
                break;
            case "5":
                mostrarFavoritas(userService, username);
                break;
            case "6":
                eliminarFavorita(userService, sc, username);
                break;
            case "7":
                mostrarNotificaciones(user);
                break;
            case "8":
                chatbotDefiniciones(sc);
                break;
            case "0":
                return true;
            default:
                System.out.println("Opción no válida.");
        }
        return false;
    }

    // Métodos auxiliares para mostrar notificaciones y favoritas
    private static void mostrarNotificaciones(UserImpl user) {
        List<String> notis = user.getNotificaciones();
        if (notis.isEmpty())
            System.out.println("No tienes notificaciones.");
        else
            notis.forEach(System.out::println);
    }

    private static void mostrarFavoritas(IUserService userService, String username) throws Exception {
        List<Casa> favoritas = userService.listarCasasFavoritas(username);
        if (favoritas.isEmpty())
            System.out.println("No tienes casas favoritas.");
        else
            favoritas.forEach(System.out::println);
    }

    private static void eliminarFavorita(IUserService userService, Scanner sc, String username) throws Exception {
        List<Casa> favs = userService.listarCasasFavoritas(username);
        if (favs.isEmpty()) {
            System.out.println("No tienes casas favoritas.");
            return;
        }
        favs.forEach(System.out::println);
        System.out.print("ID de la casa a eliminar de favoritos: ");
        int delId = Integer.parseInt(sc.nextLine());
        userService.eliminarCasaFavorita(username, delId);
        System.out.println("Casa eliminada de favoritos.");
    }

    // Método auxiliar para ver y chatear en un chat existente
    private static void verYChatear(IUserService userService, Scanner sc, String username, String role)
            throws Exception {
        List<String> misChats = userService.obtenerChatsUsuario(username);
        if (misChats.isEmpty()) {
            System.out.println("No tienes chats activos.");
        } else {
            System.out.println("Tus chats:");
            for (String cid : misChats) {
                System.out.println(cid);
            }
            System.out.print("Introduce el ID del chat para entrar o pulsa Enter para volver: ");
            String input = sc.nextLine();
            String cid = null;
            if (!input.isBlank()) {
                for (String chat : misChats) {
                    if (chat.equals(input) || chat.endsWith("-" + input)) {
                        cid = chat;
                        break;
                    }
                }
                if (cid != null) {
                    entrarEnChat(cid, userService, sc, username);
                }
            }
        }
    }

    // Método auxiliar para entrar a un chat y enviar/recibir mensajes
    private static void entrarEnChat(String chatId, IUserService userService, Scanner sc, String username)
            throws Exception {
        userService.usuarioEntraEnChat(chatId, username);
        try {
            List<ChatMessage> historial = userService.obtenerHistorial(chatId);
            System.out.println("----- Historial del chat -----");
            for (ChatMessage m : historial) {
                System.out.println(m);
            }
            System.out.println("------------------------------");

            String queueName = chatId + "-" + username;
            RabbitMQUtil.purgeQueue(queueName);

            Thread listener = new Thread(() -> {
                try {
                    RabbitMQUtil.receiveMessages(queueName, (consumerTag, delivery) -> {
                        String msg = new String(delivery.getBody(), java.nio.charset.StandardCharsets.UTF_8);
                        if (!msg.startsWith(username + ":")) {
                            System.out.println(msg);
                        }
                    });
                } catch (Exception ex) {
                    System.out.println("Error recibiendo mensajes: " + ex.getMessage());
                }
            });
            listener.setDaemon(true);
            listener.start();

            System.out.println("Escribe tu mensaje (o 'salir' para terminar):");
            while (true) {
                String mensaje = sc.nextLine();
                if (SALIR.equalsIgnoreCase(mensaje))
                    break;
                userService.enviarMensaje(chatId, new ChatMessage(username, mensaje));
                String[] partes = chatId.split("-");
                String comprador = partes[1];
                String vendedor = partes[2];
                String destinatario = username.equals(comprador) ? vendedor : comprador;
                String destQueue = chatId + "-" + destinatario;
                RabbitMQUtil.sendMessage(destQueue, username + ": " + mensaje);
            }
        } finally {
            userService.usuarioSaleDeChat(chatId, username);
        }
    }

    // Método para el chatbot de definiciones
    private static void chatbotDefiniciones(Scanner sc) {
        System.out.println("Chatbot de definiciones (escribe 'salir' para volver al menú):");
        while (true) {
            System.out.print("Tú: ");
            String input = sc.nextLine();
            if (SALIR.equalsIgnoreCase(input))
                break;
            String respuesta = obtenerDefinicionDesdeWikcionario(input);
            System.out.println("Chatbot: " + respuesta);
        }
    }

    private static String obtenerDefinicionDesdeWikcionario(String palabra) {
        try {
            String endpoint = "https://es.wiktionary.org/w/api.php?action=parse&page=" +
                    URLEncoder.encode(palabra, "UTF-8") +
                    "&format=json&prop=text&formatversion=2";
            URL url = new URL(endpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            int status = con.getResponseCode();
            if (status != 200) {
                return "No se encontró definición para '" + palabra + "'.";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            String htmlRaw = response.toString();

            Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"(.*?)\"\\s*\\}\\s*\\}\\s*\\}\\s*$", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(htmlRaw);
            String html;
            if (matcher.find()) {
                html = matcher.group(1);
            } else {
                int idx = htmlRaw.indexOf("\"text\":\"");
                if (idx == -1)
                    return "No se encontró definición para '" + palabra + "'.";
                idx += 8;
                int end = htmlRaw.indexOf("\"}", idx);
                if (end == -1)
                    end = htmlRaw.length();
                html = htmlRaw.substring(idx, end);
            }

            html = html.replace("\\n", "\n").replace("\\\"", "\"");

            int idxEsp = html.toLowerCase().indexOf(">español<");
            if (idxEsp == -1) {
                return "No se encontró una definición clara para '" + palabra + "'.";
            }
            String htmlEspanol = html.substring(idxEsp);

            Pattern dlPattern = Pattern.compile("<dl>(.*?)</dl>", Pattern.DOTALL);
            Matcher dlMatcher = dlPattern.matcher(htmlEspanol);

            StringBuilder definiciones = new StringBuilder();
            int count = 0;
            java.util.HashSet<String> definicionesUnicas = new java.util.HashSet<>();
            while (dlMatcher.find() && count < 3) {
                String dlBlock = dlMatcher.group(1);
                Pattern ddPattern = Pattern.compile("<dd>(.*?)</dd>", Pattern.DOTALL);
                Matcher ddMatcher = ddPattern.matcher(dlBlock);
                while (ddMatcher.find() && count < 3) {
                    String def = limpiarDefinicion(ddMatcher.group(1));
                    int punto = def.indexOf(".");
                    if (punto > 0)
                        def = def.substring(0, punto + 1);
                    if (!def.isEmpty() && !def.matches("^\\d+$") && definicionesUnicas.add(def)) {
                        if (!def.endsWith("."))
                            def = def + ".";
                        definiciones.append(++count).append(". ").append(def).append("\n");
                    }
                }
            }

            if (definiciones.length() == 0) {
                return "No se encontró una definición clara para '" + palabra + "'.";
            }

            return "(desde Wikcionario)\n" + definiciones.toString().trim();

        } catch (Exception e) {
            return "Error al conectar con Wikcionario: " + e.getMessage();
        }
    }

    // Extrae la limpieza de definiciones a un método aparte
    private static String limpiarDefinicion(String def) {
        return def.replaceAll("<[^>]+>", "") // quita etiquetas HTML
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replaceAll("\\s+", " ")
                .replaceAll("(?iu)sinónimos?:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)hipónimos?:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)hiperónimos?:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)relacionados?:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)ejemplo:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)ámbito:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)uso:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)isbn:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)véase también:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)traducciones?:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)referencias?:.*?(\\.|;|$)", "")
                .replaceAll("(?iu)notas?:.*?(\\.|;|$)", "")
                .replaceAll("(?u)\\[.*?\\]", "")
                .replaceAll("hipoteca\\d+", "hipoteca")
                .replaceAll("\\.mw-parser-output.*", "")
                .trim();
    }
}