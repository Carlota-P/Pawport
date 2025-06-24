package com.appcrud.comunicacion;

import java.io.Serializable;
import java.util.Date;
import com.appcrud.comunicacion.Usuario;
import com.appcrud.comunicacion.Animal;

public class Favoritos implements Serializable
{
    private int usuarioId;
    private int animalId;

    public Favoritos() {

    }

    public Favoritos(int usuarioId, int animalId) {
        this.usuarioId = usuarioId;
        this.animalId = animalId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    @Override
    public String toString() {
        return "Favoritos [usuarioId=" + usuarioId + ", animalId=" + animalId + "]";
    }
}
