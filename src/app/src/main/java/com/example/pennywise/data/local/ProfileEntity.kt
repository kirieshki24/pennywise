package com.example.pennywise.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pennywise.domain.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val monthlyLimit: Double,
    val isUnlimited: Boolean,
    val createdAt: Long
)

fun ProfileEntity.toDomain(): Profile {
    return Profile(
        id = id,
        name = name,
        monthlyLimit = monthlyLimit,
        isUnlimited = isUnlimited,
        createdAt = createdAt
    )
}

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        id = id,
        name = name,
        monthlyLimit = monthlyLimit,
        isUnlimited = isUnlimited,
        createdAt = createdAt
    )
}
