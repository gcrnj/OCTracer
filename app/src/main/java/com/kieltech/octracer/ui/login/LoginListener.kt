package com.kieltech.octracer.ui.login

import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate

interface LoginListener {

    fun onLoginStart()

    fun onLoginSuccess(
        collectionId: String,
        firestoreUserId: String,
        graduate: Graduate?,
        admin: Admin?
    )

    fun onLoginProcessDone()
}