package com.mascotavirtual

import android.app.*
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient

class PetOverlayService : Service() {
 private var windowManager: WindowManager? = null
 private var petView: WebView? = null
 override fun onBind(intent: Intent?): IBinder? = null
 override fun onCreate() {
  super.onCreate()
  val channelId="mascota_activa"
  if (Build.VERSION.SDK_INT >= 26) {
   val channel=NotificationChannel(channelId,"Mascota virtual",NotificationManager.IMPORTANCE_LOW)
   getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
  }
  val notification=if(Build.VERSION.SDK_INT>=26) Notification.Builder(this,channelId) else Notification.Builder(this)
   .setContentTitle("Mascota Virtual").setContentText("Tu mascota está paseando por la pantalla.")
   .setSmallIcon(android.R.drawable.ic_menu_compass).build()
  startForeground(7,notification)
  if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
  windowManager=getSystemService(WINDOW_SERVICE) as WindowManager
  petView=WebView(this).apply {
   setBackgroundColor(android.graphics.Color.TRANSPARENT)
   settings.javaScriptEnabled=true
   settings.domStorageEnabled=true
   webViewClient=WebViewClient()
   loadUrl("file:///android_asset/pet/index.html?overlay=1")
  }
  val type=if(Build.VERSION.SDK_INT>=26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
  val params=WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT,type,
   WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
   PixelFormat.TRANSLUCENT)
  try { windowManager?.addView(petView,params) } catch (_: Exception) { stopSelf() }
 }
 override fun onStartCommand(intent: Intent?, flags: Int, startId: Int)=START_STICKY
 override fun onDestroy() { try { if(petView!=null) windowManager?.removeView(petView) } catch (_:Exception) {}; petView?.destroy(); petView=null; super.onDestroy() }
}
