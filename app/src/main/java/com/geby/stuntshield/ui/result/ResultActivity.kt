package com.geby.stuntshield.ui.result

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.geby.stuntshield.R
import com.geby.stuntshield.data.response.AnalyzeResponse
import com.geby.stuntshield.databinding.ActivityResultBinding
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        displayResult()
    }

    private fun displayResult() {
        val result: AnalyzeResponse? = intent.getParcelableExtra("result")
        result.let {
            val stuntResult = result?.data?.stunting?.jsonMemberClass
            val stuntPercentage = result?.data?.stunting?.presentase
            val ideal = result?.data?.ideal?.jsonMemberClass
            val idealPercentage = result?.data?.ideal?.presentase
            val obesity = result?.data?.weight?.jsonMemberClass
            val obesityPercentage = result?.data?.weight?.presentase
            val recommendations = result?.data?.recommendation
            val inputGender = intent.getStringExtra("gender") ?: "No result"
            val inputAge = intent.getStringExtra("age") ?: "No result"
            val inputHeight = intent.getStringExtra("height") ?: "No result"
            val inputWeight = intent.getStringExtra("weight") ?: "No result"

            with(binding) {
                resultGender.text = inputGender
                resultAge.text = inputAge
                resultHeight.text = inputHeight
                resultWeight.text = inputWeight
                tvStuntingStatus.text = stuntResult
                tvStuntingScore.text = "${(stuntPercentage?.toFloat() ?: 0.0f).toInt()}%"
                recommendation.text = recommendations
                tvGiziStatus.text = ideal
                tvGiziScore.text = "${(idealPercentage?.toFloat() ?: 0.0f).toInt()}%"
                tvWeightStatus.text = obesity
                tvWeightScore.text = "${(obesityPercentage?.toFloat() ?: 0.0f).toInt()}%"

                obesityStatusColor(tvWeightStatus, tvWeightScore, obesity)
                stuntingStatusColor(tvStuntingStatus, tvStuntingScore, stuntResult)
                nutritionStatusColor(tvGiziStatus, tvGiziScore, ideal)

            }
        }
    }
    private fun stuntingStatusColor(view: TextView, score: TextView, status: String?) {
        val color = when (status?.lowercase(Locale.getDefault())) {
            "normal" -> R.color.normal_status
            "stunting" -> R.color.warning_status
            "tinggi" -> R.color.warning_status
            "stunting berat" -> R.color.high_status
            else -> R.color.black
        }
        view.setTextColor(ContextCompat.getColor(this, color))
        score.setTextColor(ContextCompat.getColor(this, color))
    }
    private fun obesityStatusColor(view: TextView, score: TextView, status: String?) {
        val color = when (status?.lowercase(Locale.getDefault())) {
            "berat badan sangat kurang" -> R.color.high_status
            "berat badan kurang" -> R.color.warning_status
            "berat badan normal" -> R.color.normal_status
            "risiko berat badan lebih" -> R.color.warning_status
            else -> R.color.black
        }
        view.setTextColor(ContextCompat.getColor(this, color))
        score.setTextColor(ContextCompat.getColor(this, color))
    }

    private fun nutritionStatusColor(view: TextView, score: TextView, status: String?) {
        val color = when (status?.lowercase(Locale.getDefault())) {
            "gizi buruk" -> R.color.high_status
            "gizi kurang" -> R.color.warning_status
            "gizi baik (normal)" -> R.color.normal_status
            "berisiko gizi lebih (overweight)" -> R.color.warning_status
            "gizi lebih (overweight)" -> R.color.high_status
            "obesitas" -> R.color.high_status
            else -> R.color.black
        }
        view.setTextColor(ContextCompat.getColor(this, color))
        score.setTextColor(ContextCompat.getColor(this, color))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}