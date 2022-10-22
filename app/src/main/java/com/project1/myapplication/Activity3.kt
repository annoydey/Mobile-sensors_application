package com.project1.myapplication

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.system.exitProcess


class Activity3 : AppCompatActivity(), SensorEventListener {

    private lateinit var prevbutton: Button
    private lateinit var nxtbutton: Button
    private var mp: MediaPlayer? = null
    private var currentSong = mutableListOf(R.raw.ringtone)
    lateinit var play1: FloatingActionButton
    lateinit var play2: FloatingActionButton
    lateinit var play3: FloatingActionButton
    lateinit var sensorManager : SensorManager
    lateinit var senval: TextView
    private lateinit var seekbar : SeekBar

    var cunt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        seekbar = findViewById(R.id.sebar) as SeekBar
        prevbutton = findViewById(R.id.prevbtn) as Button
        nxtbutton = findViewById(R.id.nxtbtn4) as Button
        senval = findViewById(R.id.sen_val)


        prevbutton.setOnClickListener{
            val intent = Intent(this, Activity2::class.java)
            mp?.stop()
            mp?.reset()
            startActivity(intent)
        }
        nxtbutton.setOnClickListener{
            val intent = Intent(this, Activity4::class.java)
            mp?.stop()
            mp?.reset()
            startActivity(intent)

        }

        controlSound(currentSong[0])
        setupproximitysensor()
    }

    private fun setupproximitysensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.also{
            sensorManager.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)

        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_PROXIMITY){
            senval.text = "Value :${event.values[0]} cm"
            if(event.values[0] == 0f && cunt % 2 == 0){
                mp?.pause()
                cunt++
            }else if (event.values[0] == 0f && cunt % 2 != 0){
                mp?.start()
                cunt++
            }
            cunt == 0
        }
    }

    private fun controlSound(id: Int) {

        play1 = findViewById(R.id.playbtn)
        play2 = findViewById(R.id.pausebtn)
        play3 = findViewById(R.id.stopbtn)


        play1.setOnClickListener{
            if (mp == null){
                mp = MediaPlayer.create(this, id)
                Log.d("Activity3", "Duration: ${mp!!.audioSessionId}")
                initialiseSeekBar()
            }
            mp?.start()
            Log.d("Activity3", "Duration: ${mp!!.duration/1000} seconds")
        }

        play2.setOnClickListener{
            if (mp !== null) mp?.pause()
            Log.d("Activity3", "Paused at: ${mp!!.currentPosition/1000} seconds")
        }

        play3.setOnClickListener{
            if (mp !== null){
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
            }
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) mp?.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun initialiseSeekBar() {
        seekbar.max = mp!!.duration

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekbar.progress = mp!!.currentPosition
                    handler.postDelayed(this, 1000)
                }catch (e: Exception){
                    seekbar.progress = 0
                }
            }
        },0)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
    override fun onDestroy(){
        sensorManager.unregisterListener(this)
        super.onDestroy()
        exitProcess(0)
    }
}








