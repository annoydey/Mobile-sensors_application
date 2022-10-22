package com.project1.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt
import kotlin.system.exitProcess


class Activity1 : AppCompatActivity(), SensorEventListener {

    private lateinit var toptext: TextView
    private lateinit var button: Button
    private lateinit var button2: Button
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private lateinit var sensorManager: SensorManager
    private var time = 0.0
    private lateinit var xtval: TextView
    private lateinit var ytval: TextView
    private lateinit var ztval: TextView
    private lateinit var txt2: TextView
    private var xlate = 0
    private var ylate = 0
    private var zlate = 0
    private var sensortask = false
    private var xDiff = 0
    private var yDiff = 0
    private var zDiff = 0
    private var sthreshould = 5f
    private var activate = false
    private lateinit var keyfeatures: ToggleButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)

        toptext = findViewById(R.id.upper_text)
        button2 = findViewById(R.id.startStopButton)

        xtval = findViewById(R.id.xval)
        ytval = findViewById(R.id.yval)
        ztval = findViewById(R.id.zval)


        txt2 = findViewById(R.id.kftext1)

        button = findViewById(R.id.nxt_btn) as Button

        button.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            startActivity(intent)
        }

        keyfeatures = findViewById(R.id.kfbtn)

        val toggle: ToggleButton = keyfeatures
        toggle.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, if(isChecked)"Keyboard feature is on" else "Keyboard feature is off", Toast.LENGTH_SHORT).show()
            if (isChecked) {
                activate = true
            } else {
                activate = false
            }
        }

        setUpSensorStuff()


        button2.setOnClickListener { startStopTimer() }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

    }


    private fun setUpSensorStuff(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_NORMAL)

        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val x= event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val cx = x.toInt()
            val cy = y.toInt()
            val cz = z.toInt()

            if(sensortask){
                xDiff = Math.abs(xlate - cx)
                yDiff = Math.abs(ylate - cy)
                zDiff = Math.abs(zlate - cz)

                if((xDiff > sthreshould && yDiff > sthreshould) || (xDiff > sthreshould && zDiff > sthreshould) || (yDiff > sthreshould && zDiff > sthreshould)) {
                    resetTimer()
                }
            }
            if (activate == true){
                if(cy <= 0 && cz == 9){
                    closeKeyboard(txt2)
                } else{
                    openKeyboard(txt2)
                }
            }

            xlate = cx
            ylate = cy
            zlate = cz
            sensortask = true

            xtval.text = "X axis :${x} m/s^2\n"
            ytval.text = "Y axis :${y} m/s^2\n"
            ztval.text = "Z axis :${z} m/s^2\n"
        }
    }

    private fun closeKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    private fun openKeyboard(view: View) {
        val editText = findViewById<View>(R.id.fieldtext2) as EditText
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy(){
        sensorManager.unregisterListener(this)
        super.onDestroy()
        exitProcess(0)
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        toptext.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        startService(serviceIntent)
        button2.text = "Stop"
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        button2.text = "Start"
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            toptext.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hours: Int, minutes: Int, seconds: Int): String = String.format("%02d:%02d:%02d", hours, minutes,seconds)

}




