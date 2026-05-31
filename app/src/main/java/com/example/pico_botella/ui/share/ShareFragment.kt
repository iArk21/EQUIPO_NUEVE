package com.example.pico_botella.ui.share

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R

/**
 * HU 10 — Compartir aplicación.
 *
 * Criterio 1: Muestra el bottom sheet nativo del SO con los canales de envío
 *             (WhatsApp, Facebook, Gmail, Messenger, etc.) usando Intent.ACTION_SEND.
 *             NO se construye una pantalla personalizada — es el chooser del sistema.
 *
 * Criterio 2: El mensaje enviado contiene:
 *             - Título:  "App pico botella"
 *             - Eslogan: "Solo los valientes lo juegan !!"
 *             - URL:     https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es
 *
 * Flujo correcto:
 *   ToolbarFragment → btnShare → navega a ShareFragment →
 *   onResume lanza el chooser → el usuario comparte o cancela →
 *   ShareFragment regresa automáticamente al Home.
 *
 * Por qué en onResume y no en onViewCreated:
 *   El chooser es un Intent externo; si se lanza en onViewCreated y el usuario
 *   lo cancela, el Fragment queda visible sin contenido. Con onResume + popBackStack
 *   nos aseguramos de regresar al Home en cualquier caso.
 */
class ShareFragment : Fragment() {

    // Flag para lanzar el chooser solo una vez (evita relanzarlo al volver de otra app)
    private var chooserLanzado = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Vista vacía — este Fragment no muestra UI propia (HU 10 usa el chooser del SO)
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (!chooserLanzado) {
            chooserLanzado = true
            lanzarChooserCompartir()
        }
    }

    /**
     * Construye y lanza el Intent de compartir.
     *
     * Criterio 1: createChooser() genera el bottom sheet nativo del SO
     *             con todos los canales disponibles (WhatsApp, Gmail, etc.).
     *
     * Criterio 2: El texto del mensaje incluye título, eslogan y URL de Nequi
     *             como ejemplo, tal como indica la HU.
     */
    private fun lanzarChooserCompartir() {
        // Construir el mensaje completo (Criterio 2)
        val titulo  = getString(R.string.compartir_titulo_app)   // "App pico botella"
        val eslogan = getString(R.string.compartir_eslogan)       // "Solo los valientes lo juegan !!"
        val url     = getString(R.string.compartir_url)           // URL de Nequi como ejemplo

        val mensajeCompleto = "$titulo\n$eslogan\n$url"

        // Intent de tipo texto plano — compatible con WhatsApp, Gmail, Messenger, etc.
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensajeCompleto)
        }

        // Criterio 1: createChooser muestra el bottom sheet nativo del SO
        val chooserTitulo = getString(R.string.compartir_chooser_titulo)
        val chooser = Intent.createChooser(shareIntent, chooserTitulo)

        startActivity(chooser)

        // Regresar al Home después de lanzar el chooser
        // (el usuario ya eligió su canal o canceló)
        requireActivity().runOnUiThread {
            findNavController().popBackStack()
        }
    }
}
