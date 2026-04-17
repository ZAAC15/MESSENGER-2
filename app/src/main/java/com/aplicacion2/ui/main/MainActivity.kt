package com.aplicacion2.ui.main

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.aplicacion2.R
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aplicacion2.SupabaseClient
import com.aplicacion2.data.CredencialesManager
import com.aplicacion2.ui.main.admin.AdminFragment
import com.aplicacion2.ui.main.admin.UsuariosFragment
import com.aplicacion2.ui.main.perfil.PerfilFragment
import com.aplicacion2.ui.main.productos.CatalogoFragment
import com.aplicacion2.ui.main.productos.FavoritosFragment
import com.aplicacion2.ui.main.productos.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import android.content.Intent
import com.aplicacion2.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    lateinit var navView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        val bottonNav = findViewById<BottomNavigationView>(R.id.botton_nav)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cargarFragment(HomeFragment())
        bottonNav.selectedItemId = R.id.nav_home

        bottonNav.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.nav_home -> cargarFragment(HomeFragment())
                R.id.nav_catalogo -> cargarFragment(CatalogoFragment())
                R.id.nav_perfil -> cargarFragment(PerfilFragment())
                R.id.nav_favoritos -> cargarFragment(FavoritosFragment())
            }
            true
        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId){
                R.id.nav_home -> cargarFragment(HomeFragment())
                R.id.nav_admin -> cargarFragment(AdminFragment())
                R.id.nav_usuarios -> cargarFragment(UsuariosFragment())
                R.id.nav_favoritos -> cargarFragment(FavoritosFragment())
                R.id.nav_perfil -> cargarFragment(PerfilFragment())
                R.id.nav_salir -> cerrarSesion()  // ← agregá esto
            }
            drawerLayout.closeDrawers()  // ← cierra el drawer al seleccionar cualquier item
            true
            true
        }
    }

    private fun  cargarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun cerrarSesion() {
        lifecycleScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
            } catch (e: Exception) {
                // Si falla el signOut remoto igual limpiamos local
            } finally {
                // NO BORRAR credenciales si quieres huella

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}