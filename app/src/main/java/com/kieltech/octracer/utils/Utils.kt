package com.kieltech.octracer.utils

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Utils {

    //==== COLLECTIONS =========
    val graduatesCollection = Firebase.firestore.collection(Constants.GRADUATES_COLLECTION_PATH)
    val adminCollection = Firebase.firestore.collection(Constants.ADMIN_COLLECTION_PATH)
}