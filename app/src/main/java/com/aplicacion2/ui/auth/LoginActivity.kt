package com.aplicacion2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.aplicacion2.R
import com.aplicacion2.SupabaseClient
import com.aplicacion2.data.CredencialesManager
import com.aplicacion2.ui.main.MainActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var tvIngresarConHuella: TextView

    // 3.2 onCreate — configuración inicial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        tvIngresarConHuella = findViewById(R.id.ingresar_huella)

        //inicio de sesion con huella
        tvIngresarConHuella.setOnClickListener {
            mostrarDialogoHuella()
        }

//        //Manejo del teclado para android 15/16
//        val rootView = findViewById<android.view.ViewGroup>(R.id.main)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
//            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom)
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
//            insets
//        }

        // Listeners de los botones

        findViewById<Button>(R.id.btn_ingresar)
            .setOnClickListener { iniciarSesion() }

        findViewById<TextView>(R.id.txt_registrate)
            .setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

        findViewById<TextView>(R.id.recuperar_contrasena)
            .setOnClickListener {
                Toast.makeText(this, "Proximamente", Toast.LENGTH_SHORT).show()
            }

        findViewById<View>(R.id.btnGoogle)
            .setOnClickListener { iniciarSesionConGoogle() }

    }

    override fun onResume() {
        super.onResume()
        configurarVisibilidadHuella()
    }

    private fun configurarVisibilidadHuella() {
        //verificar si hay credenciales guardadas localmente
        val huellaActiva = CredencialesManager.huellaActiva(this)

        //Verificar si el dispositivo tiene sensor de huella
        val biometricManager = BiometricManager.from(this)
        val huellaDisponible = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS

        tvIngresarConHuella.visibility = if (huellaDisponible && huellaActiva) View.VISIBLE else View.GONE
//        val huellaDisponible = biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
//        tvIngresarConHuella.visibility = if (huellaDisponible && huellaActiva) View.VISIBLE else View.GONE
    }



    // 3.3 Función iniciarSesion()
    private fun iniciarSesion() {
        android.util.Log.d("LOGIN", "Botón presionado") // ← agregá esto primero

        val correo = findViewById<EditText>(R.id.usuario_login)
            .text.toString().trim()

        val contrasena = findViewById<EditText>(R.id.pasword_login)
            .text.toString()

        android.util.Log.d("LOGIN", "Correo: $correo, Pass length: ${contrasena.length}")

        // Validaciones locales

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasena.length < 6) {
            Toast.makeText(this, "La contrasena debe tener minimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // Llamada a Supabase Auth

        lifecycleScope.launch {
            try {

                SupabaseClient.client.auth.signInWith(Email) {
                    email = correo
                    password = contrasena
                }
                CredencialesManager.guardarCredenciales(this@LoginActivity, correo, contrasena, true)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finishAffinity()

            } catch (e: Exception) {
                val mensaje = when {
                    e.message?.contains("Invalid login credentials") == true ->
                        "Correo o contrasena incorrectos"
                    else -> "Error al iniciar sesion: ${e.message}"
                }

                Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_LONG).show()
            }
        }

    }

    // 3.7 Función iniciarSesionConGoogle()
    private fun iniciarSesionConGoogle() {

        lifecycleScope.launch {
            try {

                // 1. Configurar la solicitud de Google

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("744804564422-vo9krjl56kkus9lui75pff2bcao0nl66.apps.googleusercontent.com")
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                // 2. Mostrar el selector de cuentas

                val credentialManager = CredentialManager.create(this@LoginActivity)

                val result = credentialManager.getCredential(
                    this@LoginActivity,
                    request
                )

                // 3. Obtener el token de Google

                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)

                // 4. Enviar el token a Supabase

                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = googleIdTokenCredential.idToken
                    provider = Google
                }

                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

            } catch (e: Exception) {

                Toast.makeText(
                    this@LoginActivity,
                    "Error al iniciar con Google: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarDialogoHuella (){
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val correo = CredencialesManager.obtenerCorreo(this@LoginActivity)
                val contrasena = CredencialesManager.obtenerContrasena(this@LoginActivity)
                android.util.Log.d("HUELLA", "correo: $correo, contrasena: $contrasena")
                if (correo != null && contrasena != null) {
                    //singin credenciales normales
                    lifecycleScope.launch {
                        try {
                            SupabaseClient.client.auth.signInWith(Email) {
                                email = correo
                                password = contrasena
                            }
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finishAffinity()
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Error al iniciar sesion: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }else{
                    //no hay credenciales- no logueado previamente - limpiar-ocultar
                    Toast.makeText(this@LoginActivity, "No hay credenciales", Toast.LENGTH_LONG).show()
                    CredencialesManager.limpiarCredenciales(this@LoginActivity)
                }
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON){
                    Toast.makeText(this@LoginActivity, "Error biometrico: $errString", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAuthenticationFailed() {
                Toast.makeText(this@LoginActivity, "Autenticacion fallida", Toast.LENGTH_LONG).show()

            }
        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso con huella")
            .setSubtitle("Usa tu huella dactilar")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
