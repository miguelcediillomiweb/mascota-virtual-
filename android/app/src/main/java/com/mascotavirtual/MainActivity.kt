package com.mascotavirtual

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val layout = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(32,32,32,32) }
  layout.addView(TextView(this).apply { text="🐣 Mascota Virtual\n\nActiva la mascota para verla encima de tus aplicaciones."; textSize=20f; gravity=Gravity.CENTER })
  val button=Button(this).apply { text="Activar mascota" }
  layout.addView(button)
  button.setOnClickListener {
   if (!Settings.canDrawOverlays(this)) {
    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
   } else {
    startForegroundService(Intent(this, PetOverlayService::class.java))
    finish()
   }
  }
  setContentView(layout)
 }
}
