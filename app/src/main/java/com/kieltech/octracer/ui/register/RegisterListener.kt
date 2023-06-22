package com.kieltech.octracer.ui.register

import com.kieltech.octracer.data.Graduate

interface RegisterListener {

    fun onRegisterStart()

    fun onRegisterSuccess(collectionId: String, firestoreUserId: String, graduate: Graduate)

    fun onRegisterProcessDone()
}