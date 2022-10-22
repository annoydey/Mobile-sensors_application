package com.project1.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView

class References : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var linkview1: TextView
    private lateinit var linkview2: TextView
    private lateinit var linkview3: TextView
    private lateinit var linkview4: TextView
    private lateinit var linkview5: TextView
    private lateinit var linkview6: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_references)

        button = findViewById(R.id.prev_btn1) as Button
        linkview1 = findViewById(R.id.reflink0)
        linkview2 = findViewById(R.id.reflink1)
        linkview3 = findViewById(R.id.reflink2)
        linkview4 = findViewById(R.id.reflink3)
        linkview5 = findViewById(R.id.reflink4)
        linkview6 = findViewById(R.id.reflink5)

        linkview1.movementMethod = LinkMovementMethod.getInstance()
        linkview2.movementMethod = LinkMovementMethod.getInstance()
        linkview3.movementMethod = LinkMovementMethod.getInstance()
        linkview4.movementMethod = LinkMovementMethod.getInstance()
        linkview5.movementMethod = LinkMovementMethod.getInstance()
        linkview6.movementMethod = LinkMovementMethod.getInstance()

        button.setOnClickListener {
            val intent = Intent(this, Activity4::class.java)
            startActivity(intent)
        }
    }

}