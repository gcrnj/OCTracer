package com.kieltech.octracer.ui.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseFragment
import com.kieltech.octracer.databinding.FragmentListBinding
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel

class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {

    companion object {
        fun newInstance() = ListFragment()
    }

    private val viewModel: ListViewModel by lazy {
        baseActivity.createViewModel()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}