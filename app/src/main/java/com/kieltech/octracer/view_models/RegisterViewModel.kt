package com.kieltech.octracer.view_models

import android.content.Context
import android.util.Log
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
import com.kieltech.octracer.utils.OCTracerFunctions.toHashMap
import com.kieltech.octracer.utils.Utils

class RegisterViewModel : ViewModel() {

    private val TAG = "RegisterViewModel"
    private val _currentCollection = MutableLiveData<CollectionReference?>()
    val currentCollection: LiveData<CollectionReference?> = _currentCollection

    private val _graduateErrors = MutableLiveData<RegisterValidation.Companion.GraduateErrors?>()
    val graduateErrors: LiveData<RegisterValidation.Companion.GraduateErrors?> = _graduateErrors

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
        listener: RegisterListener,
        isEdit: Boolean,
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
        if (collection == null) {
            Toast.makeText(context, context.getString(R.string.role_not_found), Toast.LENGTH_SHORT)
                .show()
            return
        }
        collection.whereEqualTo(Constants.EMAIL_KEY, graduate.email)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    // no user with same email
                    // register
                    regiserGradute(context, graduate, listener)
                } else if (snapshot.size() == 1 && graduate.id == snapshot.first().id && isEdit) {
                    // With user and is not new / edit only
                    editGraduate(context, graduate, listener)
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

    private fun editGraduate(
        context: Context,
        graduate: Graduate,
        listener: RegisterListener
    ) {
        val collection = Utils.graduatesCollection
        val newRecordsHash = graduate.toHashMap()
        newRecordsHash.remove(Constants.PASSWORD_KEY)
        newRecordsHash.remove(Constants.COLLECTION_ID_KEY)
        newRecordsHash.remove(Constants.TAG_KEY)
        newRecordsHash.remove(Constants.CREATOR_KEY)
        Log.d(TAG, "editGraduate: $newRecordsHash")
        collection.document(graduate.id!!).update(newRecordsHash)
            .addOnSuccessListener {
                listener.onRegisterSuccess(collection.id, graduate.id!!, graduate)
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}