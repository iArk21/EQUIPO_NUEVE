package com.example.pico_botella

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pico_botella.databinding.ActivityMainBinding

/**
 * MainActivity: Contenedor único (Single Activity) para toda la aplicación.
 * Implementa ViewBinding para acceso seguro a las vistas.
 *
 * CAMBIO DE VARIABLE vs versión anterior:
 * - binding  →  mainBinding
 */
class MainActivity : AppCompatActivity() {

    // VARIABLE RENOMBRADA: antes 'binding', ahora 'mainBinding'
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración de ViewBinding con nombre renombrado
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }
}
