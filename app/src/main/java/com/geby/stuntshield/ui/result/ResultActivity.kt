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

        displayResult()

        val drawable = ContextCompat.getDrawable(this, R.drawable.status_circle)
        binding.stuntDetect.background = drawable
    }

    private fun displayResult() {
        val inputGender = intent.getStringExtra("gender") ?: "No result"
        val inputAge = intent.getStringExtra("age") ?: "No result"
        val inputWeight = intent.getStringExtra("weight") ?: "No result"
        val inputHeight = intent.getStringExtra("height") ?: "No result"
        val predict = intent.getStringExtra("predict") ?: "No result"
        val confidenceScore = intent.getStringExtra("confidenceScore") ?: "No result"

        with(binding) {
            resultGender.text = inputGender
            resultAge.text = inputAge
            resultWeight.text = inputWeight
            resultHeight.text = inputHeight
            stuntedStatus.text = predict
            conScore.text = confidenceScore
        }
    }
}