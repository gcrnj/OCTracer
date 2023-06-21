package com.kieltech.octracer.utils

import android.app.Activity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.mindrot.jbcrypt.BCrypt

object OCTracerFunctions {

    fun View.visible() {
        visibility = View.VISIBLE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

    fun View.visibleOrInvisible(boolean: Boolean) {
        if (boolean) visible()
        else invisible()
    }

    fun View.visibleOrGone(boolean: Boolean) {
        if (boolean) visible()
        else gone()
    }

    fun View.invisibleOrGone(boolean: Boolean) {
        if (boolean) invisible()
        else gone()
    }

    fun View.disabled() {
        isEnabled = false
    }

    fun View.enabled() {
        isEnabled = true
    }

    fun Int.seconds(): Long {
        return this * 1000L
    }

    fun Int.milliseconds(): Long {
        return this.toLong()
    }

    fun String.hashPassword(): String {
        val password = this
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(password, salt)
    }

    fun Activity.hideSoftKeyboard() {
        val inputMethodManager: InputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            }
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    inline fun <reified VM : ViewModel> AppCompatActivity.createViewModel(): VM {
        return ViewModelProvider(this)[VM::class.java]
    }

    fun <T : Any> T.toHashMap(): HashMap<String, Any?> {
        val hashMap = HashMap<String, Any?>()
        this::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            hashMap[field.name] = field.get(this)
        }
        return hashMap
    }
}