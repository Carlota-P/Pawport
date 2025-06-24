package com.appcrud.comunicacion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

import com.appcrud.comunicacion.Peticion;
import com.appcrud.comunicacion.Respuesta;

import com.appcrud.comunicacion.Animal;
import com.appcrud.comunicacion.ExcepcionesAcoger;
import com.appcrud.comunicacion.Protectora;
import com.appcrud.comunicacion.Usuario;
import com.appcrud.comunicacion.AcogerCad;

public class HiloCliente extends Thread {
	
	private Socket socket;

	public HiloCliente(Socket socket) {
		super();
		this.socket = socket;
	}
	
	@Override 
	public void run() {
		
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		
		PropertyConfigurator.configure("logs\\log4j.properties");
        org.apache.log4j.Logger loggerNavegacion = LogManager.getLogger("NAVEGACION");
        PropertyConfigurator.configure("logs\\log4j.properties");
        org.apache.log4j.Logger loggerErrores = LogManager.getLogger("ERRORES");
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
	        
			AcogerCad cad = new AcogerCad();
			
			Peticion peticion = (Peticion) ois.readObject();
			Respuesta respuesta = new Respuesta();
			System.out.println(peticion.getTipoOperacion());
			
			switch(peticion.getTipoOperacion()) {
				case CREATE_USUARIO:
					loggerNavegacion.trace("Un nuevo usuario se ha intentado registrar");
	                cad.insertarUsuario(peticion.getUsuario());
	                respuesta.setExito(true);
                    respuesta.setMensaje("Usuario creado con éxito.");
	                break;
				case READ_USUARIO:
					loggerNavegacion.trace("Un usuario ha intentado leer su información");
				    Usuario usuario = cad.leerUsuario(peticion.getIdUsuario());
				    if (usuario != null) {
				        respuesta.setUsuario(usuario);
				        respuesta.setExito(true);
                        respuesta.setMensaje("Usuario encontrado.");
				    } else {
				        respuesta.setExito(false);
                        respuesta.setMensaje("Usuario no encontrado.");
				    }
				    break;
	            case READ_ALL_USUARIO:
					loggerNavegacion.trace("El usuario se ha intentado leer todos los usuarios");
	                List<Usuario> listaUsuarios = cad.leerUsuarios();
	                respuesta.setUsuarios(listaUsuarios);
	                respuesta.setExito(true);
                    respuesta.setMensaje("Lista de usuarios obtenida.");
	                break;
	            case UPDATE_USUARIO:
					loggerNavegacion.trace("El usuario ha intentado actualizar sus datos");
	                try {
	                    cad.actualizarUsuario(peticion.getUsuario().getUsuarioId(), peticion.getUsuario());
                        respuesta.setExito(true);
                        respuesta.setMensaje("Usuario actualizado correctamente");

	                } catch (Exception e) {
	                    e.printStackTrace();
	                    respuesta.setExito(false);
	                    respuesta.setMensaje("Error al actualizar: " + e.getMessage());
	                }
	                break;
	            case DELETE_USUARIO:
	                loggerNavegacion.trace("El usuario ha intentado eliminar su perfil");
	                try {
	                    cad.eliminarUsuario(peticion.getIdUsuario());
	                    respuesta.setExito(true);
	                    respuesta.setMensaje("Usuario eliminado con éxito.");
	                } catch (ExcepcionesAcoger e) {
	                    loggerErrores.error("Error al eliminar usuario: " + e.getMensajeErrorAdministrador());
	                    respuesta.setExito(false);
	                    respuesta.setMensaje("No se pudo eliminar el usuario. Intente más tarde.");
	                }
	                break;
	            case CREATE_ANIMAL:
					loggerNavegacion.trace("La protectora ha intentado registrar un nuevo animal");
                    cad.insertarAnimal(peticion.getAnimal());
                    respuesta.setExito(true);
                    respuesta.setMensaje("Animal creado con éxito.");
                    break;
	            case READ_ANIMAL:
					loggerNavegacion.trace("El usuario ha intentado leer un animal");
	                Animal animal = cad.leerAnimal(peticion.getIdAnimal());

	                if (animal != null) {
	                    try {
							cad.marcarSiEsFavorito(animal, peticion.getIdUsuario());
						} catch (SQLException e) {
							e.printStackTrace();
						}
	                    respuesta.setAnimal(animal);
	                    respuesta.setExito(true);
	                    respuesta.setMensaje("Animal encontrado.");
	                } else {
	                    respuesta.setExito(false);
	                    respuesta.setMensaje("Animal no encontrado.");
	                }
	                break;
                case READ_ALL_ANIMAL:
					loggerNavegacion.trace("El usuario ha intentado leer todos animales");
                    List<Animal> listaAnimales = cad.leerAnimales();
                    respuesta.setAnimales(listaAnimales);
                    respuesta.setExito(true);
                    respuesta.setMensaje("Lista de animales obtenida.");
                    break;
                case UPDATE_ANIMAL:
					loggerNavegacion.trace("La protectora ha intentado actualizar un animal");
                	try {
                        cad.actualizarAnimal(peticion.getIdAnimal(), peticion.getAnimal());
                        respuesta.setExito(true);
                        respuesta.setMensaje("Animal actualizado correctamente");

	                } catch (Exception e) {
	                    e.printStackTrace();
	                    respuesta.setExito(false);
	                    respuesta.setMensaje("Error al actualizar: " + e.getMessage());
	                }
	                break;
                case DELETE_ANIMAL:
					loggerNavegacion.trace("La protectora ha intentado eliminar un animal");
                    cad.eliminarAnimal(peticion.getIdAnimal());
                    respuesta.setExito(true);
                    respuesta.setMensaje("Animal eliminado con éxito.");
                    break;
                case CREATE_PROTECTORA:
					loggerNavegacion.trace("La protectora ha intentado registrarse");
                    cad.insertarProtectora(peticion.getProtectora());
                    respuesta.setExito(true);
                    respuesta.setMensaje("Protectora creada con éxito.");
                    break;
                case READ_PROTECTORA:
					loggerNavegacion.trace("Un usuario ha intentado leer la información de una protectora");
                    Protectora protectora = cad.leerProtectora(peticion.getIdProtectora());
                    if (protectora != null) {
                        respuesta.setProtectora(protectora);
                        respuesta.setExito(true);
                        respuesta.setMensaje("Protectora encontrada.");
                    } else {
                        respuesta.setExito(false);
                        respuesta.setMensaje("Protectora no encontrada.");
                    }
                    break;
                case READ_ALL_PROTECTORA:
					loggerNavegacion.trace("Un usuario ha intentado leer todas las protectoras");
                    List<Protectora> listaProtectoras = cad.leerProtectoras();
                    respuesta.setProtectoras(listaProtectoras);
                    respuesta.setExito(true);
                    respuesta.setMensaje("Lista de protectoras obtenida.");
                    break;
                case UPDATE_PROTECTORA:
					loggerNavegacion.trace("Una protectora ha intentado actualizar una protectora");
                    cad.actualizarProtectora(peticion.getIdProtectora(), peticion.getProtectora());
                    respuesta.setExito(true);
                    respuesta.setMensaje("Protectora actualizada con éxito.");
                    break;
                case DELETE_PROTECTORA:
					loggerNavegacion.trace("Una protectora ha intentado eliminar su perfil");
                    cad.eliminarProtectora(peticion.getIdProtectora());
                    respuesta.setExito(true);
                    respuesta.setMensaje("Protectora eliminada con éxito.");
                    break;
                case READ_ANIMALES_BY_PROTECTORA:
					loggerNavegacion.trace("Un usuario ha intentado leer los animales rgistrados de una protectora");
                    List<Animal> animalesProtectora = cad.obtenerAnimalesPorProtectora(peticion.getIdProtectora());
                    respuesta.setAnimales(animalesProtectora);
                    respuesta.setExito(true);
                    respuesta.setMensaje("Animales de la protectora obtenidos.");
                    break;
                case GUARDAR_ANIMAL_EN_USUARIO:
					loggerNavegacion.trace("Un usuario ha intentado guardar un animal en favoritos");
                    try {
                    	System.out.println(peticion.getIdUsuario()+ " " + peticion.getIdAnimal());
                        cad.insertarFavorito(peticion.getIdUsuario(), peticion.getIdAnimal());
                        respuesta.setExito(true);
                        respuesta.setMensaje("Animal añadido a favoritos con éxito.");
                    } catch (ExcepcionesAcoger e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                    }
                    break;
                case QUITAR_ANIMAL_DE_USUARIO:
					loggerNavegacion.trace("Un usuario ha intentado quitar un animal de favoritos");
                    try {
                        cad.eliminarAnimalDeUsuario(peticion.getIdUsuario(), peticion.getIdAnimal());
                        respuesta.setExito(true);
                        respuesta.setMensaje("Animal eliminado de favoritos con éxito.");
                    } catch (ExcepcionesAcoger e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                    }
                    break;
                case OBTENER_FAVORITOS_DE_USUARIO:
					loggerNavegacion.trace("Un usuario ha intentado leero los animales guardados en favoritos");
                    try {
                        List<Animal> favoritos = cad.obtenerFavoritosDeUsuario(peticion.getIdUsuario());
                        respuesta.setAnimales(favoritos);
                        respuesta.setExito(true);
                        respuesta.setMensaje("Favoritos obtenidos correctamente.");
                    } catch (ExcepcionesAcoger e) {
                        respuesta.setExito(false);
                        respuesta.setMensaje(e.getMensajeErrorUsuario());
                        respuesta.setAnimales(null);
                    }
                    break;
	            case PING:
	                respuesta.setExito(true);
	                respuesta.setMensaje("Ping exitoso.");
	                break;
	            case LOGIN_USUARIO:
					loggerNavegacion.trace("Un usuario ha intentado iniciar sesión en la aplicación");
	                try {
	                    Usuario usuarioLogin = cad.loginUsuario(peticion.getEmail(), peticion.getPasswordPlano());
	                    if (usuarioLogin != null) {
	                        respuesta.setExito(true);
	                        respuesta.setMensaje("Login de usuario exitoso.");
	                        respuesta.setRol("usuario");
	                        respuesta.setUsuario(usuarioLogin);
	                    } else {
	                        respuesta.setExito(false);
	                        respuesta.setMensaje("Credenciales incorrectas para usuario.");
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    respuesta.setExito(false);
	                    respuesta.setMensaje("Error en login de usuario: " + e.getMessage());
	                }
	                break;

	            case LOGIN_PROTECTORA:
					loggerNavegacion.trace("Una protectora ha intentado iniciar sesión en la aplicación");
	                try {
	                    Protectora protectoraLogin = cad.loginProtectora(peticion.getEmail(), peticion.getPasswordPlano());
	                    if (protectoraLogin != null) {
	                        respuesta.setExito(true);
	                        respuesta.setMensaje("Login de protectora exitoso.");
	                        respuesta.setRol("protectora");
	                        respuesta.setProtectora(protectoraLogin);
	                    } else {
	                        respuesta.setExito(false);
	                        respuesta.setMensaje("Credenciales incorrectas para protectora.");
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                    respuesta.setExito(false);
	                    respuesta.setMensaje("Error en login de protectora: " + e.getMessage());
	                }
	                break;

	            default:
	                respuesta.setExito(false);
	                respuesta.setMensaje("Operación no reconocida.");
	                break;
				}
				oos.writeObject(respuesta);
				oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExcepcionesAcoger e) {
			e.printStackTrace();
			loggerErrores.error(e.getCodigoErrorBD() + " - " + e.getMensajeErrorAdministrador() + " - " + e.getSentenciaSQL());

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if(ois != null) ois.close();
				if(oos != null) oos.close();
				if(socket != null) socket.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
