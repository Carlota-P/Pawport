package com.appcrud.comunicacion

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.appcrud.comunicacion.dialog.ImageDialogFragment
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FragmentAnimal_detail : Fragment() {

    private lateinit var backButton: ImageButton
    var esFavorito = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_animal_detail, container, false)

        parentFragmentManager.setFragmentResultListener("animalUpdateKey", viewLifecycleOwner) { _, result ->
            val actualizado = result.getBoolean("animal_actualizado", false)
            if (actualizado) {
                recargarAnimal()
            }
        }

        val animal = arguments?.getSerializable("animal") as? Animal

        animal?.let {
            rootView.findViewById<TextView>(R.id.detailRaza).text = it.raza
            rootView.findViewById<TextView>(R.id.detailEdad).text = calcularEdadConFecha(it.fechaNacimiento)
            rootView.findViewById<TextView>(R.id.textHistoria).text = it.historia
            val nombreRefugio = animal?.protectora?.nombre ?: "Desconocido"
            rootView.findViewById<TextView>(R.id.textRefugio).text = nombreRefugio
            val imageProtectora = rootView.findViewById<ImageView>(R.id.iconRefugio)
            val urlProtectora = animal?.protectora?.foto?.let { "http://192.168.1.215:8080/$it" }
            Glide.with(requireContext())
                .load(urlProtectora)
                .placeholder(R.drawable.default_refugio)
                .error(R.drawable.default_refugio)
                .into(imageProtectora)


            rootView.findViewById<TextView>(R.id.textRefugio).text = nombreRefugio
            rootView.findViewById<TextView>(R.id.detailNombreAnimal).text = animal.nombreAnimal
            val textViewRaza = rootView.findViewById<TextView>(R.id.detailRaza)
            val raza = animal.raza?.trim()
            textViewRaza.text = if (!raza.isNullOrBlank()) raza else "La raza se desconoce"
            textViewRaza.setTextColor(if (!raza.isNullOrBlank()) Color.parseColor("#18375D") else Color.GRAY)

            rootView.findViewById<TextView>(R.id.detailEdad).text = calcularEdadConFecha(animal.fechaNacimiento)

            val imageAnimal = rootView.findViewById<ImageView>(R.id.imageAnimal)

            val url = animal.foto?.lowercase(Locale.ROOT)?.let {
                "http://192.168.1.215:8080/fotos/${it.replace(" ", "%20")}"
            }

            Glide.with(requireContext())
                .load(url)
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(imageAnimal)

            imageAnimal.setOnClickListener {
                url?.let {
                    val dialog = ImageDialogFragment(it)
                    dialog.show(parentFragmentManager, "imageDialog")
                }
            }

            val bundle = Bundle()
            bundle.putSerializable("animal", animal)
            val fragment = FragmentCreateAnimal()
            fragment.arguments = bundle


            Log.d("DEBUG_FOTO", "animal.foto = ${animal.foto}")
            Log.d("DEBUG_FOTO", "protectora.foto = ${animal.protectora?.foto}")

            // Datos adicionales dinámicos
            val infoContainer = rootView.findViewById<LinearLayout>(R.id.infoAdicionalContainer)
            infoContainer.removeAllViews() // Por si se recarga el fragmento

            if (!animal.identificador.isNullOrBlank()) {
                val idView = TextView(requireContext()).apply {
                    text = "• Microhip"
                    setTextColor(Color.parseColor("#18375D"))
                    textSize = 14f
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
                }
                infoContainer.addView(idView)
            }

            val infoList = listOf(
                "Castrado" to animal.castrado,
                "Vacunado" to animal.vacuna,
                "Pasaporte" to animal.pasaporte
            )

            val filtrados = infoList.filter { it.second == "S" }

            if (filtrados.isNotEmpty()) {
                filtrados.forEach { (label, _) ->
                    val item = TextView(requireContext()).apply {
                        text = "• $label"
                        setTextColor(Color.parseColor("#18375D"))
                        textSize = 14f
                        typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
                    }
                    infoContainer.addView(item)
                }
            } else {
                val sinDatos = TextView(requireContext()).apply {
                    text = "Este animal no tiene información adicional registrada."
                    setTextColor(Color.GRAY)
                    textSize = 13f
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular)
                }
                infoContainer.addView(sinDatos)
            }

            val generoIcono = rootView.findViewById<ImageView>(R.id.detailGeneroIcono)
            val generoTexto = rootView.findViewById<TextView>(R.id.detailGeneroTexto)

            when (animal.sexo?.uppercase(Locale.getDefault())) {
                "M" -> {
                    generoIcono.setImageResource(R.drawable.ic_male_removebg)
                    generoTexto.text = "Macho"
                }

                else -> {
                    generoIcono.setImageResource(R.drawable.ic_female_removebg) // asegúrate que este icono existe
                    generoTexto.text = "Hembra"
                }
            }

            val botonInfo = rootView.findViewById<TextView>(R.id.btnMasInfo)
            botonInfo.setOnClickListener {
                val protectora = animal.protectora
                if (protectora != null) {
                    val args = Bundle().apply {
                        putInt("idProtectora", protectora.protectoraId)
                        putString("nombre", protectora.nombre ?: "Desconocido")
                        putString("email", protectora.email ?: "No disponible")
                        putString("telefono", protectora.telefono?.toString() ?: "No disponible")
                        putString("historia", protectora.historia ?: "Sin historia registrada")
                        putString("calle", protectora.calle ?: "Calle no disponible")
                        putInt("numeroCalle", protectora.numeroCalle ?: -1)
                        putInt("codPostal", protectora.codPostal ?: -1)
                    }

                    val fragment = FragmentRefugio_Detail().apply {
                        arguments = args
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    Log.w("FragmentAnimal_detail", "Protectora es null, no se puede abrir el detalle")
                }
            }
        }
        esFavorito = animal?.isEsFavorito ?: false
        actualizarIcono(rootView.findViewById(R.id.favoriteIcon))

        backButton = rootView.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val editIcon = rootView.findViewById<ImageView>(R.id.editIcon)
        val favIcon = rootView.findViewById<ImageView>(R.id.favoriteIcon)
        val prefs = requireActivity().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
        val idProtectoraSesion  = prefs.getInt("usuarioId", 0)
        val rol = prefs.getString("rol", "") ?: ""
        val idProtectoraAnimal = animal?.protectora?.protectoraId ?: 0
        val esProtectora = rol == "protectora"
        val esSuAnimal = esProtectora && idProtectoraSesion  == idProtectoraAnimal

        Log.d("EDIT_ICON_DEBUG", "rol: $rol, userId: $idProtectoraSesion , idProtectoraAnimal: $idProtectoraAnimal, esSuAnimal: $esSuAnimal")

        if (rol == "usuario") {
            favIcon.visibility = View.VISIBLE
            editIcon.visibility = View.GONE
        } else if (esSuAnimal) {
            favIcon.visibility = View.GONE
            editIcon.visibility = View.VISIBLE
        } else {
            favIcon.visibility = View.GONE
            editIcon.visibility = View.GONE
        }

        Log.d("Favoritos", "ID de usuario enviado: $idProtectoraSesion ")

        favIcon.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val tipo = if (!esFavorito)
                    Peticion.TipoOperacion.GUARDAR_ANIMAL_EN_USUARIO
                else
                    Peticion.TipoOperacion.QUITAR_ANIMAL_DE_USUARIO

                val peticion = Peticion(tipo).apply {
                    idUsuario = idProtectoraSesion
                    idAnimal = animal?.animalId ?: -1
                }

                Log.d("Favoritos", "Enviando favorito: userId=$idProtectoraSesion, animalId=${animal?.animalId}")

                val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

                withContext(Dispatchers.Main) {
                    if (respuesta?.isExito == true) {
                        esFavorito = !esFavorito
                        actualizarIcono(favIcon)
                        Toast.makeText(requireContext(), respuesta.mensaje, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar favorito", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        editIcon.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("animal", animal)
            }
            val fragment = FragmentCreateAnimal().apply {
                arguments = bundle
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return rootView

    }


    fun actualizarIcono(favIcon: ImageView) {
        favIcon.setImageResource(if (esFavorito) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline)
    }

    private fun calcularEdadConFecha(fecha: Date?): String {
        if (fecha == null) return "Fecha desconocida"

        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance().apply { time = fecha }

        val diffMillis = hoy.timeInMillis - nacimiento.timeInMillis
        val dias = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        val semanas = dias / 7
        val meses = (hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)) * 12 +
                (hoy.get(Calendar.MONTH) - nacimiento.get(Calendar.MONTH))
        val años = meses / 12

        val edadTexto = when {
            dias < 7 -> "$dias ${if (dias == 1) "día" else "días"}"
            dias < 30 -> "$semanas ${if (semanas == 1) "semana" else "semanas"}"
            meses < 12 -> "$meses ${if (meses == 1) "mes" else "meses"}"
            else -> "$años ${if (años == 1) "año" else "años"}"
        }

        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return "${formatoFecha.format(fecha)}\n$edadTexto"
    }


    private fun recargarAnimal() {
        val idAnimal = arguments?.getSerializable("animal")?.let { (it as Animal).animalId } ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.READ_ANIMAL).apply {
                this.idAnimal = idAnimal
            }

            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

            withContext(Dispatchers.Main) {
                if (respuesta?.isExito == true && respuesta.animal != null) {
                    val fragment = FragmentAnimal_detail().apply {
                        arguments = Bundle().apply {
                            putSerializable("animal", respuesta.animal)
                        }
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Error al recargar el animal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
