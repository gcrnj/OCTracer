package com.kieltech.octracer.ui.register

import android.content.Context
import com.kieltech.octracer.R
import com.kieltech.octracer.ui.login.LoginValidation
import com.kieltech.octracer.utils.Constants

class RegisterValidation {
    companion object {
        data class GraduateValidation(
            val first_name: String? = null,
            val middle_name: String? = null,
            val last_name: String? = null,
            val address: String? = null,
            val mobile_number: String? = null,
            val occupation: String? = null,
            val year_graduated: String? = null,
            val email: String? = null,
            val password: String? = null,
            val confirmPassword: String? = null,
            val shouldValidatePassword: Boolean
        ) {

            fun findErrors(context: Context): GraduateErrors? {
                var firstNameError = ""
                var middleNameError = ""
                var lastNameError = ""
                var addressError = ""
                var mobileNumberError = ""
                var occupationError = ""
                var yearGraduatedError = ""
                var emailError = ""
                var passwordError = ""
                var confirmPasswordError = ""
                var valid = true
                val loginErrors = LoginValidation().validCredentials(email ?: "", password ?: "")
                val loginEmailError = loginErrors?.emailError
                val loginPasswordError = loginErrors?.passwordError
                if (first_name.isNullOrEmpty() || first_name.length < Constants.NAME_MIN_LENGTH) {
                    firstNameError = context.getString(
                        R.string.this_field_requires_minimum_characters,
                        Constants.NAME_MIN_LENGTH
                    )
                    valid = false
                }
//                if (middle_name.isNullOrEmpty() || middle_name.length < Constants.NAME_MIN_LENGTH) {
//                    middleNameError = context.getString(
//                        R.string.this_field_requires_minimum_characters,
//                        Constants.NAME_MIN_LENGTH
//                    )
//                    valid = false
//                }
                if (last_name.isNullOrEmpty() || last_name.length < Constants.NAME_MIN_LENGTH) {
                    lastNameError = context.getString(
                        R.string.this_field_requires_minimum_characters,
                        Constants.NAME_MIN_LENGTH
                    )
                    valid = false
                }
                if (address.isNullOrEmpty() || address.length < Constants.NAME_MIN_LENGTH) {
                    addressError = context.getString(R.string.this_field_cannot_be_empty)
                    valid = false
                }
                if (mobile_number.isNullOrEmpty() || mobile_number.length != Constants.MOBILE_NUMBER_REQUIRED_LENGTH) {
                    mobileNumberError = context.getString(
                        R.string.this_field_requires_characters,
                        Constants.MOBILE_NUMBER_REQUIRED_LENGTH
                    )
                    valid = false
                }
                if (occupation.isNullOrEmpty()) {
                    occupationError = context.getString(R.string.this_field_is_required)
                    valid = false
                }
                if (year_graduated.isNullOrEmpty() || year_graduated.length != Constants.YEAR_REQUIRED_LENGTH) {
                    occupationError = context.getString(R.string.this_field_is_required)
                    valid = false
                }
                if (year_graduated.isNullOrEmpty() || !isYearInRange(year_graduated)) {
                    yearGraduatedError = context.getString(R.string.this_input_is_invalid)
                    valid = false
                }
                if (loginErrors != null && loginErrors.hasErrors()) {
                    if (loginEmailError != null) {
                        emailError = loginEmailError
                        valid = false
                    }
                    if (loginPasswordError != null && shouldValidatePassword) {
                        passwordError = loginPasswordError
                        valid = false
                    }
                }
                if (password != confirmPassword && shouldValidatePassword) {
                    confirmPasswordError = context.getString(R.string.password_should_match)
                    valid = false
                }

                return if (valid) null
                else GraduateErrors(
                    firstNameError,
                    middleNameError,
                    lastNameError,
                    addressError,
                    mobileNumberError,
                    occupationError,
                    yearGraduatedError,
                    emailError,
                    passwordError,
                    confirmPasswordError
                )
            }

            private fun isYearInRange(yearString: String): Boolean {
                val year = yearString.toIntOrNull()
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                return year != null && year in 1954..currentYear
            }

        }

        data class GraduateErrors(
            val first_name: String? = null,
            val middle_name: String? = null,
            val last_name: String? = null,
            val address: String? = null,
            val mobile_number: String? = null,
            val occupation: String? = null,
            val year_graduated: String? = null,
            val email: String? = null,
            val password: String? = null,
            val confirm_password: String? = null,
        ) {
        }
    }
}