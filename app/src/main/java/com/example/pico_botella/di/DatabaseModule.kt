package com.example.pico_botella.di

import android.content.Context
import com.example.pico_botella.data.local.AppDatabase
import com.example.pico_botella.data.local.RetoDao
import com.example.pico_botella.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Hilt para proveer las dependencias relacionadas con la base de datos Room.
 * @Module: Indica que esta clase provee dependencias.
 * @InstallIn(SingletonComponent::class): Define que las dependencias vivirán mientras la aplicación esté viva.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * @Provides: Indica a Hilt cómo construir la base de datos.
     * @Singleton: Asegura que solo exista una instancia de la base de datos.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    /**
     * Provee el DAO de retos necesario para el repositorio.
     */
    @Provides
    fun provideRetoDao(database: AppDatabase): RetoDao {
        return database.retoDao()
    }

    /**
     * Provee el DAO de usuarios.
     */
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
