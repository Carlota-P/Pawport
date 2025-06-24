package com.appcrud.comunicacion;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteSocket {

    // Declaramos el host, el puerto y el constructor

    private String host;
    private int puerto;

    public ClienteSocket(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }


    // Creamos un método enviarPeticion(peticion)

    public Respuesta enviarPeticion(Peticion peticion){
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Respuesta respuesta = null;

        try {
            socket = new Socket(host, puerto);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            // Enviamos la petición
            oos.writeObject(peticion);
            oos.flush();

            // Recibimos la respuesta
            respuesta = (Respuesta) ois.readObject();

            Log.d("ClienteSocket", "→ Enviando petición: " + peticion.getTipoOperacion());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (oos != null) oos.close();
                if (ois != null) ois.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return respuesta;

    }

}