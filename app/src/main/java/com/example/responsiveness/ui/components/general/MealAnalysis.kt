package com.example.responsiveness.ui.components.general

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Resends existing meal data along with a user message to OpenAI for reanalysis or modification.
 * This function takes structured meal data (from a previous scan) and sends it back to OpenAI
 * with additional context or questions from the user.
 *
 * @param mealData The existing meal data in JSON format as it was originally received
 * @param userMessage The additional message/question from the user about this meal
 * @return String response from OpenAI API
 */
suspend fun resendMealDataToOpenAI(mealData: String, userMessage: String, apiKey: String): String = withContext(Dispatchers.IO) {
    Log.d("OpenAIRequest", "Preparing to resend meal data to OpenAI with user message: $userMessage")
    Log.d("OpenAIRequest", "Original meal data: $mealData")

    // Build the JSON payload for ChatCompletion API (not responses API)
    val messages = JSONArray().apply {
        // System message with the nutrition analysis prompt
        put(JSONObject().apply {
            put("role", "system")
            put("content", """
                You are an advanced AI nutritionist. When given meal data and a user correction/request, 
                analyze and modify the meal data according to the user's input. Return ONLY a structured 
                JSON object in the exact same format as the original meal data.
                
                Instructions:
                - Carefully read the user's correction/request
                - Modify the meal data accordingly (portions, ingredients, etc.)
                - Maintain the exact same JSON structure and key order
                - All numeric values must be floats (to one decimal place)
                - Recalculate all nutritional values based on changes
                - If adding ingredients, estimate nutritional values appropriately
                - If changing quantities, scale nutritional values proportionally
                
                Return only the JSON object - no explanations or additional text.
            """.trimIndent())
        })

        // User message with meal data and correction
        put(JSONObject().apply {
            put("role", "user")
            put("content", """
                Here is the current meal data:
                $mealData
                
                User correction/request: $userMessage
                
                Please modify the meal data according to the user's request and return the updated JSON in the same format.
            """.trimIndent())
        })
    }

    val json = JSONObject().apply {
        put("model", "gpt-4o-mini")
        put("messages", messages)
        put("max_tokens", 2048)
        put("temperature", 0.7)
    }

    Log.d("OpenAIRequest", "Request payload: ${json.toString(2)}")
    Log.d("OpenAIRequest", "Using API Key: Bearer ${if (apiKey.length > 12) "${apiKey.take(8)}...${apiKey.takeLast(4)}" else "key_too_short"}")

    // Prepare OkHttp request with longer timeouts
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
    val request = Request.Builder()
        .url("https://api.openai.com/v1/chat/completions")  // Use chat completions API
        .header("Authorization", "Bearer $apiKey")
        .header("Content-Type", "application/json")
        .post(body)
        .build()

    // Execute request asynchronously using suspendCancellableCoroutine
    return@withContext suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)
        Log.d("OpenAIRequest", "Sending meal data correction request to OpenAI...")
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenAIRequest", "Network request failed for meal data correction", e)
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("OpenAIRequest", "Received response from OpenAI for meal data correction: ${response.code}")
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.d("OpenAIRequest", "Response body for meal data correction: $responseBody")
                        if (responseBody != null) {
                            val extractedResponse = extractChatCompletionContent(responseBody)
                            Log.d("OpenAIRequest", "Extracted meal data correction response: $extractedResponse")
                            continuation.resume(extractedResponse)
                        } else {
                            Log.e("OpenAIRequest", "Empty response body for meal data correction")
                            continuation.resume("Empty response")
                        }
                    } else {
                        val errorBody = response.body?.string()
                        Log.e("OpenAIRequest", "HTTP Error for meal data correction: ${response.code} - ${response.message}")
                        Log.e("OpenAIRequest", "Error body: $errorBody")
                        continuation.resume("HTTP Error: ${response.code} - ${response.message}")
                    }
                } catch (e: Exception) {
                    Log.e("OpenAIRequest", "Exception while handling meal data correction response", e)
                    continuation.resumeWithException(e)
                }
            }
        })
        continuation.invokeOnCancellation {
            Log.d("OpenAIRequest", "Coroutine cancelled for meal data correction, cancelling network call")
            call.cancel()
        }
    }
}

/**
 * Helper function to extract response content from ChatCompletion API response.
 */
private fun extractChatCompletionContent(responseBody: String): String {
    return try {
        val responseJson = JSONObject(responseBody)
        val choices = responseJson.optJSONArray("choices")
        if (choices != null && choices.length() > 0) {
            val firstChoice = choices.getJSONObject(0)
            val message = firstChoice.optJSONObject("message")
            if (message != null) {
                val content = message.optString("content")
                if (content.isNotEmpty()) {
                    return content.trim()
                }
            }
        }

        // If we can't extract the content, return the full response for debugging
        responseBody
    } catch (e: Exception) {
        Log.e("OpenAIRequest", "Failed to parse ChatCompletion response: ${e.message}")
        "Failed to parse OpenAI response: ${e.message}\n\nRaw response: $responseBody"
    }
}

