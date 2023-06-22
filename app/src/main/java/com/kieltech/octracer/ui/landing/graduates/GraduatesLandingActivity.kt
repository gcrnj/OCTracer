package com.kieltech.octracer.ui.landing.graduates

import android.os.Bundle
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.databinding.ActivityGraduatesLandingBinding
import com.kieltech.octracer.ui.profile.FragmentViewModel
import com.kieltech.octracer.ui.profile.ProfileFragment
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel

class GraduatesLandingActivity :
    BaseActivity<ActivityGraduatesLandingBinding>(ActivityGraduatesLandingBinding::inflate) {

    val fragmentViewModel : FragmentViewModel by lazy {
        createViewModel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        defineViewModelObservers()
        initiateProfileFragment()
    }

    private fun defineViewModelObservers() {

        fragmentViewModel.selectedFragment.observe(this){ newFragment ->
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

    private fun initiateProfileFragment() {
        val bundle = Bundle()
        val profileFragment = ProfileFragment()
        bundle.putParcelable(Constants.GRADUATES_COLLECTION_PATH, getGraduateUser())
        profileFragment.arguments = bundle
        fragmentViewModel.changeFragment(
            newFragment = profileFragment,
        )
    }
}