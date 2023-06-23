package com.kieltech.octracer.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.QuerySnapshot
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

    private val _unverifiedGraduates = MutableLiveData<List<Graduate>?>()
    val unverifiedGraduates: LiveData<List<Graduate>?> = _unverifiedGraduates

    private fun retrievedGraduates(snapshot: QuerySnapshot): List<Graduate> {
        return snapshot.map { doc ->
            val generatedGraduate = doc.generateGraduateUser()
            generatedGraduate.id = doc.id
            generatedGraduate
        }
    }

    fun retrieveNumberOfGraduates(from: Int, to: Int, listener: GetGraduateListener) {
        val graduatesCollection = Utils.graduatesCollection

        graduatesCollection
            .whereEqualTo(Constants.IS_VERIFIED_KEY, true)
            .whereGreaterThanOrEqualTo(Constants.GRADUATED_YEAR_KEY, from)
            .whereLessThanOrEqualTo(Constants.GRADUATED_YEAR_KEY, to)
            .get()
            .addOnSuccessListener { snapshot ->
                val retrievedGraduates = retrievedGraduates(snapshot)
                _graduates.value = retrievedGraduates
                listener.onGetSuccess(retrievedGraduates)
            }
            .addOnFailureListener { }
            .addOnCompleteListener { }
    }

    fun retrieveNumberOfGraduates(listener: GetGraduateListener) {
        val graduatesCollection = Utils.graduatesCollection
        graduatesCollection.whereEqualTo(Constants.IS_VERIFIED_KEY, true)
            .get()
            .addOnSuccessListener { snapshot ->
                val retrievedGraduates = retrievedGraduates(snapshot)
                _graduates.value = retrievedGraduates
                listener.onGetSuccess(retrievedGraduates)
            }
            .addOnFailureListener { }
            .addOnCompleteListener { }
    }

    fun retrieveNumberOfUnverifiedGraduates(listener: GetGraduateListener) {
        val graduatesCollection = Utils.graduatesCollection

        graduatesCollection
            .whereEqualTo(Constants.IS_VERIFIED_KEY, false)
            .get()
            .addOnSuccessListener { snapshot ->
                val retrievedGraduates = retrievedGraduates(snapshot)
                _unverifiedGraduates.value = retrievedGraduates
                listener.onGetSuccess(retrievedGraduates)
            }
            .addOnFailureListener {
            }
            .addOnCompleteListener {
                listener.onGetProcessDone()
            }
    }

    fun changeRange(newRange: IntRange) {
        _selectedRange.value = newRange
    }

}