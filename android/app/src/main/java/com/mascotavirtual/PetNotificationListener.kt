package com.mascotavirtual

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.content.Intent

class PetNotificationListener : NotificationListenerService() {
 override fun onNotificationPosted(sbn: StatusBarNotification?) {
  if(sbn==null)return
  val pkg=sbn.packageName ?: return
  val extras=sbn.notification.extras
  val text=(extras.getCharSequence("android.title")?.toString().orEmpty()+" "+extras.getCharSequence("android.text")?.toString().orEmpty()).lowercase()
  val action=when {
   pkg=="com.whatsapp" || pkg=="com.whatsapp.w4b" -> "NEW_MESSAGES"
   (pkg.contains("dialer") || pkg.contains("incallui") || pkg.contains("phone")) && listOf("llamada perdida","missed call","llamada no contestada").any{text.contains(it)} -> "MISSED_CALL"
   (pkg.contains("deskclock") || pkg.contains("clock")) && listOf("alarma","alarm","despertador").any{text.contains(it)} -> "ALARM"
   (pkg.contains("googlequicksearchbox") || pkg.contains("weather")) &&
     listOf("lluv","torment","lloviz","precipit","calor","temperatura","clima","soleado").any{text.contains(it)} -> "WEATHER"
   else -> return
  }
  val event=Intent("com.mascotavirtual.EVENT").setPackage(packageName).putExtra("kind",action)
  if(action=="WEATHER")event.putExtra("weatherText",text.take(160))
  sendBroadcast(event)
 }
}