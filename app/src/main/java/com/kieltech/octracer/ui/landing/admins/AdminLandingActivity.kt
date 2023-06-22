package com.kieltech.octracer.ui.landing.admins

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import com.kieltech.octracer.R
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.databinding.ActivityAdminLandingBinding
import com.kieltech.octracer.ui.home.HomeFragment
import com.kieltech.octracer.ui.list.ListFragment
import com.kieltech.octracer.ui.profile.ProfileFragment
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

    private fun setListFragment() {
        val listFragment = ListFragment()
        fragmentViewModel.changeFragment(
            newFragment = listFragment,
        )
    }


    private fun defineViewModelObservers() {
        fragmentViewModel.selectedFragment.observe(this) { newFragment ->
            newFragment?.let {
                supportFragmentManager.beginTransaction().apply {
                    supportFragmentManager.fragments.forEach {
                        hide(it)
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
                    binding.navigationView.setCheckedItem(R.id.homeDrawerMenu)
                }

                is ListFragment -> {
                    binding.navigationView.setCheckedItem(R.id.registeredListDrawerMenu)
                }
            }
        }
    }


}