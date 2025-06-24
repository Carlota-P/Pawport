package com.appcrud.comunicacion

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentRefugio_Detail : Fragment() {

    private lateinit var recyclerAdopcion: RecyclerView
    private lateinit var direccionTextView: TextView
    private lateinit var backButton: ImageButton
    private var rol: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_refeugio_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idProtectora = arguments?.getInt("idProtectora") ?: -1

        val nombreTextView = view.findViewById<TextView>(R.id.nombreTextView)
        val emailTextView = view.findViewById<TextView>(R.id.emailTextView)
        val telefonoTextView = view.findViewById<TextView>(R.id.telefonoTextView)
        val historiaTextView = view.findViewById<TextView>(R.id.historiaTextView)
        direccionTextView = view.findViewById(R.id.direccionTextView)
        recyclerAdopcion = view.findViewById(R.id.recyclerAdopcion)
        recyclerAdopcion.layoutManager = GridLayoutManager(requireContext(), 2)
        emailTextView.paintFlags = emailTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        backButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Cargar rol desde SharedPreferences
        rol = requireActivity()
            .getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
            .getString("rol", null)

        if (idProtectora != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val peticion = Peticion(Peticion.TipoOperacion.READ_PROTECTORA).apply {
                    this.idProtectora = idProtectora
                }

                val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                val protectora = respuesta?.protectora

                withContext(Dispatchers.Main) {
                    if (protectora != null) {
                        nombreTextView.text = protectora.nombre ?: "Nombre no disponible"
                        emailTextView.text = protectora.email ?: "Email no disponible"
                        telefonoTextView.text = protectora.telefono?.toString() ?: "No disponible"
                        historiaTextView.text = protectora.historia ?: "Sin historia registrada"

                        val direccion = buildString {
                            if (!protectora.calle.isNullOrBlank()) append(protectora.calle)
                            if ((protectora.numeroCalle ?: -1) != -1) append(", Nº ${protectora.numeroCalle}")
                            if ((protectora.codPostal ?: 0) != 0) append(", CP ${protectora.codPostal}")
                            if (!protectora.provincia.isNullOrBlank()) append(", ${protectora.provincia}")
                            if (!protectora.comunidadAutonoma.isNullOrBlank()) append(" (${protectora.comunidadAutonoma})")
                        }.ifEmpty { "Dirección no disponible" }

                        direccionTextView.text = direccion

                        val logoImageView = view.findViewById<ImageView>(R.id.logoProtectora)

                        protectora.foto?.let { ruta ->
                            val url = "http://192.168.1.215:8080/$ruta"
                            Glide.with(requireContext()).load(url).into(logoImageView)
                        } ?: run {
                            logoImageView.setImageResource(R.drawable.default_refugio)
                        }

                        protectora.protectoraId?.let {
                            cargarAnimales(it)
                        } ?: Log.e("RefugioDetail", "Protectora sin ID")
                    } else {
                        Toast.makeText(requireContext(), "Error al cargar datos de la protectora", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun cargarAnimales(idProtectora: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.READ_ANIMALES_BY_PROTECTORA).apply {
                this.idProtectora = idProtectora
            }

            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
            val listaAnimales = respuesta?.animales?.toMutableList() ?: mutableListOf()

            // 🔐 Si el usuario es un "usuario", marcamos favoritos
            val sharedPrefs = requireActivity().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
            val userId = sharedPrefs.getInt("usuarioId", -1)

            val idsFavoritos = if (rol == "usuario" && userId != -1) {
                val peticionFavs = Peticion(Peticion.TipoOperacion.OBTENER_FAVORITOS_DE_USUARIO).apply {
                    idUsuario = userId
                }
                val respuestaFavs = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticionFavs)
                respuestaFavs?.animales?.map { it.animalId }?.toSet() ?: emptySet()
            } else emptySet()

            // ✅ Marcar favoritos en la lista de animales
            listaAnimales.forEach {
                it.setEsFavorito(it.animalId in idsFavoritos)
            }

            withContext(Dispatchers.Main) {
                if (respuesta?.isExito == true) {
                    val adapter = AnimalAdapter(listaAnimales, rol) { animalSeleccionado ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val peticionDetalle = Peticion(Peticion.TipoOperacion.READ_ANIMAL).apply {
                                idAnimal = animalSeleccionado.animalId
                                idUsuario = userId
                            }

                            val respuestaDetalle = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticionDetalle)
                            val animalCompleto = respuestaDetalle?.animal

                            withContext(Dispatchers.Main) {
                                if (animalCompleto != null) {
                                    val bundle = Bundle().apply {
                                        putSerializable("animal", animalCompleto)
                                    }
                                    val detalleFragment = FragmentAnimal_detail().apply {
                                        arguments = bundle
                                    }
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, detalleFragment)
                                        .addToBackStack(null)
                                        .commit()
                                } else {
                                    Toast.makeText(requireContext(), "Error al obtener el animal completo", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    recyclerAdopcion.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Error al cargar animales", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
