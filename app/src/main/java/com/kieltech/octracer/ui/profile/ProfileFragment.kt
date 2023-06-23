package com.kieltech.octracer.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
import com.kieltech.octracer.utils.Utils

class ProfileFragment() : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val TAG = "ProfileFragment"

    private val profileViewModel: ProfileViewModel by lazy {
        baseActivity.createViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFirstGraduate()
        if (baseActivity.getGraduateUser() != null) {
            // Set UI for Graduates
            setUIForGraduates()
        } else {
            // Set UI for Admin
            setUIForAdmin()
        }
        setOnClickListeners()
        defineViewModelObservers()
    }

    private fun defineViewModelObservers() {
        profileViewModel.graduate.observe(viewLifecycleOwner) {
            setTexts(it)
        }
    }

    private fun setFirstGraduate() {
        val bundle = arguments
        val graduateFromBundle =
            bundle?.getParcelable<Graduate>(Constants.GRADUATES_COLLECTION_PATH)
        val newGraduate = baseActivity.getGraduateUser() ?: graduateFromBundle
        setGraduate(newGraduate)
    }

    private fun setGraduate(newGraduate: Graduate?) {
        profileViewModel.resetGraduate(newGraduate)
    }

    private fun setOnClickListeners() {
        binding.logoutButton.setOnClickListener {
            baseActivity.logoutUser()
        }
        binding.backButton.setOnClickListener {
            val adminLandingActivity = requireActivity() as AdminLandingActivity
            adminLandingActivity.setListFragment()
        }
        binding.editButton.setOnClickListener {
            val graduate = profileViewModel.graduate.value
            if (graduate != null) {
                val newIntent = Intent(requireActivity(), RegisterActivity::class.java)
                newIntent.putExtra(Constants.GRADUATES_COLLECTION_PATH, graduate)
                resultLauncher.launch(newIntent)
            }
        }
    }

    private fun setUIForAdmin() {
        with(binding) {
            adminActionButtonsLinearLayout.visible()
            logoutButton.gone()
        }
    }

    private fun setUIForGraduates() {
        with(binding) {
            adminActionButtonsLinearLayout.gone()
            logoutButton.visible()
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


}