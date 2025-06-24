package com.appcrud.comunicacion;

import java.io.Serializable;

import com.appcrud.comunicacion.Peticion.TipoOperacion;
import com.appcrud.comunicacion.Animal;
import com.appcrud.comunicacion.ExcepcionesAcoger;
import com.appcrud.comunicacion.Protectora;
import com.appcrud.comunicacion.Usuario;

public class Peticion implements Serializable {


    public enum TipoOperacion {

        // Operaciones para USUARIO
        CREATE_USUARIO,
        READ_USUARIO,
        READ_ALL_USUARIO,
        UPDATE_USUARIO,
        DELETE_USUARIO,

        // Operaciones para ANIMAL
        CREATE_ANIMAL,
        READ_ANIMAL,
        READ_ALL_ANIMAL,
        UPDATE_ANIMAL,
        DELETE_ANIMAL,

        // Operaciones para PROTECTORA
        CREATE_PROTECTORA,
        READ_PROTECTORA,
        READ_ALL_PROTECTORA,
        UPDATE_PROTECTORA,
        DELETE_PROTECTORA,

        // Operaciones adicionales específicas de tu CAD
        READ_ANIMALES_BY_PROTECTORA,   // Obtener animales de una protectora
        GUARDAR_ANIMAL_EN_USUARIO,     // Asignar un animal a un usuario
        QUITAR_ANIMAL_DE_USUARIO,      // Quitar un animal de un usuario
        OBTENER_FAVORITOS_DE_USUARIO,

        PING,
        LOGIN_USUARIO,
        LOGIN_PROTECTORA
    }

    private TipoOperacion tipoOperacion;
    private Usuario usuario;
    private int idUsuario;
    private Animal animal;
    private int idAnimal;
    private Protectora protectora;
    private int idProtectora;
    private String email;
    private String passwordPlano;

    public Peticion(TipoOperacion tipoOperacion, Usuario usuario, int idUsuario, Animal animal, int idAnimal, Protectora protectora, int idProtectora) {
        this.tipoOperacion = tipoOperacion;
        this.usuario = usuario;
        this.idUsuario = idUsuario;
        this.animal = animal;
        this.idAnimal = idAnimal;
        this.protectora = protectora;
        this.idProtectora = idProtectora;
    }

    // Constructor para PING
    public Peticion(TipoOperacion tipo) {
        this.tipoOperacion = tipo;
    }

    // Constructor para REGISTER y LOGIN
    public Peticion(TipoOperacion tipoOperacion, String email, String passwordPlano) {
        super();
        this.tipoOperacion = tipoOperacion;
        this.email = email;
        this.passwordPlano = passwordPlano;
    }

    public Peticion(TipoOperacion tipoOperacion, Usuario usuario, int idUsuario) {
        this.tipoOperacion = tipoOperacion;
        this.usuario = usuario;
        this.idUsuario = idUsuario;
    }

    public Peticion(TipoOperacion tipoOperacion, Protectora protectora, int idProtectora) {
        this.tipoOperacion = tipoOperacion;
        this.protectora = protectora;
        this.idProtectora = idProtectora;
    }

    // Getters y Setters
    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }
    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Animal getAnimal() {
        return animal;
    }
    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public int getIdAnimal() {
        return idAnimal;
    }
    public void setIdAnimal(int idAnimal) {
        this.idAnimal = idAnimal;
    }

    public Protectora getProtectora() {
        return protectora;
    }
    public void setProtectora(Protectora protectora) {
        this.protectora = protectora;
    }

    public int getIdProtectora() {
        return idProtectora;
    }
    public void setIdProtectora(int idProtectora) {
        this.idProtectora = idProtectora;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordPlano() {
        return passwordPlano;
    }
    public void setPasswordPlano(String passwordPlano) {
        this.passwordPlano = passwordPlano;
    }
}