package com.example.mentions

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Users {

    @GET("names")
    suspend fun getUsers(
        @Query("search_name") name: String
    ): Response
}