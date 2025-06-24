package com.appcrud.comunicacion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FavoritosAdapter(
    private val animales: List<Animal>,
    private val onClick: (Animal) -> Unit
) : RecyclerView.Adapter<FavoritosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.animalImage)
        val nombre: TextView = view.findViewById(R.id.textName)
        val edad: TextView = view.findViewById(R.id.textAge)
        val descripcion: TextView = view.findViewById(R.id.textDescription)
        val flecha: ImageView = view.findViewById(R.id.buttonMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_favoritos, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = animales[position]

        holder.nombre.text = animal.nombreAnimal
        holder.edad.text = calcularEdad(animal.fechaNacimiento)
        holder.descripcion.text = animal.historia ?: ""

        val nombreArchivo = animal.foto?.trim()?.lowercase()?.replace(" ", "%20")

        if (!nombreArchivo.isNullOrBlank()) {
            val url = "http://192.168.1.215:8080/fotos/$nombreArchivo"
            Glide.with(holder.itemView.context)
                .load(url)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(holder.imagen)

            Log.d("GlideDebug", "Cargando imagen: $url")
        } else {
            holder.imagen.setImageResource(R.drawable.ic_image)
            Log.w("GlideDebug", "Imagen no disponible para ${animal.nombreAnimal}")
        }

        holder.itemView.setOnClickListener { onClick(animal) }
        holder.flecha.setOnClickListener { onClick(animal) }
    }

    override fun getItemCount(): Int = animales.size

    private fun calcularEdad(fechaNacimiento: java.util.Date?): String {
        if (fechaNacimiento == null) return "Edad desconocida"
        val hoy = java.util.Calendar.getInstance()
        val nacimiento = java.util.Calendar.getInstance().apply { time = fechaNacimiento }

        var edad = hoy.get(java.util.Calendar.YEAR) - nacimiento.get(java.util.Calendar.YEAR)
        if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < nacimiento.get(java.util.Calendar.DAY_OF_YEAR)) {
            edad--
        }
        return "$edad ${if (edad == 1) "año" else "años"}"
    }
}
