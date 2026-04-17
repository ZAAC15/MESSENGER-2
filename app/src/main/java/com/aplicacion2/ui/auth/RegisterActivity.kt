package com.aplicacion2.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.aplicacion2.R
import com.aplicacion2.SupabaseClient
import com.aplicacion2.ui.main.MainActivity
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etReContrasena: EditText
    private lateinit var checkTerminos: CheckBox
    private lateinit var btnRegistro: Button
    private lateinit var tvCuenta: TextView

    @Serializable
    data class UsuarioData(
        val id: String,
        val nombre: String
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val regis_Text3 = findViewById<TextView>(R.id.regis_Text3)

        regis_Text3.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }


        val rootView = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInserts = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = maxOf(systemBars.bottom, imeInserts.bottom )
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)
            insets
        }

        etNombres = findViewById(R.id.regis_nombre)
        etCorreo = findViewById(R.id.regis_Correo)
        etContrasena = findViewById(R.id.regis_Contraseña)
        checkTerminos = findViewById(R.id.checkTerminos)
        btnRegistro = findViewById(R.id.regis_Btn_Registrate)
        etReContrasena = findViewById(R.id.regis_Verifica_Contraseña)


        //Escuchar el boton de registros

        btnRegistro.setOnClickListener {
            val nombres = etNombres.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val reContrasena = etReContrasena.text.toString().trim()

            //validaciones

            if (nombres.isEmpty() || contrasena.isEmpty() || correo.isEmpty() || reContrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (contrasena.length < 6) {
               Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
            }
            if (contrasena != reContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkTerminos.isChecked) {
                Toast.makeText(this, "Acepte los términos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Registro en Supabase
            lifecycleScope.launch {
                try {
                    //Registrar usuario en supabase
                    val resultado = SupabaseClient.client.auth.signUpWith(Email){
                        email = correo
                        password = contrasena
                    }
                    //Guardar datos en la base de datos
                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""
                    SupabaseClient.client.postgrest["Usuarios"].insert(
                        UsuarioData(
                            id=userId,
                            nombre = nombres
                        )
                    )
                    //Redirigir al usuario al Login
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }


                }catch (e: Exception){
                        runOnUiThread {
                            Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                btnRegistro.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()

                }
        }
    }
}