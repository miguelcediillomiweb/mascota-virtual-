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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity
import android.widget.Toast

class MainActivity : Activity() {
 private val requestNotifications=41
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  showSetup()
 }
 private fun showSetup(){
  val layout=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(28,28,28,28) }
  layout.addView(TextView(this).apply { text="🐑 Mascota Virtual\n\nPara que tu compañerita pueda pasear por encima de WhatsApp, Chrome y otras aplicaciones, activa estos permisos.\n\n1. Mostrar sobre otras aplicaciones.\n2. Permitir actividad sin optimización de batería.\n3. Notificaciones del servicio para que Android sepa que sigue activo."; textSize=17f; gravity=Gravity.CENTER })
  fun addButton(label:String,action:()->Unit){ layout.addView(Button(this).apply{text=label;setOnClickListener{action()}}) }
  addButton("1 · Permitir superposición") {
   if(!Settings.canDrawOverlays(this)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
   else Toast.makeText(this,"Superposición ya permitida",Toast.LENGTH_SHORT).show()
  }
  addButton("2 · Permitir uso sin optimización de batería") {
   val pm=getSystemService(POWER_SERVICE) as PowerManager
   if(Build.VERSION.SDK_INT>=23 && !pm.isIgnoringBatteryOptimizations(packageName)){
    try { startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:$packageName"))) }
    catch(_:Exception){ startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) }
   } else Toast.makeText(this,"La batería ya permite actividad sin optimización",Toast.LENGTH_SHORT).show()
  }
  addButton("3 · Activar mascota flotante") {
   if(!Settings.canDrawOverlays(this)){Toast.makeText(this,"Primero concede el permiso de superposición",Toast.LENGTH_LONG).show();return@addButton}
   val pm=getSystemService(POWER_SERVICE) as PowerManager
   if(Build.VERSION.SDK_INT>=23 && !pm.isIgnoringBatteryOptimizations(packageName)){
    Toast.makeText(this,"Activa también el permiso de batería para mayor continuidad",Toast.LENGTH_LONG).show();return@addButton
   }
   if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
    requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),requestNotifications);return@addButton
   }
   startPet()
  }
  setContentView(layout)
 }
 override fun onResume(){super.onResume()}
 override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray){
  super.onRequestPermissionsResult(requestCode,permissions,grantResults)
  if(requestCode==requestNotifications){ if(Settings.canDrawOverlays(this)) startPet() else showSetup() }
 }
 private fun startPet(){
  try { val i=Intent(this,PetOverlayService::class.java); if(Build.VERSION.SDK_INT>=26) startForegroundService(i) else startService(i); Toast.makeText(this,"¡Tu mascota ya está paseando!",Toast.LENGTH_SHORT).show();finish() }
  catch(e:Exception){Toast.makeText(this,"No se pudo iniciar: ${e.message}",Toast.LENGTH_LONG).show()}
 }
}
