package com.furev.foodstick

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.NavigatorProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.furev.foodstick.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    private var previousMenuItem:MenuItem?=null
    lateinit var sharedPreferences:SharedPreferences
    lateinit var navigationView:NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        sharedPreferences =this?.getSharedPreferences("kotlinsharedpreference", Context.MODE_PRIVATE)
        val editor=(sharedPreferences?.edit()?:this) as SharedPreferences.Editor


        toolbar = binding.toolbarLayout
        navigationView = binding.navigationDrawer
        drawerLayout = binding.drawerLayout
        hideSetUpToolbar()
        changeheader()




        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        navigationView.setCheckedItem(R.id.menu_home)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null){
                previousMenuItem?.isChecked =false
            }
            it.isCheckable =true
            it.isChecked = true
            previousMenuItem =it
            when(it.itemId){
                R.id.menu_home -> {
                    navigation(FragmentRestaurants(),"All Restaurants")

                }
                R.id.menu_favourite -> navigation(FragmentFavourite(),"Favourites")
                R.id.menu_profile -> navigation(FragmentProfile(),"About")
                R.id.menu_orderhistory -> navigation(FragmentOrderHistory(),"My Previous Orders")

            }
            return@setNavigationItemSelectedListener true
        }


    }
    fun navigation(s: Fragment, name:String){
        supportFragmentManager.beginTransaction()
            .replace(R.id.myNavHostFragment,s)
            .commit()

        supportActionBar?.title=name
        drawerLayout.closeDrawer(GravityCompat.START)

    }

    internal fun hideSetUpToolbar() {

        toolbar.visibility = View.GONE
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    internal fun showSetUpToolbar() {
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        toolbar.visibility = View.VISIBLE
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar title"

    }
    internal fun showSetUpToolbarUpButton() {
        val navController =this.findNavController(R.id.myNavHostFragment)
      //  NavigationUI.setupWithNavController(toolbar,navController)
        NavigationUI.setupWithNavController(toolbar,navController,drawerLayout)



    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =this.findNavController(R.id.myNavHostFragment)
        return navController.navigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)


    }
    @SuppressLint("InflateParams")
    internal  fun changeheader(){
        val convertView=LayoutInflater.from(this).inflate(R.layout.fragment_drawer_layout,null)
        val name = convertView.findViewById<TextView>(R.id.txt_drawer_name)
        val mobile = convertView.findViewById<TextView>(R.id.txt_drawer_mobile)
        name.text=sharedPreferences.getString("name","NAME")
        mobile.text=sharedPreferences.getString("mobile_number","123456789")

        val previous=navigationView.getHeaderView(0)
        navigationView.removeHeaderView(previous)
        navigationView.addHeaderView(convertView)
    }

}





