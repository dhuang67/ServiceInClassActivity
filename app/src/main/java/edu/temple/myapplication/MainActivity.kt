package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var startButton = findViewById<Button>(R.id.startButton)
        var stopButton = findViewById<Button>(R.id.stopButton)
        var textView = findViewById<TextView>(R.id.textView)

        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                textView.text = msg.what.toString()
            }
        }
        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                timerBinder = p1 as TimerService.TimerBinder
                timerBinder.setHandler(handler)
                isConnected = true
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                isConnected = false
            }
        }

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        startButton.setOnClickListener {
            if (isConnected) {
                if (!timerBinder.isRunning && !timerBinder.paused) {
                    timerBinder.start(10)
                    startButton.text = "Pause"
                } else {
                    timerBinder.pause()
                    startButton.text = if (timerBinder.paused) "Resume" else "Pause"
                }
            }
        }
        
        stopButton.setOnClickListener {
            if (isConnected) {
                timerBinder.stop()
                startButton.text = "Start"
                textView.text = "0"
            }
        }
    }
}