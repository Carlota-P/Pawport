/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appcrud.comunicacion;

/**
 *
 * @author DAM214
 */
public class ExcepcionesAcoger extends Exception
{
    private String mensajeErrorUsuario;
    private Integer codigoErrorBD;
    private String mensajeErrorAdministrador;
    private String sentenciaSQL;

    public ExcepcionesAcoger() {
    }

    public ExcepcionesAcoger(String mensajeErrorUsuario, Integer codigoErrorBD, String mensajeErrorAdministrador, String sentencia) {
        this.mensajeErrorUsuario = mensajeErrorUsuario;
        this.codigoErrorBD = codigoErrorBD;
        this.mensajeErrorAdministrador = mensajeErrorAdministrador;
        this.sentenciaSQL = sentencia;
    }

    public String getMensajeErrorUsuario() {
        return mensajeErrorUsuario;
    }

    public void setMensajeErrorUsuario(String mensajeErrorUsuario) {
        this.mensajeErrorUsuario = mensajeErrorUsuario;
    }

    public Integer getCodigoErrorBD() {
        return codigoErrorBD;
    }

    public void setCodigoErrorBD(Integer codigoErrorBD) {
        this.codigoErrorBD = codigoErrorBD;
    }

    public String getMensajeErrorAdministrador() {
        return mensajeErrorAdministrador;
    }

    public void setMensajeErrorAdministrador(String mensajeErrorAdministrador) {
        this.mensajeErrorAdministrador = mensajeErrorAdministrador;
    }

    public String getSentenciaSQL() {
        return sentenciaSQL;
    }

    public void setSentenciaSQL(String sentenciaSQL) {
        this.sentenciaSQL = sentenciaSQL;
    }

    @Override
    public String toString() {
        return "ExcepcionesAcoger{" + "mensajeErrorUsuario=" + mensajeErrorUsuario + ", codigoErrorBD=" + codigoErrorBD + ", mensajeErrorAdministrador=" + mensajeErrorAdministrador + ", sentencia=" + sentenciaSQL + '}';
    }
}
