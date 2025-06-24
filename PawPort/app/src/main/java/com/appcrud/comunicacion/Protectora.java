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
public class Protectora implements Serializable
{
    private Integer protectoraId;
    private String foto;
    private Integer capacidad;
    private String nombre;
    private String calle;
    private Integer numeroCalle;
    private Integer codPostal;
    private String email;
    private Integer telefono;
    private String historia;
    private String contrasena;
    private String provincia;
    private String comunidadAutonoma;


    public Protectora() {
    }

    public Protectora(Integer protectoraId) {
        this.protectoraId = protectoraId;
    }

    public Protectora(Integer protectoraId, Integer capacidad, String nombre) {
        this.protectoraId = protectoraId;
        this.capacidad = capacidad;
        this.nombre = nombre;
    }

    public Protectora(Integer protectoraId, Integer capacidad, String nombre, String calle, Integer numeroCalle, Integer codPostal, String email, Integer telefono, String historia, String provincia, String comunidadAutonoma, String contrasena) {
        this.protectoraId = protectoraId;
        this.capacidad = capacidad;
        this.nombre = nombre;
        this.calle = calle;
        this.numeroCalle = numeroCalle;
        this.codPostal = codPostal;
        this.email = email;
        this.telefono = telefono;
        this.historia = historia;
        this.provincia = provincia;
        this.comunidadAutonoma = comunidadAutonoma;
        this.contrasena = contrasena;
    }

    public Protectora(Integer protectoraId, Integer capacidad, String nombre, String calle, Integer numeroCalle, Integer codPostal, String email, Integer telefono, String historia, String contrasena, String provincia, String comunidadAutonoma, String foto) {
        this.protectoraId = protectoraId;
        this.capacidad = capacidad;
        this.nombre = nombre;
        this.calle = calle;
        this.numeroCalle = numeroCalle;
        this.codPostal = codPostal;
        this.email = email;
        this.telefono = telefono;
        this.historia = historia;
        this.contrasena = contrasena;
        this.foto= foto;
        this.provincia = provincia;
        this.comunidadAutonoma = comunidadAutonoma;
    }

    public Integer getProtectoraId() {
        return protectoraId;
    }

    public void setProtectoraId(Integer protectoraId) {
        this.protectoraId = protectoraId;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Integer getNumeroCalle() {
        return numeroCalle;
    }

    public void setNumeroCalle(Integer numeroCalle) {
        this.numeroCalle = numeroCalle;
    }

    public Integer getCodPostal() {
        return codPostal;
    }

    public void setCodPostal(Integer codPostal) {
        this.codPostal = codPostal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }


    public Integer getTelefono() {
        return telefono;
    }

    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getComunidadAutonoma() {
        return comunidadAutonoma;
    }

    public void setComunidadAutonoma(String comunidadAutonoma) {
        this.comunidadAutonoma = comunidadAutonoma;
    }

    @Override
    public String toString() {
        return "ProtectoraPojo{" + "protectora_Id=" + protectoraId + ", capacidad=" + capacidad + ", nombre=" + nombre + ", calle=" + calle + ", numeroCalle=" + numeroCalle + ", codPostal=" + codPostal + ", provincia=" + provincia + ", comundiad Autonoma =" + comunidadAutonoma +", email=" + email + ", telefono=" + telefono + ", historia=" + historia + ", contrasena=" + contrasena + ", foto=" + foto + '}';
    }
}
