package com.kieltech.octracer.view_models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.kieltech.octracer.R
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.ui.register.RegisterListener
import com.kieltech.octracer.ui.register.RegisterValidation
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.hashPassword
import com.kieltech.octracer.utils.OCTracerFunctions.toHashMap

class RegisterViewModel : ViewModel() {


    private val _currentCollection = MutableLiveData<CollectionReference?>()
    val currentCollection: LiveData<CollectionReference?> = _currentCollection

    private val _unauthorizedMessage = MutableLiveData<String?>()
    val unauthorizedMessage: LiveData<String?> = _unauthorizedMessage

    private val _graduateErrors = MutableLiveData<RegisterValidation.Companion.GraduateErrors?>()
    val graduateErrors: LiveData<RegisterValidation.Companion.GraduateErrors?> = _graduateErrors

    fun setUnauthorizedMessage(newMessage: String) {
        _unauthorizedMessage.value = newMessage
    }

    fun setNewErrors(newError: RegisterValidation.Companion.GraduateErrors?) {
        _graduateErrors.value = newError
    }

    fun setCurrentCollectionTo(newCollection: CollectionReference) {
        _currentCollection.value = newCollection
    }

    fun initiateRegisterForGraduate(
        context: Context,
        graduate: Graduate,
        graduateValidation: RegisterValidation.Companion.GraduateValidation,
        listener: RegisterListener
    ) {

        val errors = graduateValidation.findErrors(context)
        if (errors != null) {
            // There is an error
            setNewErrors(errors)
            listener.onRegisterProcessDone()
            return
        }

        //register
        val collection = _currentCollection.value
        graduate.hashPassword()
        if (collection == null) {
            Toast.makeText(context, context.getString(R.string.role_not_found), Toast.LENGTH_SHORT)
                .show()
            return
        }
        collection.whereEqualTo(Constants.EMAIL_KEY, graduate.email)
            .get()
            .addOnSuccessListener { snapshot->
                if(snapshot.isEmpty){
                    // no user with same email
                    // register
                    regiserGradute(context, graduate, listener)
                } else {
                    // email is taken
                    val newError = RegisterValidation.Companion.GraduateErrors(
                        email = context.getString(R.string.this_email_is_already_taken)
                    )
                    setNewErrors(newError)
                    listener.onRegisterProcessDone()
                }
            }
            .addOnFailureListener { error ->
                // Error
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                listener.onRegisterProcessDone()
            }

    }

    private fun regiserGradute(
        context: Context,
        graduate: Graduate,
        listener: RegisterListener
    ) {
        val collection = _currentCollection.value
        graduate.hashPassword()
        if (collection == null) {
            Toast.makeText(context, context.getString(R.string.role_not_found), Toast.LENGTH_SHORT)
                .show()
            return
        }
        collection.add(graduate)
            .addOnSuccessListener { document ->
                listener.onRegisterSuccess(collection.id, document.id, graduate)
            }
            .addOnFailureListener { error ->
                // Error
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                listener.onRegisterProcessDone()
            }
            .addOnCompleteListener {
                listener.onRegisterProcessDone()
            }

    }
}