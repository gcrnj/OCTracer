package com.kieltech.octracer.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        reloadData()
    }


    private fun defineViewModelObservers() {
        homeViewModel.graduates.observe(viewLifecycleOwner) { graduates ->
            val number = when(graduates?.size){
                0-> "No"
                null -> "No"
                else -> graduates.size.toString()
            }
            binding.graduatesCountTextView.text = number
        }

    }

    override fun reloadData() {
        super.reloadData()
        homeViewModel.retrieveNumberOfGraduates(this@HomeFragment)
    }

}