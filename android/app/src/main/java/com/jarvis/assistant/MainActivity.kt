package com.jarvis.assistant

import android.app.Activity
import android.os.Bundle
import android.os.StatFs
import android.content.Intent
import android.content.IntentFilter
import android.speech.RecognizerIntent
import android.view.Gravity
import android.widget.*
import android.graphics.Color
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var output: TextView
    private val foreground = Color.rgb(229, 237, 249)
    private val cyan = Color.rgb(103, 232, 249)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }
    private fun render() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 24)
            setBackgroundColor(Color.rgb(11, 18, 32))
        }
        val title = TextView(this).apply {
            text = "JARVIS"; textSize = 30f; setTextColor(cyan); gravity = Gravity.CENTER
        }
        layout.addView(title)
        val subtitle = TextView(this).apply {
            text = "Assistente Android"; setTextColor(foreground); gravity = Gravity.CENTER; textSize = 16f
        }
        layout.addView(subtitle)
        output = TextView(this).apply {
            text = "Sistema pronto. Escolha uma ferramenta."; setTextColor(foreground); textSize = 16f; setPadding(8, 24, 8, 24)
        }
        layout.addView(output)
        addButton(layout, "Informações do aparelho") {
            "Android: " + android.os.Build.VERSION.RELEASE + "\nModelo: " + android.os.Build.MODEL +
                    "\nFabricante: " + android.os.Build.MANUFACTURER
        }
        addButton(layout, "Armazenamento geral") {
            val s = StatFs(android.os.Environment.getDataDirectory().path)
            "Espaço disponível: " + (s.availableBytes / (1024 * 1024)) + " MB\nTotal: " + (s.totalBytes / (1024 * 1024)) + " MB"
        }
        addButton(layout, "Bateria") {
            val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = intent?.getIntExtra("level", -1) ?: -1
            val scale = intent?.getIntExtra("scale", 100) ?: 100
            "Bateria: " + (if (scale > 0 && level >= 0) level * 100 / scale else level) + "%"
        }
        addButton(layout, "Falar com JARVIS (voz)") {
            launchVoiceInput()
            "Abrindo reconhecimento de voz, se disponível…"
        }
        addButton(layout, "Abrir configurações") {
            startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            "Abrindo configurações…"
        }
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }
    private fun addButton(parent: LinearLayout, label: String, action: () -> String) {
        val button = Button(this).apply { text = label }
        button.setOnClickListener { try { output.text = action() } catch (e: Exception) { output.text = "Erro: " + e.localizedMessage } }
        parent.addView(button)
    }
    private fun launchVoiceInput() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("pt", "BR"))
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o JARVIS")
            }
            startActivityForResult(intent, 42)
        } catch (e: Exception) {
            output.text = "Reconhecimento de voz não está disponível neste aparelho."
        }
    }
    @Deprecated("Legacy result callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            output.text = matches?.firstOrNull()?.let { "Você disse: $it\n\nEsta versão inicial ainda não executa comandos de voz." } ?: "Não reconheci a fala."
        }
    }
}
