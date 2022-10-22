package com.project1.myapplication

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class Activity4 : AppCompatActivity(), SensorEventListener {

    private lateinit var prevbutton: Button
    private lateinit var acbtn: Button
    private lateinit var sensorManager1: SensorManager
    private lateinit var sensorManager2: SensorManager
    private lateinit var mxtval: TextView
    private lateinit var mytval: TextView
    private lateinit var mztval: TextView

    private lateinit var gxval : TextView
    private lateinit var gyval : TextView
    private lateinit var gzval : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_4)

        prevbutton = findViewById(R.id.prevbtn) as Button
        acbtn = findViewById(R.id.accbtn) as Button
        mxtval = findViewById(R.id.xvalmag)
        mytval = findViewById(R.id.yvalmag)
        mztval = findViewById(R.id.zvalmag)
        gxval = findViewById(R.id.xgval)
        gyval = findViewById(R.id.ygval)
        gzval = findViewById(R.id.zgval)


        acbtn.setOnClickListener {
            val intent = Intent(this, References::class.java)
            startActivity(intent)
        }

        prevbutton.setOnClickListener {
            val intent = Intent(this, Activity3::class.java)
            startActivity(intent)
        }
        setupmagneticsensor()
        setupgyrosensor()
    }

    private fun setupgyrosensor() {
        sensorManager1 = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager1.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also{
            sensorManager1.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL)

        }
    }

    private fun setupmagneticsensor() {
        sensorManager2 = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager2.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also{
            sensorManager2.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL)

        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
            val mx= event.values[0]
            val my= event.values[1]
            val mz= event.values[2]
            mxtval.text = "X : ${mx} µT"
            mytval.text = "Y : ${my} µT"
            mztval.text = "Z : ${mz} µT"

        }
        if(event?.sensor?.type == Sensor.TYPE_GYROSCOPE){
            val gx= event.values[0]
            val gy= event.values[1]
            val gz= event.values[2]
            gxval.text = "X : ${gx} rad/s"
            gyval.text = "Y : ${gy} rad/s"
            gzval.text = "Z : ${gz} rad/s"

        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy(){
        sensorManager2.unregisterListener(this)
        super.onDestroy()
        exitProcess(0)
    }
}