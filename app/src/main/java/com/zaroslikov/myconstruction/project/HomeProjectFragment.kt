package com.zaroslikov.myconstruction.project

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zaroslikov.myconstruction.AddProjectFragment
import com.zaroslikov.myconstruction.MainActivity
import com.zaroslikov.myconstruction.R
import com.zaroslikov.myconstruction.WarehouseFragment
import com.zaroslikov.myconstruction.db.MyDatabaseHelper


class HomeProjectFragment : Fragment() {

    private lateinit var myDB: MyDatabaseHelper
    private var name = mutableListOf<String>()
    private var data = mutableListOf<String>()
    private var id = mutableListOf<Int>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_home_project, container, false)

        val fab = requireActivity().findViewById<View>(R.id.extended_fab) as ExtendedFloatingActionButton

        fab.setOnClickListener { onClickButton(AddProjectFragment()) }
        fab.visibility = View.VISIBLE
        fab.text = "Добавить"
        fab.setIconResource(R.drawable.baseline_add_24)
        fab.icon

        myDB = MyDatabaseHelper(requireContext())

        recyclerView = layout.findViewById(R.id.recyclerView)

        storeDataInArrays()

        val adapterProject = AdapterProject(id, name, data, true)
        recyclerView.adapter = adapterProject
        val layoutManager = GridLayoutManager(
            activity, 2
        )
        recyclerView.layoutManager = layoutManager


        adapterProject.setListener(object : Listener() {
            override fun onClick(position: Int, name: String?, data: String?, id: Int) {
                inProject(position, name, data, id)
                val mainActivity = MainActivity()
                mainActivity.projectNumer = id
            }
        })


        return layout
    }
    fun storeDataInArrays() {
        val cursor: Cursor = myDB.readProject()
        if (cursor.count != 0) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(5) == 0) {
                    id.add(cursor.getInt(0))
                    name.add(cursor.getString(1))
                    data.add(cursor.getString(2))
                }
            }
        }
        cursor.close()
    }

    private fun onClickButton(fragment: Fragment?) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, fragment!!, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }

    fun inProject(name: String?, data: String?, id: Int) {
        val warehouseFragment = WarehouseFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("date", data)
        bundle.putInt("id", id)
        warehouseFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, warehouseFragment, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }


    companion object {
    }
}