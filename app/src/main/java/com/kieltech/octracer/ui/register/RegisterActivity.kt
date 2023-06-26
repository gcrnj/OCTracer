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
import com.kieltech.octracer.utils.OCTracerFunctions.parcelable
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
    private val graduateToEdit: Graduate? by lazy {
        intent.parcelable(Constants.GRADUATES_COLLECTION_PATH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        registerViewModel.setCurrentCollectionTo(Utils.graduatesCollection)
        setData()
        setOnClickListeners()
        defineViewModelObservers()
    }

    private fun setData() {
        binding.registerButton.text =
            getString(
                if (getGraduateUser() == null && getAdminUser() == null) R.string.register
                else R.string.edit
            )

        if (graduateToEdit == null) {
            //testCreds()
        } else {
            putEdit(graduateToEdit!!)
        }
    }

    private fun putEdit(graduateToEdit: Graduate) {
        binding.apply {
            graduateToEdit.apply {
                firstNameEditText.setText(first_name)
                middleNameEditText.setText(middle_name)
                lastNameEditText.setText(last_name)
                yearGraduatedEditText.setText(year_graduated?.toString())
                addressEditText.setText(address)
                mobileNumberEditText.setText(mobile_number)
                occupationEditText.setText(occupation)
                emailEditText.setText(email)
                passwordInputLayout.gone()
                confirmPasswordInputLayout.gone()
            }
        }
    }

    private fun testCreds() {
        binding.apply {
            firstNameEditText.setText(Utils.generateRandomFirstName())
            middleNameEditText.setText(Utils.generateRandomMiddleName())
            lastNameEditText.setText(Utils.generateRandomLastName())
            yearGraduatedEditText.setText(Utils.generateRandomYearGraduated())
            addressEditText.setText(Utils.generateRandomAddress())
            mobileNumberEditText.setText(Utils.generateRandomMobileNumber())
            occupationEditText.setText(Utils.generateRandomOccupation())
            emailEditText.setText(Utils.generateRandomEmail())
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

    override fun onRegisterSuccess(
        collectionId: String,
        firestoreUserId: String,
        graduate: Graduate
    ) {
        saveUserAndGoToGraduatesLanding(
            collectionId = collectionId,
            firestoreUserId = firestoreUserId,
            graduateUser = graduate,
            shouldLoginAutomatically = (getGraduateUser() == null && getAdminUser() == null),
        )
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
                        val isEdit = graduateToEdit != null
                        val isVerified = if(isEdit) graduateToEdit?.verified else false
                        val graduate = Graduate(
                            id = if (isEdit) graduateToEdit?.id else null,
                            first_name = firstNameEditText.text.toString(),
                            middle_name = middleNameEditText.text.toString(),
                            last_name = lastNameEditText.text.toString(),
                            address = addressEditText.text.toString(),
                            mobile_number = mobileNumberEditText.text.toString(),
                            occupation = occupationEditText.text.toString(),
                            year_graduated = yearGraduatedEditText.text.toString().toIntOrNull(),
                            email = emailEditText.text.toString().lowercase(),
                            password = passwordEditText.text.toString(),
                            verified = isVerified
                        )
                        registerViewModel.initiateRegisterForGraduate(
                            this@RegisterActivity,
                            graduate,
                            graduate.toValidation(confirmPasswordEditText.text.toString(), !isEdit),
                            this@RegisterActivity,
                            isEdit
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