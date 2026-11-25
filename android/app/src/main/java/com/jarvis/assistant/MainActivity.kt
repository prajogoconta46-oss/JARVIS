package com.jarvis.assistant
import android.app.Activity
import android.os.Bundle
import android.os.StatFs
import android.view.Gravity
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); render() }
 private fun render() {
  val layout=LinearLayout(this); layout.orientation=LinearLayout.VERTICAL; layout.setPadding(26,30,26,26); layout.setBackgroundColor(Color.rgb(11,18,32))
  val title=TextView(this); title.text="JARVIS"; title.textSize=30f; title.setTextColor(Color.rgb(103,232,249)); title.gravity=Gravity.CENTER; layout.addView(title)
  val subtitle=TextView(this); subtitle.text="Assistente Android"; subtitle.setTextColor(Color.WHITE); subtitle.gravity=Gravity.CENTER; layout.addView(subtitle)
  val output=TextView(this); output.text="Sistema pronto. Escolha uma ferramenta."; output.setTextColor(Color.WHITE); output.textSize=16f; output.setPadding(10,24,10,24); layout.addView(output)
  fun addButton(label:String,action:()->String) { val b=Button(this); b.text=label; b.setOnClickListener { output.text=action() }; layout.addView(b) }
  addButton("Informações do aparelho") { "Android: "+android.os.Build.VERSION.RELEASE+"\nModelo: "+android.os.Build.MODEL+"\nFabricante: "+android.os.Build.MANUFACTURER }
  addButton("Armazenamento (partição do app)") { val s=StatFs(filesDir.path); "Livre: "+(s.availableBytes/(1024*1024))+" MB\nTotal: "+(s.totalBytes/(1024*1024))+" MB" }
  addButton("Bateria") { val i=registerReceiver(null,android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED)); val level=i?.getIntExtra("level",-1) ?: -1; val scale=i?.getIntExtra("scale",100) ?: 100; "Bateria: "+(if(scale>0) level*100/scale else level)+"%" }
  addButton("Abrir configurações") { startActivity(android.content.Intent(android.provider.Settings.ACTION_SETTINGS)); "Abrindo configurações…" }
  val scroll=ScrollView(this); scroll.addView(layout); setContentView(scroll)
 }
}
