package com.kieltech.octracer.ui.register

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.ActivityRegisterBinding
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
import com.kieltech.octracer.view_models.RegisterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterActivity : BaseActivity<ActivityRegisterBinding>(ActivityRegisterBinding::inflate),
    RegisterListener {

    private val TAG = "RegisterActivity"
    private val registerViewModel: RegisterViewModel by lazy {
        createViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        registerViewModel.setCurrentCollectionTo(Utils.graduatesCollection)
        testCreds()
        setOnClickListeners()
        defineViewModelObservers()
    }

    private fun testCreds() {
        binding.apply {
            firstNameEditText.setText( Users.exampleFirstName)
            middleNameEditText.setText( Users.exampleMidName)
            lastNameEditText.setText( Users.exampleLastName)
            yearGraduatedEditText.setText(Users.exampleYearGraduated)
            addressEditText.setText(Users.exampleAddress)
            mobileNumberEditText.setText(Users.exampleMobileNumber)
            occupationEditText.setText(Users.exampleOccupation)
            emailEditText.setText(Users.exampleEmail)
            passwordEditText.setText(Users.examplePassword)
            confirmPasswordEditText.setText(Users.examplePassword)
        }
    }

    override fun onRegisterStart() {
        with(binding) {
            registerButton.disabled()
            loading.visible()
            registerViewModel.setNewErrors(null)
        }
    }

    override fun onRegisterSuccess(collectionId: String, firestoreUserId: String, graduate: Graduate) {
        saveUserAndGoToGraduatesLanding(
            collectionId = collectionId,
            firestoreUserId = firestoreUserId,
            graduateUser = graduate,
            shouldLoginAutomatically = false)
    }

    override fun onRegisterProcessDone() {
        with(binding) {
            registerButton.enabled()
            loading.gone()
        }
    }

    private fun setOnClickListeners() {
        binding.registerButton.setOnClickListener {

            with(binding) {

                lifecycleScope.launch {
                    onRegisterStart()
                    hideSoftKeyboard()
                    delay(500.milliseconds())
                    // Check roles first
                    if (registerViewModel.currentCollection.value == null) {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.please_select_a_role),
                            Toast.LENGTH_SHORT
                        ).show()
                        onRegisterProcessDone()
                        return@launch
                    }
                    // Crate user credentials
                    if (registerViewModel.currentCollection.value == Utils.graduatesCollection) {
                        //Create graduate collection
                        val graduate = Graduate(
                            first_name = firstNameEditText.text.toString(),
                            middle_name = middleNameEditText.text.toString(),
                            last_name = lastNameEditText.text.toString(),
                            address = addressEditText.text.toString(),
                            mobile_number = mobileNumberEditText.text.toString(),
                            occupation = occupationEditText.text.toString(),
                            year_graduated = yearGraduatedEditText.text.toString(),
                            email = emailEditText.text.toString().lowercase(),
                            password = passwordEditText.text.toString(),
                        )
                        Log.d(
                            TAG,
                            "defineViewModelObservers: ${
                                graduate.toValidation(
                                    confirmPasswordEditText.text.toString()
                                )
                            }"
                        )
                        registerViewModel.initiateRegisterForGraduate(
                            this@RegisterActivity,
                            graduate,
                            graduate.toValidation(confirmPasswordEditText.text.toString()),
                            this@RegisterActivity
                        )
                    }
                }
            }
        }
    }

    private fun defineViewModelObservers() {
        registerViewModel.graduateErrors.observe(this) { errors ->
            Log.d(TAG, "defineViewModelObservers: $errors")
            binding.apply {
                firstNameInputLayout.isErrorEnabled = false
                middleNameInputLayout.isErrorEnabled = false
                lastNameInputLayout.isErrorEnabled = false
                addressInputLayout.isErrorEnabled = false
                mobileNumberInputLayout.isErrorEnabled = false
                occupationInputLayout.isErrorEnabled = false
                yearGraduatedInputLayout.isErrorEnabled = false
                emailInputLayout.isErrorEnabled = false
                passwordInputLayout.isErrorEnabled = false
                confirmPasswordInputLayout.isErrorEnabled = false
                firstNameInputLayout.error = errors?.first_name
                middleNameInputLayout.error = errors?.middle_name
                lastNameInputLayout.error = errors?.last_name
                addressInputLayout.error = errors?.address
                mobileNumberInputLayout.error = errors?.mobile_number
                occupationInputLayout.error = errors?.occupation
                yearGraduatedInputLayout.error = errors?.year_graduated
                emailInputLayout.error = errors?.email
                passwordInputLayout.error = errors?.password
                confirmPasswordInputLayout.error = errors?.confirm_password
            }
        }

    }

}