package com.appcrud.comunicacion

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AnimalAdapter(
    private val animales: List<Animal>,
    private val rol: String?, // <- nuevo parámetro
    private val onClick: (Animal) -> Unit
) : RecyclerView.Adapter<AnimalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen: ImageView = view.findViewById(R.id.animalImage)
        val nombre: TextView = view.findViewById(R.id.animalName)
        val generoIcono: ImageView = view.findViewById(R.id.animalGender)
        val edad: TextView = view.findViewById(R.id.animalAge)
        val favIcon: ImageView = view.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.animal_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val animal = animales[position]

        holder.nombre.text = animal.nombreAnimal

        animal.fechaNacimiento?.let {
            holder.edad.text = calcularEdadFormateada(it)
        } ?: run {
            holder.edad.text = "Edad desconocida"
        }

        val sexo = animal.sexo?.lowercase(Locale.getDefault()) ?: "desconocido"
        holder.generoIcono.setImageResource(
            when (sexo) {
                "m", "macho" -> R.drawable.ic_male
                else -> R.drawable.ic_female
            }
        )

        animal.foto?.let { nombreArchivo ->
            val url = "http://192.168.1.215:8080/fotos/${nombreArchivo.lowercase(Locale.ROOT)}"
                .replace(" ", "%20")

            Log.d("GLIDE_URL", "Cargando imagen: $url")

            Glide.with(holder.itemView.context)
                .load(url)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(holder.imagen)
        } ?: run {
            holder.imagen.setImageResource(R.drawable.ic_image)
        }


        holder.itemView.setOnClickListener {
            onClick(animal)
        }

        Log.d("HomeFoto", "Animal: ${animal.nombreAnimal} - Foto: ${animal.foto}")
        Log.d("FOTO_URL", "Foto de ${animal.nombreAnimal} --> ${animal.foto}")

        if (rol != "usuario") {
            holder.favIcon.visibility = View.GONE
        } else {
            holder.favIcon.visibility = View.VISIBLE
            holder.favIcon.setImageResource(
                if (animal.isEsFavorito()) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline_gray2
            )

            holder.favIcon.setOnClickListener {
                animal.setEsFavorito(!animal.isEsFavorito())
                holder.favIcon.setImageResource(
                    if (animal.isEsFavorito()) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline_gray2
                )


                val scaleX = ObjectAnimator.ofFloat(holder.favIcon, "scaleX", 1f, 1.3f, 1f)
                val scaleY = ObjectAnimator.ofFloat(holder.favIcon, "scaleY", 1f, 1.3f, 1f)
                AnimatorSet().apply {
                    playTogether(scaleX, scaleY)
                    duration = 300
                    interpolator = OvershootInterpolator()
                    start()
                }


                CoroutineScope(Dispatchers.IO).launch {
                    val tipo = if (animal.isEsFavorito())
                        Peticion.TipoOperacion.GUARDAR_ANIMAL_EN_USUARIO
                    else
                        Peticion.TipoOperacion.QUITAR_ANIMAL_DE_USUARIO

                    val prefs = holder.itemView.context.getSharedPreferences(
                        "usuarioPrefs",
                        Context.MODE_PRIVATE
                    )
                    val userId = prefs.getInt("usuarioId", -1)

                    val peticion = Peticion(tipo).apply {
                        idUsuario = userId
                        idAnimal = animal.animalId
                    }

                    Log.d("Favorito", "Tipo: $tipo, Usuario: $userId, Animal: ${animal.animalId}")

                    try {
                        ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                    } catch (e: Exception) {
                        Log.e("AnimalAdapter", "Error al enviar petición de favorito", e)
                    }
                }
            }
        }
    }


    override fun getItemCount(): Int = animales.size

    private fun calcularEdadFormateada(nacimiento: Date): String {
        return try {
            val hoy = Calendar.getInstance()
            val fechaNac = Calendar.getInstance().apply { time = nacimiento }

            val años = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR)
            val meses = hoy.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH)
            val dias = hoy.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH)

            var edadMeses = años * 12 + meses
            if (dias < 0) edadMeses -= 1

            return when {
                edadMeses < 1 -> {
                    val diffMillis = hoy.timeInMillis - fechaNac.timeInMillis
                    val diasTotales = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
                    val semanas = diasTotales / 7

                    when {
                        semanas >= 1 -> "$semanas ${if (semanas == 1) "semana" else "semanas"}"
                        else -> "$diasTotales ${if (diasTotales == 1) "día" else "días"}"
                    }
                }
                edadMeses < 12 -> "$edadMeses ${if (edadMeses == 1) "mes" else "meses"}"
                else -> {
                    val edadAnios = edadMeses / 12
                    "$edadAnios ${if (edadAnios == 1) "año" else "años"}"
                }
            }
        } catch (e: Exception) {
            "Edad desconocida"
        }
    }

}
