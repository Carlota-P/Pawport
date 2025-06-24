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

class FragmentEdit_Protectora : Fragment() {

    private lateinit var editNombre: EditText
    private lateinit var editCapacidad: EditText
    private lateinit var editTelefono: EditText
    private lateinit var editCalle: EditText
    private lateinit var editNumero: EditText
    private lateinit var editCP: EditText
    private lateinit var editProvincia: EditText
    private lateinit var editComunidad: EditText
    private lateinit var editDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var backButton: ImageButton
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_edit__protectora, container, false)

        // Enlazar vistas
        editNombre = rootView.findViewById(R.id.editNombreProtectora)
        editCapacidad = rootView.findViewById(R.id.editCapacidadProtectora)
        editTelefono = rootView.findViewById(R.id.editTelefono)
        editCalle = rootView.findViewById(R.id.editCalle)
        editNumero = rootView.findViewById(R.id.editNumero)
        editCP = rootView.findViewById(R.id.editCP)
        editProvincia = rootView.findViewById(R.id.editProvincia)
        editComunidad = rootView.findViewById(R.id.editComunidad)
        editDescripcion = rootView.findViewById(R.id.historiaEditText)
        btnGuardar = rootView.findViewById(R.id.btnGuardarCambios)
        backButton = rootView.findViewById(R.id.backButton)

        val prefs = requireActivity().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)

        val idProtectora = prefs.getInt("usuarioId", 0)
        val email = prefs.getString("email", "") ?: ""
        val contrasena = prefs.getString("contrasena", "") ?: ""

        // Cargar datos actuales
        editNombre.setText(prefs.getString("nombre", ""))
        editTelefono.setText(prefs.getInt("telefono", 0).toString())
        editDescripcion.setText(prefs.getString("historia", ""))
        editCapacidad.setText(prefs.getInt("capacidad", 0).toString())
        editCalle.setText(prefs.getString("calle", ""))
        editNumero.setText(prefs.getInt("numeroCalle", 0).toString())
        editCP.setText(prefs.getInt("codPostal", 0).toString())
        editProvincia.setText(prefs.getString("provincia", ""))
        editComunidad.setText(prefs.getString("comunidadAutonoma", ""))

        // Botón guardar
        btnGuardar.setOnClickListener {
            val protectora = Protectora(
                idProtectora,
                editCapacidad.text.toString().toIntOrNull() ?: 0,
                editNombre.text.toString(),
                editCalle.text.toString(),
                editNumero.text.toString().toIntOrNull() ?: 0,
                editCP.text.toString().toIntOrNull() ?: 0,
                email,
                editTelefono.text.toString().toIntOrNull() ?: 0,
                editDescripcion.text.toString(),
                null, // ← foto no editable desde aquí
                editProvincia.text.toString(),
                editComunidad.text.toString(),
                contrasena
            )

            Log.d("ProtectoraDebug", protectora.toString())

            val peticion = Peticion(Peticion.TipoOperacion.UPDATE_PROTECTORA).apply {
                this.protectora = protectora
                this.idProtectora = protectora.getProtectoraId()
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                    withContext(Dispatchers.Main) {
                        if (respuesta?.isExito == true) {
                            with(prefs.edit()) {
                                putString("nombre", protectora.getNombre())
                                putInt("telefono", protectora.getTelefono())
                                putString("historia", protectora.getHistoria())
                                putInt("capacidad", protectora.getCapacidad())
                                putString("calle", protectora.getCalle())
                                putInt("numeroCalle", protectora.getNumeroCalle())
                                putInt("codPostal", protectora.getCodPostal())
                                putString("provincia", protectora.getProvincia())
                                putString("comunidadAutonoma", protectora.getComunidadAutonoma())
                                apply()
                            }

                            Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("EditProtectora", "Error al enviar petición", e)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Botón volver
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Ajuste de padding
        ViewCompat.setOnApplyWindowInsetsListener(rootView.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom + 40)
            insets
        }

        return rootView
    }
}

