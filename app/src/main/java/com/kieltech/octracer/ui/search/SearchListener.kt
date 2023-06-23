package com.kieltech.octracer.ui.search

import com.kieltech.octracer.data.SpannableGraduates

interface SearchListener {
    fun onSearchStart()

    fun onSearchSuccess(spannableGraduates: List<SpannableGraduates>)

    fun onSearchDone()
}