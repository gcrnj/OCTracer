package com.kieltech.octracer.ui.profile

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kieltech.octracer.data.Graduate
import com.kieltech.octracer.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _graduate = MutableLiveData<Graduate?>()
    val graduate: LiveData<Graduate?> = _graduate

    fun resetGraduate(newGraduate: Graduate?) {
        _graduate.value = newGraduate
    }

    fun deleteGraduate(
        context: Context,
        id: String,
        deleteGraduateListener: DeleteGraduateListener
    ) {
        deleteGraduateListener.onDeleteStart()
        viewModelScope.launch {
            delay(200)
            Utils.graduatesCollection
                .document(id)
                .delete()
                .addOnSuccessListener {
                    deleteGraduateListener.onDeleteSuccess()
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
                .addOnCompleteListener {
                    deleteGraduateListener.onDeleteDone()
                }
        }
    }
}