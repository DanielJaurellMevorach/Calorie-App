package com.example.anothercalorieapp.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val meal_name: String,
    val meal_nutrition_score: String?,
    val error: String?,
    val image_path: String? = null,
    val quantity: Double = 1.0, // Changed to Double for API compatibility
    val created_at: Long = System.currentTimeMillis(),
    val userId: String // Changed to String to match new user ID system
)

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val meal_id: Long,
    val name: String,
    val quantity: Double,
    val unit: String,
    val calories: Double, // Changed to Double
    val protein_g: Double = 0.0, // Renamed and changed to Double
    val carbohydrates_g: Double = 0.0, // Renamed and changed to Double
    val fat_g: Double = 0.0 // Renamed and changed to Double
)

@Entity(tableName = "nutrition")
data class NutritionEntity(
    @PrimaryKey
    val meal_id: Long,
    val energy_kcal: Double, // Changed to Double
    val protein_g: Double,
    val carbohydrates_g: Double,
    val fat_g: Double,
    val fiber_g: Double,
    val sugars_g: Double,
    val sodium_mg: Double,
    val cholesterol_mg: Double
)

data class MealWithDetails(
    @Embedded val meal: MealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "meal_id"
    )
    val ingredients: List<IngredientEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "meal_id"
    )
    val nutrition: NutritionEntity?
)
