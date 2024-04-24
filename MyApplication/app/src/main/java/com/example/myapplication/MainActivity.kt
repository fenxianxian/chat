package com.example.myapplication


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.fragment.app.Fragment
import com.example.myapplication.exit.BaseActivity

import com.google.android.material.tabs.TabLayout

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    var tl_demo: TabLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user: SharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val jumpWho = intent.getStringExtra("jump_who")
        setContentView(R.layout.activity_main)
        if(jumpWho=="please jump MessageFragment"){
            loadFragment(MessageFragment())
            bottomNavigation.selectedItemId = R.id.nav_message
        }else {
            loadFragment(HomeFragment())
        }
//        val home = bottomNavigation.getMenu().findItem(R.id.nav_home)
//        home.setIcon(R.drawable.home)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    title = resources.getString(R.string.home)
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_friend -> {
                    title = resources.getString(R.string.friend)
                    loadFragment(FriendFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_message -> {
                    title = resources.getString(R.string.message)
                    loadFragment(MessageFragment())

                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_user -> {
                    title = resources.getString(R.string.user)
                    loadFragment(UserFragment())
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false
        }

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        //transaction.addToBackStack(null)  //将当前Fragment的事务添加到后退栈中。这样做可以在用户按下返回按钮时返回上一个Fragment。
        transaction.commit()  //提交事务，使得上述的Fragment替换操作生效。一旦提交了事务，Fragment的生命周期方法会被调用，新的Fragment会被显示在界面上。
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }





}
