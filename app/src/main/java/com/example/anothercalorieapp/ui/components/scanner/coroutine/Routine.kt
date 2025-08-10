package com.example.anothercalorieapp.ui.components.scanner.coroutine

import android.util.Base64
import com.example.anothercalorieapp.BuildConfig
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
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun sendPhotoToOpenAI(photoFile: File): String = withContext(Dispatchers.IO) {
    // Read and encode the image
    val imageBytes = photoFile.readBytes()
    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

    // Build the JSON payload using your proven structure
    val inputContent = JSONArray().apply {
        put(JSONObject().apply {
            put("role", "user")
            put("content", JSONArray().apply {
//                put(JSONObject().apply {
//                    put("type", "input_text")
//                    put("text", "What's in this image? Please describe the food items you can see in detail.")
//                })
                put(JSONObject().apply {
                    put("type", "input_image")
                    put("image_url", "data:image/jpeg;base64,$base64Image")
                })
            })
        })
    }

    val promptObj = JSONObject().apply {
        put("id", BuildConfig.PROMPT_TEMPLATE)
        put("version", "3")
    }

    val json = JSONObject().apply {
        put("model", "gpt-4o-mini")
        put("prompt", promptObj)
        put("input", inputContent)
    }

    // Prepare OkHttp request with longer timeouts
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
    val request = Request.Builder()
        .url("https://api.openai.com/v1/responses")
        .header("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
        .post(body)
        .build()

    // Execute request asynchronously using suspendCancellableCoroutine
    return@withContext suspendCancellableCoroutine { continuation ->
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null) {
                            // Extract the response content
                            val extractedResponse = extractResponseContent(responseBody)
                            continuation.resume(extractedResponse)
                        } else {
                            continuation.resume("Empty response")
                        }
                    } else {
                        continuation.resume("HTTP Error: ${response.code} - ${response.message}")
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

private fun extractResponseContent(responseBody: String): String {
    return try {
        val responseJson = JSONObject(responseBody)

        // Navigate through the nested structure to find the actual response content
        val output = responseJson.optJSONArray("output")
        if (output != null && output.length() > 0) {
            val firstOutput = output.getJSONObject(0)
            val content = firstOutput.optJSONArray("content")
            if (content != null && content.length() > 0) {
                val firstContent = content.getJSONObject(0)
                val text = firstContent.optString("text")
                if (text.isNotEmpty()) {
                    return text
                }
            }
        }

        // If we can't extract the content, return the full response for debugging
        responseBody
    } catch (e: Exception) {
        "Failed to parse OpenAI response: ${e.message}\n\nRaw response: $responseBody"
    }
}
