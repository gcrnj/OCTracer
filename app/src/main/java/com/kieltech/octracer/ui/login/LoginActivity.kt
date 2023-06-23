package com.kieltech.octracer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.ActivityLoginBinding
import com.kieltech.octracer.ui.register.RegisterActivity
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.disabled
import com.kieltech.octracer.utils.OCTracerFunctions.enabled
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.hideSoftKeyboard
import com.kieltech.octracer.utils.OCTracerFunctions.milliseconds
import com.kieltech.octracer.utils.OCTracerFunctions.visible
import com.kieltech.octracer.utils.Users
import com.kieltech.octracer.utils.Utils
import com.kieltech.octracer.view_models.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate),
    LoginListener {

    private val TAG = "LoginActivity"
    private val loginViewModel: LoginViewModel by lazy {
        createViewModel()
    }
    private val rolesCollection by lazy {
        listOf(
            Utils.adminCollection,
            Utils.graduatesCollection
        )
    }
    private val rolesTitle by lazy {
        listOf(
            getString(R.string.admin),
            getString(R.string.graduate),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //testCreds()
        checkSavedUser()
        setClickListeners()
        defineRolesSelector()
        defineViewModelObservers()
    }

    private fun testCreds() {
        with(binding) {
            emailEditText.setText(Users.exampleEmail)
            passwordEditText.setText(Users.examplePassword)
        }
    }

    override fun onLoginStart() {
        binding.loginButton.disabled()
        binding.loading.visible()
        loginViewModel.clearErrors()
        hideSoftKeyboard()
    }

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

    override fun onLoginProcessDone() {
        binding.loading.gone()
        binding.loginButton.enabled()
    }

    private fun defineViewModelObservers() {
        loginViewModel.credentialsErrors.observe(this) { errors ->
            with(binding) {
                emailInputLayout.isErrorEnabled = false
                passwordInputLayout.isErrorEnabled = false
                emailInputLayout.error = errors?.emailError
                passwordInputLayout.error = errors?.passwordError
            }
        }

        loginViewModel.rolesError.observe(this) { error ->
            binding.categoryInputLayout.isErrorEnabled = false
            binding.categoryInputLayout.error = error
        }
    }

    private fun defineRolesSelector() {
        with(binding) {
            val adapter =
                ArrayAdapter(
                    this@LoginActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    rolesTitle
                )
            categoryAutoComplete.setAdapter(adapter)
            categoryAutoComplete.setOnClickListener {
                categoryAutoComplete.showDropDown()
            }
            categoryAutoComplete.setOnItemClickListener { parent, view, position, id ->
                val newCollection = rolesCollection[position]
                loginViewModel.changeUserFirestoreCollection(newCollection)
            }
        }
    }

    private fun setClickListeners() {
        with(binding) {
            registerButton.setOnClickListener {
                val registerIntent =
                    Intent(this@LoginActivity, RegisterActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                    )
                resultLauncher.launch(registerIntent)
            }
            loginButton.setOnClickListener {
                lifecycleScope.launch {
                    onLoginStart()
                    delay(500.milliseconds())
                    // Check roles first
                    if (loginViewModel.currentCollection.value == null) {
                        loginViewModel.setErrors(getString(R.string.please_select_a_role), null)
                        onLoginProcessDone()
                        return@launch
                    }
                    val email = emailEditText.text.toString()
                    val password = passwordEditText.text.toString()
                    val errors = LoginValidation().validCredentials(email, password)
                    if (errors?.hasErrors() == true) {
                        // Has Errors
                        loginViewModel.setErrors(null, errors)
                        onLoginProcessDone()
                    } else {
                        val userCredentials = LoginValidation.LoginCredentials(
                            email = email, password = password
                        )
                        val role = when (loginViewModel.currentCollection.value) {
                            Utils.adminCollection -> "admin"
                            Utils.graduatesCollection -> "Graduate"
                            else -> null
                        }
                        Log.d(TAG, "setClickListeners: $role - $userCredentials")
                        loginViewModel.login(
                            context = this@LoginActivity,
                            userInputCredentials = userCredentials,
                            loginListener = this@LoginActivity
                        )
                    }
                }
            }
        }
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
                    this@LoginActivity,
                    collection,
                    uid,
                    this@LoginActivity
                )

            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val uid = data?.getStringExtra(Constants.INTENT_EXTRA_UID)
                val role = data?.getStringExtra(Constants.INTENT_EXTRA_ROLE)
                if (uid != null && role != null) {
                    val collection = Firebase.firestore.collection(role)
                    loginViewModel.retrieveUserData(
                        this@LoginActivity,
                        collection,
                        uid,
                        this@LoginActivity
                    )
                }
            }
        }


}