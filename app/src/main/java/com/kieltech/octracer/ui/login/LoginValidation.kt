package com.kieltech.octracer.ui.login

import android.util.Patterns
import com.kieltech.octracer.utils.Constants
import org.mindrot.jbcrypt.BCrypt

class LoginValidation {

    data class LoginErrors(
        val emailError: String?,
        val passwordError: String?,
    ) {
        fun hasErrors(): Boolean = (!emailError.isNullOrBlank() || !passwordError.isNullOrBlank())
    }

    data class LoginCredentials(
        val email: String = "",
        val password: String = "",
    )

    // This function returns null - if credential is valid
    fun validCredentials(email: String, password: String): LoginErrors? {
        val emailError = getEmailError(email)
        val passwordError = getPasswordError(password)

        return if (emailError.isNullOrBlank() && passwordError.isNullOrBlank()) null
        else LoginErrors(emailError, passwordError)
    }

    private fun getEmailError(email: String): String? {
        val isEmailValid = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return if (isEmailValid) {
            Constants.INVALID_EMAIL
        } else null
    }

    private fun getPasswordError(password: String): String? {
        val isPasswordValid = password.length < Constants.PASSWORD_MIN_LENGTH
        return if (isPasswordValid) {
            Constants.PASSWORD_LESS_THAN_MIN_LENGTH
        } else null
    }
}