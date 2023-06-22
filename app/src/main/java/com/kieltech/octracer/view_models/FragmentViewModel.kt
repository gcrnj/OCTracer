package com.kieltech.octracer.view_models

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kieltech.octracer.base.BaseFragment

class FragmentViewModel : ViewModel() {

    private val _selectedFragment = MutableLiveData<BaseFragment<*>?>()
    val selectedFragment: LiveData<BaseFragment<*>?> = _selectedFragment

    fun changeFragment(
        newFragment: BaseFragment<*>
    ) {
        _selectedFragment.value = newFragment
    }
}