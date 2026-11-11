package com.mascotavirtual

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.Manifest
import android.content.pm.PackageManager
import android.widget.*
import android.view.Gravity

class MainActivity : Activity() {
 private val requestNotifications=41
 private lateinit var layout: LinearLayout
 private lateinit var person: EditText
 private lateinit var petName: EditText
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); showSetup() }
 private fun showSetup(){
  layout=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(26,24,26,24) }
  layout.addView(TextView(this).apply { text="🐏 Soy tu compañero\n\n¡Hola! Soy un carnerito y voy a acompañarte por la pantalla. Primero quiero conocerte."; textSize=21f; gravity=Gravity.CENTER })
  person=EditText(this).apply { hint="¿Cómo te llamas?"; setSingleLine(true); setText(getSharedPreferences("pet",0).getString("person","")) }
  petName=EditText(this).apply { hint="¿Cómo quieres que me llame?"; setSingleLine(true); setText(getSharedPreferences("pet",0).getString("petName","")) }
  layout.addView(person);layout.addView(petName)
  addButton("Guardar nombres") {
   val p=person.text.toString().trim();val n=petName.text.toString().trim()
   if(p.isBlank()){person.error="Dime cómo te llamas";return@addButton}
   getSharedPreferences("pet",0).edit().putString("person",p).putString("petName",n.ifBlank{"Compañero"}).apply()
   Toast.makeText(this,"¡Mucho gusto, $p!",Toast.LENGTH_SHORT).show()
  }
  layout.addView(TextView(this).apply { text="\nPara que pueda acompañarte incluso cuando cambies de aplicación, activa estos permisos. Android siempre te pedirá confirmarlos."; textSize=15f })
  addButton("1 · Permitir aparecer sobre otras aplicaciones") {
   if(!Settings.canDrawOverlays(this)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:$packageName")))
   else Toast.makeText(this,"Permiso de superposición concedido",Toast.LENGTH_SHORT).show()
  }
  addButton("2 · Permitir actividad sin restricciones de batería") {
   val pm=getSystemService(POWER_SERVICE) as PowerManager
   if(Build.VERSION.SDK_INT>=23&&!pm.isIgnoringBatteryOptimizations(packageName)){
    try{startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,Uri.parse("package:$packageName")))}
    catch(_:Exception){startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))}
   }else Toast.makeText(this,"La batería ya está configurada",Toast.LENGTH_SHORT).show()
  }
  addButton("3 · Activar avisos de mensajes y clima (opcional)") { startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
  addButton("4 · Activar a mi compañero") {
   if(person.text.toString().trim().isNotEmpty()) getSharedPreferences("pet",0).edit().putString("person",person.text.toString().trim()).putString("petName",petName.text.toString().trim().ifBlank{"Compañero"}).apply()
   if(!Settings.canDrawOverlays(this)){Toast.makeText(this,"Primero activa la superposición",Toast.LENGTH_LONG).show();return@addButton}
   if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),requestNotifications);return@addButton}
   startPet()
  }
  addButton("Agarrar y mover (15 segundos)") {
   val i=Intent(this,PetOverlayService::class.java).setAction("ENABLE_DRAG_MODE")
   if(Build.VERSION.SDK_INT>=26)startForegroundService(i) else startService(i)
  }
  setContentView(ScrollView(this).apply{addView(layout)})
 }
 private fun addButton(label:String,action:()->Unit){layout.addView(Button(this).apply{text=label;setOnClickListener{action()}})}
 private fun startPet(){val i=Intent(this,PetOverlayService::class.java);if(Build.VERSION.SDK_INT>=26)startForegroundService(i)else startService(i);Toast.makeText(this,"¡Hola! Ya estoy contigo 🐏",Toast.LENGTH_SHORT).show();finish()}
 override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray){super.onRequestPermissionsResult(requestCode,permissions,grantResults);if(requestCode==requestNotifications&&Settings.canDrawOverlays(this))startPet()}
}