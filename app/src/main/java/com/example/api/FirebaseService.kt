package com.example.api

import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseService {
    private const val TAG = "FirebaseService"
    
    var isFirebaseAvailable: Boolean = false
        private set

    fun initialize() {
        try {
            // Access instances to verify if Google Services config is loaded
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            isFirebaseAvailable = true
            Log.d(TAG, "Firebase initialized successfully.")
        } catch (e: Exception) {
            isFirebaseAvailable = false
            Log.w(TAG, "Firebase failed to initialize (usually due to missing google-services.json). Falling back to offline Room mode. Error: ${e.message}")
        }
    }

    val auth: FirebaseAuth?
        get() = if (isFirebaseAvailable) FirebaseAuth.getInstance() else null

    val db: FirebaseFirestore?
        get() = if (isFirebaseAvailable) FirebaseFirestore.getInstance() else null
}
