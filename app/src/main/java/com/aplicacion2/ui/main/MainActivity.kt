package com.aplicacion2.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aplicacion2.R
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.aplicacion2.ui.main.admin.AdminFragment
import com.aplicacion2.ui.main.admin.UsuariosFragment
import com.aplicacion2.ui.main.perfil.PerfilFragment
import com.aplicacion2.ui.main.productos.CatalogoFragment
import com.aplicacion2.ui.main.productos.FavoritosFragment
import com.aplicacion2.ui.main.productos.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
            }
            true
        }
    }

    private fun  cargarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}