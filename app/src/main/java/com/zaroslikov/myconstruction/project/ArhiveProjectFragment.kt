package com.zaroslikov.myconstruction.project

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaroslikov.myconstruction.MainActivity
import com.zaroslikov.myconstruction.R
import com.zaroslikov.myconstruction.db.MyDatabaseHelper

class ArhiveProjectFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var myDB: MyDatabaseHelper
    private var name = mutableListOf<String>()
    private var data = mutableListOf<String>()
    private var id = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_arhive_project, container, false)
        myDB = MyDatabaseHelper(requireContext())
        recyclerView = layout.findViewById(R.id.recyclerView)

        storeDataInArrays()

        val adapterProject = AdapterProject(id, name, data, false)
        recyclerView.adapter = adapterProject
        val layoutManager = GridLayoutManager(
            activity, 2
        )
        recyclerView.layoutManager = layoutManager

//        adapterProject.onClick(object : Listener() {
//            override fun onClick(position: Int, name: String?, data: String?, id: Int) {
//                inProject(name, data)
//                val mainActivity = MainActivity()
//                mainActivity.projectNumer = id
//            }
//        })

        return layout
    }

    companion object {

    }

    private fun storeDataInArrays() {
        val cursor: Cursor = myDB.readProject()
        if (cursor.count != 0) {
            while (cursor.moveToNext()) {
                if (cursor.getInt(5) == 1) {
                    id.add(cursor.getInt(0))
                    name.add(cursor.getString(1))
                    data.add(cursor.getString(2) + " - " + cursor.getString(3))
                }
            }
        }
        cursor.close()
    }

    fun inProject(name: String?, data: String?) {
        val warehouseFragment = ArhiveWarehouseFragment()
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("date", data)
        warehouseFragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, warehouseFragment, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }
}
