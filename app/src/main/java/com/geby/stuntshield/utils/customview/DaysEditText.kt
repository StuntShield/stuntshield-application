package com.geby.stuntshield.utils.customview

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText

class DaysEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    init {
        setupDaysEditText()
    }

    private fun setupDaysEditText() {
        inputType = InputType.TYPE_CLASS_NUMBER
        filters = arrayOf(InputFilterMinMax("1", "31"))
        gravity = Gravity.CENTER
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
        setPadding(0, 0, 0, 0)
    }
    private inner class InputFilterMinMax(private val minValue: String, private val maxValue: String) : InputFilter {

        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            val input = dest.toString() + source.toString()
            val value = input.toIntOrNull()

            if (value != null) {
                if (value >= minValue.toInt() && value <= maxValue.toInt()) {
                    return null
                }
            }
            error = "$minValue s/d $maxValue"
            return ""
        }
    }
}
