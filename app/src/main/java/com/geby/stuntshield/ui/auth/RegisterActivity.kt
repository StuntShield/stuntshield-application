package com.geby.stuntshield.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geby.stuntshield.R

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_register)
    }
}