package com.kieltech.octracer.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.kieltech.octracer.base.BaseActivity
import com.kieltech.octracer.data.SpannableGraduate
import com.kieltech.octracer.databinding.ActivitySearchBinding
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.createViewModel
import com.kieltech.octracer.view_models.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    override fun onSearchSuccess(spannableGraduates: List<SpannableGraduate>) {
        Log.d(TAG, "onSearchSuccess: ${spannableGraduates.size}")
        val adapter = SearchesAdapter(this, spannableGraduates) { clickedGraduate ->
            returnGraduate(clickedGraduate)
        }
        binding.searchedGraduatesRecyclerView.adapter = adapter
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
        searchViewModel.spannableGraduate.observe(this) { graduates ->
            if (graduates == null && !toSearch.isNullOrBlank()) {
                searchViewModel.search(this, toSearch!!, this)
            }
        }
    }

    private fun returnGraduate(spannableGraduate: SpannableGraduate) {
        lifecycleScope.launch {
            val newIntent = Intent()
            newIntent.putExtra(Constants.INTENT_SPANNABLE_GRADUATE, spannableGraduate)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            setResult(RESULT_OK, newIntent)
            delay(500)
            finish()
        }
    }
}