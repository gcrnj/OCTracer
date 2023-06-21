package com.kieltech.octracer.data

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Graduate(
    val first_name: String? = null,
    val middle_name: String? = null,
    val last_name: String? = null,
    val birth_date: Timestamp? = null,
    val year_graduated: Timestamp? = null,
    val email: String? = null,
) : Parcelable
