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

class ActivityLoginProtectora : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login_protectora)

        val prefs = getSharedPreferences("usuarioPrefs", MODE_PRIVATE)
        val rol = prefs.getString("rol", null)

        if (rol == "usuario" || rol == "protectora") {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finish()
            return
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etPassword = findViewById<EditText>(R.id.passwordEditText)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        backButton.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Introduce un email válido ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Introduce la contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Formato de email no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val peticion = Peticion(Peticion.TipoOperacion.LOGIN_PROTECTORA, email, password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                    withContext(Dispatchers.Main) {
                        if (respuesta?.isExito == true) {
                            Toast.makeText(this@ActivityLoginProtectora, "Login exitoso", Toast.LENGTH_SHORT).show()

                            val sharedPref = getSharedPreferences("usuarioPrefs", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putInt("usuarioId", respuesta.protectora.protectoraId ?: 0)
                                putInt("capacidad", respuesta.protectora.capacidad ?: 0)
                                putString("nombre", respuesta.protectora.nombre ?: "Sin nombre")
                                putString("calle", respuesta.protectora.calle ?: "Sin calle")
                                putInt("numeroCalle", respuesta.protectora.numeroCalle ?: 0)
                                putInt("codPostal", respuesta.protectora.codPostal ?: 0)
                                putString("email", respuesta.protectora.email ?: "")
                                putInt("telefono", respuesta.protectora.telefono ?: 0)
                                putString("historia", respuesta.protectora.historia ?: "Sin historia")
                                putString("contrasena", password)
                                putString("rol", "protectora")
                                apply()
                            }


                            val intent = Intent(this@ActivityLoginProtectora, ActivityMain::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ActivityLoginProtectora, respuesta?.mensaje ?: "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ActivityLoginProtectora, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    Log.e("LoginProtectora", "Fallo en login protectora", e)
                }
            }
        }
    }
}