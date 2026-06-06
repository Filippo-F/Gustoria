package com.example.gustoria.data.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseProvider {
    val client = createSupabaseClient(
        supabaseUrl = "__SUPABASE_URL__",
        supabaseKey = "__SUPABASE_ANON_KEY__"
    ) {
        install(Storage)
    }
}