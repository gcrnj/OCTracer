package com.kieltech.octracer.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding


open class BaseFragment<VB : ViewBinding>(
    private val viewBindingInflater: (LayoutInflater) -> VB
) : Fragment() {

    //View Binding
    val binding: VB by lazy { viewBindingInflater(layoutInflater) }

    //Base Activity
    val baseActivity: BaseActivity<*> by lazy {
        activity as BaseActivity<*>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    open fun title(): String {
        return javaClass.simpleName
    }

    open fun reloadData() {}
}
