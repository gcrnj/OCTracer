package com.kieltech.octracer.ui.register

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.ActivityRegisterBinding
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.disabled
import com.kieltech.octracer.utils.OCTracerFunctions.enabled
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.hideSoftKeyboard
import com.kieltech.octracer.utils.OCTracerFunctions.milliseconds
import com.kieltech.octracer.utils.OCTracerFunctions.visible
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

    private val noAccessDialog: AlertDialog by lazy {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(getString(R.string.you_don_t_have_an_access_to_this_page))
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok)) { _, _ ->
            dialog.dismiss()
            finish()
        }
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        testCreds()
        checkActivityAuthorization()
        setOnClickListeners()
        defineViewModelObservers()
    }

    private fun testCreds() {
        binding.apply {
            firstNameEditText.setText( "Pikoy")
            middleNameEditText.setText( "Basta")
            lastNameEditText.setText( "Last Name")
            yearGraduatedEditText.setText( "1999")
            addressEditText.setText( "Taga Didto Basta")
            mobileNumberEditText.setText( "09123456789")
            occupationEditText.setText( "Crew")
            emailEditText.setText( "first@second.com")
            passwordEditText.setText( "12345678")
            confirmPasswordEditText.setText( "12345678")
        }
    }

    override fun onRegisterStart() {
        with(binding) {
            registerButton.disabled()
            loading.visible()
            registerViewModel.setNewErrors(null)
        }
    }

    override fun onRegisterSuccess(collectionId: String, firestoreUserId: String, user: Any) {
        saveUserAndGoToNextActivity(collectionId, firestoreUserId, user)
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
                    if (registerViewModel.currentCollection.value == Utils.superAdminCollection) {
                        // TODO Create admin credentials
                    } else if (registerViewModel.currentCollection.value == Utils.graduatesCollection) {
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
        registerViewModel.unauthorizedMessage.observe(this) { message ->
            if (message == null) {
                noAccessDialog.dismiss()
            } else {
                showNoUserError(message)
            }
        }

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

    private fun checkActivityAuthorization() {
        if (getAdminUser() != null) {
            val unauthorizedMessage = getString(
                R.string.your_current_user_is,
                getString(R.string.admin)
            )
            registerViewModel.setUnauthorizedMessage(unauthorizedMessage)
        } else if (getSuperAdminUser() != null) {
            registerViewModel.setCurrentCollectionTo(Utils.adminCollection)
        } else if (getGraduateUser() == null) {
            registerViewModel.setCurrentCollectionTo(Utils.graduatesCollection)
        } else if (getGraduateUser() != null) {
            val unauthorizedMessage = getString(
                R.string.your_current_user_is,
                getString(R.string.graduate)
            )
            registerViewModel.setUnauthorizedMessage(unauthorizedMessage)
        }
    }

    private fun showNoUserError(message: String) {
        with(noAccessDialog) {
            setMessage(message)
            show()
        }

    }
}