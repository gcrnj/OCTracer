package com.kieltech.octracer.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.FragmentProfileBinding
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.ui.register.RegisterActivity
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.parcelable
import com.kieltech.octracer.utils.OCTracerFunctions.visible

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate),
    UploadProfileListener {

    private val TAG = "ProfileFragment"

    private val profileViewModel: ProfileViewModel by lazy {
        baseActivity.createViewModel()
    }

    private val onDeleteListener by lazy {
        object : DeleteGraduateListener {
            override fun onDeleteStart() {
                binding.progress.visible()
            }

            override fun onDeleteSuccess() {
                goToAdminFragment()
            }

            override fun onDeleteDone() {
                binding.progress.gone()
            }

        }
    }

    private val deleteConfirmationDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_user))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                val graduate = profileViewModel.graduate.value
                val id = graduate?.id
                dialog.dismiss()// delete user
                if (id != null) {
                    profileViewModel.deleteGraduate(requireContext(), id, onDeleteListener)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
    }

    private fun showDeleteConfirmation() {
        val graduate = profileViewModel.graduate.value
        val name = graduate?.first_name ?: ""
        deleteConfirmationDialog
            .setMessage(getString(R.string.delete_user_message, name))
            .show()
    }

    override fun onUploadStarted() {
        binding.uploadProfileProgressIndicator.visible()
    }

    override fun onUploadSuccess(uri: Uri) {
        binding.profilePicImageView.setImageURI(uri)
    }

    override fun onUploadFailure(message: String) {
    }

    override fun onUploadLoading(newProgress: Int) {
        binding.uploadProfileProgressIndicator.apply {
            progress = newProgress
        }
    }

    override fun onUploadDone() {
        binding.uploadProfileProgressIndicator.gone()
    }

    override fun reloadData() {
        super.reloadData()
        if (baseActivity.getGraduateUser() != null) {
            // Set UI for Graduates
            setUIForGraduates()
        } else {
            // Set UI for Admin
            setUIForAdmin()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstGraduate()
        setOnClickListeners()
        defineViewModelObservers()
        reloadData()
        binding.progress.gone()
    }

    private fun setProfilePicture(collectionId: String) {
        baseActivity.setProfileImageView(collectionId, binding.profilePicImageView)
    }

    private fun defineViewModelObservers() {
        profileViewModel.graduate.observe(viewLifecycleOwner) { graduate ->
            if (graduate != null) {
                setProfilePicture(graduate.id ?: "")
            }
            setTexts(graduate)
        }
    }

    private fun setFirstGraduate() {
        val bundle = arguments
        val graduateFromBundle =
            bundle?.getParcelable<Graduate>(Constants.GRADUATES_COLLECTION_PATH)
        val newGraduate = baseActivity.getGraduateUser() ?: graduateFromBundle
        setGraduate(newGraduate)
    }

    fun setGraduate(newGraduate: Graduate?) {
        binding.profilePicImageView.setImageURI(null)
        profileViewModel.resetGraduate(newGraduate)
    }

    private fun setOnClickListeners() {
        binding.logoutButton.setOnClickListener {
            baseActivity.logoutUser()
        }
        binding.backButton.setOnClickListener {
            goToAdminFragment()
        }
        binding.editButton.setOnClickListener {
            val graduate = profileViewModel.graduate.value
            if (graduate != null) {
                val newIntent = Intent(requireActivity(), RegisterActivity::class.java)
                newIntent.putExtra(Constants.GRADUATES_COLLECTION_PATH, graduate)
                resultLauncher.launch(newIntent)
            }
        }
        binding.uploadProfilePictureButton.setOnClickListener {
            baseActivity.openFileChooser(baseActivity.getGraduateUser()?.id!!, this)
        }
        binding.deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun setUIForAdmin() {
        with(binding) {
            adminActionButtonsLinearLayout.visible()
            logoutButton.gone()
            profilePicCardView.visible()
            logoutButton.gone()
            uploadProfilePictureButton.gone()
            uploadProfileProgressIndicator.gone()
        }
    }

    private fun setUIForGraduates() {
        with(binding) {
            adminActionButtonsLinearLayout.gone()
            logoutButton.visible()
            profilePicCardView.visible()
            uploadProfilePictureButton.visible()
            uploadProfileProgressIndicator.gone()
        }
    }

    private fun setTexts(graduate: Graduate?) {
        graduate?.apply {
            with(binding) {
                fullNameTextView.text = fullName()
                addressTextView.text = address
                phoneNumberTextView.text = mobile_number
                occupationTextView.text = occupation
                yearGraduatedTextView.text = year_graduated?.toString()
            }
        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val graduate: Graduate? =
                    result.data?.parcelable(Constants.GRADUATES_COLLECTION_PATH)
                setGraduate(graduate)
            }
        }

    fun goToAdminFragment() {
        val adminLandingActivity = requireActivity() as AdminLandingActivity
        adminLandingActivity.setListFragment()
    }

}