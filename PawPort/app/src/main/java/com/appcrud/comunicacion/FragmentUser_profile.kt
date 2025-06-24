package com.appcrud.comunicacion

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess


class FragmentUser_profile : Fragment() {

    private lateinit var settingsButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        val arrowIcon = view.findViewById<ImageView>(R.id.ic_arrowIcon)
        val arrowIcon2 = view.findViewById<ImageView>(R.id.ic_arrowIcon2)

        val sharedPref = requireActivity().getSharedPreferences("usuarioPrefs", 0)

        val alias = sharedPref.getString("alias", "")
        val nombre = sharedPref.getString("nombre", "")
        val apellido1 = sharedPref.getString("apellido1", "")
        val apellido2 = sharedPref.getString("apellido2", "")
        val nombreCompleto = "$nombre $apellido1 $apellido2"

        val tvAlias = view.findViewById<TextView>(R.id.tvAlias)
        val tvNombreCompleto = view.findViewById<TextView>(R.id.tvNombreCompleto)

        tvAlias.text = alias
        tvNombreCompleto.text = nombreCompleto

        settingsButton = view.findViewById(R.id.imageButtonSettings)
        settingsButton.setOnClickListener {
            showSettingsMenu(it)
        }

        val fyqBox = view.findViewById<LinearLayout>(R.id.fyqBox)
        val fyqContent = view.findViewById<LinearLayout>(R.id.fyqContent)
        val favoritosBox = view.findViewById<LinearLayout>(R.id.favoritosBox)

        val ayudaBox = view.findViewById<LinearLayout>(R.id.ayudaBox)
        val ayudaContent = view.findViewById<LinearLayout>(R.id.ayudaContent)
        var isExpanded = false

        // Mostrar/Ocultar FYQ
        fyqBox.setOnClickListener {
            val rotationAngle = if (isExpanded) 0f else 90f  // o 180f si prefieres hacia abajo
            arrowIcon.animate().rotation(rotationAngle).setDuration(200).start()
            isExpanded = !isExpanded

            fyqContent.visibility = if (fyqContent.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        // Mostrar/Ocultar Ayuda
        ayudaBox.setOnClickListener {
            val rotationAngle = if (isExpanded) 0f else 90f  // o 180f si prefieres hacia abajo
            arrowIcon2.animate().rotation(rotationAngle).setDuration(200).start()
            isExpanded = !isExpanded

            ayudaContent.visibility = if (ayudaContent.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        favoritosBox.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentFavoritos())
                .addToBackStack(null)
                .commit()
        }


        return view
    }

    private fun showSettingsMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_user_settings, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit_profile -> {
                    val fragment = FragmentEditProfile()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_delete_account -> {
                    val prefs = requireContext().getSharedPreferences("usuarioPrefs", AppCompatActivity.MODE_PRIVATE)
                    val usuarioId = prefs.getInt("usuarioId", -1)
                    if (usuarioId != -1) {
                        mostrarDialogoEliminarUsuario(usuarioId)
                    }
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

                R.id.formulario_refugio -> {
                    val fragment = FragmentFormulario()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, FragmentFormulario())
                        .addToBackStack(null)
                        .commit()

                    true
                }


                else -> false
            }
        }
        popupMenu.show()

    }

    private fun mostrarDialogoEliminarUsuario(usuarioId: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val titulo = dialogView.findViewById<TextView>(R.id.textDialogTitle)
        val mensaje = dialogView.findViewById<TextView>(R.id.textDialogMessage)
        val cancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val confirmar = dialogView.findViewById<Button>(R.id.btnConfirmar)

        titulo.text = "¿Eliminar cuenta?"
        mensaje.text = "¿Estás seguro de que deseas eliminar tu cuenta? Todos tus datos perderán."

        cancelar.setOnClickListener {
            dialog.dismiss()
        }

        confirmar.setOnClickListener {
            dialog.dismiss()
            eliminarUsuario(usuarioId)
        }

        dialog.show()
    }

    private fun eliminarUsuario(usuarioId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.DELETE_USUARIO).apply {
                idUsuario = usuarioId
            }

            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

            withContext(Dispatchers.Main) {
                if (respuesta?.isExito == true) {
                    Toast.makeText(requireContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()

                    // Cerrar sesión y volver al login
                    val prefs = requireContext().getSharedPreferences("usuarioPrefs", AppCompatActivity.MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    val intent = Intent(requireContext(), ActivityLogin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                }
            }
        }
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