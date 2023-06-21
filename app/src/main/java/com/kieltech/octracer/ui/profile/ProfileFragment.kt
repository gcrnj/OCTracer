package com.kieltech.octracer.ui.profile

import android.os.Bundle
import android.view.View
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.databinding.FragmentProfileBinding
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.Users

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: ProfileViewModel by lazy {
        baseActivity.createViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.sampleText.text = Users.GraduateUser?.toString()
    }


}