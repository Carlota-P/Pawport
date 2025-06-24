package com.appcrud.comunicacion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentEditProfile : Fragment() {
    private lateinit var editNombre: EditText
    private lateinit var editApellido1: EditText
    private lateinit var editApellido2: EditText
    private lateinit var editDni: EditText
    private lateinit var editTelefono: EditText
    private lateinit var editAlias: EditText
    private lateinit var btnGuardar: Button
    private lateinit var backButton: ImageButton
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Vincular vistas
        editNombre = rootView.findViewById(R.id.editNombre)
        editApellido1 = rootView.findViewById(R.id.editApellido1)
        editApellido2 = rootView.findViewById(R.id.editApellido2)
        editDni = rootView.findViewById(R.id.editDni)
        editTelefono = rootView.findViewById(R.id.editTelefono)
        editAlias = rootView.findViewById(R.id.editAlias)
        btnGuardar = rootView.findViewById(R.id.btnGuardarCambios)
        backButton = rootView.findViewById(R.id.backButton)

        val prefs = requireActivity().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)

        editNombre.setText(prefs.getString("nombre", ""))
        editApellido1.setText(prefs.getString("apellido1", ""))
        editApellido2.setText(prefs.getString("apellido2", ""))
        editDni.setText(prefs.getString("dni", ""))
        editTelefono.setText(prefs.getInt("telefono", 0).toString())
        editAlias.setText(prefs.getString("alias", ""))

        val email = prefs.getString("email", "") ?: ""
        val rol = prefs.getString("rol", "usuario") ?: "usuario"
        val userId = prefs.getInt("usuarioId", 0)

        // Guardar cambios
        btnGuardar.setOnClickListener {
            Log.d("DEBUG", "Se pulsó el botón guardar")
            val usuario = Usuario(
                userId,
                editDni.text.toString(),
                editAlias.text.toString(),
                editTelefono.text.toString().toIntOrNull() ?: 0,
                email,
                editNombre.text.toString(),
                editApellido1.text.toString(),
                editApellido2.text.toString()
            )

            val peticion = Peticion(
                Peticion.TipoOperacion.UPDATE_USUARIO,
                usuario,
                userId
            )

            CoroutineScope(Dispatchers.IO).launch {
                Log.d("DEBUG", "Enviando petición de $peticion al servidor...")
                try {
                    val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

                    withContext(Dispatchers.Main) {
                        if (respuesta?.isExito == true) {
                            with(prefs.edit()) {
                                putString("nombre", editNombre.text.toString())
                                putString("apellido1", editApellido1.text.toString())
                                putString("apellido2", editApellido2.text.toString())
                                putString("dni", editDni.text.toString())
                                putInt("telefono", usuario.telefono)
                                putString("alias", editAlias.text.toString())
                                apply()
                            }
                            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()

                            Log.d("DEBUG", "Petición creada: $peticion")
                            Log.d("DEBUG", "Usuario: ${usuario.usuarioId} - ${usuario.apellido1}")

                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditProfile", "Error al enviar petición", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }

            }
            Log.d("EditProfile", "Botón guardar pulsado, usuario: ${usuario.usuarioId}")
        }

        // Botón de volver
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Ajustes para evitar solape con la navegación inferior
        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom + 40)
            insets
        }

        return rootView
    }

}
