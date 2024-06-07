package com.geby.stuntshield.utils.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.geby.stuntshield.R

class MyEmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    context.getString(R.string.invalid_email)
                } else {
                    null
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }
}