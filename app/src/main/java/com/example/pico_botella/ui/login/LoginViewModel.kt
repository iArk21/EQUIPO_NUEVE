package com.example.pico_botella.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun onEmailChanged(text: String) {
        _email.value = text
        validateForm()
    }

    fun onPasswordChanged(text: String) {
        _password.value = text
        
        if (text.isNotEmpty() && text.length < 6) {
            _passwordError.value = "Mínimo 6 dígitos"
        } else {
            _passwordError.value = null
        }
        
        validateForm()
    }

    private fun validateForm() {
        val isEmailValid = _email.value.isNotBlank()
        val isPasswordValid = _password.value.length in 6..10
        _isLoginEnabled.value = isEmailValid && isPasswordValid
    }
}
