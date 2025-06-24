package com.appcrud.comunicacion;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ServidorFotos {

    // Ruta absoluta compartida (directorio actual + "/fotos")
    private static final File BASE_FOTOS = new File(System.getProperty("user.dir"), "fotos");

    public static void main(String[] args) throws IOException {
        int puerto = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", puerto), 0);

        server.createContext("/upload", new UploadHandler());
        server.createContext("/fotos", new FotoHandler());

        server.setExecutor(null);
        System.out.println("Servidor de fotos escuchando en http://192.168.1.215:" + puerto);
        System.out.println("Carpeta base de imágenes: " + BASE_FOTOS.getAbsolutePath());
        server.start();
    }

    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            Headers headers = exchange.getRequestHeaders();
            String fileName = headers.getFirst("File-Name");
            if (fileName == null || fileName.isEmpty()) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            if (!BASE_FOTOS.exists()) BASE_FOTOS.mkdirs();

            File file = new File(BASE_FOTOS, fileName);
            System.out.println("Recibida imagen: " + fileName);
            System.out.println("Guardando en: " + file.getAbsolutePath());

            try (InputStream is = exchange.getRequestBody()) {
                Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
                return;
            }

            exchange.sendResponseHeaders(200, -1);
        }
    }

    static class FotoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath().replace("/fotos/", "");
            File file = new File(BASE_FOTOS, path);
            System.out.println("Solicitud de imagen: " + path);
            System.out.println("Buscando en: " + file.getAbsolutePath());

            if (!file.exists()) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
