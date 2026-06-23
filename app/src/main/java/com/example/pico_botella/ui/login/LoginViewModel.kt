package com.example.pico_botella.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled.asStateFlow()

    private val _isRegisterEnabled = MutableStateFlow(false)
    val isRegisterEnabled: StateFlow<Boolean> = _isRegisterEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent: SharedFlow<Boolean> = _navigationEvent.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

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
        val isEmailValid = _email.value.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()
        val isPasswordValid = _password.value.length >= 6
        
        _isLoginEnabled.value = isEmailValid && isPasswordValid && !_isLoading.value
        _isRegisterEnabled.value = _email.value.isNotBlank() && _password.value.isNotBlank() && !_isLoading.value
    }

    fun onLoginClicked() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            validateForm()
            try {
                auth.signInWithEmailAndPassword(_email.value, _password.value).await()
                _navigationEvent.emit(true)
            } catch (e: Exception) {
                _errorEvent.emit("Login incorrecto")
            } finally {
                _isLoading.value = false
                validateForm()
            }
        }
    }

    fun onRegisterClicked() {
        if (_isLoading.value) return
        viewModelScope.launch {
            _isLoading.value = true
            validateForm()
            try {
                auth.createUserWithEmailAndPassword(_email.value, _password.value).await()
                _navigationEvent.emit(true)
            } catch (e: Exception) {
                _errorEvent.emit("Error en el registro")
            } finally {
                _isLoading.value = false
                validateForm()
            }
        }
    }
}
