package com.example.pico_botella

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application anotada con @HiltAndroidApp para iniciar la generación de código de Hilt.
 * Esta anotación es obligatoria para cualquier app que use Hilt.
 */
@HiltAndroidApp
class PicoBotellaApp : Application()
