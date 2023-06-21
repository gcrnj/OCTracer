package com.kieltech.octracer.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.kieltech.octracer.ui.register.RegisterValidation
import com.kieltech.octracer.utils.OCTracerFunctions.hashPassword
import kotlinx.parcelize.Parcelize

@Parcelize
data class Graduate(
    val first_name: String? = "",
    val middle_name: String? = "",
    val last_name: String? = "",
    val address: String? = "",
    val mobile_number: String? = "",
    val occupation: String? = "",
    val year_graduated: String? = "",
    val email: String? = "",
    var password: String? = "",
) : Parcelable {

    fun hashPassword() {
        password = password?.hashPassword()
    }

    fun toValidation(confirmPassword: String?): RegisterValidation.Companion.GraduateValidation =
        RegisterValidation.Companion.GraduateValidation(
            first_name,
            middle_name,
            last_name,
            address,
            mobile_number,
            occupation,
            year_graduated,
            email,
            password,
            confirmPassword
        )

}