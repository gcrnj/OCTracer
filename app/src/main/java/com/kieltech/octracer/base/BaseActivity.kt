package com.kieltech.octracer.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kieltech.octracer.R
import com.kieltech.octracer.data.Admin
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.ui.landing.graduates.GraduatesLandingActivity
import com.kieltech.octracer.ui.login.LoginActivity
import com.kieltech.octracer.ui.profile.UploadProfileListener
import com.kieltech.octracer.ui.register.RegisterActivity
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.Users


open class BaseActivity<VB : ViewBinding>(
    private val viewBindingInflater: (LayoutInflater) -> VB,
) : AppCompatActivity() {

    private val TAG = "BaseActivity"

    //View Binding
    val binding: VB by lazy { viewBindingInflater(layoutInflater) }

    val userSharedPref by lazy {
        getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE)
    }

    private lateinit var filePath: Uri
    private lateinit var userId: String
    private lateinit var uploadProfileListener: UploadProfileListener
    private val storage by lazy {
        Firebase.storage
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.activities_background))
    }

    fun saveUserAndGoToGraduatesLanding(
        collectionId: String,
        firestoreUserId: String,
        graduateUser: Graduate,
        shouldLoginAutomatically: Boolean,
    ) {
        if (shouldLoginAutomatically) {
            // Registered by no user / graduate
            // Save info
            userSharedPref.edit().apply {
                putString(Constants.SHARED_PREF_UID, firestoreUserId)
                putString(Constants.SHARED_PREF_ROLE, collectionId)
                apply()
            }
            graduateUser.id = firestoreUserId
            Users.GraduateUser = graduateUser
            // Go to graduate dashboard
            startActivity(Intent(this, GraduatesLandingActivity::class.java))
        }
        if (this is RegisterActivity) {
            finishAffinity()
            val newIntent = Intent()
            newIntent.putExtra(Constants.INTENT_EXTRA_UID, firestoreUserId)
            newIntent.putExtra(Constants.INTENT_EXTRA_ROLE, collectionId)
            newIntent.putExtra(Constants.GRADUATES_COLLECTION_PATH, graduateUser)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            setResult(RESULT_OK, newIntent)
        }
        finish()
    }

    fun saveUserAndGoToAdminLanding(
        collectionId: String,
        firestoreUserId: String,
        admin: Admin,
        shouldLoginAutomatically: Boolean
    ) {
        if (shouldLoginAutomatically) {
            // Registered by no user / graduate
            // Save info
            userSharedPref.edit().apply {
                putString(Constants.SHARED_PREF_UID, firestoreUserId)
                putString(Constants.SHARED_PREF_ROLE, collectionId)
                apply()
            }
            Users.AdminUser = admin
            // Go to graduate dashboard
            startActivity(Intent(this, AdminLandingActivity::class.java))
        }
        finish()
    }

    fun logoutUser() {
        userSharedPref.edit().apply {
            remove(Constants.SHARED_PREF_UID)
            remove(Constants.SHARED_PREF_ROLE)
            apply()
        }
        finishAffinity()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        Users.GraduateUser = null
        Users.AdminUser = null
        Users.AdminUser = null
        startActivity(intent)
    }

    fun getAdminUser(): Admin? {
        return Users.AdminUser
    }

    fun getGraduateUser(): Graduate? {
        return Users.GraduateUser
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                filePath = uri
                uploadFile()
            }
        }


    fun openFileChooser(userId: String, uploadProfileListener: UploadProfileListener) {
        this.userId = userId
        this.uploadProfileListener = uploadProfileListener
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            pickImageLauncher.launch("image/*")
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch("image/*")
            }
        }
    }

    private fun uploadFile() {
        if (::filePath.isInitialized) {
            val storageRef = storage.reference
            val fileRef = storageRef.child(getString(R.string.profile_pic_path, userId))
            uploadProfileListener.onUploadStarted()
            fileRef.putFile(filePath)
                .addOnSuccessListener {
                    Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
                    uploadProfileListener.onUploadSuccess(filePath)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    uploadProfileListener.onUploadFailure("Upload failed: ${e.message}")
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                    uploadProfileListener.onUploadLoading(progress.toInt())
                    //progressBar.progress = progress.toInt()
                }
                .addOnCompleteListener {
                    uploadProfileListener.onUploadDone()
                }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    fun setProfileImageView(id: String, imageView: ImageView) {
        val imageRef = Firebase.storage.reference.child(getString(R.string.profile_pic_path, id))
        imageRef.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener { bytes ->
                val bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                val dm = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(dm)
                imageView.minimumHeight = dm.heightPixels
                imageView.minimumWidth = dm.widthPixels
                imageView.setImageBitmap(bm)
            }.addOnFailureListener {
                // TODO Handle any errors
            }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val ONE_MEGABYTE = (1024 * 1024).toLong()

    }
}