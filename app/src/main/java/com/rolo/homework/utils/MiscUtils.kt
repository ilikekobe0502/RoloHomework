package com.rolo.homework.utils

import android.content.Context
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


object MiscUtils {

    private val TAG = MiscUtils::class.simpleName

    fun hideSoftKeyboard(view: EditText) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showSoftKeyboard(view: EditText) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun isValidEmail(s: String): Boolean {
        return !TextUtils.isEmpty(s) && android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches();
    }
}