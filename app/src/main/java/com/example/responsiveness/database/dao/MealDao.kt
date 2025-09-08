package com.example.responsiveness.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anothercalorieapp.database.IngredientEntity
import com.example.anothercalorieapp.database.MealEntity
import com.example.anothercalorieapp.database.MealWithDetails
import com.example.anothercalorieapp.database.NutritionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertIngredient(ingredient: IngredientEntity)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNutrition(nutrition: NutritionEntity)

    @Transaction
    suspend fun insertMealWithDetails(
        meal: MealEntity,
        ingredients: List<IngredientEntity>,
        nutrition: NutritionEntity?
    ): Long {
        val mealId = insertMeal(meal)
        val ingredientsWithMealId = ingredients.map { it.copy(meal_id = mealId) }
        insertIngredients(ingredientsWithMealId)
        nutrition?.let { insertNutrition(it.copy(meal_id = mealId)) }
        return mealId
    }

    @Transaction
    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealWithDetails(mealId: Long): MealWithDetails?

    @Transaction
    @Query("SELECT * FROM meals ORDER BY created_at DESC")
    fun getAllMealsWithDetails(): Flow<List<MealWithDetails>>

    @Query("SELECT * FROM meals ORDER BY created_at DESC")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM ingredients WHERE meal_id = :mealId")
    suspend fun getIngredientsForMeal(mealId: Long): List<IngredientEntity>

    @Query("SELECT * FROM nutrition WHERE meal_id = :mealId")
    suspend fun getNutritionForMeal(mealId: Long): NutritionEntity?

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMealById(mealId: Long)

    @Query("DELETE FROM ingredients WHERE meal_id = :mealId")
    suspend fun deleteIngredientsForMeal(mealId: Long)

    @Query("DELETE FROM nutrition WHERE meal_id = :mealId")
    suspend fun deleteNutritionForMeal(mealId: Long)

    @Transaction
    suspend fun deleteMealWithDetails(mealId: Long) {
        deleteIngredientsForMeal(mealId)
        deleteNutritionForMeal(mealId)
        deleteMealById(mealId)
    }

    @Query("SELECT COUNT(*) FROM meals")
    suspend fun getMealCount(): Int

    @Query("SELECT * FROM meals WHERE created_at >= :fromDate ORDER BY created_at DESC")
    suspend fun getMealsSince(fromDate: Long): List<MealEntity>

    @Query("UPDATE meals SET quantity = :quantity WHERE id = :mealId")
    suspend fun updateMealQuantity(mealId: Long, quantity: Double)

    @Query("UPDATE meals SET meal_name = :mealName, quantity = :quantity, meal_nutrition_score = :nutritionScore WHERE id = :mealId")
    suspend fun updateMealDetails(mealId: Long, mealName: String, quantity: Double, nutritionScore: String?)

    @Transaction
    @Query("SELECT * FROM meals WHERE date(created_at / 1000, 'unixepoch', 'localtime') = date('now', 'localtime') ORDER BY created_at DESC")
    suspend fun getTodayMealsWithDetails(): List<MealWithDetails>

    @Transaction
    @Query("SELECT * FROM meals WHERE date(created_at / 1000, 'unixepoch', 'localtime') = date('now', 'localtime') ORDER BY created_at DESC")
    fun getTodayMealsWithDetailsFlow(): Flow<List<MealWithDetails>>

    @Transaction
    @Query("SELECT * FROM meals ORDER BY created_at DESC")
    fun getAllMealsWithDetailsFlow(): Flow<List<MealWithDetails>>

    @Transaction
    @Query("SELECT * FROM meals WHERE created_at BETWEEN :startMillis AND :endMillis ORDER BY created_at DESC")
    fun getMealsForDateRangeFlow(startMillis: Long, endMillis: Long): Flow<List<MealEntity>>

    @Transaction
    @Query("SELECT * FROM meals WHERE created_at BETWEEN :startMillis AND :endMillis ORDER BY created_at DESC")
    fun getMealsWithDetailsForDateRangeFlow(startMillis: Long, endMillis: Long): Flow<List<MealWithDetails>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: Long = 1): Flow<com.example.anothercalorieapp.database.UserEntity>

    @Query("UPDATE users SET maxCalories = :maxCalories WHERE id = :userId")
    suspend fun updateMaxCalories(userId: Long, maxCalories: Int)

    @Query("UPDATE users SET maxCarbs = :maxCarbs WHERE id = :userId")
    suspend fun updateMaxCarbs(userId: Long, maxCarbs: Int)

    @Query("UPDATE users SET maxProtein = :maxProtein WHERE id = :userId")
    suspend fun updateMaxProtein(userId: Long, maxProtein: Int)

    @Query("UPDATE users SET maxFat = :maxFat WHERE id = :userId")
    suspend fun updateMaxFat(userId: Long, maxFat: Int)

    @Query("UPDATE users SET apiKey = :apiKey WHERE id = :userId")
    suspend fun updateApiKey(userId: Long, apiKey: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDefaultUser(user: com.example.anothercalorieapp.database.UserEntity = com.example.anothercalorieapp.database.UserEntity(
        id = 1,
        maxCalories = 2000,
        maxCarbs = 250,
        maxFat = 70,
        maxProtein = 150
    ))
}