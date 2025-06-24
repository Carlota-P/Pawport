package com.appcrud.comunicacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RefugioAdapter(private val protectoras: List<Protectora>) :
    RecyclerView.Adapter<RefugioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tvNombreProtectora)
        val historia: TextView = view.findViewById(R.id.tvDescripcion)
        val imagen: ImageView = view.findViewById(R.id.logoProtectora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_refugio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val protectora = protectoras[position]
        holder.nombre.text = protectora.nombre
        holder.historia.text = protectora.historia

        protectora.foto?.let { nombreArchivo ->
            val url = "http://192.168.1.215:8080/$nombreArchivo"
            Glide.with(holder.itemView.context).load(url).into(holder.imagen)
        } ?: run {
            holder.imagen.setImageResource(R.drawable.default_refugio)
        }

        holder.itemView.setOnClickListener {
            val fragment = FragmentRefugio_Detail().apply {
                arguments = Bundle().apply {
                    putInt("idProtectora", protectora.protectoraId)
                    putString("nombre", protectora.nombre)
                    putString("email", protectora.email)
                    putString("telefono", protectora.telefono.toString())
                    putString("historia", protectora.historia)
                    putString("direccion", "${protectora.calle}, Nº ${protectora.numeroCalle}, CP ${protectora.codPostal}")
                }
            }

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = protectoras.size
}
