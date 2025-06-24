/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appcrud.comunicacion;

import java.io.Serializable;
import java.util.Date;
import com.appcrud.comunicacion.Usuario;
import com.appcrud.comunicacion.Protectora;

/**
 *
 * @author DAM214
 */
public class Animal implements Serializable
{
    private Integer animalId;
    private Protectora protectora;
    private String identificador;
    private Date fechaNacimiento;
    private String nombreAnimal;
    private String vacuna;
    private String castrado;
    private String pasaporte;
    private String sexo;
    private String animalTipo;
    private String raza;
    private String historia;
    private String foto;
    private boolean esFavorito;

    public Animal() {
    }

    public Animal (Integer animalId) {
        this.animalId = animalId;
    }

    public Animal(Integer animalId, Protectora protectora, String identificador, Date fechaNacimiento, String nombreAnimal, String vacuna, String castrado, String pasaporte, String sexo, String animalTipo, String raza, String historia, boolean esFavorito, String foto) {
        this.animalId = animalId;
        this.protectora = protectora;
        this.identificador = identificador;
        this.fechaNacimiento = fechaNacimiento;
        this.nombreAnimal = nombreAnimal;
        this.vacuna = vacuna;
        this.castrado = castrado;
        this.pasaporte = pasaporte;
        this.sexo = sexo;
        this.animalTipo = animalTipo;
        this.raza = raza;
        this.historia = historia;
        this.esFavorito = esFavorito;
        this.foto = foto;
    }

    public Integer getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    public Protectora getProtectora() {
        return protectora;
    }

    public void setProtectora(Protectora protectora) {
        this.protectora = protectora;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombreAnimal() {
        return nombreAnimal;
    }

    public void setNombreAnimal(String nombreAnimal) {
        this.nombreAnimal = nombreAnimal;
    }

    public String getVacuna() {
        return vacuna;
    }

    public void setVacuna(String vacuna) {
        this.vacuna = vacuna;
    }

    public String getCastrado() {
        return castrado;
    }

    public void setCastrado(String castrado) {
        this.castrado = castrado;
    }

    public String getPasaporte() {
        return pasaporte;
    }

    public void setPasaporte(String pasaporte) {
        this.pasaporte = pasaporte;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getAnimalTipo() {
        return animalTipo;
    }

    public void setAnimalTipo(String animalTipo) {
        this.animalTipo = animalTipo;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getHistoria() {
        return historia;
    }

    public void setHistoria(String historia) {
        this.historia = historia;
    }

    public boolean isEsFavorito() {
        return esFavorito;
    }

    public void setEsFavorito(boolean esFavorito) {
        this.esFavorito = esFavorito;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }


    @Override
    public String toString() {
        return "Animal{" + "animalId=" + animalId + ", protectora=" + protectora + ", identificador=" + identificador + ", fechaNacimiento=" + fechaNacimiento + ", nombreAnimal=" + nombreAnimal + ", vacuna=" + vacuna + ", castrado=" + castrado + ", pasaporte=" + pasaporte + ", sexo=" + sexo + ", animalTipo=" + animalTipo + ", raza=" + raza + ", historia=" + historia + ", foto=" + foto + '}';
    }
}
