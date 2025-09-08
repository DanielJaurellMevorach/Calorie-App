package com.example.anothercalorieapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MealEntity::class, IngredientEntity::class, NutritionEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    //abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getDatabase(context: Context): MealDatabase {
            val db = INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MealDatabase::class.java,
                    "meal_database"
                )
                .fallbackToDestructiveMigration() // Add this for development
                .build()
                INSTANCE = instance
                instance
            }
            // Seed default user if none exists
            CoroutineScope(Dispatchers.IO).launch {
                val userCount = db.query("SELECT COUNT(*) FROM users", null).use { cursor ->
                    cursor.moveToFirst()
                    cursor.getInt(0)
                }
                if (userCount == 0) {
                    db.mealDao().insertDefaultUser()
                }
            }
            return db
        }
    }
}

// Add to MealDao interface:
// @Insert(onConflict = OnConflictStrategy.REPLACE)
// suspend fun insertDefaultUser(user: UserEntity = UserEntity(id = 1, maxCalories = 2000, maxCarbs = 250, maxFat = 70, maxProtein = 150))