package com.kieltech.octracer.ui.search

import com.kieltech.octracer.data.SpannableGraduate

interface SearchListener {
    fun onSearchStart()

    fun onSearchSuccess(spannableGraduates: List<SpannableGraduate>)

    fun onSearchDone()
}