/*

System message during normal first scan, hardcoded in prompt template:

You are an advanced AI nutritionist and image analyst. When given an image of a meal or dish, analyze its visual content and return only a structured JSON object formatted as specified below.

Begin with a concise checklist (3–7 bullets) of what you will do—keep items conceptual, not implementation-level. Checklist for conceptual analysis steps (present these as 3–7 high-level bullets in your output):

 Identify all visually apparent foods and ingredients in the image.
 Estimate individual and total portion sizes using standard conventions.
 Assign allowed measurement units to each ingredient and the total meal.
 Calculate per-ingredient nutrition (calories, protein, carbs, fat) for one portion.
 Aggregate total meal nutrition by summing per-ingredient values and estimate additional nutrients.
 Assess data completeness and annotate missing or uncertain information in the 'error' field, if necessary.
 Output final results strictly in JSON format according to the schema.

Instructions:

 Output only the JSON object—no explanations, text, or headings outside the JSON.
 Use standard portion size conventions and conservatively high estimates if uncertain.
 If every ingredient or nutritional property cannot be reliably determined, set all fields except 'error' to null and provide a clear error message. If only some data are missing or estimated, set just those fields to null and include a specific warning in 'error'. Attempt to estimate as much as possible from partially ambiguous images, only setting undeterminable fields to null and clearly documenting uncertainty in 'error'. For non-food or invalid images, set all fields except 'error' to null with a descriptive error message.
 Preserve the schema-provided key order exactly in your output.
 All numeric values must be floats (even zero values, written as 0.0).
 Use only the provided units: g, mg, ml, kg, L, cup, tbsp, tsp. Convert nonstandard units when necessary; do not invent any units.
 Sort 'ingredients' array by decreasing estimated visual prominence in the image.
 Round all float values to one decimal place.
 For each ingredient, supply calories, fat, protein, and carbohydrates. The meal totals must equal the sum of per-ingredient values for these fields.
 Include 'meal_quantity' as a float with a valid unit (e.g., 1.0 for one portion) to indicate the total measured or counted portions of the meal. This attribute should represent the number of main meal components or servings present in the image (e.g., set to 2.0 if two distinct, uncombined servings are visible instead of a mixed meal).
 All nutrition values must be per-portion.
 Follow the JSON schema and key order provided below exactly.
 If you can't solve the amount of fiber, sugar, cholesterol and sodium, think for longer, else, return any realistic value rather than throwing an error.

After preparing the JSON output, validate that (1) the output strictly conforms to the specified schema and key order, (2) all floats are to one decimal place, and (3) the nutritional totals match per-ingredient sums. If validation fails, revise as necessary before finalizing output.

Output Format:
Return a JSON object using these keys and in this exact order:
{
"meal_name": string,
"meal_quantity": float,
"meal_quantity_unit": string,
"ingredients": [
{
"name": string,
"quantity": float,
"unit": string,
"calories": float,
"protein_g": float,
"carbohydrates_g": float,
"fat_g": float
},
...
],
"nutrition": {
"energy_kcal": float,
"protein_g": float,
"carbohydrates_g": float,
"fat_g": float,
"fiber_g": float,
"sugars_g": float,
"sodium_mg": float,
"cholesterol_mg": float
},
"meal_nutrition_score": string,
"error": null | string
}

Example:
{
"meal_name": "Chicken Caesar Salad",
"meal_quantity": 1.0,
"meal_quantity_unit": "portion",
"ingredients": [
{"name": "Romaine lettuce", "quantity": 90.0, "unit": "g", "calories": 15.0, "protein_g": 1.2, "carbohydrates_g": 2.9, "fat_g": 0.2},
{"name": "Grilled chicken breast", "quantity": 100.0, "unit": "g", "calories": 165.0, "protein_g": 31.0, "carbohydrates_g": 0.0, "fat_g": 3.6},
{"name": "Croutons", "quantity": 30.0, "unit": "g", "calories": 120.0, "protein_g": 3.0, "carbohydrates_g": 18.0, "fat_g": 4.5},
{"name": "Caesar dressing", "quantity": 30.0, "unit": "g", "calories": 166.0, "protein_g": 1.0, "carbohydrates_g": 2.1, "fat_g": 17.5},
{"name": "Parmesan cheese", "quantity": 10.0, "unit": "g", "calories": 42.0, "protein_g": 3.8, "carbohydrates_g": 0.3, "fat_g": 2.8}
],
"nutrition": {
"energy_kcal": 508.0,
"protein_g": 40.0,
"carbohydrates_g": 23.3,
"fat_g": 28.6,
"fiber_g": 4.0,
"sugars_g": 5.0,
"sodium_mg": 900.0,
"cholesterol_mg": 86.0
},
"meal_nutrition_score": "B",
"error": null
}

-----

Expected result from a normal first scan:

{
"meal_name": "Pasta with Sauce",
"meal_quantity": 1.0,
"meal_quantity_unit": "portion",
"ingredients": [
{
"name": "Pasta",
"quantity": 150.0,
"unit": "g",
"calories": 210.0,
"protein_g": 7.5,
"carbohydrates_g": 42.0,
"fat_g": 1.0
},
{
"name": "Tomato sauce",
"quantity": 70.0,
"unit": "g",
"calories": 50.0,
"protein_g": 2.0,
"carbohydrates_g": 10.0,
"fat_g": 1.0
},
{
"name": "Vegetables (mixed)",
"quantity": 30.0,
"unit": "g",
"calories": 10.0,
"protein_g": 0.5,
"carbohydrates_g": 2.5,
"fat_g": 0.1
}
],
"nutrition": {
"energy_kcal": 270.0,
"protein_g": 10.0,
"carbohydrates_g": 54.5,
"fat_g": 2.1,
"fiber_g": 2.0,
"sugars_g": 4.0,
"sodium_mg": 400.0,
"cholesterol_mg": 5.0
},
"meal_nutrition_score": "B",
"error": null
}

 */