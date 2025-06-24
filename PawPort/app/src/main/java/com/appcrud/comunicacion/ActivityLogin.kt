package com.appcrud.comunicacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.Socket

class ActivityLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val prefs = getSharedPreferences("usuarioPrefs", MODE_PRIVATE)
        val alias = prefs.getString("alias", null)

        if (!alias.isNullOrEmpty()) {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {
                    val conectado = checkConnection(ServidorConfig.HOST, ServidorConfig.PUERTO)
                    Log.d("MainActivity", "Estado conexion = $conectado")
                    delay(5000)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error en checkConnection: ${e.message}", e)
                false
            }

        }

        val loginButton = findViewById<Button>(R.id.loginButton) // LUEGO buscas los botones
        val registerLink = findViewById<TextView>(R.id.registerText)
        val etEmail = findViewById<EditText>(R.id.emailEditText)
        val etPassword = findViewById<EditText>(R.id.passwordEditText)
        val protectoraLink = findViewById<TextView>(R.id.linkRegistroProtectora)

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

            val peticion = Peticion(Peticion.TipoOperacion.LOGIN_USUARIO, email, password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val respuesta = ClienteSocket(ServidorConfig.HOST, ServidorConfig.PUERTO).enviarPeticion(peticion)
                    withContext(Dispatchers.Main) {
                        if (respuesta?.isExito == true) {
                            Toast.makeText(this@ActivityLogin, "Login exitoso", Toast.LENGTH_SHORT).show()

                            val sharedPref = getSharedPreferences("usuarioPrefs", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("alias", respuesta.usuario?.alias ?: "Sin alias")
                                putString("nombre", respuesta.usuario?.nombre ?: "Sin nombre")
                                putString("apellido1", respuesta.usuario?.apellido1 ?: "")
                                putString("apellido2", respuesta.usuario?.apellido2 ?: "")
                                putString("email", respuesta.usuario?.email ?: "")
                                putString("dni", respuesta.usuario?.dni ?: "")
                                putInt("telefono", respuesta.usuario?.telefono ?: 0)
                                putString("rol", respuesta.usuario?.rol ?: "usuario")
                                putInt("usuarioId", respuesta.usuario?.usuarioId ?: 0)
                                putString("contrasena", password)

                                apply()
                            }

                            val intent = Intent(this@ActivityLogin, ActivityMain::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@ActivityLogin, respuesta?.mensaje ?: "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ActivityLogin, "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    Log.e("Login", "Fallo en login", e)
                }
            }
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, ActivityRegister::class.java)
            startActivity(intent)
        }

        protectoraLink.setOnClickListener {
            val intent = Intent(this, ActivityLoginProtectora::class.java)
            startActivity(intent)
        }
    }

    /**

    Verifica si el servidor está aceptando conexiones.
    @param host IP o dominio del servidor.
    @param port Puerto del servidor.
    @return true si está accesible, false si no.*/
    private fun checkConnection(host: String, port: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(host, port), 7000)
            val oos = ObjectOutputStream(socket.getOutputStream())
            val ois = ObjectInputStream(socket.getInputStream())

            // Envía una petición PING
            oos.writeObject(Peticion(Peticion.TipoOperacion.PING))
            oos.flush()

            // Lee la respuesta
            val respuesta = ois.readObject() as? Respuesta

            ois.close()
            oos.close()
            socket.close()

            respuesta?.isExito == true
        } catch (e: Exception) {
            Log.e("MainActivity", "Excepción en checkConnection: ${e.message}", e)
            false
        }
    }


}
