package com.example.pennywise.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val monthlyLimit: Double,
    val isUnlimited: Boolean,
    val createdAt: Long
)
