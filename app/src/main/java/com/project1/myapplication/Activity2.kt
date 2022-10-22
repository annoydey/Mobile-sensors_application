package com.project1.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import pl.droidsonroids.gif.GifImageView
import java.io.IOException
import java.util.*
import kotlin.system.exitProcess


class Activity2 : AppCompatActivity(), SensorEventListener {

    var sensor: Sensor? = null
    var sensorManager: SensorManager? = null
    private lateinit var sensorvaltext: TextView
    private lateinit var sensorcomment: TextView
    private lateinit var prevbutton: Button
    private lateinit var nxtbutton: Button
    private val RQ_SPEECH_REC = 102
    private lateinit var microbtn: ToggleButton
    private lateinit var txt2: TextView
    private var micactivate = false
    private var flash = 1

    private lateinit var cM: CameraManager
    private lateinit var ctxt: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        sensorvaltext = findViewById(R.id.middle_text)
        sensorcomment = findViewById(R.id.last_text)

        nxtbutton = findViewById(R.id.nxtbtn) as Button
        prevbutton = findViewById(R.id.prevbtn) as Button


        txt2 = findViewById(R.id.micbtn)
        microbtn = findViewById(R.id.micbtn)

        val toggle: ToggleButton = microbtn
        toggle.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, if(isChecked)"Microphone feature is on" else "Microphone feature is off", Toast.LENGTH_SHORT).show()
            if (isChecked) {
                micactivate = true
            } else {
                micactivate = false
            }
        }

        val isFlashAvailable = applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
        if (!isFlashAvailable) {
            Toast.makeText(this, "Flash not available", Toast.LENGTH_SHORT).show()
        }
        cM = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            ctxt = cM.cameraIdList[0]
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

        nxtbutton.setOnClickListener{
            val intent = Intent(this, Activity3::class.java)
            startActivity(intent)
            onFlash(false)
        }

        prevbutton.setOnClickListener{
            val intent = Intent(this, Activity1::class.java)
            onFlash(false)
            startActivity(intent)
        }

        val display : GifImageView = findViewById(R.id.display_img)
        display.visibility = View.INVISIBLE

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)

    }


    private fun onFlash(status: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cM.setTorchMode(ctxt, status)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RQ_SPEECH_REC && resultCode == Activity.RESULT_OK){
            var result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val z1 = result?.get(0).toString()
//            var delimiter = " "
//            val arrayspeech = z1.split(delimiter)
//            val arlen = arrayspeech.size
            var str = "turn off my flashlight"
            var str2 = "turn off flashlight"
            var str3 = "flashlight off"
            var str4 = "turn my flashlight off"
            var str5 = "turn off the flashlight"
            var str6 = "turn the flashlight off"
            var str7 = "turn flashlight off"
            var str8 = "off flashlight"

            if (str == z1 || str2 == z1 || str3 == z1 || str4 == z1 || str5 == z1 || str6 == z1 || str7 == z1 || str8 == z1){
                flash = 0
                onFlash(false)
            }else{
                flash = 1
                onFlash(true)
            }
        }
    }


    private fun askSpeechInput(){
        if (micactivate == true){
            if(!SpeechRecognizer.isRecognitionAvailable(this)){
                Toast.makeText(this, "Speech couldn't recognised", Toast.LENGTH_SHORT).show()
            } else{
                val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                i.putExtra(RecognizerIntent.EXTRA_PROMPT, "To turn of the flashlight, say flashlight off")
                startActivityForResult(i, RQ_SPEECH_REC)
            }
        }else{
//            Toast.makeText(this, "For mic feature click on orange button", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume(){
        super.onResume()
        sensorManager!!.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    var isRunning = false

    override fun onSensorChanged(event: SensorEvent?) {
        val light = event!!.values[0]
        sensorvaltext.text = "Sensor values :${light} lx\n"
        try {
            if(event!!.values[0] < 4 ){
                isRunning = true
                val display : GifImageView = findViewById(R.id.display_img)
                display.visibility = View.VISIBLE
                sensorcomment.text = "It's too dark \n"
                onFlash(true)
                flash = 1
            }
            else {
                isRunning = false
                val display: GifImageView = findViewById(R.id.display_img)
                display.visibility = View.INVISIBLE
                sensorcomment.text = "It's brighter\n"
                if(flash == 1){
                    askSpeechInput()
                }
            }
        }
        catch (e : IOException){

        }
    }

    override fun onDestroy(){
        sensorManager?.unregisterListener(this)
        super.onDestroy()
        exitProcess(0)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

}
