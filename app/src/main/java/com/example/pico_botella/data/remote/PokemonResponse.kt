package com.example.pico_botella.data.remote

data class PokemonResponse(
    val pokemon: List<Pokemon>
)

data class Pokemon(
    val id: Int,
    val name: String,
    val img: String
)
