package com.appcrud.comunicacion

object ServidorConfig {
    const val HOST = "192.168.1.215"
    const val PUERTO = 5000

    val BASE_URL: String
        get() = "http://$HOST:$PUERTO"
}