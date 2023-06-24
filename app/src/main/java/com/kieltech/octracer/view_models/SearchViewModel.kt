package com.kieltech.octracer.view_models

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.kieltech.octracer.data.SpannableGraduate
import com.kieltech.octracer.ui.search.SearchListener
import com.kieltech.octracer.utils.Constants
import com.kieltech.octracer.utils.OCTracerFunctions.generateGraduateUser
import com.kieltech.octracer.utils.Utils

class SearchViewModel : ViewModel() {
    private val TAG = "SearchViewModel"

    private val _spannableGraduate = MutableLiveData<List<SpannableGraduate>?>()
    val spannableGraduate: LiveData<List<SpannableGraduate>?> = _spannableGraduate

    fun search(context: Context, search: String, searchListener: SearchListener) {
        val searchWordsList = search.split(" ")
        searchListener.onSearchStart()
        val firstNameQuery = Utils.graduatesCollection
            .whereIn(Constants.FIRST_NAME_KEY, searchWordsList)
            .get()
        val middleNameQuery = Utils.graduatesCollection
            .whereIn(Constants.MIDDLE_NAME_KEY, searchWordsList)
            .get()
        val lastNameQuery = Utils.graduatesCollection
            .whereIn(Constants.LAST_NAME_KEY, searchWordsList)
            .get()
        val occupationQuery = Utils.graduatesCollection
            .whereIn(Constants.OCCUPATION_KEY, searchWordsList)
            .get()
        Tasks.whenAllComplete(firstNameQuery, middleNameQuery, lastNameQuery, occupationQuery)
            .addOnSuccessListener { taskList ->
                val snapshot = mutableListOf<DocumentSnapshot>()
                for (task in taskList) {
                    if (task.isSuccessful) {
                        val querySnapshot = task.result as QuerySnapshot
                        snapshot.addAll(querySnapshot.documents)
                    }
                }
                val newSpannableGraduates = snapshot.map { doc ->
                    val graduate = doc.generateGraduateUser()
                    val spannableName = getSpannable(graduate.fullName(), searchWordsList)
                    val spannableOccupation =
                        getSpannable(graduate.occupation ?: "", searchWordsList)
                    SpannableGraduate(
                        graduate = graduate,
                        nameSpannableString = spannableName,
                        occupationSpannableString = spannableOccupation
                    )
                }.distinctBy { it.graduate.id }.sortedBy { it.graduate.fullName() }
                _spannableGraduate.value = newSpannableGraduates
                searchListener.onSearchSuccess(newSpannableGraduates)
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                Log.e(TAG, "search: $it")
            }
            .addOnCompleteListener {
                searchListener.onSearchDone()
            }
    }

    private fun getSpannable(sentence: String, wordsToBold: List<String>): SpannableString {
        val spannableString = SpannableString(sentence)

        for (word in wordsToBold) {
            val startIndex = sentence.indexOf(word)
            if (startIndex != -1) {
                val endIndex = startIndex + word.length

                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        return spannableString
    }

}