package com.aplicacion2

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = "https://rhmioxhzxlqozogqumat.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJobWlveGh6eGxxb3pvZ3F1bWF0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzU2MDE5NjEsImV4cCI6MjA5MTE3Nzk2MX0.0v0Yj5woU_lnRcdiRA7PC7ohAQdfEqisuJFQFeNtPLk"
    ){
        install(Postgrest)
        install(Auth)
    }
}