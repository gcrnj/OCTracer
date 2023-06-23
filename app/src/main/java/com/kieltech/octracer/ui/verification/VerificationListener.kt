package com.kieltech.octracer.ui.verification

interface VerificationListener {
    fun onUpdateStart()
    fun onVerify(graduateId : String)
    fun onVerified()
    fun onDecline(graduateId : String)
    fun onDeclined()
    fun onDone()

}