package com.example.caloriecounter.ui.components.openai

import com.google.gson.annotations.SerializedName

data class NutritionResult(
    @SerializedName("meal_name") val mealName: String,
    val ingredients: List<Ingredient>,
    val nutrition: Nutrition,
    @SerializedName("meal_nutrition_score") val mealNutritionScore: String,
    val error: String?
)

data class Ingredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val calories: Double
)

data class Nutrition(
    @SerializedName("energy_kcal") val energyKcal: Double,
    @SerializedName("protein_g") val proteinG: Double,
    @SerializedName("carbohydrates_g") val carbohydratesG: Double,
    @SerializedName("fat_g") val fatG: Double,
    @SerializedName("fiber_g") val fiberG: Double,
    @SerializedName("sugars_g") val sugarsG: Double,
    @SerializedName("sodium_mg") val sodiumMg: Double,
    @SerializedName("cholesterol_mg") val cholesterolMg: Double,
    @SerializedName("water_l") val waterL: Double
)
