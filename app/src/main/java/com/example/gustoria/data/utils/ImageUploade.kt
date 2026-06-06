package com.example.gustoria.data.utils

import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ImageUploader {
    suspend fun uploadImage(uriString: String, folder: String): String? {
        // Don't do anything if is already a link (starts with http) or is empty
        if (uriString.isBlank() || uriString.startsWith("http")) {
            return uriString
        }

        return try {
            val uri = uriString.toUri()
            val storageRef = Firebase.storage.reference
            val fileName = "${UUID.randomUUID()}.jpg"
            val imageRef = storageRef.child("$folder/$fileName")

            // Upload
            imageRef.putFile(uri).await()
            // Get public URL for download
            imageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}