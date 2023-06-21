package com.kieltech.octracer.view_models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.kieltech.octracer.R
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.data.SuperAdmin
import com.kieltech.octracer.ui.login.LoginListener
import com.kieltech.octracer.ui.login.LoginValidation
import com.kieltech.octracer.utils.Constants
import org.mindrot.jbcrypt.BCrypt

class LoginViewModel : ViewModel() {

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
                            val passwordError = context.getString(R.string.no_users_found)
                            checkLogin(
                                firstDocument,
                                userInputCredentials,
                                credentialsFromDB,
                                firstDocument.id,
                                passwordError,
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
        userDocument: DocumentSnapshot,
        userInputCredentials: LoginValidation.LoginCredentials,
        credentialsFromDB: LoginValidation.LoginCredentials,
        credentialsId: String,
        noUsersFound: String,
        loginListener: LoginListener,
    ) {
        val emailInput = userInputCredentials.email
        val passwordInput = userInputCredentials.password

        val emailFromDB = credentialsFromDB.email
        val passwordFromDB = credentialsFromDB.password

        val equalPasswords = BCrypt.checkpw(passwordInput, passwordFromDB)

        val userFound = emailInput == emailFromDB && equalPasswords

        if (userFound) {
            val user = generateUser(userDocument)
            loginListener.onLoginSuccess(
                currentCollection.value?.id.toString(),
                credentialsId,
                user
            )
        } else {
            val newError = LoginValidation.LoginErrors(null, noUsersFound)
            setErrors(null, newError)
        }
    }

    private fun generateUser(userDocument: DocumentSnapshot): Any {
        return when (currentCollection.value?.id) {
            Constants.SUPER_ADMIN_COLLECTION_PATH -> {
                userDocument.toObject(SuperAdmin::class.java)!!
                // Go to next user
            }

            Constants.ADMIN_COLLECTION_PATH -> {
                userDocument.toObject(Admin::class.java)!!
            }

            else -> {
                userDocument.toObject(Graduate::class.java)!!
            }
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
            .addOnSuccessListener { documentSnapshot ->
                val user = generateUser(documentSnapshot)
                loginListener.onLoginSuccess(collection.id, documentSnapshot.id, user)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                loginListener.onLoginProcessDone()
            }
    }
}