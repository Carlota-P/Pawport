package com.appcrud.comunicacion;

import java.io.Serializable;
import java.util.List;
import com.appcrud.comunicacion.Animal;
import com.appcrud.comunicacion.ExcepcionesAcoger;
import com.appcrud.comunicacion.Protectora;
import com.appcrud.comunicacion.Usuario;

public class Respuesta implements Serializable {

    private boolean exito;
    private String mensaje;
    private Usuario usuario;
    private List<Usuario> usuarios;
    private Animal animal;
    private List<Animal> animales;
    private Protectora protectora;
    private List<Protectora> protectoras;
    private String rol; 

    public Respuesta() {
    	
    }
    
    public Respuesta(boolean exito, String mensaje, String rol) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.rol = rol;

    }
    
    public Respuesta(boolean exito, String mensaje, Usuario usuario, List<Usuario> usuarios, Animal animal, List<Animal> animales, Protectora protectora, List<Protectora> protectoras, String rol) {
	    this.exito = exito;
		this.mensaje = mensaje;
		this.usuario = usuario;
		this.usuarios = usuarios;
		this.animal = animal;
		this.animales = animales;
		this.protectora = protectora;
		this.protectoras = protectoras;	
		this.rol = rol;
	}

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    // -----
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public List<Animal> getAnimales() {
        return animales;
    }

    public void setAnimales(List<Animal> animales) {
        this.animales = animales;
    }

    public Protectora getProtectora() {
        return protectora;
    }

    public void setProtectora(Protectora protectora) {
        this.protectora = protectora;
    }

    public List<Protectora> getProtectoras() {
        return protectoras;
    }

    public void setProtectoras(List<Protectora> protectoras) {
        this.protectoras = protectoras;
    }
    
    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}