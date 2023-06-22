package com.kieltech.octracer.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.ui.home.GetGraduateListener
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.generateGraduateUser
import com.kieltech.octracer.utils.Utils

class HomeViewModel : ViewModel() {

    private val _selectedRange = MutableLiveData<IntRange?>()
    val selectedRange: LiveData<IntRange?> = _selectedRange

    private val _graduates = MutableLiveData<List<Graduate>?>()
    val graduates: LiveData<List<Graduate>?> = _graduates

    fun retrieveNumberOfGraduates(from: Int, to: Int, listener: GetGraduateListener) {
        val graduatesCollection = Utils.graduatesCollection

        graduatesCollection
            .whereGreaterThanOrEqualTo(Constants.GRADUATED_YEAR_KEY, from)
            .whereLessThanOrEqualTo(Constants.GRADUATED_YEAR_KEY, to)
            .get()
            .addOnSuccessListener { snapshot ->
                val retrievedGraduates = snapshot.map { doc ->
                    doc.generateGraduateUser()
                }
                _graduates.value = retrievedGraduates
                listener.onGetSuccess(retrievedGraduates)
            }
            .addOnFailureListener { }
            .addOnCompleteListener { }
    }

    fun changeRange(newRange: IntRange) {
        _selectedRange.value = newRange
    }

}