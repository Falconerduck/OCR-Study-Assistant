package com.example.aiocr

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aiocr.ui.theme.AIOCRTheme
import com.google.gson.GsonBuilder
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AIOCRTheme {
                OCRAppUI()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OCRAppUI() {
    var resultText by remember { mutableStateOf("Scanned text and summary will appear here...") }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            recognizeTextFromImage(context, it) { result ->
                resultText = result
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("OCR AI Study Assistant") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Scan Image from Gallery")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun recognizeTextFromImage(context: Context, uri: Uri, onResult: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    try {
        val image = InputImage.fromFilePath(context, uri)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extracted = visionText.text
                onResult("OCR Result:\n\n$extracted\n\nSummarizing...")

                summarizeTextWithOpenAI(extracted) { summary ->
                    onResult("OCR Result:\n\n$extracted\n\n--- Summary ---\n$summary")
                }
            }
            .addOnFailureListener { e ->
                onResult("OCR failed: ${e.message}")
            }
    } catch (e: Exception) {
        onResult("Failed to read image: ${e.message}")
    }
}

fun summarizeTextWithOpenAI(text: String, onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val gson = GsonBuilder().setLenient().create()

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://openrouter.ai/") // ✅ OpenRouter API
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val service = retrofit.create(OpenAIService::class.java)

            val response = service.getChatCompletion(
                apiKey = "Bearer ${Secrets.OPENAI_API_KEY}", // your sk-or-v1-... key
                model = "qwen/qwen3-0.6b-04-28:free", // ✅ Free and fast model
                referer = "https://github.com/Falconerduck/OCR-Study-Assistant", // or any valid link
                title = "AI-OCR-Study-Assistant",
                request = ChatRequest(
                    messages = listOf(
                        Message(role = "user", content = "Summarize this:\n$text")
                    )
                )
            )

            val rawJson = response.string()

            val jsonObject = JSONObject(rawJson)
            val summary = jsonObject
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            withContext(Dispatchers.Main) {
                onResult(summary.trim())
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onResult("Summarization failed: ${e.message}")
            }
        }
    }
}



