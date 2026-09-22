package com.example.rickandmortyapp.data

import retrofit2.http.GET

interface ApiService {
    @GET("character")
    suspend fun getCharacters(): Karakterler
}