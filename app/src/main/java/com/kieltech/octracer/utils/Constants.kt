package com.kieltech.octracer.utils

object Constants {


    // SHARED PREFERENCE KEYS
    const val USER_PREFS = "USER_PREFS"
    const val SHARED_PREF_UID = "SHARED_PREF_UID"
    const val SHARED_PREF_ROLE = "SHARED_PREF_ROLE"

    // INTENT EXTRAS
    const val INTENT_EXTRA_UID = "INTENT_EXTRA_UID"
    const val INTENT_EXTRA_ROLE = "INTENT_EXTRA_ROLE"
    const val INTENT_EXTRA_SEARCH = "INTENT_EXTRA_SEARCH"
    const val INTENT_SPANNABLE_GRADUATE = "INTENT_SPANNABLE_GRADUATE"

    // LOGIN CONSTANTS
    const val PASSWORD_MIN_LENGTH = 8
    const val INVALID_EMAIL = "Invalid Email Address"
    const val PASSWORD_LESS_THAN_MIN_LENGTH = "Password must be less than 8 characters"

    const val ADMIN_COLLECTION_PATH: String = "admin"
    const val GRADUATES_COLLECTION_PATH: String = "graduates"
    const val FIRST_NAME_KEY: String = "first_name"
    const val MIDDLE_NAME_KEY: String = "middle_name"
    const val LAST_NAME_KEY: String = "last_name"
    const val OCCUPATION_KEY: String = "occupation"
    const val EMAIL_KEY: String = "email"
    const val IS_VERIFIED_KEY: String = "verified"
    const val PASSWORD_KEY: String = "password"
    const val COLLECTION_ID_KEY: String = "collectionId"
    const val TAG_KEY: String = "TAG"
    const val CREATOR_KEY: String = "CREATOR"
    const val GRADUATED_YEAR_KEY: String = "year_graduated"

    // REGISTER CONSTANTS
    const val NAME_MIN_LENGTH = 2
    const val MOBILE_NUMBER_REQUIRED_LENGTH = 11
    const val YEAR_REQUIRED_LENGTH = 4

}