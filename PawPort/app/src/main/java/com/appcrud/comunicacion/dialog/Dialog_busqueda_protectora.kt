package com.appcrud.comunicacion.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.appcrud.comunicacion.R

class Dialog_busqueda_protectora(
    private val onBuscar: (String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_busqueda_protectora, null)

        val editNombre = view.findViewById<EditText>(R.id.editNombreProtectora)
        val btnBuscar = view.findViewById<Button>(R.id.btnBuscar)

        btnBuscar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            onBuscar(nombre)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}

