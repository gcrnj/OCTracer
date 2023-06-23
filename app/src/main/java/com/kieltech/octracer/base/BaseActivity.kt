package com.kieltech.octracer.base

import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.ui.landing.graduates.GraduatesLandingActivity
import com.kieltech.octracer.ui.login.LoginActivity
import com.kieltech.octracer.ui.register.RegisterActivity
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.Users

open class BaseActivity<VB : ViewBinding>(
    private val viewBindingInflater: (LayoutInflater) -> VB,
) : AppCompatActivity() {

    //View Binding
    val binding: VB by lazy { viewBindingInflater(layoutInflater) }

    val userSharedPref by lazy {
        getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE)
    }

    fun saveUserAndGoToGraduatesLanding(
        collectionId: String,
        firestoreUserId: String,
        graduateUser: Graduate,
        shouldLoginAutomatically: Boolean
    ) {
        if (shouldLoginAutomatically) {
            // Registered by no user / graduate
            // Save info
            userSharedPref.edit().apply {
                putString(Constants.SHARED_PREF_UID, firestoreUserId)
                putString(Constants.SHARED_PREF_ROLE, collectionId)
                apply()
            }
            Users.GraduateUser = graduateUser
            // Go to graduate dashboard
            startActivity(Intent(this, GraduatesLandingActivity::class.java))
        }
        if (this is RegisterActivity) {
            val newIntent = Intent()
            newIntent.putExtra(Constants.INTENT_EXTRA_UID, firestoreUserId)
            newIntent.putExtra(Constants.INTENT_EXTRA_ROLE, collectionId)
            setResult(RESULT_OK, newIntent)
        }
        finish()
    }

    fun saveUserAndGoToAdminLanding(
        collectionId: String,
        firestoreUserId: String,
        admin: Admin,
        shouldLoginAutomatically: Boolean
    ) {
        if (shouldLoginAutomatically) {
            // Registered by no user / graduate
            // Save info
            userSharedPref.edit().apply {
                putString(Constants.SHARED_PREF_UID, firestoreUserId)
                putString(Constants.SHARED_PREF_ROLE, collectionId)
                apply()
            }
            Users.AdminUser = admin
            // Go to graduate dashboard
            startActivity(Intent(this, AdminLandingActivity::class.java))
        }
        finish()
    }

    fun logoutUser() {
        userSharedPref.edit().apply {
            remove(Constants.SHARED_PREF_UID)
            remove(Constants.SHARED_PREF_ROLE)
            apply()
        }
        finishAffinity()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun getAdminUser(): Admin? {
        return Users.AdminUser
    }

    fun getGraduateUser(): Graduate? {
        return Users.GraduateUser
    }


}