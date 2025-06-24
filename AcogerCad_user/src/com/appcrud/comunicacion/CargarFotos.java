package com.appcrud.comunicacion;

public class CargarFotos {
    public static void main(String[] args) {
        try {
            AcogerCad cad = new AcogerCad();

            cad.cargarFotoDesdeArchivo(1, "C:/Users/Carlota/Desktop/fotos/luna1.jpg");
            cad.cargarFotoDesdeArchivo(2, "C:/Users/Carlota/Desktop/fotos/toby.jpg");
            cad.cargarFotoDesdeArchivo(3, "C:/Users/Carlota/Desktop/fotos/Apolo.jpg");
            cad.cargarFotoDesdeArchivo(4, "C:/Users/Carlota/Desktop/fotos/rex1.jpg");
            cad.cargarFotoDesdeArchivo(5, "C:/Users/Carlota/Desktop/fotos/dora1.jpg");
            cad.cargarFotoDesdeArchivo(6, "C:/Users/Carlota/Desktop/fotos/noa1.jpg");
            cad.cargarFotoDesdeArchivo(7, "C:/Users/Carlota/Desktop/fotos/thor.jpg");


            cad.cargarFotoProtectoraDesdeArchivo(1, "C:/Users/Carlota/Desktop/fotos/logo_protectora_1.png");
            cad.cargarFotoProtectoraDesdeArchivo(2, "C:/Users/Carlota/Desktop/fotos/logo_protectora_2.png");
            cad.cargarFotoProtectoraDesdeArchivo(3, "C:/Users/Carlota/Desktop/fotos/logo_protectora_3.png");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// Ruta: C:/Users/Carlota/Desktop/fotos/