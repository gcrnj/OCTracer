package com.kieltech.octracer.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.kieltech.octracer.R
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.data.SuperAdmin
import com.kieltech.octracer.ui.landing.graduates.GraduatesLandingActivity
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

    fun saveUserAndGoToNextActivity(collectionId: String, firestoreUserId: String, user: Any) {
        userSharedPref.edit().apply {
            putString(Constants.SHARED_PREF_UID, firestoreUserId)
            putString(Constants.SHARED_PREF_ROLE, collectionId)
            apply()
        }
        // Go to next page
        when (collectionId) {
            Constants.SUPER_ADMIN_COLLECTION_PATH -> {
                val superAdminUser = user as SuperAdmin
                Users.SuperAdminUser = superAdminUser
                // Go to super admin dashboard
            }

            Constants.ADMIN_COLLECTION_PATH -> {
                val adminUser = user as Admin
                Users.AdminUser = adminUser
                // Go to admin dashboard
            }

            Constants.GRADUATES_COLLECTION_PATH -> {
                val graduateUser = user as Graduate
                Users.GraduateUser = graduateUser
                // Go to graduate dashboard
                startActivity(Intent(this, GraduatesLandingActivity::class.java))
                finish()
            }

            else -> {
                Snackbar
                    .make(
                        binding.root,
                        getString(R.string.please_select_a_role),
                        Snackbar.LENGTH_LONG
                    )
                    .show()
            }
        }
    }

    fun getSuperAdminUser(): SuperAdmin? {
        return Users.SuperAdminUser
    }

    fun getAdminUser(): Admin? {
        return Users.AdminUser
    }

    fun getGraduateUser(): Graduate? {
        return Users.GraduateUser
    }


}