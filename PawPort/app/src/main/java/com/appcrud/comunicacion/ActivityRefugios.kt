package com.appcrud.comunicacion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appcrud.comunicacion.dialog.Dialog_busqueda_protectora
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActivityRefugios : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var refugioAdapter: RefugioAdapter
    private var listaOriginalProtectora: List<Protectora> = emptyList()

    // Filtro persistente
    private var filtroNombreProtectora: String? = null
    private var filtroActivo = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_refugioss, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewRefugios)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val botonBuscar = view.findViewById<View>(R.id.searchButton)
        botonBuscar.setOnClickListener {
            val dialog = Dialog_busqueda_protectora { nombreBuscado ->
                filtroNombreProtectora = nombreBuscado
                filtroActivo = true
                aplicarFiltroProtectora(nombreBuscado)
            }
            dialog.show(parentFragmentManager, "DialogBuscarProtectora")
        }

        cargarRefugios()
        return view
    }

    override fun onResume() {
        super.onResume()
        if (filtroActivo && listaOriginalProtectora.isNotEmpty()) {
            aplicarFiltroProtectora(filtroNombreProtectora ?: "")
        }
    }

    private fun cargarRefugios() {
        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.READ_ALL_PROTECTORA)
            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

            withContext(Dispatchers.Main) {
                if (respuesta?.isExito == true) {
                    listaOriginalProtectora = respuesta.protectoras ?: emptyList()

                    if (filtroActivo && !filtroNombreProtectora.isNullOrEmpty()) {
                        aplicarFiltroProtectora(filtroNombreProtectora!!)
                    } else {
                        refugioAdapter = RefugioAdapter(listaOriginalProtectora)
                        recyclerView.adapter = refugioAdapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al cargar refugios", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun aplicarFiltroProtectora(nombre: String) {
        val listaFiltrada = listaOriginalProtectora.filter {
            it.nombre?.contains(nombre, ignoreCase = true) == true
        }
        refugioAdapter = RefugioAdapter(listaFiltrada)
        recyclerView.adapter = refugioAdapter
    }
}
