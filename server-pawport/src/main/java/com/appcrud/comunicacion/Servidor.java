package com.appcrud.comunicacion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	
	public static void main(String[] args) {
		
		int puerto = 5000;
		
		try {
			ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(new java.net.InetSocketAddress("0.0.0.0", puerto));
			System.out.println("Servidor está escuchando en el puerto: " + puerto);
			
			while(true) {
				Socket socket = serverSocket.accept();
				System.out.println("Cliente conectado: " + socket.getInetAddress());
				
				HiloCliente hilo = new HiloCliente(socket);
				hilo.start();
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
