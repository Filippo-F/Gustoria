package com.example.gustoria.data.utils

import android.net.Uri
import com.example.gustoria.GustoriaApplication
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import java.util.UUID

object ImageUploader {

    suspend fun uploadImage(
        uriString: String,
        folder: String
    ): String? {

        if (uriString.isBlank() || uriString.startsWith("http")) {
            return uriString
        }

        return try {
            val uri = Uri.parse(uriString)

            val bytes = GustoriaApplication.instance.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: return null

            val fileName = "${UUID.randomUUID()}.jpg"

            val bucket = SupabaseProvider.client
                .storage.from(folder)

            bucket.upload(fileName, bytes)

            bucket.publicUrl(fileName)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteImage(
        url: String?,
        folder: String
    ) {
        if (url.isNullOrBlank() || !url.contains("supabase.co")) return

        try {
            // Extract fileName from URL
            val fileName = url.substringAfterLast("/")
            
            SupabaseProvider.client
                .storage
                .from(folder)
                .delete(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
