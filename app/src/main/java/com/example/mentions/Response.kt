package com.example.mentions

data class Response(
    val `data`: List<String>,
    val message: String,
    val status:Int,
)