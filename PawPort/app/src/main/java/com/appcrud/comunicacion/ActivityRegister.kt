package com.appcrud.comunicacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ActivityRegister : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        registerButton.setOnClickListener {

            Log.d("Register", "Botón pulsado")

            val dniNumero = findViewById<EditText>(R.id.dniNumeroEditText).text.toString().trim()
            val dniLetra = findViewById<EditText>(R.id.dniLetraEditText).text.toString().trim().uppercase()
            val dni = "$dniNumero$dniLetra"
            val alias = findViewById<EditText>(R.id.aliasEditText).text.toString().trim()
            val telefono = findViewById<EditText>(R.id.telefonoEditText).text.toString().trim()
            val email = findViewById<EditText>(R.id.emailEditText).text.toString().trim()
            val nombre = findViewById<EditText>(R.id.nombreEditText).text.toString().trim()
            val apellido1 = findViewById<EditText>(R.id.apellido1EditText).text.toString().trim()
            val apellido2 = findViewById<EditText>(R.id.apellido2EditText).text.toString().trim()
            val contrasena = findViewById<EditText>(R.id.passwordEditText).text.toString().trim()

            when {
                dni.isEmpty() -> {
                    Toast.makeText(this, "El campo DNI es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                alias.isEmpty() -> {
                    Toast.makeText(this, "El campo Alias es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                telefono.isEmpty() -> {
                    Toast.makeText(this, "El campo Teléfono es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    Toast.makeText(this, "El campo Email es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                nombre.isEmpty() -> {
                    Toast.makeText(this, "El campo Nombre es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                apellido1.isEmpty() -> {
                    Toast.makeText(this, "El campo Apellido 1 es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                contrasena.isEmpty() -> {
                    Toast.makeText(this, "El campo Contraseña es obligatorio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Formato de email no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Regex("^\\d{9}$").matches(telefono)) {
                Toast.makeText(this, "Teléfono inválido (deben ser 9 dígitos)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val telefonoInt = telefono.toIntOrNull() ?: 0

            val nuevoUsuario = Usuario(
                0,
                dni,
                alias,
                telefonoInt,
                email,
                nombre,
                apellido1,
                apellido2,
                contrasena
            )

            val peticion = Peticion(Peticion.TipoOperacion.CREATE_USUARIO).apply {
                this.usuario = nuevoUsuario
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                    withContext(Dispatchers.Main) {
                        if (respuesta?.isExito == true) {
                            Toast.makeText(this@ActivityRegister,  "Usuario creado con éxito", Toast.LENGTH_SHORT).show()

                            val sharedPref = getSharedPreferences("usuarioPrefs", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("alias", respuesta.usuario?.alias)
                                putString("nombre", respuesta.usuario?.nombre)
                                putString("apellido1", respuesta.usuario?.apellido1)
                                putString("apellido2", respuesta.usuario?.apellido2)
                                putString("email", respuesta.usuario?.email)
                                putString("dni", respuesta.usuario?.dni)
                                putInt("telefono", respuesta.usuario?.telefono ?: 0)
                                apply()
                            }
                            val intent = Intent(this@ActivityRegister, ActivityLogin::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ActivityRegister, respuesta?.mensaje ?: "Error en el registro", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ActivityRegister, "Fallo de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        Log.d("Register", "Botón pulsado")


        backButton.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }

    }
}