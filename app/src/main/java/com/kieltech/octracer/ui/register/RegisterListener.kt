package com.kieltech.octracer.ui.register

interface RegisterListener {

    fun onRegisterStart()

    fun onRegisterSuccess(collectionId: String, firestoreUserId: String, user: Any)

    fun onRegisterProcessDone()
}