package com.jarvis.assistant

import android.Manifest
import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.*
import android.graphics.drawable.GradientDrawable
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var output: TextView
    private lateinit var chat: LinearLayout
    private lateinit var input: EditText
    private lateinit var scroll: ScrollView
    private lateinit var tts: TextToSpeech
    private var speakReplies = false
    private val prefs by lazy { getSharedPreferences("jarvis_memory", MODE_PRIVATE) }
    private val accent = 0xff67e8f9.toInt()
    private val primaryTextColor = 0xffe5edf9.toInt()
    private lateinit var voiceToggle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        render()
        appendMessage("JARVIS", "Olá! Sou o JARVIS Android Ultra. Pode conversar por texto ou usar o botão de voz. Os comandos são processados localmente.")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) tts.language = Locale("pt", "BR")
    }

    private fun render() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 22, 18, 18)
            setBackgroundColor(0xff080f1d.toInt())
        }
        val heading = TextView(this).apply {
            this.text = "JARVIS ULTRA"
            textSize = 28f; setTextColor(accent); gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
            setPadding(0, 6, 0, 2)
        }
        root.addView(heading)
        root.addView(TextView(this).apply {
            this.text = "ASSISTENTE ANDROID • OFFLINE • CONTROLE AUTORIZADO"
            textSize = 11f; setTextColor(0xff94a3b8.toInt()); gravity = Gravity.CENTER
        })
        output = TextView(this).apply {
            text = "Sistema pronto."
            textSize = 13f; setTextColor(primaryTextColor); setPadding(12, 12, 12, 12)
            background = roundedPanel(0xff142238.toInt(), 16f)
        }
        root.addView(output)
        scroll = ScrollView(this).apply {
            background = roundedPanel(0xff101b2c.toInt(), 16f)
            clipToPadding = false
        }
        chat = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(10, 10, 10, 10) }
        scroll.addView(chat)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        input = EditText(this).apply {
            hint = "Escreva um comando ou mensagem..."
            setHintTextColor(0xff94a3b8.toInt()); setTextColor(primaryTextColor)
            setSingleLine(true); setPadding(14, 12, 14, 12)
            background = roundedPanel(0xff142238.toInt(), 14f)
            textSize = 15f
        }
        root.addView(input)
        val sendRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        addButton(sendRow, "Enviar", accent) { process(input.text.toString()); input.setText("") }
        addButton(sendRow, "🎙 Voz", 0xff24364f.toInt()) { requestVoice() }
        voiceToggle = addButton(sendRow, if (speakReplies) "🔊 Voz ligada" else "🔇 Voz desligada", 0xff24364f.toInt()) {
            speakReplies = !speakReplies
            voiceToggle.text = if (speakReplies) "🔊 Voz ligada" else "🔇 Voz desligada"
            respond("Leitura das respostas " + if (speakReplies) "ativada." else "desativada.")
        }
        root.addView(sendRow)
        val tools = listOf(
            "Diagnóstico" to { showDiagnostics() },
            "Segurança" to { securityAudit() },
            "Aplicações" to { showApps() },
            "Wi-Fi / DNS" to { openSetting(Settings.ACTION_WIRELESS_SETTINGS) },
            "Arquivos" to { openFiles() },
            "Notificações" to { openNotificationSettings() },
            "Localizar" to { requestLocation() },
            "Lembrete" to { createReminder() }
        )
        val grid = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        tools.chunked(2).forEach { pair ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            pair.forEach { (label, action) -> addButton(row, label, 0xff1e3350.toInt(), action) }
            grid.addView(row)
        }
        root.addView(grid)
        setContentView(root)
    }

    private fun addButton(parent: LinearLayout, label: String, color: Int = 0xff24364f.toInt(), action: () -> Unit): Button {
        val b = Button(this).apply {
            text = label; setTextColor(primaryTextColor); background = roundedPanel(color, 14f); textSize = 12f
            isAllCaps = false; setOnClickListener { try { action() } catch (e: Exception) { respond("Não foi possível concluir: ${e.message ?: "erro desconhecido"}") } }
        }
        parent.addView(b, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(4, 4, 4, 4) })
        return b
    }

    private fun roundedPanel(color: Int, radiusDp: Float): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusDp * resources.displayMetrics.density
            setStroke(1, 0x334b6688)
        }

    private fun appendMessage(who: String, message: String) {
        val line = TextView(this).apply {
            this.text = "$who: $message"; textSize = 14f; setTextColor(if (who == "JARVIS") accent else primaryTextColor)
            setPadding(5, 8, 5, 8)
        }
        chat.addView(line); scroll.post { scroll.fullScroll(View.FOCUS_DOWN) }
    }
    private fun respond(message: String) {
        output.text = message
        appendMessage("JARVIS", message)
        if (speakReplies && ::tts.isInitialized) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "jarvis")
    }
    private fun renderStatus(message: String) { output.text = message }

    private fun process(raw: String) {
        val q = raw.trim()
        if (q.isBlank()) return
        appendMessage("Você", q)
        val s = q.lowercase(Locale("pt", "BR")).normalize()
        when {
            s.contains("oi") || s.contains("olá") || s.contains("ola") || s.contains("bom dia") -> respond("Olá! Estou pronto. Pode pedir um diagnóstico, abrir aplicações, consultar bateria ou configurar uma rotina.")
            s.contains("bateria") -> batteryInfo()
            s.contains("ram") || s.contains("memória") || s.contains("memoria") -> memoryInfo()
            s.contains("armazenamento") || s.contains("espaço") -> storageInfo()
            s.contains("temperatura") || s.contains("cpu") || s.contains("desempenho") -> performanceInfo()
            s.contains("diagnóstico") || s.contains("diagnostico") -> showDiagnostics()
            s.contains("segurança") || s.contains("seguranca") || s.contains("criptografia") -> securityAudit()
            s.contains("wifi") || s.contains("wi-fi") -> openSetting(Settings.ACTION_WIFI_SETTINGS)
            s.contains("bluetooth") -> openSetting(Settings.ACTION_BLUETOOTH_SETTINGS)
            s.contains("dns") || s.contains("anúncio") || s.contains("anuncio") -> openSetting(if (Build.VERSION.SDK_INT >= 28) "android.settings.PRIVATE_DNS_SETTINGS" else Settings.ACTION_WIRELESS_SETTINGS)
            s.contains("notificação") || s.contains("notificacao") -> openNotificationSettings()
            s.contains("localizar") || s.contains("minha localização") -> requestLocation()
            s.contains("arquivo") || s.contains("pasta") -> openFiles()
            s.contains("lembrete") || s.contains("agendar") -> createReminder()
            s.contains("aplicativos") || s.contains("aplicacoes") || s.contains("apps") -> showApps()
            s.contains("whatsapp") -> launchPackage("com.whatsapp")
            s.contains("youtube") -> launchPackage("com.google.android.youtube")
            s.contains("chrome") -> launchPackage("com.android.chrome")
            s.contains("instagram") -> launchPackage("com.instagram.android")
            s.contains("hora") || s.contains("data") -> respond("Agora: " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date()))
            s.startsWith("lembrar ") -> { prefs.edit().putString("last_note", q.removePrefix("lembrar ")).apply(); respond("Guardei esta nota localmente: " + q.removePrefix("lembrar ")) }
            s.contains("minha nota") || s.contains("o que pedi para lembrar") -> respond("Sua última nota: " + (prefs.getString("last_note", "Ainda não há notas guardadas.") ?: "Sem nota."))
            else -> respond("Estou a funcionar offline. Ainda não tenho IA generativa nesta versão, mas posso executar comandos locais: bateria, RAM, armazenamento, segurança, Wi-Fi, DNS, notificações, localização, ficheiros, lembretes e abertura de apps.")
        }
    }

    private fun String.normalize(): String =
        java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
    private fun batteryInfo() {
        val b = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = b?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = b?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val temp = b?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        val status = b?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        respond("Bateria: ${if (level >= 0 && scale > 0) level * 100 / scale else "não informada"}%. Temperatura: ${if (temp >= 0) "%.1f".format(temp / 10.0) + " °C" else "indisponível"}. Estado: ${when(status) { BatteryManager.BATTERY_STATUS_CHARGING -> "carregando"; BatteryManager.BATTERY_STATUS_FULL -> "completa"; BatteryManager.BATTERY_STATUS_DISCHARGING -> "descarregando"; else -> "não informado" }}.")
    }
    private fun memoryInfo() {
        val mi = ActivityManager.MemoryInfo(); (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(mi)
        respond("RAM total: ${mi.totalMem / 1048576} MB. Disponível: ${mi.availMem / 1048576} MB. Estes valores são métricas do sistema, não memória livre para acelerar aplicações.")
    }
    private fun storageInfo() {
        val st = StatFs(android.os.Environment.getDataDirectory().path)
        respond("Armazenamento: ${st.availableBytes / 1073741824.0} GB livres de ${st.totalBytes / 1073741824.0} GB.")
    }
    private fun performanceInfo() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        val thermal = if (Build.VERSION.SDK_INT >= 29) pm.currentThermalStatus.toString() else "não suportado"
        respond("Núcleos lógicos: ${Runtime.getRuntime().availableProcessors()}. Economia de energia: ${pm.isPowerSaveMode}. Estado térmico Android: $thermal.")
    }
    private fun showDiagnostics() {
        val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = battery?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = battery?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val batteryPercent = if (level >= 0 && scale > 0) (level * 100 / scale).toString() + "%" else "indisponível"
        val mi = ActivityManager.MemoryInfo()
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(mi)
        val st = StatFs(android.os.Environment.getDataDirectory().path)
        respond("Diagnóstico: ${Build.MANUFACTURER} ${Build.MODEL}; Android ${Build.VERSION.RELEASE}; bateria $batteryPercent; RAM livre ${mi.availMem / 1048576} MB; armazenamento livre ${st.availableBytes / 1073741824} GB.")
    }
    private fun securityAudit() {
        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val adb = try { Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1 } catch (_: Exception) { false }
        val encrypted = try { (getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager).storageEncryptionStatus } catch (_: Exception) { -1 }
        respond("Bloqueio seguro: ${km.isDeviceSecure}. Depuração USB: $adb. Estado de criptografia reportado pelo Android: $encrypted. Isto não deteta invasores nem substitui uma análise antivírus. Abra Play Protect para verificar apps.")
    }
    private fun showApps() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = packageManager.queryIntentActivities(intent, 0).sortedBy { it.loadLabel(packageManager).toString() }
        val names = apps.take(35).joinToString(", ") { it.loadLabel(packageManager).toString() }
        respond("Aplicações encontradas: " + if (names.isBlank()) "lista indisponível" else names)
    }
    private fun launchPackage(pkg: String) {
        val i = packageManager.getLaunchIntentForPackage(pkg)
        if (i == null) respond("Esta aplicação não está instalada ou o Android não permite encontrá-la.")
        else { startActivity(i); respond("A abrir a aplicação solicitada.") }
    }
    private fun openSetting(action: String) {
        try { startActivity(Intent(action)); respond("Abri as definições do Android. Confirme e altere a opção manualmente.") }
        catch (_: Exception) { respond("Esta página de definições não está disponível neste aparelho.") }
    }
    private fun openFiles() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*")
        startActivityForResult(i, 70)
    }
    private fun openNotificationSettings() {
        val enabled = Settings.Secure.getString(contentResolver, "enabled_notification_listeners") ?: ""
        if (enabled.contains(packageName)) respond("Acesso a notificações já autorizado. O JARVIS só regista contagem e pacote da última notificação.")
        else { startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")); respond("Abra a lista e autorize o acesso às notificações se desejar. Pode revogar quando quiser.") }
    }
    private fun requestLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 81)
            return
        }
        try {
            val lm = getSystemService(LOCATION_SERVICE) as LocationManager
            val last = lm.getProviders(true).mapNotNull { provider -> try { lm.getLastKnownLocation(provider) } catch (_: SecurityException) { null } }.maxByOrNull { it.time }
            if (last == null) respond("Não há localização recente. Ative a localização do Android e tente novamente.")
            else respond("Última localização conhecida: ${last.latitude}, ${last.longitude}. Precisão aproximada: ${if (last.hasAccuracy()) last.accuracy.toInt().toString() + " m" else "não informada"}.")
        } catch (_: Exception) { respond("Não foi possível consultar a localização.") }
    }
    private fun createReminder() {
        val edit = EditText(this).apply { hint = "Texto do lembrete" }
        AlertDialog.Builder(this).setTitle("Novo lembrete").setView(edit)
            .setPositiveButton("Daqui a 10 minutos") { _, _ -> scheduleReminder(edit.text.toString(), 10 * 60 * 1000L) }
            .setNeutralButton("Daqui a 1 hora") { _, _ -> scheduleReminder(edit.text.toString(), 60 * 60 * 1000L) }
            .setNegativeButton("Cancelar", null).show()
    }
    private fun scheduleReminder(title: String, delay: Long) {
        val i = Intent(this, ReminderReceiver::class.java).putExtra("title", title.ifBlank { "Lembrete do JARVIS" })
        val pi = PendingIntent.getBroadcast(this, (System.currentTimeMillis() % Int.MAX_VALUE).toInt(), i, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarm = getSystemService(ALARM_SERVICE) as AlarmManager
        alarm.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi)
        respond("Lembrete agendado. O Android apresentará uma notificação no horário escolhido.")
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 81 && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) requestLocation()
        else if (requestCode == 81) respond("Permissão de localização não concedida.")
    }
    private fun requestVoice() {
        try {
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale com o JARVIS")
            startActivityForResult(i, 42)
        } catch (_: Exception) { respond("Reconhecimento de voz não está disponível.") }
    }
    @Deprecated("Legacy callback")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 42 && resultCode == RESULT_OK) {
            val spoken = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) process(spoken) else respond("Não reconheci a fala.")
        } else if (requestCode == 70 && resultCode == RESULT_OK) {
            respond("Documento selecionado: ${data?.data?.lastPathSegment ?: "ficheiro"}")
        }
    }
    override fun onDestroy() {
        if (::tts.isInitialized) { tts.stop(); tts.shutdown() }
        super.onDestroy()
    }
}