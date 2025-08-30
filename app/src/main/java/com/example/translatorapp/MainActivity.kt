package com.example.translatorapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class MainActivity : AppCompatActivity() {

    private lateinit var translator: Translator
    private var isModelReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val inputText = findViewById<EditText>(R.id.editText)
        val translateBtn = findViewById<Button>(R.id.button)
        val outputText = findViewById<TextView>(R.id.outputTxt)
        outputText.movementMethod = android.text.method.ScrollingMovementMethod.getInstance()


        // Disable button until model is downloaded
        translateBtn.isEnabled = false
        outputText.text = "Downloading translation model..."

        // Setup translator (English → Hindi for example)
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI) // change target as needed
            .build()

        translator = Translation.getClient(options)

        // Download language model
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                isModelReady = true
                translateBtn.isEnabled = true
                outputText.text = "Model ready! Type text and press Translate."
            }
            .addOnFailureListener { e ->
                outputText.text = "Model download failed: ${e.localizedMessage}"
            }

        // Translate button click
        translateBtn.setOnClickListener {
            val textToTranslate = inputText.text.toString().trim()
            if (isModelReady && textToTranslate.isNotEmpty()) {
                translator.translate(textToTranslate)
                    .addOnSuccessListener { translated ->
                        outputText.text = translated
                    }
                    .addOnFailureListener { e ->
                        outputText.text = "Translation failed: ${e.localizedMessage}"
                    }
            } else {
                outputText.text = "Please enter text or wait for model."
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        translator.close()
    }
}
