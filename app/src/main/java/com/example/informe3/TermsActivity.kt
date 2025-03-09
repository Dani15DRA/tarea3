package com.example.informe3

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)

        val tvTermsContent = findViewById<TextView>(R.id.txtTerms1)

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack?.setOnClickListener {
            finish()
        }
    }
}