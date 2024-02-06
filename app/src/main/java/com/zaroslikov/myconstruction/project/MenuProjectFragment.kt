package com.zaroslikov.myconstruction.project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.zaroslikov.myconstruction.AddProjectFragment
import com.zaroslikov.myconstruction.InFragment
import com.zaroslikov.myconstruction.R
import com.zaroslikov.myconstruction.db.MyDatabaseHelper

class MenuProjectFragment : Fragment() {

    private lateinit var myDB: MyDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val layout = inflater.inflate(R.layout.fragment_menu_project, container, false)
        myDB = MyDatabaseHelper(requireContext())
        //убириаем фаб кнопку
        //убириаем фаб кнопку
        val fab = requireActivity().findViewById<View>(R.id.extended_fab) as ExtendedFloatingActionButton
        fab.visibility = View.VISIBLE

        //настройка верхнего меню

        //настройка верхнего меню
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Мои Проекты"
        appBar.menu.findItem(R.id.deleteAll).isVisible = true
        appBar.menu.findItem(R.id.magazine).isVisible = false
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.navigationIcon = null
        appBar.setOnMenuItemClickListener { item: MenuItem ->
            val position = item.itemId
            if (position == R.id.moreAll) {
                replaceFragment(InFragment())
                appBar.title = "Информация"
            } else if (position == R.id.deleteAll) {
                deleteAllData()
            }
            true
        }


        val tabLayout = layout.findViewById<TabLayout>(R.id.tab)
        val viewPager2 = layout.findViewById<ViewPager2>(R.id.view_pager)
        val menuAdapter = MenuAdapter(
            requireActivity()
        )
        viewPager2.adapter = menuAdapter

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager2.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.getTabAt(position)!!.select()
            }
        })

        return layout
    }

    private fun replaceFragment(fragment: Fragment?) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, fragment!!, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }


    private fun deleteAllData() {
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setTitle("Удаляем ВСЕ ?")
        builder.setMessage("Вы уверены, что хотите удалить все проекты, включая архивные?")
        builder.setPositiveButton(
            "Да"
        ) { dialogInterface, i -> //
            myDB.deleteAllData()
            replaceFragment(AddProjectFragment())
        }
        builder.setNegativeButton(
            "Нет"
        ) { dialogInterface, i -> }
        builder.create().show()
    }


    companion object {

    }
}