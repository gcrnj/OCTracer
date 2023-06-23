package com.kieltech.octracer.view_models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieltech.octracer.R
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.ui.home.GetGraduateListener
import com.kieltech.octracer.ui.verification.VerificationListener
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.generateGraduateUser
import com.kieltech.octracer.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerificationViewModel : ViewModel() {

    fun verifyUser(
        context: Context,
        graduateId: String,
        verificationListener: VerificationListener
    ) {
        verificationListener.onUpdateStart()
        viewModelScope.launch {
            delay(500)
            Utils.graduatesCollection.document(graduateId)
                .update(Constants.IS_VERIFIED_KEY, true)
                .addOnSuccessListener {
                    verificationListener.onVerified()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        e.message ?: context.getString(R.string.update_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnCompleteListener {
                    verificationListener.onDone()
                }
        }
    }

    fun declineUser(
        context: Context,
        graduateId: String,
        verificationListener: VerificationListener
    ) {
        verificationListener.onUpdateStart()
        viewModelScope.launch {
            delay(500)
            Utils.graduatesCollection.document(graduateId)
                .delete()
                .addOnSuccessListener {
                    verificationListener.onDeclined()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        e.message ?: context.getString(R.string.update_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnCompleteListener {
                    verificationListener.onDone()
                }
        }
    }
}