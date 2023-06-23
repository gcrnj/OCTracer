package com.kieltech.octracer.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.SpannableGraduates
import com.kieltech.octracer.databinding.ActivitySearchBinding
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.view_models.SearchViewModel

class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate),
    SearchListener {

    private val TAG = "SearchActivity"
    private val searchViewModel: SearchViewModel by lazy {
        createViewModel()
    }

    private val toSearch by lazy {
        intent.getStringExtra(Constants.INTENT_EXTRA_SEARCH)
    }

    override fun onSearchStart() {
        Log.d(TAG, "onSearchStart: ")
    }

    override fun onSearchSuccess(spannableGraduates: List<SpannableGraduates>) {
        Log.d(TAG, "onSearchSuccess: ${spannableGraduates.map { it.graduate.id }}")
        Log.d(TAG, "onSearchSuccess: ${spannableGraduates.map { it.spannableName.toString() + it.spannableOccupancy.toString() }}")
        Toast.makeText(this@SearchActivity, "${spannableGraduates.size} records found", Toast.LENGTH_SHORT).show()
    }

    override fun onSearchDone() {
        Log.d(TAG, "onSearchDone: ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        defineObservers()
        search()
    }

    private fun search() {
        searchViewModel.search(this, toSearch!!, this)
    }

    private fun defineObservers() {
        searchViewModel.spannableGraduates.observe(this) { graduates ->
            if (graduates == null && !toSearch.isNullOrBlank()) {
                searchViewModel.search(this, toSearch!!, this)
            }
        }
    }
}