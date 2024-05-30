package com.geby.stuntshield.ui.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.geby.stuntshield.R
import com.geby.stuntshield.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Analysis Result"

        val drawable = ContextCompat.getDrawable(this, R.drawable.status_circle)
        binding.stuntDetect.background = drawable
    }
}