package com.nesib.yourbooknotes.models

data class AuthResponse(
    val userId:String,
    val token:String,
    val message:String
)
