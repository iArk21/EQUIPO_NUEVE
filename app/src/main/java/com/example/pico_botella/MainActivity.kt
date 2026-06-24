package com.example.pico_botella

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pico_botella.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * @AndroidEntryPoint: Indica a Hilt que debe inyectar dependencias en esta Activity.
 * Es necesario para que los Fragmentos contenidos también puedan usar inyección.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
    }
}
