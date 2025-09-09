package com.example.responsiveness.database.repository

import android.util.Log
import com.example.anothercalorieapp.database.IngredientEntity
import com.example.anothercalorieapp.database.MealEntity
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.anothercalorieapp.database.NutritionEntity
import com.example.anothercalorieapp.database.UserEntity
import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class MealRepository(
    private val mealDao: MealDao,
    //private val userDao: UserDao
) {

    suspend fun saveMealFromJson(
        userId: String, // Changed to String for new user ID system
        mealName: String,
        ingredients: List<Map<String, Any>>,
        nutrition: Map<String, Any>?,
        nutritionScore: String?,
        error: String?,
        imagePath: String? = null, // Add image path parameter
        timestamp: Long? = null // NEW: optional timestamp
    ): Long {
        val meal = MealEntity(
            meal_name = mealName,
            meal_nutrition_score = nutritionScore,
            error = error,
            image_path = imagePath, // Store the image path
            userId = userId, // Pass userId
            created_at = timestamp ?: System.currentTimeMillis() // Use provided timestamp or now
        )

        val ingredientEntities = ingredients.mapNotNull { ingredient ->
            val name = ingredient["name"] as? String ?: return@mapNotNull null
            val quantity = (ingredient["quantity"] as? Number)?.toDouble() ?: 0.0
            val unit = ingredient["unit"] as? String ?: ""
            val calories = (ingredient["calories"] as? Number)?.toInt() ?: 0
            val protein_g = (ingredient["protein"] as? Number)?.toDouble() ?: 0.0
            val carbohydrates_g = (ingredient["carbs"] as? Number)?.toDouble() ?: 0.0
            val fat_g = (ingredient["fat"] as? Number)?.toDouble() ?: 0.0

            IngredientEntity(
                meal_id = 0, // Will be set in the DAO transaction
                name = name,
                quantity = quantity,
                unit = unit,
                calories = (calories as? Number)?.toDouble() ?: 0.0,
                protein_g = protein_g,
                carbohydrates_g = carbohydrates_g,
                fat_g = fat_g
            )
        }

        val nutritionEntity = nutrition?.let {
            NutritionEntity(
                meal_id = 0, // Will be set in the DAO transaction
                energy_kcal = (it["energy_kcal"] as? Number)?.toDouble() ?: 0.0,
                protein_g = (it["protein_g"] as? Number)?.toDouble() ?: 0.0,
                carbohydrates_g = (it["carbohydrates_g"] as? Number)?.toDouble() ?: 0.0,
                fat_g = (it["fat_g"] as? Number)?.toDouble() ?: 0.0,
                fiber_g = (it["fiber_g"] as? Number)?.toDouble() ?: 0.0,
                sugars_g = (it["sugars_g"] as? Number)?.toDouble() ?: 0.0,
                sodium_mg = (it["sodium_mg"] as? Number)?.toDouble() ?: 0.0,
                cholesterol_mg = (it["cholesterol_mg"] as? Number)?.toDouble() ?: 0.0
            )
        }

        val mealId = mealDao.insertMealWithDetails(meal, ingredientEntities, nutritionEntity)

        // Update user nutrition values
        val totalCalories = ingredientEntities.sumOf { it.calories }
        val totalProtein = ingredientEntities.sumOf { it.protein_g }
        val totalCarbs = ingredientEntities.sumOf { it.carbohydrates_g }
        val totalFat = ingredientEntities.sumOf { it.fat_g }

//        userDao.updateUserNutrition(
//            userId = userId,
//            calories = totalCalories,
//            protein = totalProtein,
//            carbs = totalCarbs,
//            fat = totalFat
//        )

        return mealId
    }

    suspend fun getMealWithDetails(mealId: Long): MealWithDetails? {
        return mealDao.getMealWithDetails(mealId)
    }

    suspend fun getAllMealsWithDetails(): Flow<List<MealWithDetails>> {
        return mealDao.getAllMealsWithDetails()
    }

    suspend fun deleteMeal(mealId: Long) {
        mealDao.deleteMealWithDetails(mealId)
    }

    suspend fun updateMealQuantity(mealId: Long, quantity: Double) {
        Log.d("MealRepository", "updateMealQuantity called with mealId=$mealId, quantity=$quantity")
        mealDao.updateMealQuantity(mealId, quantity)
    }

    suspend fun getMealCount(): Int {
        return mealDao.getMealCount()
    }

    suspend fun getTodayMealsWithDetails(): List<MealWithDetails> {
        return mealDao.getTodayMealsWithDetails()
    }

    fun getTodayMealsWithDetailsFlow(): Flow<List<MealWithDetails>> {
        return mealDao.getTodayMealsWithDetailsFlow()
    }

    suspend fun getOrCreateUser(): UserEntity {
        val existingUser = mealDao.getUserSync()
        return if (existingUser == null) {
            mealDao.insertDefaultUser()
            mealDao.getUserSync()!!
        } else {
            existingUser
        }
    }

    fun getUser(): Flow<UserEntity?> = mealDao.getUser()

    suspend fun getUserSync(): UserEntity? = mealDao.getUserSync()

    suspend fun updateMaxCalories(userId: String, maxCalories: Int) = mealDao.updateMaxCalories(userId, maxCalories)
    suspend fun updateMaxCarbs(userId: String, maxCarbs: Int) = mealDao.updateMaxCarbs(userId, maxCarbs)
    suspend fun updateMaxProtein(userId: String, maxProtein: Int) = mealDao.updateMaxProtein(userId, maxProtein)
    suspend fun updateMaxFat(userId: String, maxFat: Int) = mealDao.updateMaxFat(userId, maxFat)
    suspend fun updateApiKey(userId: String, apiKey: String) = mealDao.updateApiKey(userId, apiKey)
    suspend fun updateLastPing(userId: String, lastPing: Long) = mealDao.updateLastPing(userId, lastPing)
}