package com.aplicacion2.ui.main.productos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplicacion2.R

class HomeFragment : Fragment() {
    private val listaProducts= listOf(
        Product("Proteina", 256000.0, R.drawable.proteina1),
        Product("Guantes", 70000.0, R.drawable.guantes1),
        Product("Trembolona", 600000.0, R.drawable.trembolona1),
        Product("Testosterona", 30000.0, R.drawable.testosterona1),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_poductos)
        recyclerView.layoutManager= GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = ProductAdapter(listaProducts)

        return view
    }
}
