package com.example.anothercalorieapp.database

import com.example.responsiveness.database.dao.MealDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatabaseSeeder(private val database: MealDatabase) {

    suspend fun seedDatabase() = withContext(Dispatchers.IO) {
        val mealDao = database.mealDao()

        // Check if database is already seeded
        if (mealDao.getMealCount() > 0) {
            return@withContext
        }

        // Seed the user first and get the userId
        // val user = UserEntity(...)
        // val userId = userDao.insertUser(user)
        val userId = 1L // Use a static userId or fetch from elsewhere if needed

        // Seed the three meals from your composables, passing userId
        seedOatmealWithAlmondMilk(mealDao, userId)
        seedBruschettaWithTomatoAndBasil(mealDao, userId)
        seedVanillaApplePie(mealDao, userId)
        seedNewImageMealsPast6Days(mealDao, userId)

        // After seeding, recalculate user's current nutrition for today
        val todayStart = getTodayTimestamp("00:00")
        val todayMeals = mealDao.getAllMealsWithDetails().first().filter {
            it.meal.created_at >= todayStart
        }
        val totalCalories = todayMeals.flatMap { it.ingredients }.sumOf { it.calories }
        val totalProtein = todayMeals.flatMap { it.ingredients }.sumOf { it.protein_g }
        val totalCarbs = todayMeals.flatMap { it.ingredients }.sumOf { it.carbohydrates_g }
        val totalFat = todayMeals.flatMap { it.ingredients }.sumOf { it.fat_g }

        // Remove userDao nutrition update calls
        // userDao.resetUserNutrition(userId)
        // userDao.updateUserNutrition(
        //     userId = userId,
        //     calories = totalCalories,
        //     protein = totalProtein,
        //     carbs = totalCarbs,
        //     fat = totalFat
        // )
    }

    private suspend fun updateUserNutrition(userId: Long, meal: MealEntity, ingredients: List<IngredientEntity>) {
        val totalCalories = ingredients.sumOf { it.calories }
        val totalProtein = ingredients.sumOf { it.protein_g }
        val totalCarbs = ingredients.sumOf { it.carbohydrates_g }
        val totalFat = ingredients.sumOf { it.fat_g }
        // Remove userDao nutrition update calls
        // database.userDao().updateUserNutrition(
        //     userId = userId,
        //     calories = totalCalories,
        //     protein = totalProtein,
        //     carbs = totalCarbs,
        //     fat = totalFat
        // )
    }

    private suspend fun seedOatmealWithAlmondMilk(mealDao: MealDao, userId: Long) {
        val meal = MealEntity(
            meal_name = "Oatmeal with Almond Milk",
            meal_nutrition_score = "A",
            error = null,
            created_at = getTodayTimestamp("09:10"),
            image_path = "meal_two.jpg",
            userId = userId
        )

        val ingredients = listOf(
            IngredientEntity(
                meal_id = 0,
                name = "Rolled oats",
                quantity = 45.0,
                unit = "g",
                calories = 171.0,
                protein_g = 6.0,
                carbohydrates_g = 30.0,
                fat_g = 3.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Almond milk",
                quantity = 200.0,
                unit = "ml",
                calories = 60.0,
                protein_g = 2.0,
                carbohydrates_g = 8.0,
                fat_g = 2.0
            )
        )

        val nutrition = NutritionEntity(
            meal_id = 0,
            energy_kcal = ingredients.sumOf { it.calories },
            protein_g = ingredients.sumOf { it.protein_g },
            carbohydrates_g = ingredients.sumOf { it.carbohydrates_g },
            fat_g = ingredients.sumOf { it.fat_g },
            fiber_g = 0.0, // Default value
            sugars_g = 0.0, // Default value
            sodium_mg = 0.0, // Default value
            cholesterol_mg = 0.0 // Default value
        )

        val mealId = mealDao.insertMealWithDetails(meal, ingredients, nutrition)
        updateUserNutrition(userId, meal.copy(id = mealId), ingredients)
    }

    private suspend fun seedBruschettaWithTomatoAndBasil(mealDao: MealDao, userId: Long) {
        val meal = MealEntity(
            meal_name = "Bruschetta with Tomato and Basil",
            meal_nutrition_score = "B",
            error = null,
            created_at = getTodayTimestamp("13:19"),
            image_path = "meal_one.jpeg",
            userId = userId
        )

        val ingredients = listOf(
            IngredientEntity(
                meal_id = 0,
                name = "Baguette bread",
                quantity = 80.0,
                unit = "g",
                calories = 216.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Roma tomatoes",
                quantity = 150.0,
                unit = "g",
                calories = 27.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Fresh basil",
                quantity = 10.0,
                unit = "g",
                calories = 2.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Extra virgin olive oil",
                quantity = 15.0,
                unit = "ml",
                calories = 135.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Garlic",
                quantity = 8.0,
                unit = "g",
                calories = 12.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Mozzarella cheese",
                quantity = 50.0,
                unit = "g",
                calories = 139.0
            )
        )

        val nutrition = NutritionEntity(
            meal_id = 0,
            energy_kcal = 431.0,
            protein_g = 10.0,
            carbohydrates_g = 14.0,
            fat_g = 17.0,
            fiber_g = 2.8,
            sugars_g = 6.2,
            sodium_mg = 485.0,
            cholesterol_mg = 22.0
        )

        mealDao.insertMealWithDetails(meal, ingredients, nutrition)
    }

    private suspend fun seedVanillaApplePie(mealDao: MealDao, userId: Long) {
        val meal = MealEntity(
            meal_name = "Vanilla Apple Pie Test",
            meal_nutrition_score = "C",
            error = null,
            created_at = getTodayTimestamp("17:47"),
            image_path = "meal_three.png",
            userId = userId
        )

        val ingredients = listOf(
            IngredientEntity(
                meal_id = 0,
                name = "Pie crust",
                quantity = 65.0,
                unit = "g",
                calories = 295.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Granny Smith apples",
                quantity = 120.0,
                unit = "g",
                calories = 63.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Granulated sugar",
                quantity = 25.0,
                unit = "g",
                calories = 97.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Vanilla extract",
                quantity = 2.0,
                unit = "ml",
                calories = 6.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Butter",
                quantity = 8.0,
                unit = "g",
                calories = 58.0
            ),
            IngredientEntity(
                meal_id = 0,
                name = "Cinnamon",
                quantity = 1.0,
                unit = "g",
                calories = 3.0
            )
        )

        val nutrition = NutritionEntity(
            meal_id = 0,
            energy_kcal = 480.0,
            protein_g = 5.0,
            carbohydrates_g = 60.0,
            fat_g = 20.0,
            fiber_g = 3.5,
            sugars_g = 35.8,
            sodium_mg = 285.0,
            cholesterol_mg = 15.0
        )

        mealDao.insertMealWithDetails(meal, ingredients, nutrition)
    }

    private suspend fun seedNewImageMealsPast6Days(mealDao: MealDao, userId: Long) {
        val meals = listOf(
            Triple("chocolatebrownies12.webp", "12 Chocolate Brownies", listOf(
                IngredientEntity(0L, 0L, "Chocolate", 200.0, "g", 1070.0),
                IngredientEntity(0L, 0L, "Butter", 100.0, "g", 720.0),
                IngredientEntity(0L, 0L, "Sugar", 150.0, "g", 600.0),
                IngredientEntity(0L, 0L, "Eggs", 2.0, "pcs", 140.0),
                IngredientEntity(0L, 0L, "Flour", 100.0, "g", 364.0)
            )),
            Triple("bacon2with1bigomelet6halfpotatoes2breadwithjam.webp", "Bacon & Omelet Breakfast", listOf(
                IngredientEntity(0L, 0L, "Bacon", 60.0, "g", 300.0),
                IngredientEntity(0L, 0L, "Eggs", 2.0, "pcs", 140.0),
                IngredientEntity(0L, 0L, "Potatoes", 150.0, "g", 120.0),
                IngredientEntity(0L, 0L, "Bread", 60.0, "g", 160.0),
                IngredientEntity(0L, 0L, "Jam", 20.0, "g", 50.0)
            )),
            Triple("churros8withchocolate.webp", "8 Churros with Chocolate", listOf(
                IngredientEntity(0L, 0L, "Churros", 8.0, "pcs", 480.0),
                IngredientEntity(0L, 0L, "Chocolate Sauce", 50.0, "g", 250.0)
            )),
            Triple("chipotlesalad.webp", "Chipotle Salad", listOf(
                IngredientEntity(0L, 0L, "Lettuce", 50.0, "g", 8.0),
                IngredientEntity(0L, 0L, "Chipotle Peppers", 20.0, "g", 40.0),
                IngredientEntity(0L, 0L, "Chicken", 100.0, "g", 165.0),
                IngredientEntity(0L, 0L, "Corn", 30.0, "g", 27.0),
                IngredientEntity(0L, 0L, "Beans", 40.0, "g", 60.0)
            )),
            Triple("lettuceonionavocadocutmuschroomshamslices.webp", "Omelette Salad", listOf(
                IngredientEntity(0L, 0L, "Lettuce", 40.0, "g", 6.0),
                IngredientEntity(0L, 0L, "Onion", 20.0, "g", 8.0),
                IngredientEntity(0L, 0L, "Avocado", 50.0, "g", 80.0),
                IngredientEntity(0L, 0L, "Mushrooms", 30.0, "g", 7.0),
                IngredientEntity(0L, 0L, "Ham", 40.0, "g", 60.0)
            )),
            Triple("spaghettibolognese.webp", "Spaghetti Bolognese", listOf(
                IngredientEntity(0L, 0L, "Spaghetti", 100.0, "g", 158.0),
                IngredientEntity(0L, 0L, "Ground Beef", 80.0, "g", 200.0),
                IngredientEntity(0L, 0L, "Tomato Sauce", 60.0, "g", 30.0),
                IngredientEntity(0L, 0L, "Onion", 20.0, "g", 8.0)
            )),
            Triple("veryhealthysaladwithlotsofvegetablecuts.webp", "Very Healthy Salad", listOf(
                IngredientEntity(0L, 0L, "Lettuce", 60.0, "g", 9.0),
                IngredientEntity(0L, 0L, "Tomato", 40.0, "g", 8.0),
                IngredientEntity(0L, 0L, "Cucumber", 40.0, "g", 6.0),
                IngredientEntity(0L, 0L, "Carrot", 30.0, "g", 12.0),
                IngredientEntity(0L, 0L, "Bell Pepper", 30.0, "g", 9.0)
            ))
        )
        for (i in 0 until 6) {
            val (imagePath, mealName, ingredients) = meals[i]
            val meal = MealEntity(
                meal_name = mealName,
                meal_nutrition_score = listOf("A", "B", "C").random(),
                error = null,
                created_at = getPastDateTimestamp(6 - i),
                image_path = imagePath,
                userId = userId
            )
            val nutrition = NutritionEntity(
                meal_id = 0,
                energy_kcal = ingredients.sumOf { it.calories },
                protein_g = 10.0 + i * 2,
                carbohydrates_g = 40.0 + i * 3,
                fat_g = 15.0 + i * 2,
                fiber_g = 5.0 + i * 0.5,
                sugars_g = 10.0 + i,
                sodium_mg = 100.0 + i * 10,
                cholesterol_mg = 0.0 + i * 2
            )
            mealDao.insertMealWithDetails(meal, ingredients, nutrition)
        }
    }

    private fun getTodayTimestamp(time: String): Long {
        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val parsedTime = timeFormat.parse(time)

        parsedTime?.let {
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = it

            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
        }

        return calendar.timeInMillis
    }

    private fun getPastDateTimestamp(daysAgo: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.timeInMillis
    }
}

// Extension function to easily seed database from any context
suspend fun MealDatabase.seedIfEmpty() {
    DatabaseSeeder(this).seedDatabase()
}

// Delte query in console to empty entire database
// DELETE FROM meals;
