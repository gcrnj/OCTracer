package com.kieltech.octracer.ui.landing.admins

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.databinding.ActivityAdminLandingBinding
import com.kieltech.octracer.ui.home.HomeFragment
import com.kieltech.octracer.ui.list.ListFragment
import com.kieltech.octracer.ui.profile.ProfileFragment
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.view_models.FragmentViewModel
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.view_models.AdminLandingViewModel

class AdminLandingActivity :
    BaseActivity<ActivityAdminLandingBinding>(ActivityAdminLandingBinding::inflate) {

    private val fragmentViewModel: FragmentViewModel by lazy {
        createViewModel()
    }

    private val adminLandingViewModel: AdminLandingViewModel by lazy {
        createViewModel()
    }

    private val searchView by lazy {
        findViewById<SearchView>(R.id.action_search)
    }

    private val logoutDialog by lazy {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_message))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                logoutUser()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        defineViewModelObservers()
        defineNavView()
        defineOnBackPressed()
        fragmentViewModel.selectedFragment.value?.let { } ?: run {
            // It's null
            setHomeFragment()
        }
    }

    private fun defineOnBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (!searchView.isIconified) {
                    searchView.isIconified = !searchView.isIconified
                } else {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun defineNavView() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    adminLandingViewModel.performSearch(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })

            topAppBar.setNavigationOnClickListener {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.homeDrawerMenu -> {
                        setHomeFragment()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }

                    R.id.registeredListDrawerMenu -> {
                        // Handle navigation item 2 selection
                        setListFragment()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }

                    R.id.logoutDrawerMenu -> {
                        // Handle navigation item 2 selection
                        logoutDialog.show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        false
                    }

                    else -> false
                }
            }
        }

    }

    private fun setHomeFragment() {
        val homeFragment = HomeFragment()
        fragmentViewModel.changeFragment(
            newFragment = homeFragment,
        )
    }

    fun setListFragment() {
        val listFragment = ListFragment()
        fragmentViewModel.changeFragment(
            newFragment = listFragment,
        )
    }

    fun setGraduateProfileFragment(graduate: Graduate) {
        val bundle = Bundle()
        val profileFragment = ProfileFragment()
        bundle.putParcelable(Constants.GRADUATES_COLLECTION_PATH, graduate)
        profileFragment.arguments = bundle
        fragmentViewModel.changeFragment(
            newFragment = profileFragment,
        )
    }


    private fun defineViewModelObservers() {
        fragmentViewModel.selectedFragment.observe(this) { newFragment ->
            newFragment?.let {
                supportFragmentManager.beginTransaction().apply {
                    supportFragmentManager.fragments.forEach {
                        remove(it)
                    }
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

            when (newFragment) {
                is HomeFragment -> {
                    binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.activities_background))
                    binding.navigationView.setCheckedItem(R.id.homeDrawerMenu)
                }

                is ListFragment -> {
                    binding.navigationView.setCheckedItem(R.id.registeredListDrawerMenu)
                    binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.activities_background))
                }

                is ProfileFragment -> {
                    binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_gray))
                }
            }
        }
    }


}