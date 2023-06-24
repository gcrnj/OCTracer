package com.kieltech.octracer.data

import android.os.Parcelable
import android.text.SpannableString
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SpannableGraduate(
    val graduate: Graduate,
    val nameSpannableString: @RawValue SpannableString,
    val occupationSpannableString: @RawValue SpannableString
) : Parcelable