package com.example.anothercalorieapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String, // Changed to String for 20-character hash
    val maxCalories: Int,
    val maxCarbs: Int,
    val maxFat: Int,
    val maxProtein: Int,
    //val apiKey: String = "",
    val apiKey: String? = null,
    val lastPing: Long? = null
)