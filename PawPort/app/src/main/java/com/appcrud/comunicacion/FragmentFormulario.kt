package com.appcrud.comunicacion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

class FragmentFormulario : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.activity_formularioo, container, false)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val editNombre = view.findViewById<EditText>(R.id.editNombre)
        val editCapacidad = view.findViewById<EditText>(R.id.editCapacidad)
        val editCalle = view.findViewById<EditText>(R.id.editCalle)
        val editNumero = view.findViewById<EditText>(R.id.editNumero)
        val editCP = view.findViewById<EditText>(R.id.editCP)
        val editProvincia = view.findViewById<EditText>(R.id.editProvincia)
        val editComunidad = view.findViewById<EditText>(R.id.editComunidad)
        val editEmail = view.findViewById<EditText>(R.id.editEmail)
        val editTelefono = view.findViewById<EditText>(R.id.editTelefono)
        val btnConfirmar = view.findViewById<Button>(R.id.btnGuardarCambios)

        btnConfirmar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val capacidad = editCapacidad.text.toString().trim()
            val calle = editCalle.text.toString().trim()
            val numero = editNumero.text.toString().trim()
            val cp = editCP.text.toString().trim()
            val provincia = editProvincia.text.toString().trim()
            val comunidad = editComunidad.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val telefono = editTelefono.text.toString().trim()

            if (nombre.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena al menos nombre y email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cuerpo = """
                Hola,

                Me gustaría registrar mi protectora en la app. Aquí tienes mis datos:

                • Nombre del responsable: $nombre
                • Email: $email
                • Teléfono: $telefono
                • Nombre de la protectora: $nombre
                • Capacidad: $capacidad animales
                • Dirección: $calle, Nº $numero, CP $cp
                • Provincia: $provincia
                • Comunidad Autónoma: $comunidad

                Gracias.
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("pawport.contacto@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Solicitud de alta de protectora")
                putExtra(Intent.EXTRA_TEXT, cuerpo)
            }

            startActivity(Intent.createChooser(intent, "Enviar solicitud con:"))
        }

        return view
    }
}
