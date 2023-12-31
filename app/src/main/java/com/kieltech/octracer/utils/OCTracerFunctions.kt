package com.kieltech.octracer.utils

import android.app.Activity
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
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

    fun DocumentSnapshot.generateGraduateUser(): Graduate {
        val graduate =  toObject(Graduate::class.java)!!
        graduate.id = id
        return graduate
    }

    fun DocumentSnapshot.generateAdminUser(): Admin {
        return toObject(Admin::class.java)!!
    }

    inline fun <reified VM : ViewModel> AppCompatActivity.createViewModel(): VM {
        return ViewModelProvider(this)[VM::class.java]
    }

    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
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