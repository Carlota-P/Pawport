package com.appcrud.comunicacion.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.appcrud.comunicacion.R

class BusquedaAvanzadaAnimalesDialog(
    private val onFiltrar: (
        especie: String,
        sexo: String,
        edadMax: Int?,
        vacunado: Boolean,
        castrado: Boolean,
        pasaporte: Boolean,
        microchip: Boolean
    ) -> Unit

) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_busqueda_avanzada, null)

        val spinnerEspecie = view.findViewById<Spinner>(R.id.spinnerEspecie)
        val spinnerSexo = view.findViewById<Spinner>(R.id.spinnerSexo)
        val editEdad = view.findViewById<EditText>(R.id.editEdad)
        val edadFormateada = view.findViewById<TextView>(R.id.edadFormateada)
        val checkVacunado = view.findViewById<CheckBox>(R.id.checkVacunado)
        val checkCastrado = view.findViewById<CheckBox>(R.id.checkCastrado)
        val checkPasaporte = view.findViewById<CheckBox>(R.id.checkPasaporte)
        val checkMicrochip = view.findViewById<CheckBox>(R.id.checkMicrochip)
        val btnBuscar = view.findViewById<Button>(R.id.btnBuscar)

        // Adaptadores
        val adapterEspecie = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Otro", "Perro", "Gato")
        )
        spinnerEspecie.adapter = adapterEspecie

        val adapterSexo = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Cualquiera", "Macho", "Hembra")
        )
        spinnerSexo.adapter = adapterSexo

        // Texto dinámico para años
        editEdad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val edad = s.toString().toIntOrNull()
                edadFormateada.text = if (edad != null) "$edad años máximo" else ""
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Acción del botón
        btnBuscar.setOnClickListener {
            val especie = spinnerEspecie.selectedItem.toString()
            val sexo = spinnerSexo.selectedItem.toString()
            val edad = editEdad.text.toString().toIntOrNull()
            val vacunado = checkVacunado.isChecked
            val castrado = checkCastrado.isChecked
            val pasaporte = checkPasaporte.isChecked
            val microchip = checkMicrochip.isChecked

            onFiltrar(especie, sexo, edad, vacunado, castrado, pasaporte, microchip)
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}
