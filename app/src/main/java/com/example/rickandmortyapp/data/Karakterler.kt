package com.example.rickandmortyapp.data

import java.io.Serializable

data class Karakterler  (
    val info: InfoDto,
    val results: List<CharacterDto>
)

data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val image: String,
    val episode: List<String>,
    val type: String,
    val gender: String,
    val origin: OriginDto

) : Serializable

data class OriginDto(
    val name: String,
    val url: String
) : Serializable