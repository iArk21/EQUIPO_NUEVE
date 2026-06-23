package com.example.pico_botella.ui.login

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        binding.tilPassword.setEndIconOnClickListener {
            togglePasswordVisibility()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.onLoginClicked()
        }

        binding.tvRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.tilPassword.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_closed)
        } else {
            binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.tilPassword.endIconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye_open)
        }
        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoginEnabled.collect { isEnabled ->
                        binding.btnLogin.isEnabled = isEnabled
                        binding.btnLogin.alpha = if (isEnabled) 1.0f else 0.5f
                    }
                }

                launch {
                    viewModel.isRegisterEnabled.collect { isEnabled ->
                        binding.tvRegister.isEnabled = isEnabled
                        if (isEnabled) {
                            binding.tvRegister.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
                            binding.tvRegister.setTypeface(null, Typeface.BOLD)
                        } else {
                            binding.tvRegister.setTextColor(android.graphics.Color.parseColor("#9EA1A1"))
                            binding.tvRegister.setTypeface(null, Typeface.NORMAL)
                        }
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
                        binding.etEmail.isEnabled = !isLoading
                        binding.etPassword.isEnabled = !isLoading
                        binding.tilPassword.isEnabled = !isLoading
                    }
                }

                launch {
                    viewModel.passwordError.collect { error ->
                        binding.tilPassword.error = error
                    }
                }

                launch {
                    viewModel.navigationEvent.collect { success ->
                        if (success) {
                            findNavController().navigate(R.id.homeFragment)
                        }
                    }
                }

                launch {
                    viewModel.errorEvent.collect { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
