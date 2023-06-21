package com.kieltech.octracer.ui.landing.graduates

import android.os.Bundle
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.databinding.ActivityGraduatesLandingBinding
import com.kieltech.octracer.ui.profile.ProfileFragment

class GraduatesLandingActivity :
    BaseActivity<ActivityGraduatesLandingBinding>(ActivityGraduatesLandingBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initiateProfileFragment()
    }

    private fun initiateProfileFragment() {
        val profileFragment = ProfileFragment()
        supportFragmentManager.beginTransaction().apply {
            val foundFragment =
                supportFragmentManager.findFragmentByTag(profileFragment.title())
            if (foundFragment != null) {
                //show the selected.
                show(foundFragment)
            } else {
                //add the selected
                add(
                    binding.frameLayout.id,
                    profileFragment,
                    profileFragment.title()
                )
            }
            commit()
        }
    }
}