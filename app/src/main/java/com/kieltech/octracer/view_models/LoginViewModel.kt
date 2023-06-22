package com.kieltech.octracer.view_models

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.kieltech.octracer.R
import com.kieltech.octracer.ui.login.LoginListener
import com.kieltech.octracer.ui.login.LoginValidation
import com.kieltech.octracer.utils.OCTracerFunctions.generateAdminUser
import com.kieltech.octracer.utils.OCTracerFunctions.generateGraduateUser
import com.kieltech.octracer.utils.Utils
import org.mindrot.jbcrypt.BCrypt

class LoginViewModel : ViewModel() {
    private val TAG = "LoginViewModel"

    private val _credentialsErrors = MutableLiveData<LoginValidation.LoginErrors?>()
    val credentialsErrors: LiveData<LoginValidation.LoginErrors?> = _credentialsErrors

    private val _rolesError = MutableLiveData<String?>()
    val rolesError: LiveData<String?> = _rolesError

    private val _currentCollection = MutableLiveData<CollectionReference?>()
    val currentCollection: LiveData<CollectionReference?> = _currentCollection


    fun setErrors(newRolesError: String?, loginErrors: LoginValidation.LoginErrors?) {
        _rolesError.value = newRolesError
        _credentialsErrors.value = loginErrors
    }

    fun clearErrors() {
        setErrors(null, null)
    }

    fun login(
        context: Context,
        userInputCredentials: LoginValidation.LoginCredentials,
        loginListener: LoginListener
    ) {
        loginListener.onLoginStart()
        //Add first
        currentCollection.value?.let { collection ->
            collection
                .whereEqualTo("email", userInputCredentials.email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    when (val size = querySnapshot.size()) {
                        0 -> {
                            // No email found
                            val emailError = context.getString(R.string.no_users_found)
                            val newError = LoginValidation.LoginErrors(emailError, null)
                            setErrors(null, newError)
                        }

                        1 -> {
                            // Only 1 email, check password
                            val firstDocument = querySnapshot.documents[0]
                            val credentialsFromDB: LoginValidation.LoginCredentials =
                                firstDocument.toObject(LoginValidation.LoginCredentials::class.java)!!
                            checkLogin(
                                context,
                                firstDocument,
                                userInputCredentials,
                                credentialsFromDB,
                                firstDocument.id,
                                loginListener
                            )
                        }

                        else -> {
                            // Too many emails
                            val emailError = context.getString(R.string.too_many_emails, size)
                            val newError = LoginValidation.LoginErrors(emailError, null)
                            setErrors(null, newError)
                        }
                    }
                    loginListener.onLoginProcessDone()
                }
                .addOnFailureListener { e ->
                    val newError = LoginValidation.LoginErrors(null, e.message)
                    setErrors(null, newError)
                }
                .addOnCompleteListener {
                    loginListener.onLoginProcessDone()
                }
        }
    }

    private fun checkLogin(
        context: Context,
        userDocument: DocumentSnapshot,
        userInputCredentials: LoginValidation.LoginCredentials,
        credentialsFromDB: LoginValidation.LoginCredentials,
        credentialsId: String,
        loginListener: LoginListener,
    ) {
        val emailInput = userInputCredentials.email
        val passwordInput = userInputCredentials.password

        val emailFromDB = credentialsFromDB.email
        val passwordFromDB = credentialsFromDB.password

        val equalPasswords = BCrypt.checkpw(passwordInput, passwordFromDB)

        Log.d(TAG, "userInputCredentials: $userInputCredentials")
        Log.d(TAG, "credentialsFromDB: $credentialsFromDB")
        val userFound = emailInput == emailFromDB && equalPasswords

        val collection = _currentCollection.value

        if (userFound) {
            when (collection) {
                Utils.graduatesCollection -> {
                    val graduate = userDocument.generateGraduateUser()
                    loginListener.onLoginSuccess(
                        currentCollection.value?.id.toString(),
                        credentialsId,
                        graduate,
                        null
                    )
                }
                Utils.adminCollection -> {
                    val admin = userDocument.generateAdminUser()
                    loginListener.onLoginSuccess(
                        currentCollection.value?.id.toString(),
                        credentialsId,
                        null,
                        admin
                    )
                }
                else -> {
                    setErrors(context.getString(R.string.please_select_a_role), null)
                }
            }
        } else {
            val newError = LoginValidation.LoginErrors(null, context.getString(R.string.no_users_found))
            setErrors(null, newError)
        }
    }

    fun changeUserFirestoreCollection(collection: CollectionReference) {
        _currentCollection.value = collection
    }

    fun retrieveUserData(
        context: Context,
        collection: CollectionReference,
        uid: String,
        loginListener: LoginListener
    ) {
        loginListener.onLoginStart()
        collection.document(uid)
            .get()
            .addOnSuccessListener { userDocument ->
                when (collection) {
                    Utils.graduatesCollection -> {
                        val graduate = userDocument.generateGraduateUser()
                        loginListener.onLoginSuccess(
                            currentCollection.value?.id.toString(),
                            uid,
                            graduate,
                            null
                        )
                    }
                    Utils.adminCollection -> {
                        val admin = userDocument.generateAdminUser()
                        loginListener.onLoginSuccess(
                            currentCollection.value?.id.toString(),
                            uid,
                            null,
                            admin
                        )
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                loginListener.onLoginProcessDone()
            }
    }
}