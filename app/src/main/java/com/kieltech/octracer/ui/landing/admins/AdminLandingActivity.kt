package com.kieltech.octracer.ui.landing.admins

import android.os.Bundle
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.databinding.ActivityAdminLandingBinding
import com.kieltech.octracer.ui.home.HomeFragment
import com.kieltech.octracer.view_models.FragmentViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel

class AdminLandingActivity :
    BaseActivity<ActivityAdminLandingBinding>(ActivityAdminLandingBinding::inflate) {

    private val fragmentViewModel: FragmentViewModel by lazy {
        createViewModel()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        defineViewModelObservers()
        fragmentViewModel.selectedFragment.value?.let { } ?: run {
            // It's null
            setHomeFragment()
        }
    }

    private fun setHomeFragment() {
        val homeFragment = HomeFragment()
        fragmentViewModel.changeFragment(
            newFragment = homeFragment,
        )
    }


    private fun defineViewModelObservers() {
        fragmentViewModel.selectedFragment.observe(this) { newFragment ->
            newFragment?.let {
                supportFragmentManager.beginTransaction().apply {
                    val foundFragment =
                        supportFragmentManager.findFragmentByTag(newFragment.title())
                    if (foundFragment != null) {
                        //show the selected.
                        show(foundFragment)
                    } else {
                        //add the selected
                        add(
                            binding.frameLayout.id,
                            newFragment,
                            newFragment.title()
                        )
                    }
                    commit()
                }
            }
        }
    }

}