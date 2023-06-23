package com.kieltech.octracer.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kieltech.octracer.data.Graduate

class ProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel



    private val _graduate = MutableLiveData<Graduate?>()
    val graduate: LiveData<Graduate?> = _graduate

    fun resetGraduate(newGraduate: Graduate?){
        _graduate.value = newGraduate
    }
}