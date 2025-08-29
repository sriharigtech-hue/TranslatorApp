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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editText)
        val button = findViewById<Button>(R.id.button)
        val outputTxt = findViewById<TextView>(R.id.outputTxt)

        // Create an English-German translator
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()

        translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        // Download translation model if needed
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                button.setOnClickListener {
                    val textToTranslate = editText.text.toString()
                    translateText(textToTranslate, outputTxt)
                }
            }
            .addOnFailureListener {
                outputTxt.text = "Model download failed"
            }
    }

    private fun translateText(inputText: String, outputTxt: TextView) {
        translator.translate(inputText)
            .addOnSuccessListener { translatedText ->
                // Translation successful
                outputTxt.text = translatedText
            }
            .addOnFailureListener {
                outputTxt.text = "Translation failed"
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        translator.close()
    }
}
