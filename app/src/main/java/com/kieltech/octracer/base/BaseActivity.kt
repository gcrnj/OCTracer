package com.kieltech.octracer.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.kieltech.octracer.utils.Constants

open class BaseActivity<VB : ViewBinding>(
    private val viewBindingInflater: (LayoutInflater) -> VB,
) : AppCompatActivity() {

    //View Binding
    val binding: VB by lazy { viewBindingInflater(layoutInflater) }

    val userSharedPref by lazy {
        getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE)
    }

}