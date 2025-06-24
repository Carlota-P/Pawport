/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appcrud.comunicacion;
import java.io.Serializable;
/**
 *
 * @author DAM214
 */
public class Usuario implements Serializable {

    private Integer usuarioId;
    private String dni;
    private String alias;
    private Integer telefono;
    private String email;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String contrasena;
    private String rol;

    public Usuario(){

    }

    public Usuario (Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Usuario(Integer usuarioId, String dni, String alias, Integer telefono, String email, String nombre, String apellido1, String apellido2, String contrasena) {
        this.usuarioId = usuarioId;
        this.dni = dni;
        this.alias = alias;
        this.telefono = telefono;
        this.email = email;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.contrasena = contrasena;
    }

    public Usuario(Integer usuarioId, String dni, String alias, Integer telefono, String email, String nombre, String apellido1, String apellido2) {
        this.usuarioId = usuarioId;
        this.dni = dni;
        this.alias = alias;
        this.telefono = telefono;
        this.email = email;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
    }

    public Usuario(String alias, String contrasena) {
        this.alias = alias;
        this.contrasena = contrasena;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "UsuarioPojo{" + "usuarioId=" + usuarioId + ", dni=" + dni + ", alias=" + alias + ", telefono=" + telefono + ", email=" + email + ", nombre=" + nombre + ", apellido1=" + apellido1 + ", apellido2=" + apellido2 + ", contrasena=" + contrasena + ", rol=" + rol + '}';
    }



}
