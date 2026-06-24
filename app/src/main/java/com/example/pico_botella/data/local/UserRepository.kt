package com.example.pico_botella.data.local

import javax.inject.Inject

/**
 * Repositorio para la gestión de usuarios.
 * @Inject: Hilt inyectará automáticamente el UserDao aquí.
 */
class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun login(email: String, password: String) = userDao.login(email, password)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun register(user: UserEntity) = userDao.register(user)
}
