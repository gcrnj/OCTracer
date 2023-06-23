package com.kieltech.octracer.ui.home

import android.os.Bundle
import android.view.View
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.FragmentHomeBinding
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.view_models.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    GetGraduateListener {

    private val homeViewModel: HomeViewModel by lazy {
        baseActivity.createViewModel()
    }

    override fun onGetStart() {

    }

    override fun onGetSuccess(graduates: List<Graduate>) {

    }

    override fun onGetProcessDone() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        defineViewModelObservers()
    }


    private fun defineViewModelObservers() {
        homeViewModel.selectedRange.observe(viewLifecycleOwner) { range ->
            range?.apply {
                homeViewModel.retrieveNumberOfGraduates(this@HomeFragment)
            }
        }
        homeViewModel.graduates.observe(viewLifecycleOwner) { graduates ->
            binding.graduatesCountTextView.text = graduates?.size?.toString() ?: "N/A"
        }


    }

    override fun onResume() {
        super.onResume()
        homeViewModel.changeRange(1990..2000)
    }

}