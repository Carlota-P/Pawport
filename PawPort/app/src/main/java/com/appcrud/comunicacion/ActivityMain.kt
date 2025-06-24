package com.appcrud.comunicacion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivityMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("usuarioPrefs", MODE_PRIVATE)
        val rol = prefs.getString("rol", "usuario")

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomBar)

        if (rol == "usuario") {
            bottomNav.menu.clear()

            bottomNav.inflateMenu(R.menu.bottom_nav_menu) // tu menú original con favoritos

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FragmentHome())
                            .commit()
                        true
                    }

                    R.id.nav_favoritos -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FragmentFavoritos())
                            .commit()
                        true
                    }

                    R.id.nav_refugios -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ActivityRefugios())
                            .commit()
                        true
                    }

                    R.id.nav_perfil -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FragmentUser_profile())
                            .commit()
                        true
                    }

                    else -> false
                }
            }
        } else {
            bottomNav.menu.clear()

            bottomNav.inflateMenu(R.menu.bottom_nav_menu_refugios)

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FragmentHome())
                            .commit()
                        true
                    }

                    R.id.nav_refugios -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ActivityRefugios())
                            .commit()
                        true
                    }

                    R.id.add_animal -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FragmentCreateAnimal())
                            .commit()
                        true
                    }

                    R.id.nav_perfil -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, FragmentRefugio_profile())
                            .commit()
                        true
                    }

                    else -> false
                }
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FragmentHome())
            .commit()

    }
}
