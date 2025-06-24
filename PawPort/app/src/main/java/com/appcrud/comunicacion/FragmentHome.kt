package com.appcrud.comunicacion

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.appcrud.comunicacion.dialog.BusquedaAvanzadaAnimalesDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FragmentHome : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var listaOriginalAnimales: List<Animal> = emptyList()
    private var userId: Int = -1
    private var rol: String? = null

    // Filtros activos
    private var filtroEspecie: String? = null
    private var filtroSexo: String? = null
    private var filtroEdad: Int? = null
    private var filtroVacunado: Boolean = false
    private var filtroCastrado: Boolean = false
    private var filtroPasaporte: Boolean = false
    private var filtrosActivos = false
    private var filtroMicrochip: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.animalGrid)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.setHasFixedSize(true)

        view.findViewById<View>(R.id.searchButton).setOnClickListener {
            val dialog = BusquedaAvanzadaAnimalesDialog { especie, sexo, edad, vacunado, castrado, pasaporte, microchip ->
                filtroEspecie = especie
                filtroSexo = sexo
                filtroEdad = edad
                filtroVacunado = vacunado
                filtroCastrado = castrado
                filtroPasaporte = pasaporte
                filtroMicrochip = microchip
                filtrosActivos = true
                aplicarFiltroActual()
            }
            dialog.show(parentFragmentManager, "BusquedaAvanzada")
        }

        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_light,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light
        )

        swipeRefreshLayout.setOnRefreshListener {
            cargarAnimales { swipeRefreshLayout.isRefreshing = false }
        }

        parentFragmentManager.setFragmentResultListener("animalUpdateKey", viewLifecycleOwner) { _, bundle ->
            if (bundle.getBoolean("animal_actualizado", false)) {
                swipeRefreshLayout.isRefreshing = true
                cargarAnimales { swipeRefreshLayout.isRefreshing = false }
            }
        }
        swipeRefreshLayout.isRefreshing = true
        cargarAnimales { swipeRefreshLayout.isRefreshing = false }
    }

    private fun cargarAnimales(onFinish: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val peticion = Peticion(Peticion.TipoOperacion.READ_ALL_ANIMAL)
                val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

                val sharedPrefs = activity?.getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
                userId = sharedPrefs?.getInt("usuarioId", -1) ?: -1
                rol = sharedPrefs?.getString("rol", null)

                val respuestaFavs = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(
                    Peticion(Peticion.TipoOperacion.OBTENER_FAVORITOS_DE_USUARIO).apply {
                        idUsuario = userId
                    }
                )

                val idsFavoritos = respuestaFavs?.animales?.map { it.animalId }?.toSet() ?: emptySet()

                withContext(Dispatchers.Main) {
                    if (!isAdded || context == null) return@withContext
                    onFinish?.invoke()

                    if (respuesta == null) {
                        Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
                        return@withContext
                    }

                    if (respuesta.isExito) {
                        listaOriginalAnimales = respuesta.animales ?: emptyList()
                        listaOriginalAnimales.forEach {
                            it.setEsFavorito(it.animalId in idsFavoritos)
                        }

                        if (filtrosActivos) {
                            aplicarFiltroActual()
                        } else {
                            crearAdaptador(listaOriginalAnimales)
                        }
                    } else {
                        Toast.makeText(requireContext(), "No hay animales para mostrar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("FragmentHome", "Excepcion al cargar animales: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onFinish?.invoke()
                    if (!isAdded || context == null) return@withContext
                    Toast.makeText(requireContext(), "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun aplicarFiltroActual() {
        val especieCodigo = when (filtroEspecie) {
            "Perro" -> "P"
            "Gato" -> "G"
            "Otro" -> "O"
            else -> null
        }

        val sexoCodigo = when (filtroSexo) {
            "Macho" -> "M"
            "Hembra" -> "F"
            else -> null
        }

        val listaFiltrada = listaOriginalAnimales.filter { animal ->
            val tipoOK = especieCodigo == null || animal.animalTipo?.trim()?.equals(especieCodigo, ignoreCase = true) == true
            val sexoOK = sexoCodigo == null || animal.sexo?.trim()?.equals(sexoCodigo, ignoreCase = true) == true

            val edadTexto = calcularEdadEnAnios(animal.fechaNacimiento?.toString() ?: "")
            val edadEnAnios = if (edadTexto.contains("meses")) 0 else edadTexto.filter { it.isDigit() }.toIntOrNull() ?: 0
            val edadOK = filtroEdad == null || edadEnAnios <= filtroEdad!!

            val vacunaOK = !filtroVacunado || animal.vacuna?.equals("S", ignoreCase = true) == true
            val castradoOK = !filtroCastrado || animal.castrado?.equals("S", ignoreCase = true) == true
            val pasaporteOK = !filtroPasaporte || animal.pasaporte?.equals("S", ignoreCase = true) == true
            val microchipOK = !filtroMicrochip || !animal.identificador.isNullOrBlank()

            tipoOK && sexoOK && edadOK && vacunaOK && castradoOK && pasaporteOK && microchipOK
        }

        Log.d("Filtro", "Filtro aplicado, resultados: ${listaFiltrada.size}")
        crearAdaptador(listaFiltrada)
    }

    private fun crearAdaptador(animales: List<Animal>) {
        recyclerView.adapter = AnimalAdapter(animales, rol) { animalSeleccionado ->
            abrirDetalleAnimal(animalSeleccionado)
        }
    }

    private fun abrirDetalleAnimal(animalSeleccionado: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
            val peticionDetalle = Peticion(Peticion.TipoOperacion.READ_ANIMAL).apply {
                idAnimal = animalSeleccionado.animalId
                idUsuario = userId
            }

            val respuestaDetalle = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticionDetalle)
            val animalCompleto = respuestaDetalle?.animal

            withContext(Dispatchers.Main) {
                if (animalCompleto != null) {
                    val bundle = Bundle().apply { putSerializable("animal", animalCompleto) }
                    val detalleFragment = FragmentAnimal_detail().apply { arguments = bundle }
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

    private fun calcularEdadEnAnios(fechaNacimiento: String): String {
        return try {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val nacimiento = formato.parse(fechaNacimiento) ?: return "Edad desconocida"
            val hoy = Calendar.getInstance()
            val fechaNac = Calendar.getInstance().apply { time = nacimiento }

            val años = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR)
            val meses = hoy.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH)
            val dias = hoy.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH)

            var edadMeses = años * 12 + meses
            if (dias < 0) edadMeses -= 1

            return if (edadMeses < 12) "$edadMeses meses" else {
                val edadAnios = edadMeses / 12
                "$edadAnios ${if (edadAnios == 1) "año" else "años"}"
            }
        } catch (e: Exception) {
            "Edad desconocida"
        }
    }
}
