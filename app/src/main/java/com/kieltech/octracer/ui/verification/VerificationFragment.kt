package com.kieltech.octracer.ui.verification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.FragmentVerificationBinding
import com.kieltech.octracer.ui.home.GetGraduateListener
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.ui.list.GraduatesListAdapter
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.gone
import com.kieltech.octracer.utils.OCTracerFunctions.visible
import com.kieltech.octracer.utils.OCTracerFunctions.visibleOrGone
import com.kieltech.octracer.view_models.HomeViewModel
import com.kieltech.octracer.view_models.VerificationViewModel

class VerificationFragment :
    BaseFragment<FragmentVerificationBinding>(FragmentVerificationBinding::inflate),
    GetGraduateListener {

    private val verificationViewModel: VerificationViewModel by lazy {
        baseActivity.createViewModel()
    }
    private val homeViewModel: HomeViewModel by lazy {
        baseActivity.createViewModel()
    }

    private val onVerificationListener: VerificationListener by lazy {
        object : VerificationListener {
            override fun onVerify(graduateId: String) {
                verificationViewModel.verifyUser(requireContext(), graduateId, this)
            }

            override fun onVerified() {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.verified_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDecline(graduateId: String) {
                verificationViewModel.declineUser(requireContext(), graduateId, this)
            }

            override fun onDeclined() {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.user_declined),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onUpdateStart() {
                binding.loading.visible()
            }

            override fun onDone() {
                binding.loading.gone()
                refreshList()
            }

        }
    }

    override fun onGetStart() {
        binding.loading.visible()
    }

    override fun onGetSuccess(graduates: List<Graduate>) {
        GraduatesListAdapter(
            requireActivity() as AdminLandingActivity,
            graduates,
            null,
            true,
            onVerificationListener
        ).also {
            binding.graduatesListRecyclerView.adapter = it
        }
        binding.emptyListLayout.root.visibleOrGone(graduates.isEmpty())
    }

    override fun onGetProcessDone() {
        binding.loading.gone()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        defineViewModel()
        refreshList()
    }

    private fun defineViewModel() {
        homeViewModel.graduates.observe(viewLifecycleOwner) { graduates ->
            if (graduates != null) {
                onGetSuccess(graduates)
            }
        }
    }

    private fun refreshList() {
        homeViewModel.retrieveNumberOfVerifiedGraduates(this)
    }


}