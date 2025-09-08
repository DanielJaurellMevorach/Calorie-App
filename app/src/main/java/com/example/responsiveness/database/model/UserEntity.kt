package com.example.anothercalorieapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val maxCalories: Int,
    val maxCarbs: Int,
    val maxFat: Int,
    val maxProtein: Int,
    val apiKey: String = ""
)