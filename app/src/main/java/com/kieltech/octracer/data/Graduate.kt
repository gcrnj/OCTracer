package com.kieltech.octracer.data

import android.os.Parcelable
import android.util.Log
import com.kieltech.octracer.ui.register.RegisterValidation
import com.kieltech.octracer.utils.OCTracerFunctions.hashPassword
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Graduate(
    var collectionId: String? = "",
    val first_name: String? = "",
    val middle_name: String? = "",
    val last_name: String? = "",
    val address: String? = "",
    val mobile_number: String? = "",
    val occupation: String? = "",
    val year_graduated: Int? = 0,
    val email: String? = "",
    var password: String? = "",
    val profilePic : String? = ""
) : Parcelable {

    @IgnoredOnParcel
    private val TAG = "Graduate"

    fun fullName(): String {
        val newMiddleName = if (middle_name.isNullOrBlank()) "" else middle_name.trim()
        val newLastName = " $last_name"
        return "$first_name$newMiddleName$newLastName"
    }

    fun hashPassword() {
        Log.d(TAG, "oldPass: $password")
        val hashedPass = password?.hashPassword()
        password = hashedPass
        Log.d(TAG, "hashPassword: $password")
    }

    fun toValidation(confirmPassword: String?, shouldValidatePassword: Boolean): RegisterValidation.Companion.GraduateValidation =
        RegisterValidation.Companion.GraduateValidation(
            first_name,
            middle_name,
            last_name,
            address,
            mobile_number,
            occupation,
            year_graduated.toString(),
            email,
            password,
            confirmPassword,
            shouldValidatePassword
        )

}
