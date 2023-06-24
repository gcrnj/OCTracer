package com.kieltech.octracer.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.ActivityFlashBinding
import com.kieltech.octracer.ui.login.LoginActivity
import com.kieltech.octracer.ui.login.LoginListener
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.disabled
import com.kieltech.octracer.utils.OCTracerFunctions.enabled
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.hideSoftKeyboard
import com.kieltech.octracer.utils.OCTracerFunctions.visible
import com.kieltech.octracer.utils.Utils
import com.kieltech.octracer.view_models.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlashActivity : BaseActivity<ActivityFlashBinding>(ActivityFlashBinding::inflate),
    LoginListener {


    override fun onLoginStart() {}

    override fun onLoginSuccess(
        collectionId: String,
        firestoreUserId: String,
        graduate: Graduate?,
        admin: Admin?
    ) {
        if (graduate != null) {
            saveUserAndGoToGraduatesLanding(
                collectionId = collectionId,
                firestoreUserId = firestoreUserId,
                graduateUser = graduate,
                shouldLoginAutomatically = true
            )
        } else if (admin != null) {
            saveUserAndGoToAdminLanding(
                collectionId = collectionId,
                firestoreUserId = firestoreUserId,
                admin = admin,
                shouldLoginAutomatically = true
            )
        }
    }

    override fun onLoginProcessDone() {}

    private val TAG = "FlashActivity"
    private val loginViewModel: LoginViewModel by lazy {
        createViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkSavedUser()
    }

    private fun checkSavedUser() {
        userSharedPref.apply {
            val uid = getString(Constants.SHARED_PREF_UID, null)
            val role = getString(Constants.SHARED_PREF_ROLE, null)
            Log.d(TAG, "checkSavedUser: $uid - $role")

            if (uid != null && role != null) {
                // there is a user
                val collection = when (role) {
                    Constants.ADMIN_COLLECTION_PATH -> {
                        Utils.adminCollection
                        // Go to admin dashboard
                    }

                    else -> {
                        Utils.graduatesCollection
                    }
                }
                loginViewModel.retrieveUserData(
                    this@FlashActivity,
                    collection,
                    uid,
                    this@FlashActivity
                )
            } else {

                lifecycleScope.launch {
                    delay(500)
                    val loginIntent = Intent(this@FlashActivity, LoginActivity::class.java)
                    finishAffinity()
                    startActivity(loginIntent)
                }
            }
        }
    }
}