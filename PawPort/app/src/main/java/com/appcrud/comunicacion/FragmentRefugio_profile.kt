package com.appcrud.comunicacion

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

class FragmentRefugio_profile : Fragment() {

    private lateinit var nombreTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var telefonoTextView: TextView
    private lateinit var historiaTextView: TextView
    private lateinit var direccionTextView: TextView
    private lateinit var recyclerAdopcion: RecyclerView
    private lateinit var settingsButton: ImageButton
    private lateinit var logoImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_refugio_profile, container, false)

        nombreTextView = view.findViewById(R.id.nombreTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        emailTextView.paintFlags = emailTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        telefonoTextView = view.findViewById(R.id.telefonoTextView)
        historiaTextView = view.findViewById(R.id.historiaTextView)
        direccionTextView = view.findViewById(R.id.direccionTextView)
        recyclerAdopcion = view.findViewById(R.id.recyclerAdopcion)
        logoImageView = view.findViewById(R.id.logoProtectora)
        settingsButton = view.findViewById(R.id.imageButtonSettings)

        settingsButton.setOnClickListener {
            showSettingsMenu(it)
        }

        val recyclerAnimales = view.findViewById<RecyclerView>(R.id.recyclerAdopcion)
        recyclerAnimales.layoutManager = GridLayoutManager(requireContext(), 2)

        val sharedPref = requireActivity().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
        val idProtectora = sharedPref.getInt("usuarioId", -1)

        if (idProtectora != -1) {
            cargarDatosProtectora(idProtectora)
            cargarAnimalesRegistrados(idProtectora, recyclerAnimales)
        }

        return view
    }

    private fun cargarDatosProtectora(idProtectora: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.READ_PROTECTORA).apply {
                this.idProtectora = idProtectora
            }

            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

            withContext(Dispatchers.Main) {
                val protectora = respuesta?.protectora
                if (respuesta?.isExito == true && protectora != null) {
                    nombreTextView.text = protectora.nombre
                    emailTextView.text = protectora.email
                    telefonoTextView.text = "+${protectora.telefono}"
                    historiaTextView.text = protectora.historia
                    val direccion = buildString {
                        if (!protectora.calle.isNullOrBlank()) append(protectora.calle)
                        if ((protectora.numeroCalle ?: -1) != -1) append(", Nº ${protectora.numeroCalle}")
                        if ((protectora.codPostal ?: 0) != 0) append(", CP ${protectora.codPostal}")
                        if (!protectora.provincia.isNullOrBlank()) append(", ${protectora.provincia}")
                        if (!protectora.comunidadAutonoma.isNullOrBlank()) append(" (${protectora.comunidadAutonoma})")
                    }
                    direccionTextView.text = direccion.ifEmpty { "Dirección no disponible" }

                    protectora.foto?.let { nombreArchivo ->
                        val url = "http://192.168.1.215:8080/$nombreArchivo"
                        Glide.with(requireContext()).load(url).into(logoImageView)
                    } ?: run {
                        logoImageView.setImageResource(R.drawable.default_refugio)
                    }


                    Log.d("ProtectoraProfileDebug", """
                        id = $idProtectora
                        nombre = ${protectora.nombre}
                        email = ${protectora.email}
                        telefono = ${protectora.telefono}
                        historia = ${protectora.historia}
                        calle = ${protectora.calle}
                        numero = ${protectora.numeroCalle}
                        codPostal = ${protectora.codPostal}
                    """.trimIndent())

                } else {
                    Toast.makeText(requireContext(), "No se pudieron cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cargarAnimalesRegistrados(idProtectora: Int, recyclerView: RecyclerView) {
        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.READ_ANIMALES_BY_PROTECTORA).apply {
                this.idProtectora = idProtectora
            }

            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

            withContext(Dispatchers.Main) {
                if (respuesta?.isExito == true) {

                    val rol = requireActivity()
                        .getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
                        .getString("rol", null)

                    val listaAnimales = respuesta.animales ?: emptyList()

                    val adapter = AnimalAdapter(listaAnimales, rol) { animalSeleccionado ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val detalle = Peticion(Peticion.TipoOperacion.READ_ANIMAL).apply {
                                idAnimal = animalSeleccionado.animalId
                            }

                            val respuestaDetalle = ClienteSocket(
                                ServidorConfig.HOST,
                                ServidorConfig.PUERTO
                            ).enviarPeticion(detalle)
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
                                    Toast.makeText(
                                        requireContext(),
                                        "Error al obtener el animal completo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }

                    recyclerAdopcion.adapter = adapter

                }
            }
        }
    }

    private fun showSettingsMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_refugio_settings, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_profile -> {
                    val fragment = FragmentEdit_Protectora()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.exit_account -> {
                    mostrarDialogoCerrarSesion()
                    true
                }

                R.id.exit_app -> {
                    requireActivity().finishAffinity()
                    exitProcess(0)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun mostrarDialogoCerrarSesion() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val titulo = dialogView.findViewById<TextView>(R.id.textDialogTitle)
        val mensaje = dialogView.findViewById<TextView>(R.id.textDialogMessage)
        val cancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val confirmar = dialogView.findViewById<Button>(R.id.btnConfirmar)

        titulo.text = "¿Cerrar sesión?"
        mensaje.text = "¿Estás seguro de que deseas cerrar sesión? Podrás volver a iniciar sesión más tarde."
        confirmar.text = "Confirmar"

        cancelar.setOnClickListener {
            dialog.dismiss()
        }

        confirmar.setOnClickListener {
            dialog.dismiss()
            cerrarSesion()
        }

        dialog.show()
    }

    private fun cerrarSesion() {
        val sharedPref = requireContext().getSharedPreferences("usuarioPrefs", AppCompatActivity.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        val intent = Intent(requireContext(), ActivityLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        requireActivity().finish()
    }

}
