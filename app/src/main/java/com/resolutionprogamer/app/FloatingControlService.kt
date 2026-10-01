package com.resolutionprogamer.app

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView

class FloatingControlService : Service() {
    private lateinit var wm: WindowManager
    private var root: View? = null
    private var expanded = true

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(7, notification())
        showOverlay()
    }

    private fun createChannel() {
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel("overlay", "Gaming overlay", NotificationManager.IMPORTANCE_LOW)
        )
    }

    private fun notification(): Notification =
        Notification.Builder(this, "overlay")
            .setContentTitle("Resolution Pro Gamer")
            .setContentText("Panel flotante activo")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .build()

    private fun showOverlay() {
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 14, 18, 14)
            setBackgroundColor(Color.rgb(23, 19, 33))
        }
        val title = TextView(this).apply {
            text = "RPG  •  1.56X"
            setTextColor(Color.WHITE); textSize = 16f
            setPadding(4, 4, 4, 12)
        }
        box.addView(title)
        val collapse = TextView(this).apply {
            text = "▾  PLEGAR"
            setTextColor(Color.rgb(190, 110, 255)); textSize = 13f
            setPadding(4, 8, 4, 8)
            setOnClickListener {
                expanded = !expanded
                title.text = if (expanded) "RPG  •  1.56X" else "RPG  •  ●"
                this.text = if (expanded) "▾  PLEGAR" else "●  ABRIR"
                for (i in 1 until box.childCount)
                    box.getChildAt(i).visibility = if (expanded) View.VISIBLE else View.GONE
            }
        }
        box.addView(collapse)
        box.addView(TextView(this).apply {
            text = "Guardar perfil"; setTextColor(Color.WHITE); setPadding(4, 10, 4, 10)
        })
        box.addView(TextView(this).apply {
            text = "Restaurar"; setTextColor(Color.WHITE); setPadding(4, 10, 4, 10)
        })

        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 20; y = 180 }

        box.setOnTouchListener(object : View.OnTouchListener {
            var downX = 0; var downY = 0; var startX = 0; var startY = 0
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.actionMasked) {
                    MotionEvent.ACTION_DOWN -> { downX=e.rawX.toInt(); downY=e.rawY.toInt(); startX=lp.x; startY=lp.y; return true }
                    MotionEvent.ACTION_MOVE -> {
                        lp.x=startX+e.rawX.toInt()-downX; lp.y=startY+e.rawY.toInt()-downY
                        wm.updateViewLayout(box, lp); return true
                    }
                }
                return false
            }
        })
        root = box
        wm.addView(box, lp)
    }

    override fun onDestroy() {
        root?.let { runCatching { wm.removeView(it) } }
        root = null
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
