package com.kieltech.octracer.ui.list

import android.os.Bundle
import android.view.View
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.FragmentListBinding
import com.kieltech.octracer.ui.home.GetGraduateListener
import com.kieltech.octracer.ui.landing.admins.AdminLandingActivity
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.visibleOrGone
import com.kieltech.octracer.view_models.HomeViewModel

class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate),
    GetGraduateListener {

    private val homeViewModel: HomeViewModel by lazy {
        baseActivity.createViewModel()
    }

    override fun onGetStart() {
    }

    override fun onGetSuccess(graduates: List<Graduate>) {
        binding.emptyListLayout.root.visibleOrGone(graduates.isEmpty())
        GraduatesListAdapter(requireActivity() as AdminLandingActivity, graduates, null, false).also {
            binding.graduatesListRecyclerView.adapter = it
        }
    }

    override fun onGetProcessDone() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reloadData()
    }

    override fun reloadData() {
        super.reloadData()
        homeViewModel.retrieveNumberOfGraduates(this)
    }


}