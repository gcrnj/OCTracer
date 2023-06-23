package com.kieltech.octracer.ui.profile

import android.net.Uri
import android.os.Message

interface UploadProfileListener {

    fun onUploadStarted()
    fun onUploadSuccess(uri: Uri)
    fun onUploadFailure(message: String)
    fun onUploadLoading(newProgress: Int)
    fun onUploadDone()
}