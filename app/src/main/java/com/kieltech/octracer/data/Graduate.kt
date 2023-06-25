package com.kieltech.octracer.data

import android.os.Parcelable
import android.util.Log
import com.kieltech.octracer.ui.register.RegisterValidation
import com.kieltech.octracer.utils.OCTracerFunctions.hashPassword
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Graduate(
    var id: String? = "",
    val first_name: String? = "",
    val middle_name: String? = "",
    val last_name: String? = "",
    val address: String? = "",
    val mobile_number: String? = "",
    val occupation: String? = "",
    val year_graduated: Int? = 0,
    val email: String? = "",
    var password: String? = "",
    val profilePic: String? = "",
    val verified: Boolean? = false,
    var searchable: List<String>? = emptyList(),
) : Parcelable {

    @IgnoredOnParcel
    private val TAG = "Graduate"

    fun fullName(): String {
        val fullName = StringBuilder()
        fullName.append(first_name?.trim())
        if (!middle_name.isNullOrBlank()) {
            fullName.append(" ").append(middle_name.trim())
        }
        fullName.append(" ").append(last_name?.trim())
        return fullName.toString()
    }

    fun hashPassword() {
        Log.d(TAG, "oldPass: $password")
        val hashedPass = password?.hashPassword()
        password = hashedPass
        Log.d(TAG, "hashPassword: $password")
    }

    fun toValidation(
        confirmPassword: String?,
        shouldValidatePassword: Boolean
    ): RegisterValidation.Companion.GraduateValidation =
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

    fun addSearchable() {
        val firstNameList = splitLowercase(first_name)
        val middleNameList = splitLowercase(middle_name)
        val lastNameList = splitLowercase(last_name)
        val addressList = splitLowercase(address)
        val mobileNumberList = splitLowercase(mobile_number)
        val occupationList = splitLowercase(occupation)
        val yearGraduatedList = splitLowercase(year_graduated?.toString())
        val emailList = splitLowercase(email)

        searchable = combineLists(
            firstNameList,
            middleNameList,
            lastNameList,
            addressList,
            mobileNumberList,
            occupationList,
            yearGraduatedList,
            emailList
        )
    }

    private fun splitLowercase(field: String?): List<String>? {
        return field?.split(" ")?.map { it.lowercase().trim(',') }
    }

    private fun combineLists(vararg lists: List<String>?): List<String> {
        return lists.flatMap { it ?: emptyList() }
    }

}
