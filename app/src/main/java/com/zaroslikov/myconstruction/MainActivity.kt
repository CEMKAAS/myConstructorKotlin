package com.zaroslikov.myconstruction

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zaroslikov.myconstruction.databinding.ActivityMainBinding
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import com.zaroslikov.myconstruction.project.MenuProjectFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myDB: MyDatabaseHelper
    private var position = 0
    var projectNumer = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)

        myDB = MyDatabaseHelper(this)

        if (savedInstanceState == null) {

        }

        val fab = findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
        fab.visibility = View.GONE

        val appBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.setOnMenuItemClickListener {
            true
        }

        set()
        binding.navView.visibility = View.GONE
        binding.navView.setOnItemSelectedListener { item : MenuItem ->
            when(item.itemId){
                R.id.warehouse_button -> {
                    replaceFragment(WarehouseFragment())
                    appBar.title = "Мой Склад"
                    fab.hide()
                    fab.visibility = View.GONE
                }
                R.id.add_button ->{
                    replaceFragment(AddFragment())
                }
                R.id.writeOff_button ->{
                    replaceFragment(WriteOffFragment())
                }
                R.id.finance_button ->{
                    replaceFragment(FinanceFragment())
                }
            }
            true
        }


        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentByTag("visible_fragment")

            if (fragment is WarehouseFragment) {
                binding.navView.visibility = View.VISIBLE
                position = 0
            }
            if (fragment is MenuProjectFragment) {
                binding.navView.visibility = View.GONE
            }
            if (fragment is FinanceFragment) {
                binding.navView.visibility = View.VISIBLE
                position = 1
            }
            if (fragment is AddFragment) {
                binding.navView.visibility = View.VISIBLE
                position = 2
            }
            if (fragment is WriteOffFragment) {
                binding.navView.visibility = View.VISIBLE
                position = 3
            }
            binding.navView.menu.getItem(position).isChecked = true
        }


    }

    fun set() {

        val cursor = myDB.readProduct()

        when (cursor.count) {
            0 -> {
                replaceFragment(AddProjectFragment())
            }

            1 -> {
                cursor.moveToNext()
                projectNumer = cursor.getInt(0)

                replaceFragment(WarehouseFragment())
            }

            else -> {
                replaceFragment(MenuProjectFragment())
            }
        }
        cursor.close()

    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,fragment)
            .commit()

    }


}