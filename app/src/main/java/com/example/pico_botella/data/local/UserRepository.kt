package com.example.pico_botella.data.local

class UserRepository(private val userDao: UserDao) {
    suspend fun login(email: String, password: String) = userDao.login(email, password)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    suspend fun register(user: UserEntity) = userDao.register(user)
}
