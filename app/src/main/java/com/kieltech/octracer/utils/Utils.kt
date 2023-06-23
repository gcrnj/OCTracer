package com.kieltech.octracer.utils

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Utils {

    //==== COLLECTIONS =========
    val graduatesCollection = Firebase.firestore.collection(Constants.GRADUATES_COLLECTION_PATH)
    val adminCollection = Firebase.firestore.collection(Constants.ADMIN_COLLECTION_PATH)
    var collectionToEdit : CollectionReference? = null

    //Auto generator
    private val firstNames = listOf(
        "John",
        "Emma",
        "Michael",
        "Sophia",
        "William",
        "Olivia",
        "James",
        "Ava",
        "Benjamin",
        "Isabella"
    )
    private val middleNames = listOf(
        "Lee", "Grace", "Robert", "Elizabeth", "Thomas", "Rose", "David", "Mia", "Daniel", "Emily"
    )
    private val lastNames = listOf(
        "Smith",
        "Johnson",
        "Brown",
        "Taylor",
        "Wilson",
        "Anderson",
        "Miller",
        "Davis",
        "Garcia",
        "Martinez"
    )
    private val occupations = listOf(
        "Engineer",
        "Teacher",
        "Doctor",
        "Designer",
        "Accountant",
    )
    private val addresses = listOf(
        "123 Main St, Anytown",
        "456 Elm St, Springfield",
        "789 Oak Ave, Lakeside",
        // Add more addresses as needed
    )

    fun generateRandomFirstName(): String {
        return firstNames.random()
    }

    fun generateRandomMiddleName(): String {
        return middleNames.random()
    }

    fun generateRandomLastName(): String {
        return lastNames.random()
    }

    fun generateRandomYearGraduated(): String {
        val fromYear = 1990
        val toYear = 2022
        return (fromYear..toYear).random().toString()
    }

    fun generateRandomAddress(): String {
        return addresses.random()
    }

    fun generateRandomMobileNumber(): String {
        val prefix = "09"
        val randomNumber = (100_000_000..999_999_999).random()
        return prefix + randomNumber.toString()
    }

    fun generateRandomOccupation(): String {
        return occupations.random()
    }

    fun generateRandomEmail(): String {
        val domains = listOf("example.com", "test.com", "domain.com", "gmail.com", "oc.com", "tracer.com", "octracer.com")
        val randomUsername = (1000..9999).random() // Generate a random number as the username
        val randomDomain = domains.random()
        return "user$randomUsername@$randomDomain"
    }
}