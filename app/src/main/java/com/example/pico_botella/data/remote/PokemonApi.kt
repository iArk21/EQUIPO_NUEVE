package com.example.pico_botella.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface PokemonApi {
    @GET("Biuni/PokemonGO-Pokedex/master/pokedex.json")
    suspend fun getPokedex(): PokemonResponse

    companion object {
        private const val BASE_URL = "https://raw.githubusercontent.com/"

        fun create(): PokemonApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(PokemonApi::class.java)
        }
    }
}
