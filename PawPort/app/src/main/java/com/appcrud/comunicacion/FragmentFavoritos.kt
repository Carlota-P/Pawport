package com.appcrud.comunicacion

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentFavoritos : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_favoritoss, container, false)

        recyclerView = view.findViewById(R.id.recyclerFavoritos)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        val sharedPref = requireActivity().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("usuarioId", -1)

        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.OBTENER_FAVORITOS_DE_USUARIO).apply {
                idUsuario = userId
            }

            try {
                val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                val lista = respuesta?.animales ?: emptyList()

                withContext(Dispatchers.Main) {
                    Log.d("Favoritos", "Animales recibidos: ${lista.size}")
                    if (lista.isNotEmpty()) {
                        recyclerView.adapter = FavoritosAdapter(lista) { animal ->
                            val bundle = Bundle().apply {
                                putSerializable("animal", animal)
                            }
                            val detalleFragment = FragmentAnimal_detail().apply { arguments = bundle }
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, detalleFragment)
                                .addToBackStack(null)
                                .commit()
                        }


                    } else {
                        Toast.makeText(requireContext(), "No tienes favoritos guardados.", Toast.LENGTH_SHORT).show()
                    }
                }

                Log.d("FragmentFavoritos", "Lista recibida: ${lista.size}")
                Log.d("Favoritos", "Animales recibidos: ${lista.size}")
                lista.forEach {
                    Log.d("Favoritos", "Nombre: ${it.nombreAnimal}")
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al cargar favoritos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

}
