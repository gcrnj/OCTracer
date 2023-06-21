package com.kieltech.octracer.ui.login

interface LoginListener {

    fun onLoginStart()

    fun onLoginSuccess(collectionId: String, firestoreUserId: String, user: Any)

    fun onLoginProcessDone()
}