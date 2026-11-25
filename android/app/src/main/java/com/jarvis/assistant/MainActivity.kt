package com.jarvis.assistant

import android.app.Activity
import android.os.Bundle
import android.os.StatFs
import android.content.Intent
import android.content.IntentFilter
import android.speech.RecognizerIntent
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.graphics.Color
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var output: TextView

    private val textColor: Int = Color.parseColor("#E5EDF9")
    private val accentColor: Int = Color.parseColor("#67E8F9")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    private fun render() {
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        content.setPadding(24, 30, 24, 24)
        content.setBackgroundColor(Color.parseColor("#0B1220"))

        val title = TextView(this)
        title.text = "JARVIS"
        title.textSize = 30f
        title.setTextColor(Color.parseColor("#67E8F9"))
        title.gravity = Gravity.CENTER
        content.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Assistente Android"
        subtitle.setTextColor(Color.parseColor("#E5EDF9"))
        subtitle.gravity = Gravity.CENTER
        subtitle.textSize = 16f
        content.addView(subtitle)

        output = TextView(this)
        output.text = "Sistema pronto. Escolha uma ferramenta."
        output.setTextColor(Color.parseColor("#E5EDF9"))
        output.textSize = 16f
        output.setPadding(8, 24, 8, 24)
        content.addView(output)

        addButton(content, "Informações do aparelho") {
            "Android: ${android.os.Build.VERSION.RELEASE}\nModelo: ${android.os.Build.MODEL}\nFabricante: ${android.os.Build.MANUFACTURER}"
        }
        addButton(content, "Armazenamento geral") {
            val stats = StatFs(android.os.Environment.getDataDirectory().path)
            "Espaço disponível: ${stats.availableBytes / (1024 * 1024)} MB\nTotal: ${stats.totalBytes / (1024 * 1024)} MB"
        }
        addButton(content, "Bateria") {
            val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = battery?.getIntExtra("level", -1) ?: -1
            val scale = battery?.getIntExtra("scale", 100) ?: 100
            "Bateria: ${if (scale > 0 && level >= 0) level * 100 / scale else level}%"
        }
        addButton(content, "Falar com JARVIS (voz)") {
            launchVoiceInput()
            "Abrindo reconhecimento de voz, se disponível..."
        }
        addButton(content, "Abrir configurações") {
            startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
            "Abrindo configurações..."
        }

        val scroll = ScrollView(this)
        scroll.addView(content)
        setContentView(scroll)
    }

    private fun addButton(parent: LinearLayout, label: String, action: () -> String) {
        val button = Button(this)
        button.text = label
        button.setOnClickListener {
            try {
                output.text = action()
            } catch (error: Exception) {
                output.text = "Erro: ${error.localizedMessage ?: "desconhecido"}"
            }
        }
        parent.addView(button)
    }

    private fun launchVoiceInput() {
        try {
            val voiceIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("pt", "BR"))
            voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o JARVIS")
            startActivityForResult(voiceIntent, 42)
        } catch (error: Exception) {
            output.text = "Reconhecimento de voz não está disponível neste aparelho."
        }
    }

    @Deprecated("Legacy result callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            output.text = results?.firstOrNull()?.let {
                "Você disse: $it\n\nEsta versão inicial ainda não executa comandos de voz."
            } ?: "Não reconheci a fala."
        }
    }
}
