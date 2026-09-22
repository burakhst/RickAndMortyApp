package com.example.rickandmortyapp.data

import java.io.Serializable

data class Karakterler  (
    val results: List<CharacterDto>
)
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val image: String
) : Serializable