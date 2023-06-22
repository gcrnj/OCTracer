package com.kieltech.octracer.ui.profile

import android.os.Bundle
import android.view.View
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.FragmentProfileBinding
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.visible
import com.kieltech.octracer.utils.Users

class ProfileFragment() : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by lazy {
        baseActivity.createViewModel()
    }

    private val graduateUser by lazy {
        baseActivity.getGraduateUser()?.let {
            val bundle = arguments
            bundle?.getParcelable<Graduate>(Constants.GRADUATES_COLLECTION_PATH)
        }
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