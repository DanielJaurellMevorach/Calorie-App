package com.example.responsiveness.ui.screens.scanner.components

import android.util.Base64
import android.util.Log
import com.example.responsiveness.BuildConfig
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
    Log.d("OpenAIRequest", "Preparing to send photo to OpenAI: ${photoFile.path}")
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
        //put("version", "6")
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
        Log.d("OpenAIRequest", "Sending request to OpenAI...")
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenAIRequest", "Network request failed", e)
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("OpenAIRequest", "Received response from OpenAI: ${response.code}")
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.d("OpenAIRequest", "Response body: $responseBody")
                        if (responseBody != null) {
                            val extractedResponse = extractResponseContent(responseBody)
                            Log.d("OpenAIRequest", "Extracted response: $extractedResponse")
                            continuation.resume(extractedResponse)
                        } else {
                            Log.e("OpenAIRequest", "Empty response body")
                            continuation.resume("Empty response")
                        }
                    } else {
                        Log.e("OpenAIRequest", "HTTP Error: ${response.code} - ${response.message}")
                        continuation.resume("HTTP Error: ${response.code} - ${response.message}")
                    }
                } catch (e: Exception) {
                    Log.e("OpenAIRequest", "Exception while handling response", e)
                    continuation.resumeWithException(e)
                }
            }
        })
        continuation.invokeOnCancellation {
            Log.d("OpenAIRequest", "Coroutine cancelled, cancelling network call")
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
