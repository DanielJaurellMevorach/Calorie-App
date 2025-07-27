package com.example.caloriecounter.ui.components.openai

import android.util.Base64
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// to be called from a coroutine
suspend fun analyzeMealImageWithOpenAI(
    apiKey: String,
    imagePath: String
): String {
    // Read and encode the image
    val imageFile = File(imagePath)
    val imageBytes = imageFile.readBytes()
    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

    // Build the JSON payload
    val inputContent = JSONArray().apply {
        put(JSONObject().apply {
            put("role", "user")
            put("content", JSONArray().apply {
                put(JSONObject().apply {
                    put("type", "input_image")
                    put("image_url", "data:image/jpeg;base64,$base64Image")
                })
            })
        })
    }

    val promptObj = JSONObject().apply {
        put("id", "pmpt_68863aeafeac81938e914c1c5c839b130b2c29fc60d7fe24")
        put("version", "1")
    }

    val json = JSONObject().apply {
        put("model", "gpt-4o-mini")
        put("prompt", promptObj)
        put("input", inputContent)
    }

    // Prepare OkHttp request
    val client = OkHttpClient()
    val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
    val request = Request.Builder()
        .url("https://api.openai.com/v1/responses")
        .header("Authorization", "Bearer $apiKey")
        .post(body)
        .build()

    // Execute request asynchronously using suspendCancellableCoroutine
    return suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        // Parse the OpenAI response and extract the actual nutrition JSON
                        val extractedJson = extractNutritionDataFromResponse(responseBody)
                        continuation.resume(extractedJson)
                    } else {
                        continuation.resume("Empty response")
                    }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        })

        // Handle cancellation
        continuation.invokeOnCancellation {
            call.cancel()
        }
    }
}

private fun extractNutritionDataFromResponse(responseBody: String): String {
    return try {
        val responseJson = JSONObject(responseBody)

        // Navigate through the nested structure to find the actual nutrition data
        val output = responseJson.optJSONArray("output")
        if (output != null && output.length() > 0) {
            val firstOutput = output.getJSONObject(0)
            val content = firstOutput.optJSONArray("content")
            if (content != null && content.length() > 0) {
                val firstContent = content.getJSONObject(0)
                val text = firstContent.optString("text")
                if (text.isNotEmpty()) {
                    // The text field contains the actual nutrition JSON
                    return text
                }
            }
        }

        // If we can't extract the nutrition data, return a fallback
        createFallbackNutritionJson("Could not extract nutrition data from response")
    } catch (e: Exception) {
        createFallbackNutritionJson("Failed to parse OpenAI response: ${e.message}")
    }
}

private fun createFallbackNutritionJson(errorMessage: String): String {
    return """
    {
        "meal_name": "Analysis Failed",
        "ingredients": [],
        "nutrition": {
            "energy_kcal": 0.0,
            "protein_g": 0.0,
            "carbohydrates_g": 0.0,
            "fat_g": 0.0,
            "fiber_g": 0.0,
            "sugars_g": 0.0,
            "sodium_mg": 0.0,
            "cholesterol_mg": 0.0,
            "water_l": 0.0
        },
        "meal_nutrition_score": "F",
        "error": "$errorMessage"
    }
    """.trimIndent()
}
