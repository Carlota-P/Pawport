package com.appcrud.comunicacion

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class FragmentCreateAnimal : Fragment() {

    private var animal: Animal? = null
    private lateinit var btnSeleccionarImagen: ImageButton
    private var rutaImagenSeleccionada: String? = null
    private val PICK_IMAGE_REQUEST = 1

    private lateinit var nombreEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var historiaEditText: EditText
    private lateinit var razaEditText: EditText
    private lateinit var identificadorEditText: EditText
    private lateinit var checkVacuna: CheckBox
    private lateinit var checkPasaporte: CheckBox
    private lateinit var checkCastrado: CheckBox
    private lateinit var radioMacho: RadioButton
    private lateinit var radioHembra: RadioButton
    private lateinit var radioPerro: RadioButton
    private lateinit var radioGato: RadioButton
    private lateinit var radioOtro: RadioButton
    private lateinit var btnConfirmar: Button
    private lateinit var btnEliminar: Button
    private var fechaNacimiento: Date? = null
    private var uriImagenSeleccionada: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_animal, container, false)

        animal = arguments?.getSerializable("animal") as? Animal
        val prefs = requireContext().getSharedPreferences("usuarioPrefs", Context.MODE_PRIVATE)
        val idProtectora = prefs.getInt("usuarioId", -1)

        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen)
        nombreEditText = view.findViewById(R.id.editTextNombre)
        fechaNacimientoEditText = view.findViewById(R.id.editTextFechaNacimiento)
        historiaEditText = view.findViewById(R.id.historiaTxt)
        razaEditText = view.findViewById(R.id.editTextRaza)
        identificadorEditText = view.findViewById(R.id.editTextIdentificador)
        checkVacuna = view.findViewById(R.id.checkVacuna)
        checkPasaporte = view.findViewById(R.id.checkPasaporte)
        checkCastrado = view.findViewById(R.id.checkCastrado)
        radioMacho = view.findViewById(R.id.radioMacho)
        radioHembra = view.findViewById(R.id.radioHembra)
        radioPerro = view.findViewById(R.id.radioPerro)
        radioGato = view.findViewById(R.id.radioGato)
        radioOtro = view.findViewById(R.id.radioOtro)
        btnConfirmar = view.findViewById(R.id.btnConfirmar)
        btnEliminar = view.findViewById(R.id.btnEliminar)

        fechaNacimientoEditText.inputType = 0
        fechaNacimientoEditText.setOnClickListener { mostrarDatePicker() }
        fechaNacimientoEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) mostrarDatePicker()
        }

        animal?.let {
            it.foto?.let { nombreArchivo ->
                val url = "http://192.168.1.215:8080/fotos/$nombreArchivo"
                Glide.with(this).load(url).into(btnSeleccionarImagen)
            }
            nombreEditText.setText(it.nombreAnimal)
            razaEditText.setText(it.raza)
            historiaEditText.setText(it.historia)
            identificadorEditText.setText(it.identificador)
            fechaNacimiento = it.fechaNacimiento
            fechaNacimientoEditText.setText(
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.fechaNacimiento)
            )
            checkVacuna.isChecked = it.vacuna == "S"
            checkPasaporte.isChecked = it.pasaporte == "S"
            checkCastrado.isChecked = it.castrado == "S"
            when (it.sexo) {
                "M" -> radioMacho.isChecked = true
                "F" -> radioHembra.isChecked = true
            }
            when (it.animalTipo) {
                "P" -> radioPerro.isChecked = true
                "G" -> radioGato.isChecked = true
                "O" -> radioOtro.isChecked = true
            }
        } ?: run {
            btnEliminar.visibility = View.GONE
        }

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnConfirmar.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val raza = razaEditText.text.toString().trim()
            val historia = historiaEditText.text.toString().trim()
            val identificador = identificadorEditText.text.toString().trim()

            if (nombre.isEmpty() || fechaNacimiento == null) {
                Toast.makeText(requireContext(), "Faltan datos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val limiteAntiguedad = Calendar.getInstance().apply {
                set(1990, Calendar.JANUARY, 1)
            }.time
            if (fechaNacimiento!!.before(limiteAntiguedad)) {
                Toast.makeText(requireContext(), "La fecha es demasiado antigua", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sexo = when {
                radioMacho.isChecked -> "M"
                radioHembra.isChecked -> "F"
                else -> {
                    Toast.makeText(requireContext(), "Seleccione el sexo", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val tipoAnimal = when {
                radioPerro.isChecked -> "P"
                radioGato.isChecked -> "G"
                radioOtro.isChecked -> "O"
                else -> {
                    Toast.makeText(requireContext(), "Seleccione el tipo de animal", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val nuevoAnimal = Animal().apply {
                if (animal != null) {
                    animalId = animal?.animalId
                    protectora = animal?.protectora
                } else {
                    protectora = Protectora().apply { protectoraId = idProtectora }
                }
                nombreAnimal = nombre
                this.raza = raza
                this.fechaNacimiento = this@FragmentCreateAnimal.fechaNacimiento
                this.historia = historia
                vacuna = if (checkVacuna.isChecked) "S" else "N"
                pasaporte = if (checkPasaporte.isChecked) "S" else "N"
                castrado = if (checkCastrado.isChecked) "S" else "N"
                this.identificador = identificador
                this.sexo = sexo
                animalTipo = tipoAnimal
                this.foto = rutaImagenSeleccionada ?: animal?.foto
            }

            val tipoOperacion = if (animal == null)
                Peticion.TipoOperacion.CREATE_ANIMAL
            else
                Peticion.TipoOperacion.UPDATE_ANIMAL


            CoroutineScope(Dispatchers.IO).launch {
                if (animal == null && uriImagenSeleccionada != null) {
                    val subidaExitosa = subirImagen(uriImagenSeleccionada!!)
                    if (!subidaExitosa) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }
                }

                val peticion = Peticion(tipoOperacion).apply {
                    this.animal = nuevoAnimal
                    if (tipoOperacion == Peticion.TipoOperacion.UPDATE_ANIMAL && nuevoAnimal.animalId != null) {
                        this.idAnimal = nuevoAnimal.animalId
                    }
                }

                val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                withContext(Dispatchers.Main) {
                    if (respuesta?.isExito == true) {
                        val resultBundle = Bundle().apply {
                            putBoolean("animal_actualizado", true)
                        }

                        val homeFragment = FragmentHome()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, homeFragment)
                            .commit()

                        val mensaje = if (animal == null) "Animal creado correctamente" else "Animal actualizado correctamente"
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnEliminar.setOnClickListener {
            animal?.let {
                mostrarDialogoEliminarAnimal(it)
            }
        }


        Log.d("DEBUG", "Imagen subida: $rutaImagenSeleccionada")
        val urlTest = "http://192.168.1.215:8080/fotos/$rutaImagenSeleccionada"
        Log.d("DEBUG", "Probando acceso: $urlTest")

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            uriImagenSeleccionada = uri

            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                    if (nameIndex != -1 && sizeIndex != -1) {
                        var fileName = it.getString(nameIndex)
                        val fileSize = it.getLong(sizeIndex)

                        Log.d("ImagenRuta", "📦 Nombre: $fileName | Tamaño: $fileSize bytes")

                        if (fileSize > 5_242_880) { // 5MB
                            Toast.makeText(requireContext(), "La imagen supera los 5 MB", Toast.LENGTH_LONG).show()
                            rutaImagenSeleccionada = null
                            uriImagenSeleccionada = null
                            btnSeleccionarImagen.setImageResource(R.drawable.ic_image)
                            return
                        }

                        if (!fileName.contains(".")) {
                            val mimeType = requireContext().contentResolver.getType(uri)
                            val extension = when (mimeType) {
                                "image/jpeg" -> "jpg"
                                "image/png" -> "png"
                                "image/webp" -> "webp"
                                else -> "jpg"
                            }
                            fileName += ".$extension"
                        }

                        rutaImagenSeleccionada = fileName
                        Log.d("ImagenRuta", "✅ Nombre imagen final: $fileName")
                    }
                }
            }

            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                btnSeleccionarImagen.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e("ImagenRuta", "❌ Error al cargar preview", e)
                Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                rutaImagenSeleccionada = null
                uriImagenSeleccionada = null
            }
        }
    }


    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val fechaSeleccionada = Calendar.getInstance()
                fechaSeleccionada.set(year, month, dayOfMonth)
                fechaNacimiento = fechaSeleccionada.time
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaNacimientoEditText.setText(formato.format(fechaNacimiento!!))
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun mostrarDialogoEliminarAnimal(animal: Animal) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_delete, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val titulo = dialogView.findViewById<TextView>(R.id.textDialogTitle)
        val mensaje = dialogView.findViewById<TextView>(R.id.textDialogMessage)
        val cancelar = dialogView.findViewById<Button>(R.id.btnCancelar)
        val confirmar = dialogView.findViewById<Button>(R.id.btnConfirmar)

        titulo.text = "¿Eliminar animal?"
        mensaje.text = "¿Estás seguro de que deseas eliminar a \"${animal.nombreAnimal}\"? Esta acción no se puede deshacer."

        cancelar.setOnClickListener {
            dialog.dismiss()
        }

        confirmar.setOnClickListener {
            dialog.dismiss()
            eliminarAnimal(animal)
        }

        dialog.show()
    }

    private fun eliminarAnimal(animal: Animal) {
        CoroutineScope(Dispatchers.IO).launch {
            val peticion = Peticion(Peticion.TipoOperacion.DELETE_ANIMAL).apply {
                idAnimal = animal.animalId
            }

            val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)

            withContext(Dispatchers.Main) {
                if (respuesta?.isExito == true) {
                    Toast.makeText(requireContext(), "Animal eliminado correctamente", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al eliminar el animal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun subirImagen(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes() ?: return@withContext false
                val fileName = rutaImagenSeleccionada ?: return@withContext false

                val connection = URL("http://192.168.1.215:8080/upload").openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                connection.setRequestProperty("File-Name", fileName)

                connection.outputStream.use { it.write(bytes) }

                val responseCode = connection.responseCode
                responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                Log.e("SubidaImagen", "Error al subir imagen", e)
                false
            }
        }
    }

}

