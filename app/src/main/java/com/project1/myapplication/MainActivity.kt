package com.project1.myapplication

import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import android.hardware.biometrics.BiometricPrompt
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import pl.droidsonroids.gif.GifImageView
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(){

    private lateinit var buttonauth: Button
    private lateinit var authstatus: TextView
    private lateinit var permission: ImageView

    lateinit var executor: Executor
    lateinit var biometricPrompt: androidx.biometric.BiometricPrompt
    lateinit var promptInfo: androidx.biometric.BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonauth = findViewById(R.id.authbtn) as Button
        authstatus = findViewById(R.id.Auth_status) as TextView

        permission = findViewById(R.id.acess)
        permission.visibility = View.INVISIBLE
        permission = findViewById(R.id.acessdenied)
        permission.visibility = View.INVISIBLE

        val display : GifImageView = findViewById(R.id.fingergif)
        display.visibility = View.VISIBLE

        val intent = Intent(this, Activity1::class.java)
        var i = 0
        buttonauth.setOnClickListener{
            val display : GifImageView = findViewById(R.id.fingergif)
            display.visibility = View.INVISIBLE
            biometricPrompt.authenticate(promptInfo)
        }

        executor=ContextCompat.getMainExecutor(this)
        biometricPrompt=androidx.biometric.BiometricPrompt(this@MainActivity, executor,object:androidx.biometric.BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                authstatus.text="Error"
            }

            override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                i = 1
                authstatus.text="Successfully Auth"
                permission = findViewById(R.id.acessdenied)
                permission.visibility = View.INVISIBLE
                permission = findViewById(R.id.acess)
                permission.visibility = View.VISIBLE
                if (i==1) {
                    startActivity(intent)
                    finish()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                authstatus.text="Auth Failed"
                permission = findViewById(R.id.acessdenied)
                permission.visibility = View.VISIBLE
                permission = findViewById(R.id.acess)
                permission.visibility = View.INVISIBLE
            }
        })

        promptInfo=androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Login Using Fingerprint or Face ID")
            .setNegativeButtonText("Cancel")
            .build()
    }
}




