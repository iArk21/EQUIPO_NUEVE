package com.example.pico_botella.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onEmailChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPasswordChanged(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Criterio 6: Toggle de contraseña con iconos específicos
        binding.tilPassword.setEndIconOnClickListener {
            togglePasswordVisibility()
        }

        binding.btnLogin.setOnClickListener {
            // Navegar al Home después del Login
            findNavController().navigate(R.id.homeFragment)
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            // Criterio 6: Cambia a ojo cerrado y deja visible la contraseña
            binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            binding.tilPassword.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_closed)
        } else {
            // Oculta nuevamente la contraseña y muestra ojo abierto
            binding.etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            binding.tilPassword.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open)
        }
        // Mantener el cursor al final
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoginEnabled.collect { isEnabled ->
                        binding.btnLogin.isEnabled = isEnabled
                        // Criterio 8: Blanco bold cuando se habilita
                        if (isEnabled) {
                            binding.btnLogin.alpha = 1.0f
                            binding.btnLogin.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                        } else {
                            binding.btnLogin.alpha = 0.5f
                        }
                    }
                }

                launch {
                    viewModel.passwordError.collect { error ->
                        // Criterio 5: Mensaje rojo y borde rojo en tiempo real
                        binding.tilPassword.error = error
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
