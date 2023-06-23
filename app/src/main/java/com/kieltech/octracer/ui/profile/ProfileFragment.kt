package com.kieltech.octracer.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.View
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.FragmentProfileBinding
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.visible

class ProfileFragment() : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val TAG = "ProfileFragment"

    private val viewModel: ProfileViewModel by lazy {
        baseActivity.createViewModel()
    }

    private val graduateUser by lazy {
        val bundle = arguments
        val graduateFromBundle = bundle?.getParcelable<Graduate>(Constants.GRADUATES_COLLECTION_PATH)
        baseActivity.getGraduateUser() ?: graduateFromBundle
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (baseActivity.getGraduateUser() != null) {
            // Set UI for Graduates
            setUIForGraduates()
        } else {
            // Set UI for Admin
            setUIForAdmin()
        }
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.logoutButton.setOnClickListener {
            baseActivity.logoutUser()
        }
        binding.backButton.setOnClickListener {
            val adminLandingActivity = requireActivity() as AdminLandingActivity
            adminLandingActivity.setListFragment()
        }
    }

    private fun setUIForAdmin() {
        setTexts()
        with(binding){
            backButton.visible()
            logoutButton.gone()
        }
    }

    private fun setUIForGraduates() {
        setTexts()
        with(binding){
            backButton.gone()
            logoutButton.visible()
        }
    }

    private fun setTexts() {
        graduateUser?.apply {
            with(binding){
                fullNameTextView.text = fullName()
                addressTextView.text = address
                phoneNumberTextView.text = mobile_number
                occupationTextView.text = occupation
                yearGraduatedTextView.text = year_graduated?.toString()
            }
        }
    }


}