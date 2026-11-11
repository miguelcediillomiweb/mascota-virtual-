package com.mascotavirtual

import android.app.*
import android.content.Intent
import android.graphics.*
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.FrameLayout
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import java.util.Calendar
import kotlin.random.Random

class PetOverlayService : Service() {
 private lateinit var wm: WindowManager
 private lateinit var params: WindowManager.LayoutParams
 private lateinit var duck: DuckView
 private val handler=Handler(Looper.getMainLooper())
 private var vx=5f
 private var vy=0f
 private var mode="walk"
 private var ticks=0
 private var charredUntil=0L
 private var lastNoticeAt=0L
 private var speechText=""
 private var speechUntil=0L
 private var weatherMood=""
 private val weatherReset=Handler(Looper.getMainLooper())
 private val speechHandler=Handler(Looper.getMainLooper())
 private fun speak(message:String){speechText=message;speechUntil=System.currentTimeMillis()+6500;if(::duck.isInitialized)duck.invalidate()}
 private fun setWeather(mood:String){weatherMood=mood;weatherReset.removeCallbacksAndMessages(null);weatherReset.postDelayed({weatherMood="";if(::duck.isInitialized)duck.invalidate()},18_000L);if(::duck.isInitialized)duck.invalidate()}
 private val eventReceiver=object:BroadcastReceiver(){override fun onReceive(context:Context?,intent:Intent?){val now=System.currentTimeMillis();if(now-lastNoticeAt<4*60_000L)return;when(intent?.getStringExtra("kind")){ "NEW_MESSAGES"->{if(Random.nextInt(100)<65){lastNoticeAt=now;speak("Hola, tienes nuevos mensajes 💬")}} "MISSED_CALL"->{lastNoticeAt=now;speak("Hola, tienes una llamada perdida 📞")} "ALARM"->{lastNoticeAt=now;speak("¡Tu alarma está sonando! ⏰")} "WEATHER"->{val t=intent.getStringExtra("weatherText").orEmpty();val rain=listOf("lluv","torment","lloviz","precipit").any{t.contains(it)};val hot=listOf("calor","temperatura").any{t.contains(it)};setWeather(if(rain)"rain" else if(hot)"hot" else "sun");lastNoticeAt=now;speak(when{rain->"Parece que puede llover. ¡Paraguas! ☔";hot->"¡Qué calor! Estoy sudando ☀️";else->"¡Actualización del clima! 🌤️"})} } }}
 private val greetingLoop=object:Runnable{override fun run(){if(!::duck.isInitialized)return;val person=getSharedPreferences("pet",MODE_PRIVATE).getString("person","amigo")?: "amigo";val hour=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);speak(when{hour>=21->"Hola $person, recuerda descansar. Mañana será otro día 💤";hour>=12->"Hola $person, ¿cómo va tu día?";else->"Hola $person, ¡que tengas un buen día!"});speechHandler.postDelayed(this,35*60_000L+Random.nextLong(45*60_000L))}}
 private val loop=object:Runnable { override fun run(){ step(); handler.postDelayed(this,45) } }

 override fun onBind(intent:Intent?):IBinder?=null
 override fun onCreate(){
  super.onCreate()
  val channel="mascota_activa"
  if(Build.VERSION.SDK_INT>=26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(channel,"Mascota virtual",NotificationManager.IMPORTANCE_LOW))
  val nb=if(Build.VERSION.SDK_INT>=26) Notification.Builder(this,channel) else Notification.Builder(this)
  startForeground(7,nb.setContentTitle("Mascota Virtual").setContentText("Tu mascota está paseando por la pantalla.").setSmallIcon(android.R.drawable.ic_menu_compass).build())
  if(!Settings.canDrawOverlays(this)){stopSelf();return}
  wm=getSystemService(WINDOW_SERVICE) as WindowManager
  duck=DuckView()
  val type=if(Build.VERSION.SDK_INT>=26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
  params=WindowManager.LayoutParams(112,104,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT).apply{gravity=Gravity.TOP or Gravity.START;x=80;y=100}
  try{
   wm.addView(duck,params)
   if(Build.VERSION.SDK_INT>=33)registerReceiver(eventReceiver,IntentFilter("com.mascotavirtual.EVENT"),Context.RECEIVER_NOT_EXPORTED) else registerReceiver(eventReceiver,IntentFilter("com.mascotavirtual.EVENT"))
   speechHandler.postDelayed(greetingLoop,30*60_000L+Random.nextLong(25*60_000L))
   handler.postDelayed(loop,500)
  }catch(_:Exception){stopSelf()}
 }
 private fun step(){
  val screenWidth=resources.displayMetrics.widthPixels
  val screenHeight=resources.displayMetrics.heightPixels
  if(screenWidth>screenHeight){duck.visibility=View.GONE;return}else duck.visibility=View.VISIBLE
  ticks++
  if(mode=="sleep"){if(ticks>95){mode="walk";ticks=0;vx=if(Random.nextBoolean())5f else -5f};duck.mode=mode;duck.invalidate();try{wm.updateViewLayout(duck,params)}catch(_:Exception){};return}
  if(mode in listOf("walk","run","rocket","fall","stunned","eat","drink","pee","bath","jump","hang","abduct")){
   params.x+=(vx*(if(mode=="run")2.8f else 1f)).toInt()
   params.y+=vy.toInt()
   val width=resources.displayMetrics.widthPixels
   val height=resources.displayMetrics.heightPixels
   if(mode!="rocket"&&(params.x<0||params.x>width-params.width)){vx=-vx;duck.flip=vx<0}
   if(mode=="abduct" && ticks in 25..62) params.y-=2
   if(mode=="abduct" && ticks in 63..91) params.y-=9
   if(mode=="abduct" && ticks>91){mode="walk";ticks=0;params.y=((height*.46f).toInt()-params.height).coerceAtLeast(25);vx=if(Random.nextBoolean())5f else -5f}
   if(mode=="rocket"&&params.y < -150){params.y=(height*.34f).toInt();params.x=Random.nextInt(20,(width-params.width).coerceAtLeast(21));mode="walk";charredUntil=System.currentTimeMillis()+3200L;vx=if(Random.nextBoolean())5f else -5f}
   if(mode=="walk"&&ticks%70==0){val r=Random.nextInt(100);mode=when{r<12->"sleep";r<23->"eat";r<33->"drink";r<43->"pee";r<51->"bath";r<63->"run";r<73->"jump";r<82->"hang";r<90->"rocket";r<96->"abduct";else->"walk"};ticks=0;if(mode=="rocket"){vx=0f;vy=-13f}else if(mode=="run"){vx=if(vx<0)-10f else 10f}else{vy=0f}}
   if(mode=="run"&&ticks>22){mode="walk";vx=if(vx<0)-5f else 5f;ticks=0}
   if(mode=="rocket"&&ticks%7==0)params.y-=8
   if(mode in listOf("sleep","eat","drink","pee","bath","jump","hang")&&ticks>38){mode="walk";ticks=0;vy=0f}
   if(mode=="jump")params.y-=if(ticks<18)5 else -5
   if(mode=="hang"&&ticks>25){mode="fall";ticks=0}
   if(mode=="fall"){
    vy=(vy+0.85f).coerceAtMost(22f);params.y+=vy.toInt()
    val floor=(height*.46f).toInt()-params.height-24
    if(params.y>=floor){params.y=floor;vy=-vy*0.42f
     if(kotlin.math.abs(vy)<2.2f){vy=0f;mode="stunned";ticks=0}
    }
    if(params.x<0||params.x>width-params.width){params.x=params.x.coerceIn(0,width-params.width);vx=-vx*.65f}
   }
   if(mode=="stunned"&&ticks>18){mode="walk";ticks=0;vy=0f}
  }
  params.y=params.y.coerceIn(if(mode=="rocket"||mode=="abduct")-180 else 24,((resources.displayMetrics.heightPixels*.46f).toInt()-params.height).coerceAtLeast(25))
  duck.mode=mode;duck.invalidate()
  try{wm.updateViewLayout(duck,params)}catch(_:Exception){}
 }
 override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int = START_STICKY
 override fun onDestroy(){try{unregisterReceiver(eventReceiver)}catch(_:Exception){};speechHandler.removeCallbacksAndMessages(null);weatherReset.removeCallbacksAndMessages(null);handler.removeCallbacks(loop);try{wm.removeView(duck)}catch(_:Exception){};super.onDestroy()}


 inner class DuckView:View(this@PetOverlayService){
  val paint=Paint(Paint.ANTI_ALIAS_FLAG)
  var mode="walk";var flip=false
  private fun oval(c:Canvas,color:Int,l:Float,t:Float,r:Float,b:Float){paint.color=color;c.drawOval(l,t,r,b,paint)}
  override fun onDraw(canvas:Canvas){
   super.onDraw(canvas);val c=canvas;val save=c.save();c.scale(width/128f,height/112f)
   if(weatherMood=="rain"){paint.textSize=28f;paint.color=Color.BLUE;c.drawText("☂",75f,16f,paint)}
   if(weatherMood=="hot"){paint.textSize=25f;paint.color=Color.rgb(30,150,220);c.drawText("💦",100f,35f,paint);c.drawText("💦",17f,47f,paint)}
   if(weatherMood=="sun"){paint.textSize=23f;c.drawText("☀",99f,20f,paint)}
   if(mode=="rocket"){paint.textSize=48f;c.drawText("🚀",35f,78f,paint);paint.textSize=26f;c.drawText("🪖",45f,25f,paint);c.restoreToCount(save);return}
   if(mode=="abduct"){
    val pulse=(System.currentTimeMillis()/180L%2L)==0L
    paint.color=Color.argb(if(pulse)105 else 70,90,210,255)
    val beam=Path().apply{moveTo(39f,28f);lineTo(89f,28f);lineTo(112f,105f);lineTo(16f,105f);close()};c.drawPath(beam,paint)
    paint.color=Color.rgb(110,205,255);c.drawOval(40f,4f,88f,35f,paint)
    paint.color=Color.rgb(75,90,125);c.drawOval(17f,19f,111f,43f,paint)
    paint.color=Color.rgb(180,220,245);c.drawOval(38f,12f,90f,34f,paint)
    paint.color=Color.rgb(255,235,110);for(x in listOf(30f,48f,64f,80f,98f))c.drawCircle(x,34f,3.2f,paint)
   }
   if(flip){c.scale(-1f,1f,64f,56f)}
   if(mode=="eat"){paint.textSize=23f;c.drawText("🌱",10f,103f,paint)}
   if(mode=="drink"){oval(c,Color.rgb(90,205,236),5f,94f,56f,105f)}
  if(mode=="bath"){paint.textSize=24f;paint.color=Color.rgb(60,175,235);c.drawText("💦",7f,45f,paint);c.drawText("🫧",92f,42f,paint)}
   if(mode=="pee"){oval(c,Color.rgb(226,216,112),9f,98f,43f,105f)}
   val bob=if(mode=="walk"||mode=="run") kotlin.math.sin(System.currentTimeMillis()/85.0).toFloat()*3 else 0f
   c.save();c.translate(0f,bob)
   oval(c,Color.rgb(235,151,81),31f,83f,62f,106f);oval(c,Color.rgb(235,151,81),76f,83f,108f,106f)
   oval(c,if(System.currentTimeMillis()<charredUntil)Color.DKGRAY else Color.rgb(247,235,137),12f,13f,111f,99f)
   paint.color=Color.rgb(244,166,105);val bill=Path().apply{moveTo(94f,48f);quadTo(121f,37f,126f,52f);quadTo(116f,66f,94f,62f);close()};c.drawPath(bill,paint)
   oval(c,Color.rgb(245,190,142),79f,30f,111f,69f)
   if(mode=="sleep"){paint.color=Color.BLACK;paint.strokeWidth=3f;c.drawLine(88f,50f,99f,50f,paint);paint.textSize=16f;c.drawText("Z",103f,20f,paint)}
   else{oval(c,Color.WHITE,86f,39f,103f,60f);oval(c,Color.BLACK,94f,44f,101f,57f)}
   paint.color=Color.rgb(193,76,222);val cap=Path().apply{moveTo(35f,33f);quadTo(42f,3f,75f,8f);lineTo(101f,18f);lineTo(103f,32f);lineTo(35f,41f);close()};c.drawPath(cap,paint)
   paint.color=Color.rgb(133,48,180);c.drawRect(35f,31f,104f,39f,paint)
   if(mode=="hang"){c.rotate(85f,64f,20f)}
   if(mode=="fall"||mode=="stunned")c.rotate(80f,64f,65f)
   c.restore();c.restoreToCount(save)
   if(System.currentTimeMillis()<speechUntil && speechText.isNotBlank()){val pp=Paint(Paint.ANTI_ALIAS_FLAG);pp.color=Color.WHITE;pp.textSize=12f;pp.typeface=Typeface.create("sans-serif",Typeface.BOLD);val msg=speechText.take(38);val tw=pp.measureText(msg);c.drawRoundRect(0f,0f,tw+20f,28f,9f,9f,pp);pp.color=Color.DKGRAY;c.drawText(msg,10f,19f,pp)}
  }
 }
}
